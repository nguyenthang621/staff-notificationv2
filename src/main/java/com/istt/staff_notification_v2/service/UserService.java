package com.istt.staff_notification_v2.service;

import java.util.List;
import java.util.UUID;

import javax.persistence.NoResultException;
import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import com.istt.staff_notification_v2.configuration.ApplicationProperties;
import com.istt.staff_notification_v2.dto.UserDTO;
import com.istt.staff_notification_v2.dto.UserResponse;
import com.istt.staff_notification_v2.entity.Employee;
import com.istt.staff_notification_v2.entity.User;
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
}

@Service
class UserServiceImpl implements UserService {

	@Autowired
	private UserRepo userRepo;

	@Autowired
	ApplicationProperties props;

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

			// map employee
			Employee employee = new Employee();
			if (user.getEmployee() != null) {
				employee = user.getEmployee();
				if (!props.getSTATUS_EMPLOYEE().contains(user.getEmployee().getStatus())) { // Validate if not contain
																							// set default suspend
					employee.setStatus(props.getSTATUS_EMPLOYEE().get(0));
				}
			} else {
				employee.setEmail(user.getUsername());
				employee.setStatus(props.getSTATUS_EMPLOYEE().get(0));
			}
			employee.setEmployeeId(UUID.randomUUID().toString().replaceAll("-", ""));

			user.setEmployee(employee);

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
	public UserDTO update(UserDTO userDTO) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean delete(String id) {
		// TODO Auto-generated method stub
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

}