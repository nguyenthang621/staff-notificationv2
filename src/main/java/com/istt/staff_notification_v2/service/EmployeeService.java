package com.istt.staff_notification_v2.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.NoResultException;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import com.istt.staff_notification_v2.apis.errors.BadRequestAlertException;
import com.istt.staff_notification_v2.configuration.ApplicationProperties;
import com.istt.staff_notification_v2.configuration.ApplicationProperties.StatusEmployeeRef;
import com.istt.staff_notification_v2.dto.EmployeeDTO;
import com.istt.staff_notification_v2.dto.EmployeeRelationshipResponse;
import com.istt.staff_notification_v2.dto.LevelDTO;
import com.istt.staff_notification_v2.dto.ResponseDTO;
import com.istt.staff_notification_v2.dto.SearchDTO;
import com.istt.staff_notification_v2.entity.Attendance;
import com.istt.staff_notification_v2.entity.Department;
import com.istt.staff_notification_v2.entity.Employee;
import com.istt.staff_notification_v2.entity.Level;
import com.istt.staff_notification_v2.entity.Rule;
import com.istt.staff_notification_v2.entity.User;
import com.istt.staff_notification_v2.repository.AttendanceRepo;
import com.istt.staff_notification_v2.repository.DepartmentRepo;
import com.istt.staff_notification_v2.repository.EmployeeRepo;
import com.istt.staff_notification_v2.repository.LevelRepo;
import com.istt.staff_notification_v2.repository.RuleRepo;
import com.istt.staff_notification_v2.repository.UserRepo;
import com.istt.staff_notification_v2.utils.utils;
import com.istt.staff_notification_v2.utils.utils.DateRange;

public interface EmployeeService {
	EmployeeDTO create(EmployeeDTO employeeDTO);

	EmployeeDTO findByName(String name);

	EmployeeDTO findByEmail(String email);

	EmployeeDTO getEmployeeByEmployeename(String employeename);

	EmployeeDTO update(EmployeeDTO employeeDTO);

	Boolean delete(String id);

	Boolean deleteAll(List<Integer> ids);

	EmployeeDTO get(String id);

	List<String> filterEmployeeDependence(Employee employee);

	List<EmployeeDTO> getEmployeeDependence(String employeeId);

	ResponseDTO<List<EmployeeDTO>> search(SearchDTO searchDTO);

	Map<String, List<EmployeeRelationshipResponse>> getEmployeeRelationship();

	List<EmployeeDTO> test();

	List<EmployeeRelationshipResponse> getAllRelationshipByRule();

//	NodeDepartment buildDepartmentTree(List<Employee> employees);

	Map<String, List<EmployeeDTO>> findEmployeeToExportExcel();

	Employee calCountOfDayOff(String employeeId);

	EmployeeDTO saveCountOfDayOff(String employeeId);

	ResponseDTO<List<EmployeeDTO>> saveCountOfDayOffs(List<String> ids);

}

@Service
class EmployeeServiceImpl implements EmployeeService {
	@Autowired
	private EmployeeRepo employeeRepo;

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private LevelRepo levelRepo;

	@Autowired
	private RuleRepo ruleRepo;

	@Autowired
	private DepartmentRepo departmentRepo;

	@Autowired
	private AttendanceRepo attendanceRepo;

	@Autowired
	ApplicationProperties props;

	private static final String ENTITY_NAME = "isttEmployee";

	private static Long getMaxLevelCode(Employee e) {
		Long max = Long.MIN_VALUE;
		for (Level level : e.getLevels()) {
			if (level.getLevelCode() > max) {
				max = Long.valueOf(level.getLevelCode());
			}
		}
		return max;
	}

	@Override
	public List<String> filterEmployeeDependence(Employee employeeCurrent) { // return list dependence employeeId
		Optional<List<Employee>> employeesRaw = employeeRepo.findAllByDepartmentId(employeeCurrent.getDepartment(),
				StatusEmployeeRef.ACTIVE.toString());
		if (employeesRaw.isEmpty())
			throw new BadRequestAlertException("Bad request: Not found employee in employee` department", ENTITY_NAME,
					"Not found");

		Long maxLevelCurrentEmployee = getMaxLevelCode(employeeCurrent);
		List<String> employeeIdDependences = new ArrayList<>();

		for (Employee e : employeesRaw.get()) {
			if (getMaxLevelCode(e) > maxLevelCurrentEmployee)
				employeeIdDependences.add(e.getEmployeeId());
		}

		return employeeIdDependences;
	}

