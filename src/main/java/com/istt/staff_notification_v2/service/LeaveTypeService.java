package com.istt.staff_notification_v2.service;

import java.util.UUID;

import javax.persistence.NoResultException;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import com.istt.staff_notification_v2.apis.errors.BadRequestAlertException;
import com.istt.staff_notification_v2.dto.LeaveTypeDTO;
import com.istt.staff_notification_v2.entity.LeaveType;
import com.istt.staff_notification_v2.repository.LeaveTypeRepo;

public interface LeaveTypeService {
	LeaveTypeDTO create(LeaveTypeDTO leaveTypeDTO);

	LeaveTypeDTO delete(String id);

}

@Service
class LeaveTypeServiceImpl implements LeaveTypeService {

	@Autowired
	private LeaveTypeRepo leaveTypeRepo;

	private static final String ENTITY_NAME = "isttLeaveType";

	@Override
	public LeaveTypeDTO create(LeaveTypeDTO leaveTypeDTO) {
		try {
			if (leaveTypeRepo.findByLeavetypeName(leaveTypeDTO.getLeavetypeName()).isPresent()) {
				throw new BadRequestAlertException("LeaveType Name already exists", ENTITY_NAME, "exist");
			}
			ModelMapper mapper = new ModelMapper();
			LeaveType leaveType = mapper.map(leaveTypeDTO, LeaveType.class);
			System.out.println("leaveType: " + leaveType);
			leaveType.setLeavetypeId(UUID.randomUUID().toString().replaceAll("-", ""));

			leaveTypeRepo.save(leaveType);
			return leaveTypeDTO;
		} catch (ResourceAccessException e) {
			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
		} catch (HttpServerErrorException | HttpClientErrorException e) {
			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
		}
	}

	@Override
	public LeaveTypeDTO delete(String id) {
		try {
			LeaveType leaveType = leaveTypeRepo.findByLeavetypeId(id).orElseThrow(NoResultException::new);
			if (leaveType != null) {
				leaveTypeRepo.deleteById(id);
				return new ModelMapper().map(leaveType, LeaveTypeDTO.class);
			}
			return null;
		} catch (ResourceAccessException e) {
			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
		} catch (HttpServerErrorException | HttpClientErrorException e) {
			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
		}
	}

}