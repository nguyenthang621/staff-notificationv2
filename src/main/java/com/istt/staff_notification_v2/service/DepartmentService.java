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

import com.istt.staff_notification_v2.apis.errors.BadRequestAlertException;
import com.istt.staff_notification_v2.dto.DepartmentDTO;
import com.istt.staff_notification_v2.dto.ResponseDTO;
import com.istt.staff_notification_v2.dto.SearchDTO;
import com.istt.staff_notification_v2.entity.Department;
import com.istt.staff_notification_v2.repository.DepartmentRepo;

public interface DepartmentService {
	DepartmentDTO create(DepartmentDTO roleDTO);

	Boolean delete(String id);

	ResponseDTO<List<DepartmentDTO>> search(SearchDTO searchDTO);

	DepartmentDTO update(DepartmentDTO departmentDTO);

	DepartmentDTO findById(String id);

	List<DepartmentDTO> getAll();

	DepartmentDTO findByName(String name);

	List<DepartmentDTO> getAllDepartment();

	List<Department> deleteAllbyIds(List<String> ids);

}

@Service
class DepartmentServiceImpl implements DepartmentService {
	@Autowired
	private DepartmentRepo departmentRepo;

	private static final String ENTITY_NAME = "isttDepartment";

	@Transactional
	@Override
	public DepartmentDTO create(DepartmentDTO departmentDTO) {
		try {

			if (departmentRepo.findByDepartmentName(departmentDTO.getDepartmentName()).isPresent())
				throw new BadRequestAlertException("Bad request: Department already exists", ENTITY_NAME, "Exists");

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

	@Override
	@Transactional
	public DepartmentDTO update(DepartmentDTO departmentDTO) {
		try {
			Optional<Department> departOptionalOptional = departmentRepo
					.findByDepartmentId(departmentDTO.getDepartmentId());
			if (departmentRepo.findByDepartmentId(departmentDTO.getDepartmentId()).isEmpty())
				throw new BadRequestAlertException("Department not found", ENTITY_NAME, "Not found");

			if (departmentRepo.findByDepartmentName(departmentDTO.getDepartmentName()).isPresent())
				throw new BadRequestAlertException("Department already exists", ENTITY_NAME, "exist");

			Department department = departOptionalOptional.get();
			department.setDepartmentName(departmentDTO.getDepartmentName());
			departmentRepo.save(department);
			return departmentDTO;
		} catch (ResourceAccessException e) {
			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
		} catch (HttpServerErrorException | HttpClientErrorException e) {
			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
		}
	}

	@Override
	public DepartmentDTO findById(String id) {
		try {
			Department department = departmentRepo.findById(id).orElseThrow(NoResultException::new);
			return new ModelMapper().map(department, DepartmentDTO.class);
		} catch (ResourceAccessException e) {
			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
		} catch (HttpServerErrorException | HttpClientErrorException e) {
			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
		}

	}

	@Override
	public DepartmentDTO findByName(String name) {
		try {
			Department department = departmentRepo.findByDepartmentName(name).get();
			return new ModelMapper().map(department, DepartmentDTO.class);
		} catch (ResourceAccessException e) {
			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
		} catch (HttpServerErrorException | HttpClientErrorException e) {
			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
		}

	}

	@Override
	public List<DepartmentDTO> getAllDepartment() {
		try {
			List<Department> departments = departmentRepo.findAll();
			return departments.stream().map(department -> new ModelMapper().map(department, DepartmentDTO.class))
					.collect(Collectors.toList());
		} catch (ResourceAccessException e) {
			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
		} catch (HttpServerErrorException | HttpClientErrorException e) {
			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
		}
	}

	@Override
	public List<Department> deleteAllbyIds(List<String> ids) {
		try {
			List<Department> list = departmentRepo.findByDepartmentIds(ids).orElseThrow(NoResultException::new);
			System.out.println("------------" + list.size());
			if (!list.isEmpty()) {
				departmentRepo.deleteAllInBatch(list);
				return list;
			}
			throw new BadRequestAlertException("Department empty", ENTITY_NAME, "invalid");
		} catch (ResourceAccessException e) {
			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
		} catch (HttpServerErrorException | HttpClientErrorException e) {
			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
		}
	}

	@Override
	public List<DepartmentDTO> getAll() {
		try {
			List<Department> departments = departmentRepo.findAll();
			return departments.stream().map(department -> new ModelMapper().map(department, DepartmentDTO.class))
					.collect(Collectors.toList());
		} catch (ResourceAccessException e) {
			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
		} catch (HttpServerErrorException | HttpClientErrorException e) {
			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
		}
	}
}
