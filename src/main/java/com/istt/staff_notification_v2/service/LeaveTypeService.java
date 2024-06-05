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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import com.istt.staff_notification_v2.apis.errors.BadRequestAlertException;
import com.istt.staff_notification_v2.dto.LeaveTypeDTO;
import com.istt.staff_notification_v2.dto.ResponseDTO;
import com.istt.staff_notification_v2.dto.SearchDTO;
import com.istt.staff_notification_v2.entity.LeaveType;
import com.istt.staff_notification_v2.repository.LeaveTypeRepo;

public interface LeaveTypeService {
	LeaveTypeDTO create(LeaveTypeDTO leaveTypeDTO);

	LeaveTypeDTO delete(String id);

	ResponseDTO<List<LeaveTypeDTO>> search(SearchDTO searchDTO);

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

	@Override
	public ResponseDTO<List<LeaveTypeDTO>> search(SearchDTO searchDTO) {
		try {
			List<Sort.Order> orders = Optional.ofNullable(searchDTO.getOrders()).orElseGet(Collections::emptyList)
					.stream().map(order -> {
						if (order.getOrder().equals(SearchDTO.ASC))
							return Sort.Order.asc(order.getProperty());

						return Sort.Order.desc(order.getProperty());
					}).collect(Collectors.toList());
			Pageable pageable = PageRequest.of(searchDTO.getPage(), searchDTO.getSize(), Sort.by(orders));

			Page<LeaveType> page = leaveTypeRepo.searchByLeavetypeName(searchDTO.getValue(), pageable);

			List<LeaveTypeDTO> leaveTypeDTOs = new ArrayList<>();
			for (LeaveType leaveType : page.getContent()) {
				LeaveTypeDTO leaveTypeDTO = new ModelMapper().map(leaveType, LeaveTypeDTO.class);
				leaveTypeDTOs.add(leaveTypeDTO);
			}

			ResponseDTO<List<LeaveTypeDTO>> responseDTO = new ModelMapper().map(page, ResponseDTO.class);
			responseDTO.setData(leaveTypeDTOs);
			return responseDTO;
		} catch (ResourceAccessException e) {
			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
		} catch (HttpServerErrorException | HttpClientErrorException e) {
			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
		}
	}

}