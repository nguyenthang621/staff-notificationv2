package com.istt.staff_notification_v2.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.NoResultException;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.istt.staff_notification_v2.dto.EmployeeDTO;
import com.istt.staff_notification_v2.dto.LeaveRequestDTO;
import com.istt.staff_notification_v2.dto.MailRequestDTO;
import com.istt.staff_notification_v2.entity.Employee;
import com.istt.staff_notification_v2.entity.LeaveRequest;
import com.istt.staff_notification_v2.entity.LeaveType;
import com.istt.staff_notification_v2.repository.EmployeeRepo;
import com.istt.staff_notification_v2.repository.LeaveRequestRepo;
import com.istt.staff_notification_v2.repository.LeaveTypeRepo;
import com.istt.staff_notification_v2.repository.UserRepo;

public interface LeaveRequestService {

//	LeaveRequestDTO create(LeaveRequestDTO leaveRequestDTO);
	LeaveRequestDTO create(@RequestBody MailRequestDTO mailRequestDTO);

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

	@Autowired
	private MailService mailService;

	@Autowired
	private LeaveTypeRepo leaveTypeRepo;

	private static final String ENTITY_NAME = "isttLeaveRequestType";

	private ResponseEntity<String> sendNotification(@RequestBody MailRequestDTO mailRequestDTO) {

		try {
			System.err.println(mailRequestDTO.getRecceiverList().size());
			for (int i = 0; i < mailRequestDTO.getRecceiverList().size(); i++) {
				EmployeeDTO receiver = new EmployeeDTO();
				receiver = mailRequestDTO.getRecceiverList().get(i);
				mailService.sendEmail(mailRequestDTO.getLeaveRequestDTO(), receiver, mailRequestDTO.getSubject());
			}
			return ResponseEntity.status(HttpStatus.OK).body("Email sent successfully");

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error sending email");

		}

	}

	@Override
	public LeaveRequestDTO create(@RequestBody MailRequestDTO mailRequestDTO) {
		try {
			LeaveRequestDTO leaveRequestDTO = mailRequestDTO.getLeaveRequestDTO();
			ModelMapper mapper = new ModelMapper();
			LeaveRequest leaveRequest = mapper.map(leaveRequestDTO, LeaveRequest.class);
			leaveRequest.setLeaveqequestId(UUID.randomUUID().toString().replaceAll("-", ""));

			if (leaveRequestDTO.getEmployee().getEmployeeId() != null) { // map employee
				Employee employee = employeeRepo.findByEmployeeId(leaveRequestDTO.getEmployee().getEmployeeId())
						.orElseThrow(NoResultException::new);
				leaveRequest.setEmployee(employee);

				if (leaveRequestDTO.getLeavetype().getLeavetypeId() == null)
					throw new BadRequestAlertException("Bad request: Missing LeaveRequestId", ENTITY_NAME, "Missing");

				LeaveType leaveType = leaveTypeRepo.findByLeavetypeId(leaveRequestDTO.getLeavetype().getLeavetypeId())
						.orElseThrow(NoResultException::new);

				if (leaveType.isSpecialType()) {
					leaveRequest
							.setStatus(props.getSTATUS_LEAVER_REQUEST().get(StatusLeaveRequestRef.APPROVED.ordinal()));
				} else {
					leaveRequest.setStatus(
							props.getSTATUS_LEAVER_REQUEST().get(StatusLeaveRequestRef.NOT_APPROVED.ordinal()));
				}

//				Handle send mail:
				Optional<List<Employee>> employeesRaw = employeeRepo
						.findByEmployeeIds(employee.getEmployeeDependence());
				System.out.println("level: " + employee.getLevels().stream().collect(Collectors.toList()).getClass());

				if (employeesRaw.isEmpty())
					throw new BadRequestAlertException("Bad request: Not found employee in employee` department",
							ENTITY_NAME, "Not found");
//				List employees = employeesRaw.get().stream().filter(e -> Integer.valueOf(e.getLevels().stream().sorted())).collect(Collectors.toList());
				List<Employee> employees = new ArrayList<>();

//				for (Employee e : employeesRaw.get()) {
//					for (Level l : e.getLevels()) {
//						System.out.println(e.getEmail() + "-l: " + l.getLevelCode() + " " + l.getLevelName());
//					}
//				}

			} else {
				throw new BadRequestAlertException("Bad request: Employee not found!", ENTITY_NAME, "Not Found!");
			}

//			leaveRequestRepo.save(leaveRequest);
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
