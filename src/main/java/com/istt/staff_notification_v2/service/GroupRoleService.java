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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import com.istt.staff_notification_v2.apis.errors.BadRequestAlertException;
import com.istt.staff_notification_v2.dto.EmployeeDTO;
import com.istt.staff_notification_v2.dto.GroupRoleDTO;
import com.istt.staff_notification_v2.dto.LevelDTO;
import com.istt.staff_notification_v2.dto.RoleDTO;
import com.istt.staff_notification_v2.entity.Department;
import com.istt.staff_notification_v2.entity.Employee;
import com.istt.staff_notification_v2.entity.GroupRole;
import com.istt.staff_notification_v2.entity.Level;
import com.istt.staff_notification_v2.entity.Role;
import com.istt.staff_notification_v2.repository.GroupRoleRepo;
import com.istt.staff_notification_v2.repository.RoleRepo;

public interface GroupRoleService {
	GroupRoleDTO create(GroupRoleDTO groupRoleDTO);
	GroupRoleDTO update(GroupRoleDTO groupRoleDTO);
	GroupRoleDTO get(GroupRoleDTO groupRoleDTO);
	Boolean delete(String id);
	List<GroupRoleDTO> deleteByList(List<String> ids);
	List<GroupRoleDTO> getAll();
	List<GroupRoleDTO> updateByList(List<GroupRoleDTO> groupRoleDTOs);
	List<GroupRoleDTO> searchByRole(String role);
}

@Service
class GroupRoleServiceImpl implements GroupRoleService{

	@Autowired
	private GroupRoleRepo groupRoleRepo;
	
	@Autowired
	private RoleRepo roleRepo;
	
	private static final String ENTITY_NAME = "isttGroupRole";
	private static final Logger logger = LogManager.getLogger(GroupRoleService.class);

	
	@Override
	public List<GroupRoleDTO> getAll() {
		ModelMapper mapper = new ModelMapper();
		List<GroupRole> groups = groupRoleRepo.findAll();
		return groups
				  .stream().map(group -> mapper.map(group, GroupRoleDTO.class))
				  .collect(Collectors.toList());
	}
	@Override
	public GroupRoleDTO create(GroupRoleDTO groupRoleDTO) {
		try {
			ModelMapper modelMapper = new ModelMapper();
			GroupRole groupRole = modelMapper.map(groupRoleDTO, GroupRole.class);
			
			groupRole.setGroupId(UUID.randomUUID().toString().replaceAll("-", ""));
			
			if(groupRoleRepo.existsByGroupName(groupRoleDTO.getGroupName())) throw new BadRequestAlertException("Group role is exists",ENTITY_NAME, "exists");
			
			Set<Role> roles = new HashSet<Role>();
			if(groupRoleDTO.getRoles().size()>0) {
			for (RoleDTO roleDTO : groupRoleDTO.getRoles()) {
				Role role = roleRepo.findById(roleDTO.getRoleId()).orElseThrow(NoResultException::new);
				roles.add(role);
			}}
			groupRole.setRoles(roles);
			
			groupRoleRepo.save(groupRole);
			return modelMapper.map(groupRole, GroupRoleDTO.class);
			
		} catch (ResourceAccessException e) {
			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
		} catch (HttpServerErrorException | HttpClientErrorException e) {
			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
		}
	}
	@Override
	public GroupRoleDTO update(GroupRoleDTO groupRoleDTO) {
		try {
			ModelMapper modelMapper = new ModelMapper();
			
			if(!groupRoleRepo.existsById(groupRoleDTO.getGroupId()))
				throw new BadRequestAlertException("GroupRole not found", ENTITY_NAME, "missingdata");
			GroupRole groupRole = groupRoleRepo.findById(groupRoleDTO.getGroupId()).get();
			
			groupRole.setGroupName(groupRoleDTO.getGroupName());
			Set<Role> roles = new HashSet<Role>();
			if(groupRoleDTO.getRoles().size()>0) {
			for (RoleDTO roleDTO : groupRoleDTO.getRoles()) {
				Role role = roleRepo.findById(roleDTO.getRoleId()).orElseThrow(NoResultException::new);
				roles.add(role);
			}}
			groupRole.setRoles(roles);
			
			groupRoleRepo.save(groupRole);
			return modelMapper.map(groupRole, GroupRoleDTO.class);
			
		} catch (ResourceAccessException e) {
			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
		} catch (HttpServerErrorException | HttpClientErrorException e) {
			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
		}
	}
	@Override
	public Boolean delete(String id) {
		try {
			if(groupRoleRepo.existsById(id)) {
				groupRoleRepo.deleteById(id);
				return true;
			}
			return false;
			
		} catch (ResourceAccessException e) {
			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
		} catch (HttpServerErrorException | HttpClientErrorException e) {
			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
		}
	}
	@Override
	public List<GroupRoleDTO> deleteByList(List<String> ids) {
		try {
			List<GroupRole> list = groupRoleRepo.findAllById(ids);
			if(list.size()>0) {
				groupRoleRepo.deleteAllInBatch(list);
				return list.stream().map(groupRole -> new ModelMapper().map(groupRole, GroupRoleDTO.class))
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
	public List<GroupRoleDTO> updateByList(List<GroupRoleDTO> groupRoleDTOs) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public GroupRoleDTO get(GroupRoleDTO groupRoleDTO) {
		try {
			GroupRole groupRole = groupRoleRepo.findById(groupRoleDTO.getGroupId()).orElseThrow(NoResultException::new);
			return new ModelMapper().map(groupRole, GroupRoleDTO.class);
		} catch (ResourceAccessException e) {
			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
		} catch (HttpServerErrorException | HttpClientErrorException e) {
			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
		}
	}
	@Override
	public List<GroupRoleDTO> searchByRole(String role) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
