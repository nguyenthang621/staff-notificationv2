package com.istt.staff_notification_v2.apis;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.logging.LogManager;

import javax.validation.Valid;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.istt.staff_notification_v2.apis.errors.BadRequestAlertException;
import com.istt.staff_notification_v2.dto.EmployeeDTO;
import com.istt.staff_notification_v2.dto.EmployeeDependence;
import com.istt.staff_notification_v2.dto.EmployeeRelationshipResponse;
import com.istt.staff_notification_v2.dto.LevelDTO;
import com.istt.staff_notification_v2.dto.ResponseDTO;
import com.istt.staff_notification_v2.dto.SearchDTO;
import com.istt.staff_notification_v2.entity.Employee;
import com.istt.staff_notification_v2.security.securityv2.CurrentUser;
import com.istt.staff_notification_v2.security.securityv2.UserPrincipal;
import com.istt.staff_notification_v2.service.EmployeeService;

@RestController
@RequestMapping("/employee")
public class EmployeeAPI {
	@Autowired
	private EmployeeService employeeService;

	private static final String ENTITY_NAME = "isttEmployee";
	
	@PreAuthorize("hasRole('ROLE_EMPLOYEE_CREATE')&&hasRole('ROLE_EMPLOYEE_ACCESS')")
	@PostMapping("")
	public ResponseDTO<EmployeeDTO> create(@RequestBody @Valid EmployeeDTO employeeDTO) throws URISyntaxException {
		if (employeeDTO.getEmail() == null || employeeDTO.getFullname() == null
				|| employeeDTO.getDepartment() == null) {
			throw new BadRequestAlertException("Bad request: missing data", ENTITY_NAME, "missing");
		}

		employeeService.create(employeeDTO);
		return ResponseDTO.<EmployeeDTO>builder().code(String.valueOf(HttpStatus.OK.value())).data(employeeDTO).build();
	}

	@PreAuthorize("hasRole('ROLE_EMPLOYEE_DELETE')&&hasRole('ROLE_EMPLOYEE_ACCESS')")
	@DeleteMapping("/{id}")
	public ResponseDTO<Void> delete(@PathVariable(value = "id") String id) throws URISyntaxException {
		if (id == null) {
			throw new BadRequestAlertException("Bad request: missing id", ENTITY_NAME, "missing_id");
		}
		employeeService.delete(id);
		return ResponseDTO.<Void>builder().code(String.valueOf(HttpStatus.OK.value())).build();
	}

	@PreAuthorize("hasRole('ROLE_EMPLOYEE_VIEW')&&hasRole('ROLE_EMPLOYEE_ACCESS')")
	@GetMapping("/{id}")
	public ResponseDTO<EmployeeDTO> get(@PathVariable(value = "id") String id) {
		if (id == null) {
			throw new BadRequestAlertException("Bad request: missing id", ENTITY_NAME, "missing_id");
		}
		return ResponseDTO.<EmployeeDTO>builder().code(String.valueOf(HttpStatus.OK.value()))
				.data(employeeService.get(id)).build();
	}
	
	@PreAuthorize("hasRole('ROLE_EMPLOYEE_VIEW')&&hasRole('ROLE_EMPLOYEE_ACCESS')")
	@GetMapping("/dependence/{id}")
	public ResponseDTO<List<EmployeeDTO>> getEmployeeDependence(@PathVariable(value = "id") String id) {
		if (id == null) {
			throw new BadRequestAlertException("Bad request: missing id", ENTITY_NAME, "missing_id");
		}
		return ResponseDTO.<List<EmployeeDTO>>builder().code(String.valueOf(HttpStatus.OK.value()))
				.data(employeeService.getEmployeeDependence(id)).build();
	}

	@PutMapping("/")
	@PreAuthorize("hasRole('ROLE_EMPLOYEE_UPDATE')&&hasRole('ROLE_EMPLOYEE_ACCESS')")
	public ResponseDTO<EmployeeDTO> update(@RequestBody @Valid EmployeeDTO employeeDTO) throws IOException {
		employeeService.update(employeeDTO);
		return ResponseDTO.<EmployeeDTO>builder().code(String.valueOf(HttpStatus.OK.value())).data(employeeDTO).build();

	}

	@PreAuthorize("hasRole('ROLE_EMPLOYEE_VIEW')&&hasRole('ROLE_EMPLOYEE_ACCESS')")
	@PostMapping("/search")
	public ResponseDTO<List<EmployeeDTO>> search(@RequestBody @Valid SearchDTO searchDTO) {
		return employeeService.search(searchDTO);
	}
	@PreAuthorize("hasRole('ROLE_EMPLOYEE_VIEW')&&hasRole('ROLE_EMPLOYEE_ACCESS')")
	@GetMapping("/employeeRelationship")
	public ResponseDTO<Map<String, List<EmployeeRelationshipResponse>>> getEmployeeRelationship() {
		return ResponseDTO.<Map<String, List<EmployeeRelationshipResponse>>>builder()
				.code(String.valueOf(HttpStatus.OK.value())).data(employeeService.getEmployeeRelationship()).build();
	}

//	@GetMapping("/test")
//	public ResponseDTO<List<EmployeeDTO>> test() {
//		return ResponseDTO.<List<EmployeeDTO>>builder().code(String.valueOf(HttpStatus.OK.value()))
//				.data(employeeService.test()).build();
//	}

//	@PreAuthorize("hasRole('ROLE_EMPLOYEE_ALLRELATIONSHIP')")
//	@GetMapping("/allRelationship")
//	public ResponseDTO<List<EmployeeRelationshipResponse>> getAllRelationshipByRule() {
//		return ResponseDTO.<List<EmployeeRelationshipResponse>>builder().code(String.valueOf(HttpStatus.OK.value()))
//				.data(employeeService.getAllRelationshipByRule()).build();
//	}