	@Transactional
	@Override
	public EmployeeDTO create(EmployeeDTO employeeDTO) {
		try {
			ModelMapper mapper = new ModelMapper();
			Employee employee = mapper.map(employeeDTO, Employee.class);
			employee.setEmployeeId(UUID.randomUUID().toString().replaceAll("-", ""));

			Set<Level> levels = new HashSet<Level>();
			for (LevelDTO level : employeeDTO.getLevels()) {
				levels.add(levelRepo.findByLevelId(level.getLevelId()).orElseThrow(NoResultException::new));
			}
			employee.setLevels(levels);

			if (!props.getSTATUS_EMPLOYEE().contains(employee.getStatus())) {
				employee.setStatus(props.getSTATUS_EMPLOYEE().get(StatusEmployeeRef.ACTIVE.ordinal()));
			}
			filterEmployeeDependence(employee);

			employee.setEmployeeDependence(filterEmployeeDependence(employee));
			employeeRepo.save(employee);

//			create new user 
			if (userRepo.findByUsername(employee.getEmail()).isPresent()) {
				throw new BadRequestAlertException("Bad request: Employee already exists", ENTITY_NAME,
						"Employee exists");
			}
			User user = new User();
			user.setUserId(UUID.randomUUID().toString().replaceAll("-", ""));
			user.setPassword(new BCryptPasswordEncoder().encode("abcd456789"));
			user.setUsername(employee.getEmail());

			user.setEmployee(employee);

			// commit save
			userRepo.save(user);
			return employeeDTO;
		} catch (ResourceAccessException e) {
			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
		} catch (HttpServerErrorException | HttpClientErrorException e) {
			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
		}
	}

	@Override
	public EmployeeDTO findByEmail(String email) {
		Employee employeePage = employeeRepo.findByEmail(email);
		EmployeeDTO responseDTO = new ModelMapper().map(employeePage, EmployeeDTO.class);
		return responseDTO;
	}

