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
import com.istt.staff_notification_v2.dto.LevelDTO;
import com.istt.staff_notification_v2.dto.ResponseGroupDTO;
import com.istt.staff_notification_v2.dto.ResponseRoleDTO;
import com.istt.staff_notification_v2.dto.RoleDTO;
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
			
			Set<Role> roles = new HashSet<Role>();
			if(groupDTO.getRoles().size()>0) {
			for (RoleDTO roleDTO : groupDTO.getRoles()) {
				Role role = roleRepo.findById(roleDTO.getRoleId()).orElseThrow(NoResultException::new);
				roles.add(role);
			}}
			group.setRoles(roles);
			
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
			Set<Role> roles = new HashSet<Role>();
			if(groupDTO.getRoles().size()>0) {
			for (RoleDTO roleDTO : groupDTO.getRoles()) {
				Role role = roleRepo.findById(roleDTO.getRoleId()).orElseThrow(NoResultException::new);
				roles.add(role);
			}}
			group.setRoles(roles);
			
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
//		try {
//			Group group = new Group();
//			if(groupRepo.existsById(id)) {
//				group = groupRepo.findById(id).get();
//				List<Role> roles = roleRepo.findByGroupRole(group);
//				if(roles.size()>0) {
//					for (Role role : roles) {
//						role.getGroups().remove(group);
//					}
//				}
//				groupRepo.deleteById(id);
//				return true;
//			}
//			return false;
//			
//		} catch (ResourceAccessException e) {
//			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
//		} catch (HttpServerErrorException | HttpClientErrorException e) {
//			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
//		}
		return null;
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
	public ResponseGroupDTO addRoleToGroup(ResponseGroupDTO resGroupDTO) {
		Group group = groupRepo.findById(resGroupDTO.getGroupId()).orElseThrow(NoResultException::new);
		Set<ResponseRoleDTO> resRoles = new HashSet<ResponseRoleDTO>();
		for (FeatureDTO featureDTO : resGroupDTO.getFeatures()) {
			for (ResponseRoleDTO responseRoleDTO : featureDTO.getRoles()) {
				if(responseRoleDTO.getIsActive()) resRoles.add(responseRoleDTO);
			}
		}
		ModelMapper mapper = new ModelMapper();
		Set<Role> role = resRoles.stream().map(resRole -> mapper.map(resRole, Role.class)).collect(Collectors.toSet());
		group.setRoles(role);
		groupRepo.save(group);
		return resGroupDTO;
	}
	
}
