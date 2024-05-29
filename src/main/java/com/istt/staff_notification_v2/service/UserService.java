package com.istt.staff_notification_v2.service;

import java.util.List;
import java.util.UUID;

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

	UserDTO get(Integer id);
}

@Service
class UserServiceImpl implements UserService {

	@Autowired
	private UserRepo useRepo;

	@Autowired
	ApplicationProperties props;

	@Override
	@Transactional
	public UserDTO create(UserDTO userDTO) {
		try {
			ModelMapper mapper = new ModelMapper();
			User user = mapper.map(userDTO, User.class);
			String employee_id = UUID.randomUUID().toString().replaceAll("-", "");
			user.setUser_id(UUID.randomUUID().toString().replaceAll("-", ""));
			user.setPassword(new BCryptPasswordEncoder().encode(userDTO.getPassword()));
			useRepo.save(user);
			return userDTO;
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
	public UserDTO get(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

}