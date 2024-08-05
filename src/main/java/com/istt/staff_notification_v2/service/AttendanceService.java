package com.istt.staff_notification_v2.service;

import java.util.ArrayList;
import java.util.Calendar;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.expression.spel.ast.OpAnd;
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
import com.istt.staff_notification_v2.dto.EmployeeLeaveDTO;
import com.istt.staff_notification_v2.dto.ILeaveMonthDTO;
import com.istt.staff_notification_v2.dto.LeaveMonthDTO;
import com.istt.staff_notification_v2.dto.ResponseDTO;
import com.istt.staff_notification_v2.dto.SearchAttendence;
import com.istt.staff_notification_v2.dto.SearchDTO;
import com.istt.staff_notification_v2.entity.Attendance;
import com.istt.staff_notification_v2.entity.Employee;
import com.istt.staff_notification_v2.entity.LeaveType;
import com.istt.staff_notification_v2.entity.User;
import com.istt.staff_notification_v2.repository.AttendanceRepo;
import com.istt.staff_notification_v2.repository.EmployeeRepo;
import com.istt.staff_notification_v2.repository.LeaveTypeRepo;
import com.istt.staff_notification_v2.repository.UserRepo;
import com.istt.staff_notification_v2.utils.utils;
import com.istt.staff_notification_v2.utils.utils.DateRange;

public interface AttendanceService {

	AttendanceDTO create(AttendanceDTO attendanceDTO);

	ResponseDTO<List<AttendanceDTO>> searchByEmployeeName(SearchDTO searchDTO);

	AttendanceDTO update(AttendanceDTO attendanceDTO);

	Boolean deleteById(String id);

	Boolean deletebylistId(List<String> id);

	AttendanceDTO get(String id);

	List<AttendanceDTO> getAll();

	List<AttendanceDTO> getStatus(String type);

	ResponseDTO<List<AttendanceDTO>> search(SearchAttendence searchAttendence);
	
	List<AttendanceDTO> getByCurrentEmployee(String id);
	
	Set<EmployeeLeaveDTO> getEmployeeLeave();
	
	List<LeaveMonthDTO> countLeaveMont(Long year);

}

@Service
class AttendanceServiceImpl implements AttendanceService {

	@Autowired
	private AttendanceRepo attendanceRepo;

	@Autowired
	private EmployeeRepo employeeRepo;

	@Autowired
	ApplicationProperties props;

	@Autowired
	private LeaveTypeRepo leaveTypeRepo;
	
	@Autowired 
	private EmployeeService employeeService;
	
	@Autowired 
	private UserRepo userRepo;
	
	private static final Logger logger = LogManager.getLogger(AttendanceServiceImpl.class);
	

	private static final String ENTITY_NAME = "isttAttendance";

