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
import com.istt.staff_notification_v2.dto.GroupDTO;
import com.istt.staff_notification_v2.dto.GroupUserDTO;
import com.istt.staff_notification_v2.dto.LevelDTO;
import com.istt.staff_notification_v2.dto.ResponseDTO;
import com.istt.staff_notification_v2.dto.ResponseGroupDTO;
import com.istt.staff_notification_v2.entity.Group;
import com.istt.staff_notification_v2.entity.User;
import com.istt.staff_notification_v2.security.securityv2.CurrentUser;
import com.istt.staff_notification_v2.security.securityv2.UserPrincipal;
import com.istt.staff_notification_v2.service.GroupService;
import com.istt.staff_notification_v2.service.LeaveTypeService;

@RestController
@RequestMapping("/groupRole")
public class GroupAPI {
	
	@Autowired
	private GroupService groupService;

	private static final String ENTITY_NAME = "isttGroupRole";
	
	@GetMapping("/{id}")
	public ResponseGroupDTO get(@PathVariable(value = "id") String id) {
		return groupService.getGroup(id);
	}
	
	@GetMapping("getUser/{id}")
	public GroupUserDTO getUser(@PathVariable(value = "id") String id) {
		return groupService.getUser(id);
	}
	@GetMapping("/getAll")
	public List<GroupDTO> getAll() {
		return groupService.getAll();
	}
	@PostMapping
	public ResponseGroupDTO addRole(@RequestBody @Valid ResponseGroupDTO responseGroupDTO ) {
		return groupService.addRoleToGroup(responseGroupDTO);
	}
	
	@PostMapping("/addUser")
	public GroupUserDTO addUser(@RequestBody @Valid GroupUserDTO responseGroupDTO ) {
		return groupService.addUserToGroup(responseGroupDTO);
	}
	
	@PostMapping("/addroletoadmin/{username}")
	public ResponseDTO<Void> addRoleToAdmin(@PathVariable(value = "username") String username) throws URISyntaxException {
		groupService.addAllRole(username);
		return ResponseDTO.<Void>builder().code(String.valueOf(HttpStatus.OK.value())).build();
	}
	
	@PostMapping("/create")
	public ResponseDTO<GroupDTO> create(@RequestBody @Valid GroupDTO groupDTO)
			throws URISyntaxException {

		groupService.create(groupDTO);
		return ResponseDTO.<GroupDTO>builder().code(String.valueOf(HttpStatus.OK.value())).data(groupDTO)
				.build();
	}
	@DeleteMapping("/{id}")
	public ResponseDTO<Void> delete(@PathVariable(value = "id") String id) throws URISyntaxException {
		groupService.delete(id);
		return ResponseDTO.<Void>builder().code(String.valueOf(HttpStatus.OK.value())).build();
	}
	
	@GetMapping("/getGroup")
	public ResponseDTO<GroupDTO> getGroup(@CurrentUser UserPrincipal currentuser)
			throws URISyntaxException {
		if(currentuser == null) throw new BadRequestAlertException("missing data", ENTITY_NAME, "missingdata");
		return ResponseDTO.<GroupDTO>builder().code(String.valueOf(HttpStatus.OK.value())).data(groupService.getMinGroupFromUser(currentuser.getUser_id()))
				.build();
	}
}