	@Override
	public EmployeeDTO findByName(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Transactional
	@Override
	public EmployeeDTO getEmployeeByEmployeename(String employeename) {
		// TODO Auto-generated method stub
		return null;
	}

	@Transactional
	@Override
	public EmployeeDTO update(EmployeeDTO employeeDTO) {
		try {
			Employee employeeInDB = employeeRepo.findByEmployeeId(employeeDTO.getEmployeeId())
					.orElseThrow(NoResultException::new);

			if (employeeDTO.getEmployeeDependence().size() > 0) {
				for (String employeeId : employeeDTO.getEmployeeDependence()) {
					if (!employeeRepo.existsByEmployeeId(employeeId))
						throw new BadRequestAlertException(
								"Bad request: Invalid employee dependence in employee` department", ENTITY_NAME,
								"Invalid");
				}
			}

			Set<LevelDTO> levels = new HashSet<LevelDTO>();
			for (LevelDTO level : employeeDTO.getLevels()) {
				levels.add(new ModelMapper().map(
						levelRepo.findByLevelId(level.getLevelId()).orElseThrow(NoResultException::new),
						LevelDTO.class));
			}
			employeeDTO.setLevels(levels);
			ModelMapper mapper = new ModelMapper();

			mapper.createTypeMap(EmployeeDTO.class, Employee.class).setProvider(p -> employeeRepo
					.findByEmployeeId(employeeDTO.getEmployeeId()).orElseThrow(NoResultException::new));

			Employee employee = mapper.map(employeeDTO, Employee.class);
			employee.setEmployeeId(employeeInDB.getEmployeeId());

			employeeRepo.save(employee);
			return employeeDTO;
		} catch (ResourceAccessException e) {
			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
		} catch (HttpServerErrorException | HttpClientErrorException e) {
			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
		}

	}

	@Transactional
	@Override
	public Boolean delete(String id) {
		Employee employee = employeeRepo.findByEmployeeId(id).orElseThrow(NoResultException::new);
		if (employee != null) {
			employee.setStatus(StatusEmployeeRef.SUSPEND.toString());
			employeeRepo.save(employee);
			return true;
		}
		return false;
	}

	@Override
	public Boolean deleteAll(List<Integer> ids) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EmployeeDTO get(String id) {
		try {
			Employee employee = employeeRepo.findByEmployeeId(id).orElseThrow(NoResultException::new);
			EmployeeDTO employeeDTO = new ModelMapper().map(employee, EmployeeDTO.class);
			return employeeDTO;

		} catch (ResourceAccessException e) {
			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
		} catch (HttpServerErrorException | HttpClientErrorException e) {
			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
		}
	}

	@Override
	public List<EmployeeDTO> getEmployeeDependence(String employeeId) {
		try {

			Employee employee = employeeRepo.findByEmployeeId(employeeId).orElseThrow(NoResultException::new);
			List<String> employeeDependenceIds = employee.getEmployeeDependence();
			if (employeeDependenceIds.size() == 0)
				return new ArrayList<>();

			List<Employee> employeeDependences = employeeRepo.findByEmployeeIds(employeeDependenceIds)
					.orElseThrow(NoResultException::new);

			List<EmployeeDTO> employeeDependencesDTO = new ArrayList<>();
			for (Employee e : employeeDependences) {
				EmployeeDTO employeeDTO = new ModelMapper().map(e, EmployeeDTO.class);
				employeeDependencesDTO.add(employeeDTO);
			}
			return employeeDependencesDTO;

		} catch (ResourceAccessException e) {
			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
		} catch (HttpServerErrorException | HttpClientErrorException e) {
			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
		}
	}

	@Override
	public ResponseDTO<List<EmployeeDTO>> search(SearchDTO searchDTO) {
		List<Sort.Order> orders = Optional.ofNullable(searchDTO.getOrders()).orElseGet(Collections::emptyList).stream()
				.map(order -> {
					if (order.getOrder().equals(SearchDTO.ASC))
						return Sort.Order.asc(order.getProperty());

					return Sort.Order.desc(order.getProperty());
				}).collect(Collectors.toList());
		Pageable pageable = PageRequest.of(searchDTO.getPage(), searchDTO.getSize(), Sort.by(orders));

		String email = null;
		String department = null;
		String status = null;

		if (StringUtils.hasText(searchDTO.getFilterBys().get("email"))) {
			System.out.println("1 " + searchDTO.getFilterBys() + " " + searchDTO.getValue());
			email = String.valueOf(searchDTO.getFilterBys().get("email"));
			status = String.valueOf(searchDTO.getFilterBys().get("status"));

			Page<Employee> page = employeeRepo.searchByFullnameAndEmail(searchDTO.getValue(), email, status, pageable);

			List<EmployeeDTO> employeeDTOs = new ArrayList<>();
			for (Employee employee : page.getContent()) {
				EmployeeDTO employeeDTO = new ModelMapper().map(employee, EmployeeDTO.class);
				employeeDTOs.add(employeeDTO);
			}
			ResponseDTO<List<EmployeeDTO>> responseDTO = new ModelMapper().map(page, ResponseDTO.class);
			responseDTO.setData(employeeDTOs);

			return responseDTO;
		} else if (StringUtils.hasText(searchDTO.getFilterBys().get("department_name"))) {
			status = String.valueOf(searchDTO.getFilterBys().get("status"));
			System.out.println("3 " + searchDTO.getFilterBys() + " " + searchDTO.getValue());
			department = String.valueOf(searchDTO.getFilterBys().get("department_name"));
			Page<Employee> page = employeeRepo.searchByFullnameAndDepartment(searchDTO.getValue(), department, status,
					pageable);

			List<EmployeeDTO> employeeDTOs = new ArrayList<>();
			for (Employee employee : page.getContent()) {
				EmployeeDTO employeeDTO = new ModelMapper().map(employee, EmployeeDTO.class);
				employeeDTOs.add(employeeDTO);
			}
			ResponseDTO<List<EmployeeDTO>> responseDTO = new ModelMapper().map(page, ResponseDTO.class);
			responseDTO.setData(employeeDTOs);
			return responseDTO;

		} else {

			status = String.valueOf(searchDTO.getFilterBys().get("status"));
			Page<Employee> page = employeeRepo.searchByFullname(searchDTO.getValue(), status, pageable);
			List<EmployeeDTO> employeeDTOs = new ArrayList<>();
			for (Employee employee : page.getContent()) {
				EmployeeDTO employeeDTO = new ModelMapper().map(employee, EmployeeDTO.class);
				employeeDTOs.add(employeeDTO);
			}
			ResponseDTO<List<EmployeeDTO>> responseDTO = new ModelMapper().map(page, ResponseDTO.class);
			responseDTO.setData(employeeDTOs);
			return responseDTO;
		}
	}

	@Override
	public Map<String, List<EmployeeRelationshipResponse>> getEmployeeRelationship() {
		try {
			// get list employees:
//			List<Employee> employees = employeeRepo
//					.getByEmployeeStatus(props.getSTATUS_EMPLOYEE().get(StatusEmployeeRef.ACTIVE.ordinal()))
//					.orElseThrow(NoResultException::new);
			// get list department:
			List<Department> departments = departmentRepo.getAll().orElseThrow(NoResultException::new);

//		Set<Department> DepartmentSet = new HashSet<Department>();
//		Set<NodeDepartment> nodeDepartments = new HashSet<NodeDepartment>();

//			List<List<EmployeeRelationshipResponse>> nodeByDepartment = new ArrayList<>();
			Map<String, List<EmployeeRelationshipResponse>> nodeByDepartment = new HashMap<>();
			for (Department department : departments) {

				if (!nodeByDepartment.containsKey(department.getDepartmentId())) {
					nodeByDepartment.put(department.getDepartmentId(), new ArrayList<>());

				}
				List<EmployeeRelationshipResponse> nodeDepartment = getNodeDepartment(department);
				if (nodeDepartment != null) {
					nodeByDepartment.get(department.getDepartmentId()).addAll(nodeDepartment);
				}
			}
			return nodeByDepartment;

		} catch (ResourceAccessException e) {
			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
		} catch (HttpServerErrorException | HttpClientErrorException e) {
			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
		}

	}

	private List<EmployeeRelationshipResponse> getNodeDepartment(Department department) {
		List<Employee> employeesInDepartment = employeeRepo
				.findAllByDepartmentId(department, StatusEmployeeRef.ACTIVE.toString())
				.orElseThrow(NoResultException::new);
		List<EmployeeDTO> employeesInDepartmentDTO = employeesInDepartment.stream()
				.map(e -> new ModelMapper().map(e, EmployeeDTO.class)).collect(Collectors.toList());

		Map<Long, List<EmployeeRelationshipResponse>> employeesByLevel = new HashMap<>();

		for (EmployeeDTO employeeDTO : employeesInDepartmentDTO) { // loop all employee in department
//			List<LevelDTO> levels = employeeDTO.getLevels().stream().collect(Collectors.toList());

			List<LevelDTO> levels = employeeDTO.getLevels().stream()
					.sorted(Comparator.comparingLong((LevelDTO level) -> level.getLevelCode()).reversed())
					.collect(Collectors.toList());

			if (!employeesByLevel.containsKey(levels.get(0).getLevelCode())) { // add key level into map
																				// employeesByLevel if
				// // not found
				employeesByLevel.put(levels.get(0).getLevelCode(), new ArrayList<>());

			}
			employeesByLevel.get(levels.get(0).getLevelCode())
					.add(new ModelMapper().map(employeeDTO, EmployeeRelationshipResponse.class));

		}

		// Sort levels in descending order
		List<Long> sortedLevels = employeesByLevel.keySet().stream().sorted(Comparator.naturalOrder())
				.collect(Collectors.toList());

		if (sortedLevels.size() > 0) {

			for (int i = 0; i <= (sortedLevels.size() - 1); i++) {
				if (i != 0) {
					for (EmployeeRelationshipResponse employeeRelationshipResponse : employeesByLevel
							.get(sortedLevels.get(i))) {
						employeeRelationshipResponse.setSubordinates(employeesByLevel.get(sortedLevels.get(i - 1)));
					}
				}
			}
//			Collections.reverse(sortedLevels); 

			return employeesByLevel.get(sortedLevels.get(sortedLevels.size() - 1));
		}
		return null;
	}

	@Override
	public List<EmployeeRelationshipResponse> getAllRelationshipByRule() { // Handle rules only department` Director
		try {

			Map<String, List<EmployeeRelationshipResponse>> employeeRelationships = getEmployeeRelationship();

//			List<Department> departments = departmentRepo.getAll().orElseThrow(NoResultException::new);
//
//			Map<String, List<EmployeeRelationshipResponse>> nodeByDepartment = new HashMap<>();

			Map<Long, List<EmployeeRelationshipResponse>> employeesByLevel = new HashMap<>();
			List<Rule> rules = ruleRepo.getAll().orElseThrow(NoResultException::new);
			if (rules != null && rules.size() > 0) {
				for (Rule rule : rules) {
					List<Level> levels = rule.getEmployee().getLevels().stream()
							.sorted(Comparator.comparingLong((Level level) -> level.getLevelCode()).reversed())
							.collect(Collectors.toList());

					if (!employeesByLevel.containsKey(levels.get(0).getLevelCode())) { // add key level into map
																						// employeesByLevel if
						// // not found
						employeesByLevel.put(levels.get(0).getLevelCode(), new ArrayList<>());

					}
					employeesByLevel.get(levels.get(0).getLevelCode())
							.add(new ModelMapper().map(rule.getEmployee(), EmployeeRelationshipResponse.class));
				}
			}

			// Sort levels in descending order
			List<Long> sortedLevels = employeesByLevel.keySet().stream().sorted(Comparator.naturalOrder())
					.collect(Collectors.toList());

			if (sortedLevels.size() > 0) {

				List<EmployeeRelationshipResponse> initSubordinatesCurrent = new ArrayList<>();
				for (int i = 0; i <= (sortedLevels.size() - 1); i++) {
					for (EmployeeRelationshipResponse employeeRelationshipResponse : employeesByLevel
							.get(sortedLevels.get(i))) {

						Optional<Rule> ruleOfEmployeeOp = ruleRepo
								.findByEmployeeId(employeeRelationshipResponse.getEmployeeId());

						if (ruleOfEmployeeOp.isPresent()) {
							Rule ruleOfEmployee = ruleOfEmployeeOp.get();

							System.out.println(i + ", " + employeeRelationshipResponse.getFullname() + " : "
									+ ruleOfEmployee.getDepartmentDependence().size());

							if (ruleOfEmployee.getDepartmentDependence() != null
									&& ruleOfEmployee.getDepartmentDependence().size() > 1) {
								for (String departmentId : ruleOfEmployee.getDepartmentDependence()) {
									System.out.println("departmentId: " + departmentId);
									initSubordinatesCurrent = Stream
											.of(initSubordinatesCurrent, employeeRelationships.get(departmentId))
											.flatMap(Collection::stream).collect(Collectors.toList());
								}

							}
							System.out.println("1-->> " + initSubordinatesCurrent.size());

							if (ruleOfEmployee.getEmployeeDependenceSpecial() != null
									&& ruleOfEmployee.getEmployeeDependenceSpecial().size() > 0) {
								List<Employee> employeeSubordinates = employeeRepo
										.findByEmployeeIds(ruleOfEmployee.getEmployeeDependenceSpecial()).get();
								List<EmployeeRelationshipResponse> employeeSubordinateRelationships = employeeSubordinates
										.stream().map(e -> new ModelMapper().map(e, EmployeeRelationshipResponse.class))
										.collect(Collectors.toList());
								initSubordinatesCurrent = Stream
										.of(initSubordinatesCurrent, employeeSubordinateRelationships)
										.flatMap(Collection::stream).collect(Collectors.toList());

							}

							System.out.println("2-->> " + initSubordinatesCurrent.size());
							employeeRelationshipResponse.setSubordinates(initSubordinatesCurrent);
							System.out.println("----------------");
							System.out.println("level: " + sortedLevels.get(i) + " ");

						}
					}
				}

				return employeesByLevel.get(sortedLevels.get(sortedLevels.size() - 1));
			}

			return null;

		} catch (ResourceAccessException e) {
			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
		} catch (HttpServerErrorException | HttpClientErrorException e) {
			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
		}

	}

	@Override
	public List<EmployeeDTO> test() {
		List<Employee> employeesInDepartment = employeeRepo
				.findAllByDepartmentId(departmentRepo.findByDepartmentName("RnD").get(),
						StatusEmployeeRef.ACTIVE.toString())
				.orElseThrow(NoResultException::new);
		List<EmployeeDTO> employeesInDepartmentDTO = employeesInDepartment.stream()
				.map(e -> new ModelMapper().map(e, EmployeeDTO.class)).collect(Collectors.toList());

		Map<Long, List<EmployeeDTO>> employeesByLevel = new HashMap<>();

		for (EmployeeDTO employeeDTO : employeesInDepartmentDTO) { // loop all employee in department
//			List<LevelDTO> levels = employeeDTO.getLevels().stream().collect(Collectors.toList());

			List<LevelDTO> levels = employeeDTO.getLevels().stream()
					.sorted(Comparator.comparingLong((LevelDTO level) -> level.getLevelCode()).reversed())
					.collect(Collectors.toList());

			if (!employeesByLevel.containsKey(levels.get(0).getLevelCode())) { // add key level into map
																				// employeesByLevel if
				// // not found
				employeesByLevel.put(levels.get(0).getLevelCode(), new ArrayList<>());

			}
			employeesByLevel.get(levels.get(0).getLevelCode()).add(employeeDTO);

		}

		// Sort levels in descending order
		List<Long> sortedLevels = employeesByLevel.keySet().stream().sorted(Comparator.naturalOrder())
				.collect(Collectors.toList());

		if (sortedLevels.size() > 0) {

			for (int i = 0; i <= (sortedLevels.size() - 1); i++) {
				if (i != 0) {
					for (EmployeeDTO employeeDTO : employeesByLevel.get(sortedLevels.get(i))) {
						employeeDTO.setSubordinates(employeesByLevel.get(sortedLevels.get(i - 1)));
					}
				}
			}
			Collections.reverse(sortedLevels);

//			Long SetEmployeeBylevel = employeesByLevel.keySet().stream().mapToLong(v -> v).max()
//					.orElseThrow(NoSuchElementException::new);

			return employeesByLevel.get(sortedLevels.get(0));
		}
		return null;

	}

	@Override
	public Map<String, List<EmployeeDTO>> findEmployeeToExportExcel() {
		try {
			Map<String, List<EmployeeDTO>> employeesByDepartment = new HashMap<>();
			List<Department> departments = departmentRepo.getAll().orElseThrow(NoResultException::new);
			for (Department department : departments) {
				employeesByDepartment.put(department.getDepartmentName(), new ArrayList<>());
				List<Employee> employeeInDepartment = employeeRepo
						.findAllByDepartmentId(department, StatusEmployeeRef.ACTIVE.toString())
						.orElseThrow(NoResultException::new);
				employeesByDepartment.get(department.getDepartmentName()).addAll(employeeInDepartment.stream()
						.map(e -> new ModelMapper().map(e, EmployeeDTO.class)).collect(Collectors.toList()));
			}
			return employeesByDepartment;

		} catch (ResourceAccessException e) {
			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
		} catch (HttpServerErrorException | HttpClientErrorException e) {
			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
		}
	}

	@Override
	public Employee calCountOfDayOff(String employeeId) {
		float count = 0;
		Employee employee = employeeRepo.findById(employeeId).orElseThrow(NoResultException::new);
		if (getMaxLevelCode(employee) == 0) {
			return employee;
		}
		DateRange dateRange = utils.getCurrentMonth();
		Calendar calendarStartDate = Calendar.getInstance();
		calendarStartDate.setTime(dateRange.getStartDate());

		Calendar calendarEndDate = Calendar.getInstance();
		calendarEndDate.setTime(dateRange.getEndDate());

		List<Attendance> attendances = attendanceRepo.findByIndex(Long.valueOf(calendarStartDate.get(Calendar.YEAR)),
				Long.valueOf(calendarStartDate.get(Calendar.MONTH) + 1),
				Long.valueOf(calendarStartDate.get(Calendar.DAY_OF_MONTH)),
				Long.valueOf(calendarEndDate.get(Calendar.YEAR)), Long.valueOf(calendarEndDate.get(Calendar.MONTH) + 1),
				Long.valueOf(calendarEndDate.get(Calendar.DAY_OF_MONTH)));
		if (attendances.size() > 0) {
			for (Attendance attendance : attendances) {
				// chi tru nhung lan nghi phep k dac biet trong thang
				if (!attendance.getLeaveType().isSpecialType())
					count += attendance.getDuration();
				System.err.println(count);
			}
			count = employee.getCountOfDayOff() - count;
			employee.setCountOfDayOff(count);
		}
		return employee;
	}

	@Override
	public EmployeeDTO saveCountOfDayOff(String employeeId) {
		Employee employee = calCountOfDayOff(employeeId);
		EmployeeDTO employeeDTO = new ModelMapper().map(employee, EmployeeDTO.class);
		employeeRepo.save(employee);
		return employeeDTO;
	}

	@Override
	public ResponseDTO<List<EmployeeDTO>> saveCountOfDayOffs(List<String> ids) {
		List<Employee> employees = employeeRepo.findAllById(ids);
		List<EmployeeDTO> employeeDTOs = new ArrayList<EmployeeDTO>();
		if (employees.size() == 0)
			return null;
		for (Employee employee : employees) {
			EmployeeDTO employeeDTO = saveCountOfDayOff(employee.getEmployeeId());
			employeeDTOs.add(employeeDTO);
		}
		ResponseDTO<List<EmployeeDTO>> responseDTO = new ModelMapper().map(employeeDTOs, ResponseDTO.class);
		responseDTO.setData(employeeDTOs);
		return responseDTO;
	}

}