package com.istt.staff_notification_v2.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.persistence.NoResultException;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import com.istt.staff_notification_v2.apis.errors.BadRequestAlertException;
import com.istt.staff_notification_v2.configuration.ApplicationProperties;
import com.istt.staff_notification_v2.configuration.ApplicationProperties.StatusEmployeeRef;
import com.istt.staff_notification_v2.dto.EmployeeDTO;
import com.istt.staff_notification_v2.entity.Employee;
import com.istt.staff_notification_v2.entity.Level;
import com.istt.staff_notification_v2.entity.User;
import com.istt.staff_notification_v2.repository.EmployeeRepo;
import com.istt.staff_notification_v2.repository.LevelRepo;
import com.istt.staff_notification_v2.repository.UserRepo;

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
		Optional<List<Employee>> employeesRaw = employeeRepo.findAllByDepartmentId(employeeCurrent.getDepartment());
		if (employeesRaw.isEmpty())
			throw new BadRequestAlertException("Bad request: Not found employee in employee` department", ENTITY_NAME,
					"Not found");

		Long maxLevelCurrentEmployee = getMaxLevelCode(employeeCurrent);
		List<String> employeeIdDependences = new ArrayList<>();

		for (Employee e : employeesRaw.get()) {
			if (getMaxLevelCode(e) > maxLevelCurrentEmployee)
				employeeIdDependences.add(e.getEmployeeId());
		}

		System.out.println("maxLevelCurrentEmployee: " + maxLevelCurrentEmployee);

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
			for (Level level : employeeDTO.getLevels()) {
				levels.add(levelRepo.findByLevelId(level.getLevelId()).orElseThrow(NoResultException::new));
			}
			System.out.println(1111);
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

			if (employeeDTO.getEmployeeDependence().size() > 0) {
				for (String employeeId : employeeDTO.getEmployeeDependence()) {
					if (!employeeRepo.existsByEmployeeId(employeeId))
						throw new BadRequestAlertException(
								"Bad request: Invalid employee dependence in employee` department", ENTITY_NAME,
								"Invalid");
				}
			}
			ModelMapper mapper = new ModelMapper();

			mapper.createTypeMap(EmployeeDTO.class, Employee.class).setProvider(p -> employeeRepo
					.findByEmployeeId(employeeDTO.getEmployeeId()).orElseThrow(NoResultException::new));
			Employee employee = mapper.map(employeeDTO, Employee.class);
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
		Employee employee = employeeRepo.findById(id).orElseThrow(NoResultException::new);
		if (employee != null) {
			employeeRepo.delete(employee);
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

}