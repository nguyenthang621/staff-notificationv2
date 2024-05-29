package com.istt.staff_notification_v2.apis;

import java.net.URISyntaxException;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.istt.staff_notification_v2.apis.errors.BadRequestAlertException;
import com.istt.staff_notification_v2.dto.DepartmentDTO;
import com.istt.staff_notification_v2.dto.ResponseDTO;
import com.istt.staff_notification_v2.service.DepartmentService;

@RestController
@RequestMapping("/department")
public class DepartmentAPI {
	@Autowired
	private DepartmentService departmentService;

	private static final String ENTITY_NAME = "isttDepartment";

	@PostMapping("")
	public ResponseDTO<DepartmentDTO> create(@RequestBody @Valid DepartmentDTO departmentDTO)
			throws URISyntaxException {
		if (departmentDTO.getDepartment_name() == null) {
			throw new BadRequestAlertException("Bad request: missing data", ENTITY_NAME, "missin_name");
		}
		departmentService.create(departmentDTO);
		return ResponseDTO.<DepartmentDTO>builder().code(String.valueOf(HttpStatus.OK.value())).data(departmentDTO)
				.build();
	}

	@DeleteMapping("/{id}")
	public ResponseDTO<Void> delete(@PathVariable(value = "id") String id) throws URISyntaxException {
		if (id == null) {
			throw new BadRequestAlertException("Bad request: missing id", ENTITY_NAME, "missing_id");
		}
		departmentService.delete(id);
		return ResponseDTO.<Void>builder().code(String.valueOf(HttpStatus.OK.value())).build();
	}

}