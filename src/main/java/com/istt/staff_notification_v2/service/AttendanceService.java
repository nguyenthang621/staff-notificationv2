package com.istt.staff_notification_v2.service;

import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import com.istt.staff_notification_v2.configuration.ApplicationProperties;
import com.istt.staff_notification_v2.dto.AttendanceDTO;
import com.istt.staff_notification_v2.entity.Attendance;
import com.istt.staff_notification_v2.entity.Employee;
import com.istt.staff_notification_v2.repository.AttendanceRepo;

public interface AttendanceService {
	AttendanceDTO create(AttendanceDTO reasonDTO);

	AttendanceDTO update(AttendanceDTO reason);

	AttendanceDTO delete(Integer id);

	AttendanceDTO deleteAll(List<Integer> ids);

	AttendanceDTO get(String id);

}

class AttendanceServiceImpl implements AttendanceService {
	@Autowired
	AttendanceRepo attendanceRepo;

	@Autowired
	ApplicationProperties props;

	@Override
	public AttendanceDTO create(AttendanceDTO attendanceDTO) {
		try {
			ModelMapper mapper = new ModelMapper();
			Attendance attendance = mapper.map(attendanceDTO, Attendance.class);
			attendance.setAttendanceId(UUID.randomUUID().toString().replaceAll("-", ""));

			// map employee
			Employee employee = new Employee();
			employee.setStatus(props.getSTATUS_EMPLOYEE().get(0));
			employee.setEmployeeId(UUID.randomUUID().toString().replaceAll("-", ""));

			attendanceDTO.setEmployee(employee);

			attendanceRepo.save(attendance);
			return attendanceDTO;
		} catch (ResourceAccessException e) {
			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
		} catch (HttpServerErrorException | HttpClientErrorException e) {
			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
		}

	}

	@Override
	public AttendanceDTO update(AttendanceDTO reason) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AttendanceDTO delete(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AttendanceDTO deleteAll(List<Integer> ids) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AttendanceDTO get(String id) {
		// TODO Auto-generated method stub
		return null;
	}

}
