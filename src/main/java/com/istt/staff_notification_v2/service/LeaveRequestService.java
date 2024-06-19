package com.istt.staff_notification_v2.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.NoResultException;
import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import com.istt.staff_notification_v2.apis.errors.BadRequestAlertException;
import com.istt.staff_notification_v2.configuration.ApplicationProperties;
import com.istt.staff_notification_v2.configuration.ApplicationProperties.StatusLeaveRequestRef;
import com.istt.staff_notification_v2.dto.AttendanceDTO;
import com.istt.staff_notification_v2.dto.EmployeeDTO;
import com.istt.staff_notification_v2.dto.LeaveRequestDTO;
import com.istt.staff_notification_v2.dto.MailRequestDTO;
import com.istt.staff_notification_v2.dto.ResponseLeaveRequest;
import com.istt.staff_notification_v2.dto.SearchLeaveRequest;
import com.istt.staff_notification_v2.entity.Employee;
import com.istt.staff_notification_v2.entity.LeaveRequest;
import com.istt.staff_notification_v2.repository.EmployeeRepo;
import com.istt.staff_notification_v2.repository.LeaveRequestRepo;
import com.istt.staff_notification_v2.repository.LeaveTypeRepo;
import com.istt.staff_notification_v2.repository.UserRepo;
import com.istt.staff_notification_v2.utils.utils;

public interface LeaveRequestService {

//	LeaveRequestDTO create(LeaveRequestDTO leaveRequestDTO);
	MailRequestDTO create(@RequestBody MailRequestDTO mailRequestDTO);

	ResponseLeaveRequest changeStatusLeaveRequest(@RequestBody ResponseLeaveRequest responseLeaveRequeest);

	LeaveRequestDTO update(LeaveRequestDTO leaveRequest);

	LeaveRequestDTO delete(String id);

	LeaveRequestDTO get(String id);

	List<LeaveRequestDTO> searchLeaveRequest(SearchLeaveRequest searchLeaveRequest);

}

@Service
class LeaveRequestServiceImpl implements LeaveRequestService {

	@Autowired
	LeaveRequestRepo leaveRequestRepo;

	@Autowired
	EmployeeRepo employeeRepo;

	@Autowired
	AttendanceService attendanceService;

	@Autowired
	ApplicationProperties props;

	@Autowired
	private UserRepo userRepo;

	@Autowired
	MailService mailService;

	@Autowired
	private LeaveTypeRepo leaveTypeRepo;

	private static final String ENTITY_NAME = "isttLeaveRequestType";

	private Boolean sendNotification(@RequestBody MailRequestDTO mailRequestDTO) {

		try {
			System.err.println(mailRequestDTO.getRecceiverList().size());
			for (int i = 0; i < mailRequestDTO.getRecceiverList().size(); i++) {
				EmployeeDTO receiver = new EmployeeDTO();
				receiver = mailRequestDTO.getRecceiverList().get(i);
				System.out.println("receiver maiL : " + receiver.getEmail());
				mailService.sendEmail(mailRequestDTO.getLeaveRequestDTO(), receiver, mailRequestDTO.getSubject());
			}
			return true;

		} catch (Exception e) {
			return false;

		}

	}

