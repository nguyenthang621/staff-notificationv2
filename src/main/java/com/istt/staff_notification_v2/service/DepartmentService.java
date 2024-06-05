package com.istt.staff_notification_v2.service;

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

import com.istt.staff_notification_v2.dto.DepartmentDTO;
import com.istt.staff_notification_v2.entity.Department;
import com.istt.staff_notification_v2.repository.DepartmentRepo;

public interface DepartmentService {
	DepartmentDTO create(DepartmentDTO roleDTO);

	Boolean delete(String id);

}

@Service
class DepartmentServiceImpl implements DepartmentService {
	@Autowired
	private DepartmentRepo departmentRepo;

	@Transactional
	@Override
	public DepartmentDTO create(DepartmentDTO departmentDTO) {
		try {
			ModelMapper mapper = new ModelMapper();
			Department department = mapper.map(departmentDTO, Department.class);
			department.setDepartmentId(UUID.randomUUID().toString().replaceAll("-", ""));
			departmentRepo.save(department);
			return departmentDTO;
		} catch (ResourceAccessException e) {
			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
		} catch (HttpServerErrorException | HttpClientErrorException e) {
			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
		}
	}

	@Transactional
	@Override
	public Boolean delete(String id) {
		try {
			Department department = departmentRepo.findById(id).orElseThrow(NoResultException::new);
			if (department != null) {
				departmentRepo.deleteById(id);
				return true;
			}
			return false;
		} catch (ResourceAccessException e) {
			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
		} catch (HttpServerErrorException | HttpClientErrorException e) {
			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
		}
	}

}
