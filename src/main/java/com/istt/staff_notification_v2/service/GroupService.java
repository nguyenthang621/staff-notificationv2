package com.istt.staff_notification_v2.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.NoResultException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import com.istt.staff_notification_v2.apis.errors.BadRequestAlertException;
import com.istt.staff_notification_v2.dto.EmployeeDTO;
import com.istt.staff_notification_v2.dto.FeatureDTO;
import com.istt.staff_notification_v2.dto.GroupDTO;
import com.istt.staff_notification_v2.dto.GroupUserDTO;
import com.istt.staff_notification_v2.dto.LevelDTO;
import com.istt.staff_notification_v2.dto.ResponseGroupDTO;
import com.istt.staff_notification_v2.dto.ResponseRoleDTO;
import com.istt.staff_notification_v2.dto.RoleDTO;
import com.istt.staff_notification_v2.dto.UserResponse;
import com.istt.staff_notification_v2.entity.Department;
import com.istt.staff_notification_v2.entity.Employee;
import com.istt.staff_notification_v2.entity.Feature;
import com.istt.staff_notification_v2.entity.Group;
import com.istt.staff_notification_v2.entity.Level;
import com.istt.staff_notification_v2.entity.Role;
import com.istt.staff_notification_v2.entity.User;
import com.istt.staff_notification_v2.repository.FeatureRepo;
import com.istt.staff_notification_v2.repository.GroupRepo;
import com.istt.staff_notification_v2.repository.RoleRepo;
import com.istt.staff_notification_v2.repository.UserRepo;

public interface GroupService {
	GroupDTO create(GroupDTO groupDTO);
	GroupDTO update(GroupDTO groupDTO);
	GroupDTO get(String id);
	Boolean delete(String id);
	List<GroupDTO> deleteByList(List<String> ids);
	List<GroupDTO> getAll();
	List<GroupDTO> updateByList(List<GroupDTO> groupDTOs);
	
	ResponseGroupDTO getGroup(String id);
	ResponseGroupDTO addRoleToGroup(ResponseGroupDTO resGroupDTO);
	GroupUserDTO getUser(String id);
	GroupUserDTO addUserToGroup(GroupUserDTO groupUserDTO);
	Boolean addAllRole(String username);
//	GroupDTO getMinGroupFromUser(String userId);
}

@Service
class GroupRoleServiceImpl implements GroupService{

	@Autowired
	private FeatureRepo featureRepo;
	
	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private GroupRepo groupRepo;
	
	@Autowired
	private RoleRepo roleRepo;
	
	@Autowired
	private FeatureService featureService;
	
	
	private static final String ENTITY_NAME = "isttGroupRole";
	private static final Logger logger = LogManager.getLogger(GroupService.class);
	
//	private static Long getMaxGroupLevel(User user) {
//		ModelMapper mapper = new ModelMapper();
//		Long min = Long.MIN_VALUE;
//		List<GroupDTO> groupDTOs = user.getGroups().stream().map(group -> mapper.map(group, GroupDTO.class)).collect(Collectors.toList());
//		for (GroupDTO groupDTO : groupDTOs) {
//			if(min < groupDTO.getGroupLevel()) min = groupDTO.getGroupLevel();
//		}
//		return min;
//	}
	
	@Override
	public List<GroupDTO> getAll() {
		ModelMapper mapper = new ModelMapper();
		List<Group> groups = groupRepo.findAll();
		return groups
				  .stream().map(group -> mapper.map(group, GroupDTO.class))
				  .collect(Collectors.toList());
	}
	@Override
	public GroupDTO create(GroupDTO groupDTO) {
		try {
			ModelMapper modelMapper = new ModelMapper();
			Group group = modelMapper.map(groupDTO, Group.class);
			
			group.setGroupId(UUID.randomUUID().toString().replaceAll("-", ""));
			
			if(groupRepo.existsByGroupName(groupDTO.getGroupName())) throw new BadRequestAlertException("Group role is exists",ENTITY_NAME, "exists");
			
			groupRepo.save(group);
			return modelMapper.map(group, GroupDTO.class);
			
		} catch (ResourceAccessException e) {
			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
		} catch (HttpServerErrorException | HttpClientErrorException e) {
			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
		}
	}
	@Override
	public GroupDTO update(GroupDTO groupDTO) {
		try {
			ModelMapper modelMapper = new ModelMapper();
			
			if(!groupRepo.existsById(groupDTO.getGroupId()))
				throw new BadRequestAlertException("Group not found", ENTITY_NAME, "missingdata");
			Group group = groupRepo.findById(groupDTO.getGroupId()).get();
			
			group.setGroupName(groupDTO.getGroupName());
			
			groupRepo.save(group);
			return modelMapper.map(group, GroupDTO.class);
			
		} catch (ResourceAccessException e) {
			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
		} catch (HttpServerErrorException | HttpClientErrorException e) {
			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
		}
	}
	@Override
	public Boolean delete(String id) {
		try {
			Optional<Group> groupOp = groupRepo.findById(id);
			if(groupOp.isEmpty()) return false;
			Group group = groupOp.get();
			
//			Set<Role> roles = group.getRoles();
//			for (Role role : roles) {
//				role.getGroups().remove(group);
//			}
//			roleRepo.saveAll(roles);
			group.setRoles(null);
			
//			Set<User> users = group.getUsers();
//			for (User user : users) {
//				user.getGroups().remove(group);
//			}
//			userRepo.saveAll(users);
			group.setUsers(null);
			
			groupRepo.delete(group);
			return true;
			
		} catch (ResourceAccessException e) {
			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
		} catch (HttpServerErrorException | HttpClientErrorException e) {
			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
		}
	}
	@Override
	public List<GroupDTO> deleteByList(List<String> ids) {
		try {
			List<Group> list = groupRepo.findAllById(ids);
			if(list.size()>0) {
				groupRepo.deleteAllInBatch(list);
				return list.stream().map(groupRole -> new ModelMapper().map(groupRole, GroupDTO.class))
						.collect(Collectors.toList());
			}
			return null;
		} catch (ResourceAccessException e) {
			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
		} catch (HttpServerErrorException | HttpClientErrorException e) {
			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
	}
	}
	@Override
	public List<GroupDTO> updateByList(List<GroupDTO> groupDTOs) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public GroupDTO get(String id) {
		try {
			Group group = groupRepo.findById(id).orElseThrow(NoResultException::new);
			return new ModelMapper().map(group, GroupDTO.class);
		} catch (ResourceAccessException e) {
			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
		} catch (HttpServerErrorException | HttpClientErrorException e) {
			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
		}
	}
	@Override
	public ResponseGroupDTO getGroup(String id) {
		ModelMapper mapper = new ModelMapper();
		Group group = groupRepo.findById(id).orElseThrow(NoResultException::new);
		//get all role in group
		Set<Role> roles = group.getRoles();
		ResponseGroupDTO resGroup = new ResponseGroupDTO();
		resGroup.setGroupId(id);
		resGroup.setGroupName(group.getGroupName());
		Set<FeatureDTO> featureDTOs = featureService.getFeaturesFromRoles(roles);
		resGroup.setFeatures(featureDTOs);
		return resGroup;
	}
	
