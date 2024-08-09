package com.istt.staff_notification_v2.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.NoResultException;
import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
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
import com.istt.staff_notification_v2.dto.EmployeeLeaveDTO;
import com.istt.staff_notification_v2.dto.LeaveAprroveDTO;
import com.istt.staff_notification_v2.dto.LeaveRequestDTO;
import com.istt.staff_notification_v2.dto.MailRequestDTO;
import com.istt.staff_notification_v2.dto.ResponseDTO;
import com.istt.staff_notification_v2.dto.ResponseLeaveRequest;
import com.istt.staff_notification_v2.dto.SearchDTO;
import com.istt.staff_notification_v2.dto.SearchLeaveRequest;
import com.istt.staff_notification_v2.entity.Employee;
import com.istt.staff_notification_v2.entity.LeaveRequest;
import com.istt.staff_notification_v2.entity.User;
import com.istt.staff_notification_v2.repository.EmployeeRepo;
import com.istt.staff_notification_v2.repository.LeaveRequestRepo;
import com.istt.staff_notification_v2.repository.LeaveTypeRepo;
import com.istt.staff_notification_v2.repository.UserRepo;
import com.istt.staff_notification_v2.utils.utils;
import com.istt.staff_notification_v2.utils.utils.DateRange;

public interface LeaveRequestService {

//	LeaveRequestDTO create(LeaveRequestDTO leaveRequestDTO);
	MailRequestDTO create(@RequestBody MailRequestDTO mailRequestDTO);

	//gắn với role_update
	ResponseLeaveRequest changeStatusLeaveRequest(@RequestBody ResponseLeaveRequest responseLeaveRequeest);

//	LeaveRequestDTO create(LeaveRequestDTO leaveRequestDTO);
	MailRequestDTO update(@RequestBody MailRequestDTO mailRequestDTO);

	LeaveRequestDTO delete(String id);

	LeaveRequestDTO get(String id);
	
	ResponseDTO<List<LeaveRequestDTO>> getLeaveThisMonth(SearchDTO searchDTO);
	
	ResponseDTO<List<LeaveRequestDTO>> getAllLeaveThisMonth();
	
	ResponseDTO<List<LeaveRequestDTO>> searchLeaveRequest(SearchLeaveRequest searchLeaveRequest);

	Set<EmployeeLeaveDTO> getApproved(String email);
	
	ResponseDTO<List<LeaveAprroveDTO>> getApproveCurrentDay();
	
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
	
	private static final Logger logger = LogManager.getLogger(LeaveRequestServiceImpl.class);
	
	private Boolean sendNotification(@RequestBody MailRequestDTO mailRequestDTO) {

		try {
			System.err.println(mailRequestDTO.getRecceiverList().size());
			for (int i = 0; i < mailRequestDTO.getRecceiverList().size(); i++) {
				EmployeeDTO receiver = new EmployeeDTO();
				receiver = mailRequestDTO.getRecceiverList().get(i);
				System.out.println("receiver maiL : " + receiver.getEmail());
				
				mailService.sendEmail(mailRequestDTO.getLeaveRequestDTO(), receiver, mailRequestDTO.getSubject());
				logger.error("mail start send");
			}
			return true;

		} catch (Exception e) {
			return false;

		}

	}

