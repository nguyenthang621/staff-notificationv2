package com.istt.staff_notification_v2.apis;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

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
import com.istt.staff_notification_v2.dto.ResponseDTO;
import com.istt.staff_notification_v2.dto.RoleDTO;
import com.istt.staff_notification_v2.dto.SearchDTO;
import com.istt.staff_notification_v2.security.securityv2.CurrentUser;
import com.istt.staff_notification_v2.security.securityv2.UserPrincipal;
import com.istt.staff_notification_v2.service.RoleService;

@RestController
@RequestMapping("/role")
public class RoleAPI {
	@Autowired
	private RoleService roleService;

	private static final String ENTITY_NAME = "isttRole";

	@PostMapping("")
//	@PreAuthorize("hasAuthority('ADMIN') and hasAuthority('USER')")
	public ResponseDTO<RoleDTO> create(@RequestBody RoleDTO roleDTO) throws URISyntaxException {

		if (roleDTO.getRole() == null) {
			throw new BadRequestAlertException("Bad request: missing data", ENTITY_NAME, "missing_role");
		}
		roleService.create(roleDTO);
		return ResponseDTO.<RoleDTO>builder().code(String.valueOf(HttpStatus.OK.value())).data(roleDTO).build();
	}

	@DeleteMapping("/{id}")
	public ResponseDTO<Void> delete(@CurrentUser UserPrincipal currentUser, @PathVariable(value = "id") String id)
			throws URISyntaxException {
		if (id == null) {
			throw new BadRequestAlertException("Bad request: missing data", ENTITY_NAME, "missing_id");
		}
		roleService.delete(id);
		return ResponseDTO.<Void>builder().code(String.valueOf(HttpStatus.OK.value())).build();
	}

	@PostMapping("/search")
	public ResponseDTO<List<RoleDTO>> search(@RequestBody @Valid SearchDTO searchDTO) {
		return roleService.search(searchDTO);
	}

	@DeleteMapping("/ids")
	public ResponseDTO<List<String>> deletebyListId(@RequestBody @Valid List<String> ids) throws URISyntaxException {

		if (ids.isEmpty()) {
			throw new BadRequestAlertException("Bad request: missing roles", ENTITY_NAME, "missing_roles");
		}
		roleService.deleteAllbyIds(ids);
		return ResponseDTO.<List<String>>builder().code(String.valueOf(HttpStatus.OK.value())).data(ids).build();
	}

	@PutMapping("/")
	public ResponseDTO<RoleDTO> update(@RequestBody @Valid RoleDTO roleDTO) throws IOException {
		roleService.update(roleDTO);
		return ResponseDTO.<RoleDTO>builder().code(String.valueOf(HttpStatus.OK.value())).data(roleDTO).build();

	}

	@GetMapping("/all")
	public ResponseDTO<List<RoleDTO>> getAll() throws IOException {
		return ResponseDTO.<List<RoleDTO>>builder().code(String.valueOf(HttpStatus.OK.value()))
				.data(roleService.getAll()).build();
	}

}