	@Override
	public MailRequestDTO create(@RequestBody MailRequestDTO mailRequestDTO) {
		try {

			LeaveRequestDTO leaveRequestDTO = mailRequestDTO.getLeaveRequestDTO();
			ModelMapper mapper = new ModelMapper();
			LeaveRequest leaveRequest = mapper.map(leaveRequestDTO, LeaveRequest.class);
			// Validate duration:
			if (leaveRequestDTO.getDuration() <= 0.0 || leaveRequestDTO.getDuration() % 0.5 != 0.0)
				throw new BadRequestAlertException("Bad request: Invalid duration", ENTITY_NAME, "Invalid");

			leaveRequest.setLeaveqequestId(UUID.randomUUID().toString().replaceAll("-", ""));

			// Validate employeeId in leaveRequest
			if (leaveRequestDTO.getEmployee().getEmployeeId() == null)
				throw new BadRequestAlertException("Bad request: Employee not found!", ENTITY_NAME, "Not Found!");

			// map employee if true
			Employee employee = employeeRepo.findByEmployeeId(leaveRequestDTO.getEmployee().getEmployeeId())
					.orElseThrow(NoResultException::new);
			leaveRequest.setEmployee(employee);

			if (leaveRequestDTO.getLeavetype().getLeavetypeId() == null)
				throw new BadRequestAlertException("Bad request: Missing LeaveTypeId", ENTITY_NAME, "Missing");

			// default set status is NOT_APPROVED if normal
//			LeaveType leaveType = leaveTypeRepo.findByLeavetypeId(leaveRequestDTO.getLeavetype().getLeavetypeId())
//					.orElseThrow(NoResultException::new);
//			if (leaveType.isSpecialType()) {
//				leaveRequest.setStatus(props.getSTATUS_LEAVER_REQUEST().get(StatusLeaveRequestRef.APPROVED.ordinal()));
//			} else {
//				leaveRequest.setStatus(props.getSTATUS_LEAVER_REQUEST().get(StatusLeaveRequestRef.WAITING.ordinal()));
//			}

			leaveRequest.setStatus(props.getSTATUS_LEAVER_REQUEST().get(StatusLeaveRequestRef.WAITING.ordinal()));

			// Valid only send to a employee
			if (mailRequestDTO.getRecceiverList().size() != 1)
				throw new BadRequestAlertException("Bad request: Can only be sent to 1 person", ENTITY_NAME, "Invalid");
			leaveRequest.setReceiver(mailRequestDTO.getRecceiverList().get(0).getEmployeeId());

			// Handle send mail:
//			Optional<List<Employee>> employeeDependences = employeeRepo
//					.findByEmployeeIds(employee.getEmployeeDependence());

			// Get in receiver request
			Optional<List<Employee>> employeeDependences = employeeRepo.findByEmployeeIds(mailRequestDTO
					.getRecceiverList().stream().map(item -> item.getEmployeeId()).collect(Collectors.toList()));

			if (employeeDependences.isEmpty())
				throw new BadRequestAlertException("Bad request: Not found employee dependence in employee` department",
						ENTITY_NAME, "Not found");

			MailRequestDTO mailRequestEachEmployee = new MailRequestDTO();
			mailRequestEachEmployee.setLeaveRequestDTO(leaveRequestDTO);
			mailRequestEachEmployee.setRecceiverList(employeeDependences.get().stream()
					.map(item -> new ModelMapper().map(item, EmployeeDTO.class)).collect(Collectors.toList()));
			mailRequestEachEmployee.setSubject(mailRequestDTO.getSubject());

			// Excute send mail
			if (!sendNotification(mailRequestEachEmployee))
				throw Problem.builder().withStatus(Status.INTERNAL_SERVER_ERROR).withDetail("ERROR SEND MAIL").build();

			// Commit leaveRequest
			leaveRequestRepo.save(leaveRequest);
			return mailRequestDTO;
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

	@Override
	@Transactional
	public ResponseLeaveRequest changeStatusLeaveRequest(ResponseLeaveRequest responseLeaveRequest) {
		try {
			if (!props.getSTATUS_LEAVER_REQUEST().contains(responseLeaveRequest.getStatus())) {
				throw new BadRequestAlertException("Bad request: Invalid STATUS_LEAVER_REQUEST", ENTITY_NAME,
						"Invalid");
			}

			Employee employee = employeeRepo.findByEmployeeId(responseLeaveRequest.getByEmployeeId())
					.orElseThrow(NoResultException::new);

			LeaveRequest leaveRequest = leaveRequestRepo.findByLeaveqequestId(responseLeaveRequest.getLeaveqequestId())
					.orElseThrow(NoResultException::new);

			if (!props.getSTATUS_LEAVER_REQUEST().contains(leaveRequest.getStatus()))
				throw new BadRequestAlertException("Bad request: Invalid type", ENTITY_NAME, "Invalid");

			if (leaveRequest.getStatus().equals(StatusLeaveRequestRef.APPROVED.toString()))
				throw new BadRequestAlertException("Bad request: This request has been approved by others", ENTITY_NAME,
						"APPROVED");

			if (employee.getEmployeeId().equals(leaveRequest.getEmployee().getEmployeeId()))
				throw new BadRequestAlertException("Bad request: Must be the superior approval", ENTITY_NAME,
						"Not role");

			if (responseLeaveRequest.getStatus().equals(StatusLeaveRequestRef.NOT_APPROVED.toString())
					|| responseLeaveRequest.getStatus().equals(StatusLeaveRequestRef.REJECT.toString())) {
				if (responseLeaveRequest.getAnrreason().isEmpty())
					throw new BadRequestAlertException("Bad request: If refused, there must be a reason", ENTITY_NAME,
							"Missing");
			}

			leaveRequest.setAnrreason(responseLeaveRequest.getAnrreason());
			leaveRequest.setStatus(responseLeaveRequest.getStatus());
			leaveRequest.setResponseBy(responseLeaveRequest.getByEmployeeId());

			leaveRequestRepo.save(leaveRequest);

			if (responseLeaveRequest.getStatus().equals(StatusLeaveRequestRef.APPROVED.toString())) { // Case approved
				AttendanceDTO attendanceDTO = new AttendanceDTO();
				attendanceDTO.setApprovedBy(responseLeaveRequest.getByEmployeeId());
				attendanceDTO.setLeaveRequest(leaveRequest);
				attendanceDTO.setEmployee(leaveRequest.getEmployee());
				attendanceDTO.setApprovedBy(responseLeaveRequest.getByEmployeeId());

				attendanceDTO.setLeaveType(leaveRequest.getLeavetype());
				attendanceDTO.setNote(responseLeaveRequest.getAnrreason());
				attendanceDTO.setCreateAt(new Date());
				// Handle reset time start date
				attendanceDTO.setStartDate(new utils().resetStartDate(leaveRequest.getStartDate()));

				// Handle calculator endDate
				attendanceDTO.setEndDate(
						new utils().calculatorEndDate(leaveRequest.getStartDate(), leaveRequest.getDuration()));

				attendanceDTO.setDuration(leaveRequest.getDuration());

				attendanceService.create(attendanceDTO);

				// Handle send mail
				ModelMapper mapper = new ModelMapper();
				String subject = "Phản hồi đơn xin nghỉ phép";
				mailService.sendReponseApprovedEmail(mapper.map(leaveRequest, LeaveRequestDTO.class),
						mapper.map(attendanceDTO.getEmployee(), EmployeeDTO.class), subject);
			} else if (responseLeaveRequest.getStatus().equals(StatusLeaveRequestRef.NOT_APPROVED.toString())
					|| responseLeaveRequest.getStatus().equals(StatusLeaveRequestRef.REJECT.toString())) { // Case
																											// reject or
																											// not_approved

				// Handle send mail
				ModelMapper mapper = new ModelMapper();
				String subject = "Phản hồi đơn xin nghỉ phép";

				String reason = responseLeaveRequest.getAnrreason();
				mailService.sendReponseRejectEmail(mapper.map(leaveRequest, LeaveRequestDTO.class),
						mapper.map(leaveRequest.getEmployee(), EmployeeDTO.class), subject, reason,
						responseLeaveRequest.getStatus());
			}
			// handle date

			return responseLeaveRequest;

		} catch (ResourceAccessException e) {
			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
		} catch (HttpServerErrorException | HttpClientErrorException e) {
			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
		}
	}

	@Override
	public List<LeaveRequestDTO> searchLeaveRequest(SearchLeaveRequest searchLeaveRequest) {
		try {
			if (searchLeaveRequest.getEmail() != null && searchLeaveRequest.getStatus() == null
					&& searchLeaveRequest.getStartDate() == null) {
				Optional<List<LeaveRequest>> resultOp = leaveRequestRepo.findEmail(searchLeaveRequest.getEmail());
				if (resultOp.isEmpty())
					return new ArrayList<>();
				return resultOp.get().stream().map(l -> new ModelMapper().map(l, LeaveRequestDTO.class))
						.collect(Collectors.toList());
			} else if (searchLeaveRequest.getEmail() != null && searchLeaveRequest.getStatus() != null
					&& searchLeaveRequest.getStartDate() == null) {
				Optional<List<LeaveRequest>> resultOp = leaveRequestRepo
						.findEmailAndStatus(searchLeaveRequest.getEmail(), searchLeaveRequest.getStatus());
				if (resultOp.isEmpty())
					return new ArrayList<>();
				return resultOp.get().stream().map(l -> new ModelMapper().map(l, LeaveRequestDTO.class))
						.collect(Collectors.toList());
			} else if (searchLeaveRequest.getEmail() != null && searchLeaveRequest.getStatus() != null
					&& searchLeaveRequest.getStartDate() != null) {
				Optional<List<LeaveRequest>> resultOp = leaveRequestRepo
						.findEmailAndStatus(searchLeaveRequest.getEmail(), searchLeaveRequest.getStatus());
				if (resultOp.isEmpty())
					return new ArrayList<>();
				return resultOp.get().stream().map(l -> new ModelMapper().map(l, LeaveRequestDTO.class))
						.collect(Collectors.toList());
			} else if (searchLeaveRequest.getEmail() == null && searchLeaveRequest.getStatus() != null
					&& searchLeaveRequest.getStartDate() == nul) {
				Optional<List<LeaveRequest>> resultOp = leaveRequestRepo.findstatus(searchLeaveRequest.getStatus());
				if (resultOp.isEmpty())
					return new ArrayList<>();
				return resultOp.get().stream().map(l -> new ModelMapper().map(l, LeaveRequestDTO.class))
						.collect(Collectors.toList());
			}
			return new ArrayList<>();

		} catch (ResourceAccessException e) {
			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
		} catch (HttpServerErrorException | HttpClientErrorException e) {
			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
		}
	}

}