	@Override
	public MailRequestDTO create(@RequestBody MailRequestDTO mailRequestDTO) {
		try {

			DateRange dateRange= utils.getDate(new Date());
			Optional<List<LeaveRequest>> leaveOp = leaveRequestRepo.findByReqDateAndEmployeeId(
					mailRequestDTO.getLeaveRequestDTO().getEmployee().getEmployeeId(),
					dateRange.getStartDate(), 
					dateRange.getEndDate());
			if(leaveOp.isPresent()&&leaveOp.get().size()>3) {
				throw new BadRequestAlertException("You can send 10 leaves/day", ENTITY_NAME, "Spam");
			}
			LeaveRequestDTO leaveRequestDTO = mailRequestDTO.getLeaveRequestDTO();
			ModelMapper mapper = new ModelMapper();
			LeaveRequest leaveRequest = mapper.map(leaveRequestDTO, LeaveRequest.class);
			// Validate duration:
			if (leaveRequestDTO.getDuration() <= 0.0 || leaveRequestDTO.getDuration() % 0.5 != 0.0)
				throw new BadRequestAlertException("Bad request: Invalid duration", ENTITY_NAME, "Invalid");

			leaveRequest.setLeaveqequestId(UUID.randomUUID().toString().replaceAll("-", ""));

			Date requestDate = new Date();
			leaveRequest.setRequestDate(requestDate);
			
			
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
			
			//valid receiver in dependences
			
			if(!employee.getEmployeeDependence().contains(mailRequestDTO.getRecceiverList().get(0).getEmployeeId())) {
				throw new BadRequestAlertException("Bad request: Can only send to superior", ENTITY_NAME, "Invalid");
			}
			
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
	public LeaveRequestDTO delete(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LeaveRequestDTO get(String id) {
		ModelMapper mapper = new ModelMapper();
		Optional<LeaveRequest> leaveOptional = leaveRequestRepo.findById(id);
		if(leaveOptional.isEmpty()) {
			throw new NoResultException();
		}
		LeaveRequestDTO leaveRequestDTO = mapper.map(leaveOptional.get(), LeaveRequestDTO.class);
		logger.error(leaveRequestDTO.getLeaveqequestId());
		return leaveRequestDTO;
		
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
			leaveRequest.setResponseDate(new Date());
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
						mapper.map(employee, EmployeeDTO.class), subject);
			} else if (responseLeaveRequest.getStatus().equals(StatusLeaveRequestRef.NOT_APPROVED.toString())
					|| responseLeaveRequest.getStatus().equals(StatusLeaveRequestRef.REJECT.toString())) { // Case
																											// reject or
																											// not_approved

				// Handle send mail
				ModelMapper mapper = new ModelMapper();
				String subject = "Phản hồi đơn xin nghỉ phép";

				String reason = responseLeaveRequest.getAnrreason();
				mailService.sendReponseRejectEmail(mapper.map(leaveRequest, LeaveRequestDTO.class),
						mapper.map(employee, EmployeeDTO.class), subject, reason,
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
	public ResponseDTO<List<LeaveRequestDTO>> searchLeaveRequest(SearchLeaveRequest searchLeaveRequest) {
		try {
				ModelMapper mapper = new ModelMapper();
				Pageable pageable = PageRequest.of(searchLeaveRequest.getPage(), searchLeaveRequest.getSize());
				Page<LeaveRequest> page = new PageImpl<>(Collections.emptyList());
				if (searchLeaveRequest.getEmail() != null && searchLeaveRequest.getStatus() == null
						&& searchLeaveRequest.getStartDate() == null && searchLeaveRequest.getMailReciver() == null) {
					page = leaveRequestRepo.findEmail(searchLeaveRequest.getEmail(), pageable);
				} else if (searchLeaveRequest.getEmail() != null && searchLeaveRequest.getStatus() != null
						&& searchLeaveRequest.getStartDate() == null && searchLeaveRequest.getMailReciver() == null) {
					page = leaveRequestRepo
							.findEmailAndStatus(searchLeaveRequest.getEmail(), searchLeaveRequest.getStatus(), pageable);
					
				} else if (searchLeaveRequest.getEmail() != null && searchLeaveRequest.getStatus() != null
						&& searchLeaveRequest.getStartDate() != null && searchLeaveRequest.getMailReciver() == null) {
					page = leaveRequestRepo.find(searchLeaveRequest.getEmail(),
							searchLeaveRequest.getStatus(), searchLeaveRequest.getStartDate(), pageable);

				} else if (searchLeaveRequest.getEmail() == null && searchLeaveRequest.getStatus() != null
						&& searchLeaveRequest.getStartDate() == null && searchLeaveRequest.getMailReciver() == null) {
					page = leaveRequestRepo.findByStatusOrderByResponseDateDesc(searchLeaveRequest.getStatus(), pageable);
					
				} else if (searchLeaveRequest.getMailReciver() != null && searchLeaveRequest.getStatus() != null
						&& searchLeaveRequest.getStartDate() != null && searchLeaveRequest.getEmail() != null) {
					page = leaveRequestRepo.findByReceiver(
							searchLeaveRequest.getMailReciver(), searchLeaveRequest.getStatus(),
							searchLeaveRequest.getStartDate(), pageable);
				}else if (searchLeaveRequest.getMailReciver() != null && searchLeaveRequest.getStatus() == null
						&& searchLeaveRequest.getStartDate() == null && searchLeaveRequest.getEmail() == null) {
					Employee employee = employeeRepo.findByEmail(searchLeaveRequest.getMailReciver());
					logger.error(employee.getEmail()+"/"+ employee.getFullname());
					searchLeaveRequest.setStatus(props.getSTATUS_LEAVER_REQUEST().get(StatusLeaveRequestRef.WAITING.ordinal()));
					page = leaveRequestRepo.findByReceiverStatus(
							employee.getEmployeeId(), searchLeaveRequest.getStatus(), pageable);
				
				}
				List<LeaveRequestDTO> leaveRequestDTOs = new ArrayList<>();
				if(!page.getContent().isEmpty()) {
					leaveRequestDTOs = page.getContent().stream().map(leave-> mapper.map(leave, LeaveRequestDTO.class)).collect(Collectors.toList());
					for (int i = 0; i < leaveRequestDTOs.size(); i++) {
						LeaveRequestDTO leaveDto = leaveRequestDTOs.get(i);
						logger.error(leaveDto.getReceiver());
						Employee employee = employeeRepo.findByEmployeeId(leaveDto.getReceiver()).get();
						leaveDto.setReceiver(employee.getFullname());
						leaveRequestDTOs.set(i, leaveDto);
					}
				}
				ResponseDTO<List<LeaveRequestDTO>> responseDTO = mapper.map(page, ResponseDTO.class);
				responseDTO.setData(leaveRequestDTOs);
				return responseDTO;
		} catch (ResourceAccessException e) {
			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
		} catch (HttpServerErrorException | HttpClientErrorException e) {
			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
		}
			
	}


	@Override
	public Set<EmployeeLeaveDTO> getApproved(String email) {
		Employee employee = employeeRepo.findByEmail(email);
		Set<EmployeeLeaveDTO> employeeLeaveDTOs = new HashSet<EmployeeLeaveDTO>();
		if(employee==null) throw new BadRequestAlertException("not found employee", ENTITY_NAME, "missing data");
		String status = props.getSTATUS_LEAVER_REQUEST().get(StatusLeaveRequestRef.WAITING.ordinal());
		Optional<List<LeaveRequest>> listOp = leaveRequestRepo.findByReceiverandStatus(employee.getEmployeeId(), status);
		if(listOp.isEmpty()) return null;
		
		for (LeaveRequest leaveRequest : listOp.get()) {
			EmployeeLeaveDTO employeeLeaveDTO = new EmployeeLeaveDTO();
			employeeLeaveDTO.setId(leaveRequest.getLeaveqequestId());
			employeeLeaveDTO.setEmployeeName(leaveRequest.getEmployee().getFullname());
			employeeLeaveDTO.setEmployeeDepartment(leaveRequest.getEmployee().getDepartment().getDepartmentName());
			employeeLeaveDTOs.add(employeeLeaveDTO);
		}
		return employeeLeaveDTOs;
	}

	@Override
	public ResponseDTO<List<LeaveRequestDTO>> getLeaveThisMonth(SearchDTO searchDTO) {
		List<Sort.Order> orders = Optional.ofNullable(searchDTO.getOrders()).orElseGet(Collections::emptyList).stream()
				.map(order -> {
					if (order.getOrder().equals(SearchDTO.ASC))
						return Sort.Order.asc(order.getProperty());

					return Sort.Order.desc(order.getProperty());
				}).collect(Collectors.toList());
		Pageable pageable = PageRequest.of(searchDTO.getPage(), searchDTO.getSize(), Sort.by(orders));
		String status = "%%";
		String date= "";
		DateRange dateRange = utils.getCurrentMonth();
		Date startDate = dateRange.getStartDate();
		Date endDate = dateRange.getEndDate();
		if(StringUtils.hasText(searchDTO.getFilterBys().get("status"))) {
			status = String.valueOf(searchDTO.getFilterBys().get("status")).toUpperCase();
		}
		if(StringUtils.hasText(searchDTO.getFilterBys().get("startDate"))) {
			startDate = utils.format(String.valueOf(searchDTO.getFilterBys().get("startDate")));
		}
		if(StringUtils.hasText(searchDTO.getFilterBys().get("endDate"))) {
				endDate = utils.format(String.valueOf(searchDTO.getFilterBys().get("endDate")));
		}
		if(!props.getSTATUS_LEAVER_REQUEST().contains(status)) status = "%%";
		ModelMapper mapper = new ModelMapper();
		Page<LeaveRequest> page = leaveRequestRepo.findByStatusReqdateDesc(status,startDate, endDate, pageable);
			List<LeaveRequestDTO> leaveRequestDTOs = page.getContent().stream().map(l -> mapper.map(l, LeaveRequestDTO.class))
					.collect(Collectors.toList());
			ResponseDTO<List<LeaveRequestDTO>> responseDTO = mapper.map(page, ResponseDTO.class);
			responseDTO.setData(leaveRequestDTOs);
			return responseDTO;
	}

	@Override
	public ResponseDTO<List<LeaveRequestDTO>> getAllLeaveThisMonth() {
		List<LeaveRequestDTO> leaveRequestDTOs = new ArrayList<>();
		ModelMapper mapper = new ModelMapper();
		DateRange dateRange = utils.getCurrentMonth();
		Optional<List<LeaveRequest>> leaveOptional = leaveRequestRepo.findByReqdate(dateRange.getStartDate(), dateRange.getEndDate());
		if(leaveOptional.isPresent()) {
			leaveRequestDTOs = leaveOptional.get().stream().map(l -> mapper.map(l, LeaveRequestDTO.class))
					.collect(Collectors.toList());
		}
		ResponseDTO<List<LeaveRequestDTO>> responseDTO = mapper.map(leaveRequestDTOs, ResponseDTO.class);
		responseDTO.setData(leaveRequestDTOs);
		return responseDTO;
	}
	
	@Override
	public ResponseDTO<List<LeaveAprroveDTO>> getApproveCurrentDay() {
		List<LeaveAprroveDTO> leaveRequestDTOs = new ArrayList<>();
		ModelMapper mapper = new ModelMapper();
		DateRange dateRange = utils.getDate(new Date());
		String status = props.getSTATUS_LEAVER_REQUEST().get(StatusLeaveRequestRef.APPROVED.ordinal());
		Optional<List<LeaveRequest>> leaveOptional = leaveRequestRepo.findByResponsedate(dateRange.getStartDate(), dateRange.getEndDate(), status);
		if(leaveOptional.isPresent()) {
			for (LeaveRequest leaveRequest : leaveOptional.get()) {
				LeaveAprroveDTO leaveAprroveDTO = new LeaveAprroveDTO();
				leaveAprroveDTO.setLeaveId(leaveRequest.getLeaveqequestId());
				leaveAprroveDTO.setFullName(leaveRequest.getEmployee().getFullname());
				leaveAprroveDTO.setDepartmentName(leaveRequest.getEmployee().getDepartment().getDepartmentName());
				leaveAprroveDTO.setStartDate(leaveRequest.getStartDate());
				leaveAprroveDTO.setEndDate(utils.calculatorEndDate(leaveRequest.getStartDate(), leaveRequest.getDuration()));
				leaveRequestDTOs.add(leaveAprroveDTO);
			}
		}
		
		ResponseDTO<List<LeaveAprroveDTO>> responseDTO = mapper.map(leaveRequestDTOs, ResponseDTO.class);
		responseDTO.setData(leaveRequestDTOs);
		return responseDTO;
	}

	@Override
	public MailRequestDTO update(MailRequestDTO mailRequestDTO) {
		try {
			LeaveRequestDTO leaveRequestDTO = mailRequestDTO.getLeaveRequestDTO();
			ModelMapper mapper = new ModelMapper();
			if(leaveRequestRepo.findById(leaveRequestDTO.getLeaveqequestId()).isEmpty()) {
				throw new BadRequestAlertException("Not Found LeaveRequest", ENTITY_NAME, "missing data");
			}
			LeaveRequest leaveRequest = mapper.map(leaveRequestDTO, LeaveRequest.class);
			// Validate duration:
			if (leaveRequestDTO.getDuration() <= 0.0 || leaveRequestDTO.getDuration() % 0.5 != 0.0)
				throw new BadRequestAlertException("Bad request: Invalid duration", ENTITY_NAME, "Invalid");
			
			Date requestDate = new Date();
			leaveRequest.setRequestDate(requestDate);
			
			// Validate employeeId in leaveRequest
			if (leaveRequestDTO.getEmployee().getEmployeeId() == null)
				throw new BadRequestAlertException("Bad request: Employee not found!", ENTITY_NAME, "Not Found!");

			// map employee if true
			Employee employee = employeeRepo.findByEmployeeId(leaveRequestDTO.getEmployee().getEmployeeId())
					.orElseThrow(NoResultException::new);
			leaveRequest.setEmployee(employee);

			if (leaveRequestDTO.getLeavetype().getLeavetypeId() == null)
				throw new BadRequestAlertException("Bad request: Missing LeaveTypeId", ENTITY_NAME, "Missing");
			
			leaveRequest.setStatus(props.getSTATUS_LEAVER_REQUEST().get(StatusLeaveRequestRef.WAITING.ordinal()));
			
			//valid receiver in dependences
			
			if(!employee.getEmployeeDependence().contains(mailRequestDTO.getRecceiverList().get(0).getEmployeeId())) {
				throw new BadRequestAlertException("Bad request: Can only send to superior", ENTITY_NAME, "Invalid");
			}
			
			// Valid only send to a employee
			if (mailRequestDTO.getRecceiverList().size() != 1)
				throw new BadRequestAlertException("Bad request: Can only be sent to 1 person", ENTITY_NAME, "Invalid");
			leaveRequest.setReceiver(mailRequestDTO.getRecceiverList().get(0).getEmployeeId());
			

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

	
}
