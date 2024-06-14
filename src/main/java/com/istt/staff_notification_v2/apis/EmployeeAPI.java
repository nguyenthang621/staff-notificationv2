package com.istt.staff_notification_v2.apis;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import com.istt.staff_notification_v2.service.EmployeeService;

@RestController
@RequestMapping("/employee")
public class EmployeeAPI {
	@Autowired
	private EmployeeService employeeService;

	private static final String ENTITY_NAME = "isttEmployee";

	@PostMapping("")
	public ResponseDTO<EmployeeDTO> create(@RequestBody @Valid EmployeeDTO employeeDTO) throws URISyntaxException {
		if (employeeDTO.getEmail() == null || employeeDTO.getFullname() == null
				|| employeeDTO.getDepartment() == null) {
			throw new BadRequestAlertException("Bad request: missing data", ENTITY_NAME, "missing");
		}

		employeeService.create(employeeDTO);
		return ResponseDTO.<EmployeeDTO>builder().code(String.valueOf(HttpStatus.OK.value())).data(employeeDTO).build();
	}

	@DeleteMapping("/{id}")
	public ResponseDTO<Void> delete(@PathVariable(value = "id") String id) throws URISyntaxException {
		if (id == null) {
			throw new BadRequestAlertException("Bad request: missing id", ENTITY_NAME, "missing_id");
		}
		employeeService.delete(id);
		return ResponseDTO.<Void>builder().code(String.valueOf(HttpStatus.OK.value())).build();
	}

	@GetMapping("/{id}")
	// @PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseDTO<EmployeeDTO> get(@PathVariable(value = "id") String id) {
		if (id == null) {
			throw new BadRequestAlertException("Bad request: missing id", ENTITY_NAME, "missing_id");
		}
		return ResponseDTO.<EmployeeDTO>builder().code(String.valueOf(HttpStatus.OK.value()))
				.data(employeeService.get(id)).build();
	}

	@GetMapping("/dependence/{id}")
	public ResponseDTO<List<EmployeeDTO>> getEmployeeDependence(@PathVariable(value = "id") String id) {
		if (id == null) {
			throw new BadRequestAlertException("Bad request: missing id", ENTITY_NAME, "missing_id");
		}
		return ResponseDTO.<List<EmployeeDTO>>builder().code(String.valueOf(HttpStatus.OK.value()))
				.data(employeeService.getEmployeeDependence(id)).build();
	}

	@PutMapping("/")
//  @PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseDTO<EmployeeDTO> update(@RequestBody @Valid EmployeeDTO employeeDTO) throws IOException {
		employeeService.update(employeeDTO);
		return ResponseDTO.<EmployeeDTO>builder().code(String.valueOf(HttpStatus.OK.value())).data(employeeDTO).build();

	}

	@PostMapping("/search")
//	 @PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseDTO<List<EmployeeDTO>> search(@RequestBody @Valid SearchDTO searchDTO) {
		return employeeService.search(searchDTO);
	}

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

	@GetMapping("/allRelationship")
	public ResponseDTO<List<EmployeeRelationshipResponse>> getAllRelationshipByRule() {
		return ResponseDTO.<List<EmployeeRelationshipResponse>>builder().code(String.valueOf(HttpStatus.OK.value()))
				.data(employeeService.getAllRelationshipByRule()).build();
	}

	@GetMapping("/findEmployeeToExportExcel")
	public ResponseDTO<Map<String, List<EmployeeDTO>>> findEmployeeToExportExcel() {
		return ResponseDTO.<Map<String, List<EmployeeDTO>>>builder().code(String.valueOf(HttpStatus.OK.value()))
				.data(employeeService.findEmployeeToExportExcel()).build();
	}
}
