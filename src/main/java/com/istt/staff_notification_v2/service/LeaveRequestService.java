package com.istt.staff_notification_v2.service;

import java.util.UUID;

import javax.persistence.NoResultException;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import com.istt.staff_notification_v2.apis.errors.BadRequestAlertException;
import com.istt.staff_notification_v2.configuration.ApplicationProperties;
import com.istt.staff_notification_v2.dto.LeaveRequestDTO;
import com.istt.staff_notification_v2.entity.Employee;
import com.istt.staff_notification_v2.entity.LeaveRequest;
import com.istt.staff_notification_v2.entity.User;
import com.istt.staff_notification_v2.repository.EmployeeRepo;
import com.istt.staff_notification_v2.repository.LeaveRequestRepo;
import com.istt.staff_notification_v2.repository.UserRepo;

public interface LeaveRequestService {

	LeaveRequestDTO create(LeaveRequestDTO leaveRequestDTO);

	LeaveRequestDTO update(LeaveRequestDTO leaveRequest);

	LeaveRequestDTO delete(String id);

	LeaveRequestDTO get(String id);

}

@Service
class LeaveRequestServiceImpl implements LeaveRequestService {

	@Autowired
	LeaveRequestRepo leaveRequestRepo;

	@Autowired
	EmployeeRepo employeeRepo;

	@Autowired
	ApplicationProperties props;

	@Autowired
	private UserRepo userRepo;

	private static final String ENTITY_NAME = "isttLeaveRequestType";

	@Override
	public LeaveRequestDTO create(LeaveRequestDTO leaveRequestDTO) {
		try {
			ModelMapper mapper = new ModelMapper();
			LeaveRequest leaveRequest = mapper.map(leaveRequestDTO, LeaveRequest.class);
			leaveRequest.setLeaveqequestId(UUID.randomUUID().toString().replaceAll("-", ""));

			if (leaveRequestDTO.getEmployee().getEmployeeId() != null) { // map employee
				Employee employee = employeeRepo.findByEmployeeId(leaveRequestDTO.getEmployee().getEmployeeId())
						.orElseThrow(NoResultException::new);
				leaveRequest.setEmployee(employee);
			} else { // map new employee

				Employee employeeExits = employeeRepo.findByEmail(leaveRequestDTO.getEmployee().getEmail());
				System.out.println("employeeExits= " + employeeExits);
				if (employeeExits != null) {
					throw new BadRequestAlertException("Employees already have an account, please log in", ENTITY_NAME,
							"User exits");
				}

				// creatte new user
				User user = new User();
				String user_id = UUID.randomUUID().toString().replaceAll("-", "");
				user.setUserId(user_id);
				user.setUsername(leaveRequestDTO.getEmployee().getEmail());
				user.setPassword(new BCryptPasswordEncoder().encode("abcd456789"));

				// map new employee
				Employee employee = leaveRequestDTO.getEmployee();
				employee.setEmployeeId(UUID.randomUUID().toString().replaceAll("-", ""));
				employee.setEmail(user.getUsername());
				employee.setStatus(props.getSTATUS_EMPLOYEE().get(0));

				user.setEmployee(employee);

				// commit save
				userRepo.save(user);

				leaveRequest.setEmployee(employee);
			}

			leaveRequestRepo.save(leaveRequest);
			return leaveRequestDTO;
		} catch (ResourceAccessException e) {
			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
		} catch (HttpServerErrorException | HttpClientErrorException e) {
			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
		}

	}

	@Override
	public LeaveRequestDTO update(LeaveRequestDTO leaveRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LeaveRequestDTO delete(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LeaveRequestDTO get(String id) {
		// TODO Auto-generated method stub
		return null;
	}

}
