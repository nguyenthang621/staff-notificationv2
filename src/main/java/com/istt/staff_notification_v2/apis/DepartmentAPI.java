package com.istt.staff_notification_v2.apis;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

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
import com.istt.staff_notification_v2.dto.DepartmentDTO;
import com.istt.staff_notification_v2.dto.ResponseDTO;
import com.istt.staff_notification_v2.dto.SearchDTO;
import com.istt.staff_notification_v2.service.DepartmentService;

@RestController
@RequestMapping("/department")
public class DepartmentAPI {
	@Autowired
	private DepartmentService departmentService;

	private static final String ENTITY_NAME = "isttDepartment";

	@PreAuthorize("hasRole('ROLE_DEPARTMENT_CREATE')")
	@PostMapping("")
	public ResponseDTO<DepartmentDTO> create(@RequestBody @Valid DepartmentDTO departmentDTO)
			throws URISyntaxException {
		if (departmentDTO.getDepartmentName() == null) {
			throw new BadRequestAlertException("Bad request: missing data", ENTITY_NAME, "missin_name");
		}
		departmentService.create(departmentDTO);
		return ResponseDTO.<DepartmentDTO>builder().code(String.valueOf(HttpStatus.OK.value())).data(departmentDTO)
				.build();
	}
	@PreAuthorize("hasRole('ROLE_DEPARTMENT_DELETE')")
	@DeleteMapping("/{id}")
	public ResponseDTO<Void> delete(@PathVariable(value = "id") String id) throws URISyntaxException {
		if (id == null) {
			throw new BadRequestAlertException("Bad request: missing id", ENTITY_NAME, "missing_id");
		}
		departmentService.delete(id);
		return ResponseDTO.<Void>builder().code(String.valueOf(HttpStatus.OK.value())).build();
	}

	@PreAuthorize("hasRole('ROLE_DEPARTMENT_SEARCH')")
	@PostMapping("/search")
	public ResponseDTO<List<DepartmentDTO>> search(@RequestBody @Valid SearchDTO searchDTO) {
		return departmentService.search(searchDTO);
	}

	@PreAuthorize("hasRole('ROLE_DEPARTMENT_DELETE')")
	@DeleteMapping("/ids")
	public ResponseDTO<List<String>> deletebyListId(@RequestBody @Valid List<String> ids) throws URISyntaxException {

		if (ids.isEmpty()) {
			throw new BadRequestAlertException("Bad request: missing departments", ENTITY_NAME, "missing_departments");
		}
		departmentService.deleteAllbyIds(ids);
		return ResponseDTO.<List<String>>builder().code(String.valueOf(HttpStatus.OK.value())).data(ids).build();
	}

	@PreAuthorize("hasRole('ROLE_DEPARTMENT_UPDATE')")
	@PutMapping("/")
	public ResponseDTO<DepartmentDTO> update(@RequestBody @Valid DepartmentDTO departmentDTO) throws IOException {
		departmentService.update(departmentDTO);
		return ResponseDTO.<DepartmentDTO>builder().code(String.valueOf(HttpStatus.OK.value())).data(departmentDTO)
				.build();

	}

	@PreAuthorize("hasRole('ROLE_DEPARTMENT_GETALL')")
	@GetMapping("/all")
	public ResponseDTO<List<DepartmentDTO>> getAll() throws IOException {
		return ResponseDTO.<List<DepartmentDTO>>builder().code(String.valueOf(HttpStatus.OK.value()))
				.data(departmentService.getAll()).build();
	}

}