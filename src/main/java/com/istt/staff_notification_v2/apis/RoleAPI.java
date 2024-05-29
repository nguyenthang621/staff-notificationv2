package com.istt.staff_notification_v2.apis;

import java.net.URISyntaxException;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.istt.staff_notification_v2.apis.errors.BadRequestAlertException;
import com.istt.staff_notification_v2.dto.ResponseDTO;
import com.istt.staff_notification_v2.dto.RoleDTO;
import com.istt.staff_notification_v2.service.RoleService;

@RestController
@RequestMapping("/role")
public class RoleAPI {
	@Autowired
	private RoleService roleService;

	private static final String ENTITY_NAME = "isttRole";

	@PostMapping("")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseDTO<RoleDTO> create(@RequestBody @Valid RoleDTO roleDTO) throws URISyntaxException {
		if (roleDTO.getRole() == null) {
			throw new BadRequestAlertException("Bad request: missing data", ENTITY_NAME, "missing_role");
		}
		roleService.create(roleDTO);
		return ResponseDTO.<RoleDTO>builder().code(String.valueOf(HttpStatus.OK.value())).data(roleDTO).build();
	}

	@DeleteMapping("/{id}")
	public ResponseDTO<Void> delete(@PathVariable(value = "id") String id) {
		if (id == null) {
			throw new BadRequestAlertException("Bad request: missing data", ENTITY_NAME, "missing_id");
		}
		roleService.delete(id);
		return ResponseDTO.<Void>builder().code(String.valueOf(HttpStatus.OK.value())).build();
	}
}
