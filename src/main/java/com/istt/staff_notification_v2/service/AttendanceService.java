package com.istt.staff_notification_v2.service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
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
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import com.istt.staff_notification_v2.apis.errors.BadRequestAlertException;
import com.istt.staff_notification_v2.configuration.ApplicationProperties;
import com.istt.staff_notification_v2.configuration.ApplicationProperties.StatusEmployeeRef;
import com.istt.staff_notification_v2.dto.AttendanceDTO;
import com.istt.staff_notification_v2.dto.ResponseDTO;
import com.istt.staff_notification_v2.dto.SearchDTO;
import com.istt.staff_notification_v2.entity.Attendance;
import com.istt.staff_notification_v2.entity.Employee;
import com.istt.staff_notification_v2.repository.AttendanceRepo;
import com.istt.staff_notification_v2.repository.EmployeeRepo;

public interface AttendanceService {

	AttendanceDTO create(AttendanceDTO attendanceDTO);

	ResponseDTO<List<AttendanceDTO>> searchByEmployeeName(SearchDTO searchDTO);

	AttendanceDTO update(AttendanceDTO attendanceDTO);

	Boolean deleteById(String id);

	Boolean deletebylistId(List<String> id);

	AttendanceDTO get(String id);

	List<AttendanceDTO> getAll();

	List<AttendanceDTO> getStatus(String type);

	ResponseDTO<List<AttendanceDTO>> search(SearchDTO searchDTO);
}

@Service
class AttendanceServiceImpl implements AttendanceService {

	@Autowired
	private AttendanceRepo attendanceRepo;

	@Autowired
	private EmployeeRepo employeeRepo;

	@Autowired
	ApplicationProperties props;

	private static final String ENTITY_NAME = "isttAttendance";

	@Transactional
	@Override
	public AttendanceDTO create(AttendanceDTO attendanceDTO) {
		try {

			Attendance attendance = new ModelMapper().map(attendanceDTO, Attendance.class);
			attendance.setAttendanceId(UUID.randomUUID().toString().replaceAll("-", ""));

			Optional<Employee> employeeCreateOptional = employeeRepo.findByEmployeeId(attendanceDTO.getApprovedBy());
			if (employeeCreateOptional.isEmpty())
				throw new BadRequestAlertException("Employee create attendance not found.", ENTITY_NAME, "Not found");

			Optional<Employee> employeeOptional = employeeRepo
					.findByEmployeeId(attendanceDTO.getEmployee().getEmployeeId());
			if (employeeOptional.isEmpty())
				throw new BadRequestAlertException("Employee not found.", ENTITY_NAME, "Not found");

			Employee employee = employeeOptional.get();
			if (employee.getStatus().equals(StatusEmployeeRef.SUSPEND.toString()))
				throw new BadRequestAlertException("This employee has been suspended", ENTITY_NAME, "Suspend");

			attendance.setEmployee(employee);
			attendance.setCreateAt(new Date());

			if (!props.getTYPE_ATTENDANCE().contains(attendanceDTO.getType()))
				throw new BadRequestAlertException("Invalid TYPE ATTENDANCE", ENTITY_NAME, "Invalid");

			attendanceRepo.save(attendance);

			return attendanceDTO;
		} catch (ResourceAccessException e) {
			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
		} catch (HttpServerErrorException | HttpClientErrorException e) {
			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
		}
	}

	@Transactional
	@Override
	public AttendanceDTO update(AttendanceDTO attendanceDTO) {
		try {
			Attendance attendance = new ModelMapper().map(attendanceDTO, Attendance.class);

			Optional<Employee> employeeUpdateOptional = employeeRepo.findByEmployeeId(attendanceDTO.getUpdateBy());
			if (employeeUpdateOptional.isEmpty())
				throw new BadRequestAlertException("Employee update not found.", ENTITY_NAME, "Not found");

			Optional<Employee> employeeOptional = employeeRepo
					.findByEmployeeId(attendanceDTO.getEmployee().getEmployeeId());
			if (employeeOptional.isEmpty())
				throw new BadRequestAlertException("Employee not found.", ENTITY_NAME, "Not found");

			Employee employee = employeeOptional.get();
			if (employee.getStatus().equals(StatusEmployeeRef.SUSPEND.toString()))
				throw new BadRequestAlertException("This employee has been suspended", ENTITY_NAME, "Suspend");

			attendance.setEmployee(employee);
			attendance.setUpdateAt(new Date());
			attendance.setUpdateBy(employeeUpdateOptional.get().getEmployeeId());

			if (!props.getTYPE_ATTENDANCE().contains(attendanceDTO.getType()))
				throw new BadRequestAlertException("Invalid STATUS ATTENDANCE", ENTITY_NAME, "Invalid");

			attendanceRepo.save(attendance);

			return attendanceDTO;
		} catch (ResourceAccessException e) {
			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
		} catch (HttpServerErrorException | HttpClientErrorException e) {
			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
		}
	}