	@Transactional
	@Override
	public AttendanceDTO create(AttendanceDTO attendanceDTO) {
		try {
			float count=0;
			
			Optional<List<Attendance>> attendanceOp = attendanceRepo.existsAttendance(attendanceDTO.getStartDate(), attendanceDTO.getEndDate(), attendanceDTO.getEmployee().getEmployeeId());
			
			Attendance attendance = new ModelMapper().map(attendanceDTO, Attendance.class);
			attendance.setAttendanceId(UUID.randomUUID().toString().replaceAll("-", ""));

			Optional<Employee> employeeCreateOptional = employeeRepo.findByEmployeeId(attendanceDTO.getApprovedBy());
			if (employeeCreateOptional.isEmpty())
				throw new BadRequestAlertException("Employee create attendance not found.", ENTITY_NAME, "Not found");

			Optional<Employee> employeeOptional = employeeRepo
					.findByEmployeeId(attendanceDTO.getEmployee().getEmployeeId());
			if (employeeOptional.isEmpty())
				throw new BadRequestAlertException("Employee not found.", ENTITY_NAME, "Not found");

			if (attendanceDTO.getDuration() <= 0.0 || attendanceDTO.getDuration() % 0.5 != 0.0)
				throw new BadRequestAlertException("Bad request: Invalid duration", ENTITY_NAME, "Invalid");

			Optional<LeaveType> leaveTypeOp = leaveTypeRepo
					.findByLeavetypeId(attendanceDTO.getLeaveType().getLeavetypeId());
			if (leaveTypeOp.isEmpty())
				throw new BadRequestAlertException("Invalid TYPE ATTENDANCE", ENTITY_NAME, "Invalid");

			attendance.setLeaveType(leaveTypeOp.get());

			Employee employee = employeeOptional.get();
			if (employee.getStatus().equals(StatusEmployeeRef.SUSPEND.toString()))
				throw new BadRequestAlertException("This employee has been suspended", ENTITY_NAME, "Suspend");

			attendance.setEmployee(employee);
			attendance.setCreateAt(new Date());

			attendance.setStartDate(new utils().resetStartDate(attendanceDTO.getStartDate()));

			attendance.setEndDate(
					new utils().calculatorEndDate(attendanceDTO.getStartDate(), attendanceDTO.getDuration()));

			List<Attendance> splitAttendences = new utils().handleSplitAttendence(attendance);
			for (Attendance splitAttendence : splitAttendences) {
				splitAttendence.setAttendanceId(UUID.randomUUID().toString().replaceAll("-", ""));
				if (attendanceRepo
						.findByStartDate(splitAttendence.getStartDate(), splitAttendence.getEmployee().getEmployeeId())
						.isEmpty()) {
					count += splitAttendence.getDuration();
					attendanceRepo.save(splitAttendence);
				}
			}
			
			//ngay nghi bi tru luong
			if(!leaveTypeOp.get().isSpecialType())
				employeeService.calDayOff(attendance.getEmployee().getEmployeeId(), count, false);
			
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
			ModelMapper mapper = new ModelMapper();
			Optional<Attendance> attendanceOp = attendanceRepo.findById(attendanceDTO.getAttendanceId());
			
			if(attendanceOp==null) throw new BadRequestAlertException("not found attendance", ENTITY_NAME, "missing data");
			Attendance attendance = attendanceOp.get();
			
			//check leavetype
			Optional<LeaveType> leaveTypeOp = leaveTypeRepo.findById(attendanceDTO.getLeaveType().getLeavetypeId());
			if(leaveTypeOp.isEmpty()) {
				throw new BadRequestAlertException("not found leavetype", ENTITY_NAME, "missing data");
			}
			attendanceDTO.setLeaveType(leaveTypeOp.get());
			//neu doi type
			if(attendanceDTO.getLeaveType().isSpecialType()!= attendance.getLeaveType().isSpecialType()) {
				//truong hop doi tu co luong sang khong luong
				if(attendanceDTO.getLeaveType().isSpecialType()) {
					employeeService.calDayOff(attendance.getEmployee().getEmployeeId(), attendance.getDuration(), true);
				}
				else {
					//truong hop doi tu khong luong sang co luong
					employeeService.calDayOff(attendance.getEmployee().getEmployeeId(), attendanceDTO.getDuration(), false);
				}
			}else { //khong doi type
				//neu khong luong
				if(!attendance.getLeaveType().isSpecialType()) {
					employeeService.calDayOff(attendance.getEmployee().getEmployeeId(), attendanceDTO.getDuration()- attendance.getDuration(), false);
				}
			}
			
			attendance.setLeaveType(leaveTypeOp.get());
			attendance.setDuration(attendanceDTO.getDuration());
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
		
		//check xem attendance nay nghi co bi tru phép khong neu co cong lai cho nguoi ta
		if(!attendance.getLeaveType().isSpecialType())
			employeeService.calDayOff(attendance.getEmployee().getEmployeeId(), attendance.getDuration(),true);
		
		if (attendance != null) {
			attendanceRepo.deleteById(id);
		}
		return null;
	}

	@Transactional
	@Override
	public Boolean deletebylistId(List<String> ids) {
		List<Attendance> attendances = attendanceRepo.findAllById(ids);
		
		for (Attendance attendance : attendances) {
			//check xem attendance nay nghi co bi tru phép khong neu co cong lai cho nguoi ta
			if(!attendance.getLeaveType().isSpecialType())
				employeeService.calDayOff(attendance.getEmployee().getEmployeeId(), attendance.getDuration(),true);
		}
		
		
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
	public ResponseDTO<List<AttendanceDTO>> search(SearchAttendence searchAttendence) {
		try {
			List<Sort.Order> orders = Optional.ofNullable(searchAttendence.getSearch().getOrders())
					.orElseGet(Collections::emptyList).stream().map(order -> {
						if (order.getOrder().equals(SearchDTO.ASC))
							return Sort.Order.asc(order.getProperty());

						return Sort.Order.desc(order.getProperty());
					}).collect(Collectors.toList());
			Pageable pageable = PageRequest.of(searchAttendence.getSearch().getPage(),
					searchAttendence.getSearch().getSize(), Sort.by(orders));

//			Page<Attendance> page = attendanceRepo.searchByMultiAllType(searchAttendence.getSearch().getValue(),
//					searchAttendence.getStartDate(), searchAttendence.getEndDate(), pageable);

			Calendar calendarStartDate = Calendar.getInstance();
			calendarStartDate.setTime(searchAttendence.getStartDate());

			Calendar calendarEndDate = Calendar.getInstance();
			calendarEndDate.setTime(searchAttendence.getEndDate());

			Page<Attendance> page = attendanceRepo.searchByIndex(searchAttendence.getSearch().getValue(),
					Long.valueOf(calendarStartDate.get(Calendar.YEAR)),
					Long.valueOf(calendarStartDate.get(Calendar.MONTH) + 1),
					Long.valueOf(calendarStartDate.get(Calendar.DAY_OF_MONTH)),
					Long.valueOf(calendarEndDate.get(Calendar.YEAR)),
					Long.valueOf(calendarEndDate.get(Calendar.MONTH) + 1),
					Long.valueOf(calendarEndDate.get(Calendar.DAY_OF_MONTH)), pageable);
			if (searchAttendence.getType().isEmpty()) {
				page = attendanceRepo.searchByMulti(searchAttendence.getSearch().getValue(),
						searchAttendence.getStartDate(), searchAttendence.getEndDate(), searchAttendence.getType(),
						pageable);
			}

			ModelMapper mapper = new ModelMapper();
			List<AttendanceDTO> attendanceDTOs = page.getContent().stream()
					.map(attendance -> mapper.map(attendance, AttendanceDTO.class)).collect(Collectors.toList());

			ResponseDTO<List<AttendanceDTO>> responseDTO = mapper.map(page, ResponseDTO.class);
			responseDTO.setData(attendanceDTOs);
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
			List<AttendanceDTO> attendanceDTOs = page.getContent().stream()
					.map(attendance -> mapper.map(attendance, AttendanceDTO.class)).collect(Collectors.toList());
//			System.err.println(searchDTO.getValue());
//			System.err.print(page.getContent().size());
			ResponseDTO<List<AttendanceDTO>> responseDTO = mapper.map(page, ResponseDTO.class);
			responseDTO.setData(attendanceDTOs);
			return responseDTO;
		} catch (ResourceAccessException e) {
			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
		} catch (HttpServerErrorException | HttpClientErrorException e) {
			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
		}
	}

	@Override
	public List<AttendanceDTO> getByCurrentEmployee(String id) {
		User user = userRepo.findById(id).orElseThrow(NoResultException::new);
		Employee employee = user.getEmployee();
		List<Attendance> attendances = attendanceRepo.findByEmployee(employee);
		ModelMapper mapper = new ModelMapper();
		
		return attendances
				  .stream()
				  .map(attendance-> mapper.map(attendance, AttendanceDTO.class))
				  .collect(Collectors.toList());
	}

	@Override
	public Set<EmployeeLeaveDTO> getEmployeeLeave() {
		Date date = new Date();
		DateRange dateRange = utils.getDate(date);
		Set<EmployeeLeaveDTO> employeeLeaveDTOs = new HashSet<EmployeeLeaveDTO>();
		Optional<List<Attendance>> attendances = attendanceRepo.findByDate(dateRange.getStartDate(), dateRange.getEndDate());
		if(attendances.isEmpty()) return null;
		for (Attendance attendance: attendances.get()) {
			EmployeeLeaveDTO employeeLeaveDTO = new EmployeeLeaveDTO();
			employeeLeaveDTO.setId(attendance.getAttendanceId());
			employeeLeaveDTO.setEmployeeName(attendance.getEmployee().getFullname());
			employeeLeaveDTO.setEmployeeDepartment(attendance.getEmployee().getDepartment().getDepartmentName());
			employeeLeaveDTOs.add(employeeLeaveDTO);
		}
		return employeeLeaveDTOs;
	}

	@Override
	public List<LeaveMonthDTO> countLeaveMont(Long year) {
		Optional<List<ILeaveMonthDTO>> countLeaveMonth = attendanceRepo.countLeaveMonth(year);
		List<LeaveMonthDTO> list = new ArrayList<LeaveMonthDTO>();
		for (int i = 1; i <13; i++) {
			LeaveMonthDTO leaveMonthDTO = new LeaveMonthDTO();
			leaveMonthDTO.setCountEmployee(0);
			leaveMonthDTO.setYear(year);
			leaveMonthDTO.setMonth((long) i);
			leaveMonthDTO.setId(leaveMonthDTO.getMonth().toString()+leaveMonthDTO.getYear());
			list.add(leaveMonthDTO);
		}
		if(countLeaveMonth.isPresent()) {
			for (ILeaveMonthDTO iLeaveMonthDTO : countLeaveMonth.get()) {
				LeaveMonthDTO leaveMonthDTO = new LeaveMonthDTO();
				leaveMonthDTO.setCountEmployee(iLeaveMonthDTO.getCountEmployee());
				leaveMonthDTO.setYear(iLeaveMonthDTO.getYear());
				leaveMonthDTO.setMonth(iLeaveMonthDTO.getMonth());
				leaveMonthDTO.setId(leaveMonthDTO.getMonth().toString()+leaveMonthDTO.getYear());
				list.add(leaveMonthDTO);
			}
		}
		return list;
	}
	

}
