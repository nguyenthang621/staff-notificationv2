package com.istt.staff_notification_v2.apis;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

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
import com.istt.staff_notification_v2.dto.EmployeeRelationshipResponse;
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

//	@PreAuthorize("hasRole('ROLE_EMPLOYEE_CREATE')")
	@PostMapping("")
	public ResponseDTO<EmployeeDTO> create(@RequestBody @Valid EmployeeDTO employeeDTO) throws URISyntaxException {
		if (employeeDTO.getEmail() == null || employeeDTO.getFullname() == null
				|| employeeDTO.getDepartment() == null) {
			throw new BadRequestAlertException("Bad request: missing data", ENTITY_NAME, "missing");
		}

		employeeService.create(employeeDTO);
		return ResponseDTO.<EmployeeDTO>builder().code(String.valueOf(HttpStatus.OK.value())).data(employeeDTO).build();
	}

//	@PreAuthorize("hasRole('ROLE_EMPLOYEE_DELETE')")
	@DeleteMapping("/{id}")
	public ResponseDTO<Void> delete(@PathVariable(value = "id") String id) throws URISyntaxException {
		if (id == null) {
			throw new BadRequestAlertException("Bad request: missing id", ENTITY_NAME, "missing_id");
		}
		employeeService.delete(id);
		return ResponseDTO.<Void>builder().code(String.valueOf(HttpStatus.OK.value())).build();
	}

//	@PreAuthorize("hasRole('ROLE_EMPLOYEE_GET')")
	@GetMapping("/{id}")
	// @PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseDTO<EmployeeDTO> get(@PathVariable(value = "id") String id) {
		if (id == null) {
			throw new BadRequestAlertException("Bad request: missing id", ENTITY_NAME, "missing_id");
		}
		return ResponseDTO.<EmployeeDTO>builder().code(String.valueOf(HttpStatus.OK.value()))
				.data(employeeService.get(id)).build();
	}
	
//	@PreAuthorize("hasRole('ROLE_EMPLOYEE_DEPENDENCE')")
	@GetMapping("/dependence/{id}")
	public ResponseDTO<List<EmployeeDTO>> getEmployeeDependence(@PathVariable(value = "id") String id) {
		if (id == null) {
			throw new BadRequestAlertException("Bad request: missing id", ENTITY_NAME, "missing_id");
		}
		return ResponseDTO.<List<EmployeeDTO>>builder().code(String.valueOf(HttpStatus.OK.value()))
				.data(employeeService.getEmployeeDependence(id)).build();
	}

	@PutMapping("/")
//	@PreAuthorize("hasRole('ROLE_EMPLOYEE_UPDATE')")
	public ResponseDTO<EmployeeDTO> update(@RequestBody @Valid EmployeeDTO employeeDTO) throws IOException {
		employeeService.update(employeeDTO);
		return ResponseDTO.<EmployeeDTO>builder().code(String.valueOf(HttpStatus.OK.value())).data(employeeDTO).build();

	}

//	@PreAuthorize("hasRole('ROLE_EMPLOYEE_SEARCH')")
	@PostMapping("/search")
//	 @PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseDTO<List<EmployeeDTO>> search(@RequestBody @Valid SearchDTO searchDTO) {
		return employeeService.search(searchDTO);
	}
//	@PreAuthorize("hasRole('ROLE_EMPLOYEE_RELATIONSHIP')")
	@GetMapping("/employeeRelationship")
	public ResponseDTO<Map<String, List<EmployeeRelationshipResponse>>> getEmployeeRelationship() {
		return ResponseDTO.<Map<String, List<EmployeeRelationshipResponse>>>builder()
				.code(String.valueOf(HttpStatus.OK.value())).data(employeeService.getEmployeeRelationship()).build();
	}

	@GetMapping("/test")
	public ResponseDTO<List<EmployeeDTO>> test() {
		return ResponseDTO.<List<EmployeeDTO>>builder().code(String.valueOf(HttpStatus.OK.value()))
				.data(employeeService.test()).build();
	}

//	@PreAuthorize("hasRole('ROLE_EMPLOYEE_ALLRELATIONSHIP')")
	@GetMapping("/allRelationship")
	public ResponseDTO<List<EmployeeRelationshipResponse>> getAllRelationshipByRule() {
		return ResponseDTO.<List<EmployeeRelationshipResponse>>builder().code(String.valueOf(HttpStatus.OK.value()))
				.data(employeeService.getAllRelationshipByRule()).build();
	}

//	@PreAuthorize("hasRole('ROLE_EMPLOYEE_FINDEMPLOYEETOEXPORTEXCEL')")
	@GetMapping("/findEmployeeToExportExcel")
	public ResponseDTO<Map<String, List<EmployeeDTO>>> findEmployeeToExportExcel() {
		return ResponseDTO.<Map<String, List<EmployeeDTO>>>builder().code(String.valueOf(HttpStatus.OK.value()))
				.data(employeeService.findEmployeeToExportExcel()).build();
	}

//	@PreAuthorize("hasRole('ROLE_EMPLOYEE_RESETDEPENDENCE')")
	@PostMapping("/resetDependence/ids")
	public ResponseDTO<List<EmployeeDTO>> resetDependence(@RequestBody @Valid List<String> ids)
			throws URISyntaxException {

		if (ids.isEmpty()) {
			throw new BadRequestAlertException("Bad request: missing ", ENTITY_NAME, "missing");
		}

		return ResponseDTO.<List<EmployeeDTO>>builder().code(String.valueOf(HttpStatus.OK.value()))
				.data(employeeService.resetDependence(ids)).build();
	}

//	@PreAuthorize("hasRole('ROLE_EMPLOYEE_HIERARCHY')")
	@GetMapping("/hierarchy/{id}")
	public Employee getEmployeeHierarchy(@PathVariable String id) {
		return employeeService.getEmployeeHierarchyFrom(id);
	}
	
//	@PreAuthorize("hasRole('ROLE_EMPLOYEE_GETALL')")
	@GetMapping("/getAll")
    public List<EmployeeDTO> getAll() {
        return employeeService.getAll();
    }
	
//	@PreAuthorize("hasRole('ROLE_EMPLOYEE_FILTERBYSTAFFID')")
	@GetMapping("/filterbystaffid")
    public List<EmployeeDTO> getByStaffId() {
        return employeeService.filterStaffId();
    }
	
//	@PreAuthorize("hasRole('ROLE_EMPLOYEE_EMPLOYEEINFO')")
	@GetMapping("/employeeInfo")
	public ResponseDTO<EmployeeDTO> getEmployee(@CurrentUser UserPrincipal currentUser) {
		if (currentUser == null) {
			throw new BadRequestAlertException("Bad request: missing id", ENTITY_NAME, "missing_id");
		}
		return ResponseDTO.<EmployeeDTO>builder().code(String.valueOf(HttpStatus.OK.value()))
				.data(employeeService.getEmployeeFromUser(currentUser.getUser_id())).build();
	}

}