	@Transactional
	@Override
	public Boolean deleteById(String id) {
		Attendance attendance = attendanceRepo.findById(id).orElseThrow(NoResultException::new);
		if (attendance != null) {
			attendanceRepo.deleteById(id);
		}
		return null;
	}

	@Transactional
	@Override
	public Boolean deletebylistId(List<String> ids) {
		List<Attendance> attendances = attendanceRepo.findAllById(ids);
		if (attendances.size() > 0) {
			attendanceRepo.deleteAllById(ids);
			return true;
		}
		return false;
	}

	@Override
	public AttendanceDTO get(String id) {
		ModelMapper mapper = new ModelMapper();
		Attendance attendance = attendanceRepo.findById(id).get();
		if (attendance == null)
			throw new NoResultException();
		return mapper.map(attendance, AttendanceDTO.class);
	}

	@Override
	public List<AttendanceDTO> getAll() {
		List<Attendance> attendances = attendanceRepo.getAll();
		ModelMapper mapper = new ModelMapper();
		return attendances.stream().map(attendance -> mapper.map(attendance, AttendanceDTO.class))
				.collect(Collectors.toList());
	}

	@Override
	public List<AttendanceDTO> getStatus(String type) {
		if (!props.getTYPE_ATTENDANCE().contains(type)) {
			throw new BadRequestAlertException("not have status", ENTITY_NAME, "exists");
		}

		List<Attendance> attendances = attendanceRepo.getType(type);

		ModelMapper mapper = new ModelMapper();

		if (attendances.size() < 1)
			return null;
		return attendances.stream().map(attendance -> mapper.map(attendance, AttendanceDTO.class))
				.collect(Collectors.toList());
	}

	@Override
	public ResponseDTO<List<AttendanceDTO>> search(SearchDTO searchDTO) {
		try {
			List<Sort.Order> orders = Optional.ofNullable(searchDTO.getOrders()).orElseGet(Collections::emptyList)
					.stream().map(order -> {
						if (order.getOrder().equals(SearchDTO.ASC))
							return Sort.Order.asc(order.getProperty());

						return Sort.Order.desc(order.getProperty());
					}).collect(Collectors.toList());
			Pageable pageable = PageRequest.of(searchDTO.getPage(), searchDTO.getSize(), Sort.by(orders));

			Page<Attendance> page = attendanceRepo.findByType(searchDTO.getValue(), pageable);
			ModelMapper mapper = new ModelMapper();
			List<AttendanceDTO> levelDTOs = page.getContent().stream()
					.map(attendance -> mapper.map(attendance, AttendanceDTO.class)).collect(Collectors.toList());

			ResponseDTO<List<AttendanceDTO>> responseDTO = mapper.map(page, ResponseDTO.class);
			responseDTO.setData(levelDTOs);
			return responseDTO;
		} catch (ResourceAccessException e) {
			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
		} catch (HttpServerErrorException | HttpClientErrorException e) {
			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
		}
	}

	@Override
	public ResponseDTO<List<AttendanceDTO>> searchByEmployeeName(SearchDTO searchDTO) {
		try {
			List<Sort.Order> orders = Optional.ofNullable(searchDTO.getOrders()).orElseGet(Collections::emptyList)
					.stream().map(order -> {
						if (order.getOrder().equals(SearchDTO.ASC))
							return Sort.Order.asc(order.getProperty());

						return Sort.Order.desc(order.getProperty());
					}).collect(Collectors.toList());
			Pageable pageable = PageRequest.of(searchDTO.getPage(), searchDTO.getSize(), Sort.by(orders));

			Page<Attendance> page = attendanceRepo.searchByEmployeeName(searchDTO.getValue(), pageable);
			ModelMapper mapper = new ModelMapper();
			List<AttendanceDTO> levelDTOs = page.getContent().stream()
					.map(attendance -> mapper.map(attendance, AttendanceDTO.class)).collect(Collectors.toList());

			ResponseDTO<List<AttendanceDTO>> responseDTO = mapper.map(page, ResponseDTO.class);
			responseDTO.setData(levelDTOs);
			return responseDTO;
		} catch (ResourceAccessException e) {
			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
		} catch (HttpServerErrorException | HttpClientErrorException e) {
			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
		}
	}

}