	@PreAuthorize("hasRole('ROLE_EMPLOYEE_VIEW')&&hasRole('ROLE_EMPLOYEE_ACCESS')")
	@GetMapping("/findEmployeeToExportExcel")
	public ResponseDTO<Map<String, List<EmployeeDTO>>> findEmployeeToExportExcel() {
		return ResponseDTO.<Map<String, List<EmployeeDTO>>>builder().code(String.valueOf(HttpStatus.OK.value()))
				.data(employeeService.findEmployeeToExportExcel()).build();
	}

	@PreAuthorize("hasRole('ROLE_EMPLOYEE_UPDATE')&&hasRole('ROLE_EMPLOYEE_ACCESS')")
	@PostMapping("/resetDependence/ids")
	public ResponseDTO<List<EmployeeDTO>> resetDependence(@RequestBody @Valid List<String> ids)
			throws URISyntaxException {

		if (ids.isEmpty()) {
			throw new BadRequestAlertException("Bad request: missing ", ENTITY_NAME, "missing");
		}

		return ResponseDTO.<List<EmployeeDTO>>builder().code(String.valueOf(HttpStatus.OK.value()))
				.data(employeeService.resetDependence(ids)).build();
	}

	@PreAuthorize("hasRole('ROLE_EMPLOYEE_UPDATE')&&hasRole('ROLE_EMPLOYEE_ACCESS')")
	@GetMapping("/hierarchy/{id}")
	public Employee getEmployeeHierarchy(@PathVariable String id) {
		return employeeService.getEmployeeHierarchyFrom(id);
	}
	
	@PreAuthorize("hasRole('ROLE_EMPLOYEE_VIEW')&&hasRole('ROLE_EMPLOYEE_ACCESS')")
	@GetMapping("/getAll")
    public List<EmployeeDTO> getAll() {
        return employeeService.getAll();
    }
	
	@PreAuthorize("hasRole('ROLE_EMPLOYEE_UPDATE')&&hasRole('ROLE_EMPLOYEE_ACCESS')")
	@GetMapping("/filterbystaffid")
    public List<EmployeeDTO> getByStaffId() {
        return employeeService.filterStaffId();
    }
	
	@PreAuthorize("hasRole('ROLE_EMPLOYEE_ACCESS')")
	@GetMapping("/employeeInfo")
	public ResponseDTO<EmployeeDTO> getEmployee(@CurrentUser UserPrincipal currentUser) {
		if (currentUser == null) {
			throw new BadRequestAlertException("Bad request: missing id", ENTITY_NAME, "missing_id");
		}
		return ResponseDTO.<EmployeeDTO>builder().code(String.valueOf(HttpStatus.OK.value()))
				.data(employeeService.getEmployeeFromUser(currentUser.getUser_id())).build();
	}
	
	@PreAuthorize("hasRole('ROLE_EMPLOYEE_UPDATE')&&hasRole('ROLE_EMPLOYEE_ACCESS')")
	@GetMapping("/filterLevel")
	public ResponseDTO<Void> filter() {
		employeeService.filterLevel();
		return ResponseDTO.<Void>builder().code(String.valueOf(HttpStatus.OK.value()))
				.build();
	}
	
	
	@PreAuthorize("hasRole('ROLE_EMPLOYEE_UPDATE')&&hasRole('ROLE_EMPLOYEE_ACCESS')")
	@GetMapping("/filterDE")
	public ResponseDTO<Void> filterDe() {
		employeeService.filterEmployeeDepend();
		return ResponseDTO.<Void>builder().code(String.valueOf(HttpStatus.OK.value()))
				.build();
	}
	
	
	@PostMapping("/getDe")
    public ResponseDTO<List<EmployeeDependence>> showDE(@RequestBody @Valid Employee employee) {
        return ResponseDTO.<List<EmployeeDependence>>builder().code(String.valueOf(HttpStatus.OK.value())).data(employeeService.getDE(employee.getEmployeeId()))
				.build();
    }
	
	
	@PreAuthorize("hasRole('ROLE_EMPLOYEE_UPDATE')&&hasRole('ROLE_EMPLOYEE_ACCESS')")
	@PutMapping("/updateStaff")
	public ResponseDTO<EmployeeDTO> updateStaff(@RequestBody @Valid EmployeeDTO employeeDTO) throws IOException {
		return ResponseDTO.<EmployeeDTO>builder().code(String.valueOf(HttpStatus.OK.value())).data(employeeService.updateStaff(employeeDTO)).build();
	}
	
	

}
