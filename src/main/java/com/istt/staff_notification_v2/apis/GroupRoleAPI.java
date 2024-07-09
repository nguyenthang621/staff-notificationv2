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
import com.istt.staff_notification_v2.dto.EmployeeDTO;
import com.istt.staff_notification_v2.dto.GroupRoleDTO;
import com.istt.staff_notification_v2.dto.LevelDTO;
import com.istt.staff_notification_v2.dto.ResponseDTO;
import com.istt.staff_notification_v2.service.GroupRoleService;
import com.istt.staff_notification_v2.service.LeaveTypeService;

@RestController
@RequestMapping("/groupRole")
public class GroupRoleAPI {
	
	@Autowired
	private GroupRoleService groupRoleService;

	private static final String ENTITY_NAME = "isttGroupRole";
	
	@PreAuthorize("hasRole('ROLE_GROUPROLE_GETALL')")
	@GetMapping("/getAll")
    public List<GroupRoleDTO> getAll() {
        return groupRoleService.getAll();
    }
	
	@PreAuthorize("hasRole('ROLE_GROUPROLE_CREATE')")
	@PostMapping("")
	public ResponseDTO<GroupRoleDTO> create(@RequestBody GroupRoleDTO groupRoleDTO) throws URISyntaxException {
		if (groupRoleDTO.getGroupName()==null) {
			throw new BadRequestAlertException("Bad request: missing data", ENTITY_NAME, "missing_level");
		}
		groupRoleService.create(groupRoleDTO);
		return ResponseDTO.<GroupRoleDTO>builder().code(String.valueOf(HttpStatus.OK.value())).data(groupRoleDTO).build();
	}
	
	@PreAuthorize("hasRole('ROLE_GROUPROLE_DELETE')")
	@DeleteMapping("/{id}")
	public ResponseDTO<Void> delete(@PathVariable(value = "id") String id) throws URISyntaxException {
		if (id == null) {
			throw new BadRequestAlertException("Bad request: missing id", ENTITY_NAME, "missing_id");
		}
		groupRoleService.delete(id);
		return ResponseDTO.<Void>builder().code(String.valueOf(HttpStatus.OK.value())).build();
	}
	
	@PreAuthorize("hasRole('ROLE_GROUPROLE_GET')")
	@GetMapping("/{id}")
	public ResponseDTO<GroupRoleDTO> get(@PathVariable(value = "id") String id) {
		if (id == null) {
			throw new BadRequestAlertException("Bad request: missing id", ENTITY_NAME, "missing_id");
		}
		return ResponseDTO.<GroupRoleDTO>builder().code(String.valueOf(HttpStatus.OK.value()))
				.data(groupRoleService.get(id)).build();
	}

	
	@PutMapping("/")
	@PreAuthorize("hasRole('ROLE_GROUPROLE_UPDATE')")
	public ResponseDTO<GroupRoleDTO> update(@RequestBody @Valid GroupRoleDTO groupRoleDTO ) throws IOException {
		groupRoleService.update(groupRoleDTO);
		return ResponseDTO.<GroupRoleDTO>builder().code(String.valueOf(HttpStatus.OK.value())).data(groupRoleDTO).build();

	}
}
