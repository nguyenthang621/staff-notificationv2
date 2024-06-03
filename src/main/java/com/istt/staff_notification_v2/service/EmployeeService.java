package com.istt.staff_notification_v2.service;

import java.util.List;
import java.util.UUID;

import javax.persistence.NoResultException;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import com.istt.staff_notification_v2.dto.EmployeeDTO;
import com.istt.staff_notification_v2.entity.Employee;
import com.istt.staff_notification_v2.repository.EmployeeRepo;

public interface EmployeeService {
	EmployeeDTO create(EmployeeDTO employeeDTO);

	EmployeeDTO findByName(String name);

	EmployeeDTO findByEmail(String email);

	EmployeeDTO getEmployeeByEmployeename(String employeename);

	EmployeeDTO update(EmployeeDTO employeeDTO);

	Boolean delete(String id);

	Boolean deleteAll(List<Integer> ids);

	EmployeeDTO get(String id);
}

@Service
class EmployeeServiceImpl implements EmployeeService {
	@Autowired
	private EmployeeRepo employeeRepo;

	private static final String ENTITY_NAME = "isttEmployee";

	@Transactional
	@Override
	public EmployeeDTO create(EmployeeDTO employeeDTO) {
		try {
			ModelMapper mapper = new ModelMapper();
			Employee employee = mapper.map(employeeDTO, Employee.class);
			employee.setEmployeeId(UUID.randomUUID().toString().replaceAll("-", ""));
			employeeRepo.save(employee);
			return employeeDTO;
		} catch (ResourceAccessException e) {
			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
		} catch (HttpServerErrorException | HttpClientErrorException e) {
			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
		}
	}

	@Override
	public EmployeeDTO findByEmail(String email) {
		Employee employeePage = employeeRepo.findByEmail(email);
		EmployeeDTO responseDTO = new ModelMapper().map(employeePage, EmployeeDTO.class);
		return responseDTO;
	}

	@Override
	public EmployeeDTO findByName(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Transactional
	@Override
	public EmployeeDTO getEmployeeByEmployeename(String employeename) {
		// TODO Auto-generated method stub
		return null;
	}

	@Transactional
	@Override
	public EmployeeDTO update(EmployeeDTO employeeDTO) {
		// TODO Auto-generated method stub
		return null;
	}

	@Transactional
	@Override
	public Boolean delete(String id) {
		Employee employee = employeeRepo.findById(id).orElseThrow(NoResultException::new);
		if (employee != null) {
			employeeRepo.delete(employee);
			return true;
		}
		return false;
	}

	@Override
	public Boolean deleteAll(List<Integer> ids) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EmployeeDTO get(String id) {
		try {
			Employee employee = employeeRepo.findByEmployeeId(id).orElseThrow(NoResultException::new);
			EmployeeDTO employeeDTO = new ModelMapper().map(employee, EmployeeDTO.class);
			return employeeDTO;

		} catch (ResourceAccessException e) {
			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
		} catch (HttpServerErrorException | HttpClientErrorException e) {
			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
		}
	}

}