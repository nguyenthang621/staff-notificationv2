package com.istt.staff_notification_v2.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.NoResultException;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import com.istt.staff_notification_v2.apis.errors.BadRequestAlertException;
import com.istt.staff_notification_v2.dto.ResponseDTO;
import com.istt.staff_notification_v2.dto.RoleDTO;
import com.istt.staff_notification_v2.dto.SearchDTO;
import com.istt.staff_notification_v2.entity.Role;
import com.istt.staff_notification_v2.entity.User;
import com.istt.staff_notification_v2.repository.RoleRepo;
import com.istt.staff_notification_v2.repository.UserRepo;

public interface RoleService {
	RoleDTO create(RoleDTO roleDTO);

	RoleDTO delete(String id);

	ResponseDTO<List<RoleDTO>> search(SearchDTO searchDTO);

	RoleDTO findById(String id);

	RoleDTO findByName(String name);

	RoleDTO update(RoleDTO roleDTO);

	List<RoleDTO> getAll();

	List<RoleDTO> deleteAllbyIds(List<String> ids);
	
	List<RoleDTO> getRoleFromUser(String userId);
	
	List<RoleDTO> createAll(List<RoleDTO> roleDTO);
	
}

@Service
class RoleServiceImpl implements RoleService {

	@Autowired
	private RoleRepo roleRepo;
	
	@Autowired
	private UserRepo userRepo;

	private static final String ENTITY_NAME = "isttRole";

	@Transactional
	@Override
	public RoleDTO create(RoleDTO roleDTO) {
		try {

			if (roleRepo.findByRoleName(roleDTO.getRole()).isPresent())
				throw new BadRequestAlertException("Bad request: Role already exists", ENTITY_NAME, "Exists");
			ModelMapper mapper = new ModelMapper();
			Role role = mapper.map(roleDTO, Role.class);
			role.setRoleId(UUID.randomUUID().toString().replaceAll("-", ""));
			roleRepo.save(role);
			return roleDTO;
		} catch (ResourceAccessException e) {
			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
		} catch (HttpServerErrorException | HttpClientErrorException e) {
			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
		}
	}

	@Transactional
	@Override
	public RoleDTO delete(String id) {
		try {
			Role role = roleRepo.findById(id).orElseThrow(NoResultException::new);
			if (role != null) {
				roleRepo.deleteById(id);
				return new ModelMapper().map(role, RoleDTO.class);
			}
			return null;
		} catch (ResourceAccessException e) {
			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
		} catch (HttpServerErrorException | HttpClientErrorException e) {
			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
		}
	}

	@Override
	public ResponseDTO<List<RoleDTO>> search(SearchDTO searchDTO) {
		try {
			List<Sort.Order> orders = Optional.ofNullable(searchDTO.getOrders()).orElseGet(Collections::emptyList)
					.stream().map(order -> {
						if (order.getOrder().equals(SearchDTO.ASC))
							return Sort.Order.asc(order.getProperty());

						return Sort.Order.desc(order.getProperty());
					}).collect(Collectors.toList());
			Pageable pageable = PageRequest.of(searchDTO.getPage(), searchDTO.getSize(), Sort.by(orders));

			Page<Role> page = roleRepo.searchByName(searchDTO.getValue(), pageable);

			List<RoleDTO> roleDTOs = new ArrayList<>();
			for (Role role : page.getContent()) {
				RoleDTO roleDTO = new ModelMapper().map(role, RoleDTO.class);
				roleDTOs.add(roleDTO);
			}

			ResponseDTO<List<RoleDTO>> responseDTO = new ModelMapper().map(page, ResponseDTO.class);
			responseDTO.setData(roleDTOs);
			return responseDTO;
		} catch (ResourceAccessException e) {
			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
		} catch (HttpServerErrorException | HttpClientErrorException e) {
			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
		}
	}

	@Override
	@Transactional
	public RoleDTO update(RoleDTO roleDTO) {
		try {
			if (roleRepo.findById(roleDTO.getRoleId()).isEmpty())
				throw new BadRequestAlertException("Role not found", ENTITY_NAME, "Not found");

			roleRepo.save(new ModelMapper().map(roleDTO, Role.class));
			return roleDTO;
		} catch (ResourceAccessException e) {
			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
		} catch (HttpServerErrorException | HttpClientErrorException e) {
			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
		}
	}

	@Override
	public RoleDTO findById(String id) {
		try {
			Role role = roleRepo.findById(id).orElseThrow(NoResultException::new);
			return new ModelMapper().map(role, RoleDTO.class);
		} catch (ResourceAccessException e) {
			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
		} catch (HttpServerErrorException | HttpClientErrorException e) {
			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
		}

	}

	@Override
	public RoleDTO findByName(String name) {
		try {
			Role role = roleRepo.findByRoleName(name).get();
			return new ModelMapper().map(role, RoleDTO.class);
		} catch (ResourceAccessException e) {
			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
		} catch (HttpServerErrorException | HttpClientErrorException e) {
			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
		}

	}

	@Override
	public List<RoleDTO> getAll() {
		try {
			List<Role> roles = roleRepo.findAll();
			return roles.stream().map(role -> new ModelMapper().map(role, RoleDTO.class)).collect(Collectors.toList());
		} catch (ResourceAccessException e) {
			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
		} catch (HttpServerErrorException | HttpClientErrorException e) {
			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
		}
	}

	@Override
	public List<RoleDTO> deleteAllbyIds(List<String> ids) {
		try {
			List<Role> list = roleRepo.findByRoleIds(ids).orElseThrow(NoResultException::new);

			if (!list.isEmpty()) {
				roleRepo.deleteAllInBatch(list);
				return list.stream().map(role -> new ModelMapper().map(role, RoleDTO.class))
						.collect(Collectors.toList());
			}
			throw new BadRequestAlertException("Role empty", ENTITY_NAME, "invalid");
		} catch (ResourceAccessException e) {
			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
		} catch (HttpServerErrorException | HttpClientErrorException e) {
			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
		}
	}

	@Override
	public List<RoleDTO> getRoleFromUser(String userId) {
//		Optional<User> user = userRepo.findById(userId);
//		List<Role> roles = roleRepo.findByGroupRole(user.get().getGroup());
//		ModelMapper mapper = new ModelMapper();
		return null;
	}

	@Override
	public List<RoleDTO> createAll(List<RoleDTO> roles) {
		for (RoleDTO roleDTO2 : roles) {
			create(roleDTO2);
		}
		return roles;
	}

}