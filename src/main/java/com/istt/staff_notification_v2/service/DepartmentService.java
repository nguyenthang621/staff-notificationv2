package com.istt.staff_notification_v2.service;

import java.util.ArrayList;
import java.util.Collections;
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

import com.istt.staff_notification_v2.dto.DepartmentDTO;
import com.istt.staff_notification_v2.dto.ResponseDTO;
import com.istt.staff_notification_v2.dto.SearchDTO;
import com.istt.staff_notification_v2.entity.Department;
import com.istt.staff_notification_v2.repository.DepartmentRepo;

public interface DepartmentService {
	DepartmentDTO create(DepartmentDTO roleDTO);

	Boolean delete(String id);

	ResponseDTO<List<DepartmentDTO>> search(SearchDTO searchDTO);

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
			department.setDepartmentName(departmentDTO.getDepartmentName().toUpperCase());
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

	@Override
	public ResponseDTO<List<DepartmentDTO>> search(SearchDTO searchDTO) {
		try {
			List<Sort.Order> orders = Optional.ofNullable(searchDTO.getOrders()).orElseGet(Collections::emptyList)
					.stream().map(order -> {
						if (order.getOrder().equals(SearchDTO.ASC))
							return Sort.Order.asc(order.getProperty());

						return Sort.Order.desc(order.getProperty());
					}).collect(Collectors.toList());
			Pageable pageable = PageRequest.of(searchDTO.getPage(), searchDTO.getSize(), Sort.by(orders));

			Page<Department> page = departmentRepo.searchByDepartmentName(searchDTO.getValue(), pageable);

			List<DepartmentDTO> departmentDTOs = new ArrayList<>();
			for (Department department : page.getContent()) {
				DepartmentDTO departmentDTO = new ModelMapper().map(department, DepartmentDTO.class);
				departmentDTOs.add(departmentDTO);
			}

			ResponseDTO<List<DepartmentDTO>> responseDTO = new ModelMapper().map(page, ResponseDTO.class);
			responseDTO.setData(departmentDTOs);
			return responseDTO;
		} catch (ResourceAccessException e) {
			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
		} catch (HttpServerErrorException | HttpClientErrorException e) {
			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
		}
	}

}
