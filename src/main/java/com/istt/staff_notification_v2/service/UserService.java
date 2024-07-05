package com.istt.staff_notification_v2.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.NoResultException;
import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import com.istt.staff_notification_v2.apis.errors.BadRequestAlertException;
import com.istt.staff_notification_v2.configuration.ApplicationProperties;
import com.istt.staff_notification_v2.dto.EmployeeDTO;
import com.istt.staff_notification_v2.dto.ResponseDTO;
import com.istt.staff_notification_v2.dto.SearchDTO;
import com.istt.staff_notification_v2.dto.UpdatePassword;
import com.istt.staff_notification_v2.dto.UserDTO;
import com.istt.staff_notification_v2.dto.UserResponse;
import com.istt.staff_notification_v2.entity.Role;
import com.istt.staff_notification_v2.entity.User;
import com.istt.staff_notification_v2.repository.RoleRepo;
import com.istt.staff_notification_v2.repository.UserRepo;

public interface UserService {
	UserDTO create(UserDTO userDTO);

	UserDTO findByName(String name);

	UserDTO findByEmail(String email);

	UserDTO getEmployeeByEmployeename(String employeename);

	UserDTO update(UserDTO userDTO);

	Boolean delete(String id);

	Boolean deleteAll(List<Integer> ids);

	UserResponse get(String id);

	UserResponse updatePassword(UpdatePassword updatePassword);

	ResponseDTO<List<UserResponse>> search(SearchDTO searchDTO);

}

@Service
class UserServiceImpl implements UserService {

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private RoleRepo roleRepo;

	@Autowired
	ApplicationProperties props;

	private static final String ENTITY_NAME = "isttUser";

	@Override
	@Transactional
	public UserDTO create(UserDTO userDTO) {
		try {
			ModelMapper mapper = new ModelMapper();
			// creatte user
			User user = mapper.map(userDTO, User.class);
			String user_id = UUID.randomUUID().toString().replaceAll("-", "");
			user.setUserId(user_id);
			user.setPassword(new BCryptPasswordEncoder().encode(userDTO.getPassword()));

			if (userRepo.findByUsername(userDTO.getUsername()).isPresent()) {
				throw new BadRequestAlertException("Bad request: USER already exists", ENTITY_NAME, "USER exists");
			}
			// map role
			Set<Role> roles = new HashSet<>();
			roles.addAll(roleRepo
					.findByRoleIds(
							userDTO.getRoles().stream().map(role -> role.getRoleId()).collect(Collectors.toList()))
					.orElseThrow(NoResultException::new));
			user.setRoles(roles);

			// commit save
			userRepo.save(user);
			return mapper.map(user, UserDTO.class);

		} catch (ResourceAccessException e) {
			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
		} catch (HttpServerErrorException | HttpClientErrorException e) {
			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
		}
	}

	@Override
	public UserDTO findByName(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserDTO findByEmail(String email) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserDTO getEmployeeByEmployeename(String employeename) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional
	public UserDTO update(UserDTO userDTO) {
		try {
			User user = userRepo.findByUserId(userDTO.getId()).orElseThrow(NoResultException::new);
			user.setPassword(new BCryptPasswordEncoder().encode(userDTO.getPassword()));
			Set<Role> roles = new HashSet<Role>();
			roles.addAll(roleRepo
					.findByRoleIds(
							userDTO.getRoles().stream().map(role -> role.getRoleId()).collect(Collectors.toList()))
					.orElseThrow(NoResultException::new));
			user.setRoles(roles);
			userRepo.save(user);
			return userDTO;

		} catch (ResourceAccessException e) {
			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
		} catch (HttpServerErrorException | HttpClientErrorException e) {
			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
		}
	}

	@Override
	@Transactional
	public Boolean delete(String id) {
		User user = userRepo.findById(id).orElseThrow(NoResultException::new);
		if (user != null) {
			userRepo.deleteById(id);
		}
		return null;
	}

	@Override
	public Boolean deleteAll(List<Integer> ids) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional
	public UserResponse get(String id) {
		try {
			User user = userRepo.findByUserId(id).orElseThrow(NoResultException::new);
			UserResponse userResponse = new ModelMapper().map(user, UserResponse.class);
			return userResponse;

		} catch (ResourceAccessException e) {
			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
		} catch (HttpServerErrorException | HttpClientErrorException e) {
			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
		}

	}

	@Override
	@Transactional
	public UserResponse updatePassword(UpdatePassword updatePassword) {
		try {
			User user = userRepo.findByUserId(updatePassword.getUserId()).orElseThrow(NoResultException::new);
			Boolean compare_password = BCrypt.checkpw(updatePassword.getOldPassword(), user.getPassword());
			if (!compare_password)
				throw new BadRequestAlertException("Bad request: Old Password wrong !!!", ENTITY_NAME,
						"Invalid password");

			if (!updatePassword.getNewPassword().equals(updatePassword.getConfirmPassword())) {
				throw new BadRequestAlertException("Bad request: Password do not match", ENTITY_NAME,
						"Invalid password");
			}
			user.setPassword(new BCryptPasswordEncoder().encode(updatePassword.getNewPassword()));
			userRepo.save(user);
			UserResponse userResponse = new ModelMapper().map(user, UserResponse.class);
			return userResponse;

		} catch (ResourceAccessException e) {
			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
		} catch (HttpServerErrorException | HttpClientErrorException e) {
			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
		}

	}

	@Override
	public ResponseDTO<List<UserResponse>> search(SearchDTO searchDTO) {
		try {
			List<Sort.Order> orders = Optional.ofNullable(searchDTO.getOrders()).orElseGet(Collections::emptyList)
					.stream().map(order -> {
						if (order.getOrder().equals(SearchDTO.ASC))
							return Sort.Order.asc(order.getProperty());

						return Sort.Order.desc(order.getProperty());
					}).collect(Collectors.toList());

			Pageable pageable = PageRequest.of(searchDTO.getPage(), searchDTO.getSize(), Sort.by(orders));

			Page<User> page = userRepo.search(searchDTO.getValue(), pageable);

			List<UserResponse> userDTOList = new ArrayList<>();

			for (User user : page.getContent()) {
				UserResponse userResponse = new ModelMapper().map(user, UserResponse.class);
				userDTOList.add(userResponse);
			}

			ResponseDTO<List<UserResponse>> responseDTO = new ModelMapper().map(page, ResponseDTO.class);
			responseDTO.setData(userDTOList);

			return responseDTO;
		} catch (ResourceAccessException e) {
			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
		} catch (HttpServerErrorException | HttpClientErrorException e) {
			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
		}
	}

	
	

}