	@Override
	public GroupUserDTO getUser(String id) {
		ModelMapper mapper = new ModelMapper();
		Group group = groupRepo.findById(id).orElseThrow(NoResultException::new);
		GroupUserDTO groupUserDTO = mapper.map(group, GroupUserDTO.class);
		Set<UserResponse> userResponses = new HashSet<UserResponse>();
		// System.err.println(group.getUsers().size());
		userResponses = group.getUsers().stream().map(userR -> mapper.map(userR, UserResponse.class))
				.collect(Collectors.toSet());
		groupUserDTO.setUser(userResponses);
		return groupUserDTO;
	}
	@Override
	public ResponseGroupDTO addRoleToGroup(ResponseGroupDTO resGroupDTO) {
		Set<Role> roles = new HashSet<Role>();
		Role role = new Role();
		Group group = groupRepo.findById(resGroupDTO.getGroupId()).orElseThrow(NoResultException::new);
		Set<ResponseRoleDTO> resRoles = new HashSet<ResponseRoleDTO>();
		for (FeatureDTO featureDTO : resGroupDTO.getFeatures()) {
			for (ResponseRoleDTO responseRoleDTO : featureDTO.getRoles()) {
				if(responseRoleDTO.getIsActive()) {
					role= roleRepo.findById(responseRoleDTO.getRoleId()).get();
					roles.add(role);
				}
			}
		}
		ModelMapper mapper = new ModelMapper();
//		Set<Role> role = resRoles.stream().map(resRole -> mapper.map(resRole, Role.class)).collect(Collectors.toSet());
		
		group.setRoles(roles);
		groupRepo.save(group);
		
		return resGroupDTO;
	}
	@Override
	public GroupUserDTO addUserToGroup(GroupUserDTO groupUserDTO) {
		Group group = groupRepo.findById(groupUserDTO.getGroupId()).orElseThrow(NoResultException::new);
		// if(group!=null)
		// 	System.err.println(group.getGroupId());
		// else System.err.println("NOT FOUND GROUP");
		Set<User> users = new HashSet<User>();
		for (UserResponse userRes : groupUserDTO.getUser()) {
			User user = userRepo.findById(userRes.getUserId()).orElseThrow(NoResultException::new);
			user.getGroups().add(group);
			users.add(user);
		}
		userRepo.saveAll(users);
		group.setUsers(users);
		groupRepo.save(group);
		return groupUserDTO;
	}
	@Override
	public Boolean addAllRole(String username) {
		Group group = groupRepo.findByGroupName("admin").get();
		List<Role> roles = roleRepo.findAll();
		Set<Role> setRoles = new HashSet<Role>();
		setRoles.addAll(roles);
		group.setRoles(setRoles);
		User user = userRepo.findByUsername(username).get(); 
		user.getGroups().add(group);
		userRepo.save(user);
		group.getUsers().add(user);
		groupRepo.save(group);
		return true;
	}
//	@Override
//	public GroupDTO getMinGroupFromUser(String userId) {
//		GroupDTO groupDTO = new GroupDTO();
//		User user = userRepo.findById(userId).orElseThrow(NoResultException::new);
//		if(user.getGroups().size()>0) {
//			Long min = getMaxGroupLevel(user);
//			Group group = groupRepo.findByGroupLevel(min).get();
//			return new ModelMapper().map(group, GroupDTO.class);
//		}
//		return groupDTO;
//	}
	
}
