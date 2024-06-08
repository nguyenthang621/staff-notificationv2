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
import com.istt.staff_notification_v2.dto.LeaveTypeDTO;
import com.istt.staff_notification_v2.dto.ResponseDTO;
import com.istt.staff_notification_v2.dto.SearchDTO;
import com.istt.staff_notification_v2.security.securityv2.CurrentUser;
import com.istt.staff_notification_v2.security.securityv2.UserPrincipal;
import com.istt.staff_notification_v2.service.LeaveTypeService;

@RestController
@RequestMapping("/leaveType")
public class LeaveTypeAPI {

	@Autowired
	private LeaveTypeService leaveTypeService;

	private static final String ENTITY_NAME = "isttLeaveType";

	@PostMapping("")
//	@PreAuthorize("hasAuthority('ADMIN') and hasAuthority('USER')")
	public ResponseDTO<LeaveTypeDTO> create(@RequestBody LeaveTypeDTO leaveTypeDTO) throws URISyntaxException {
		if (leaveTypeDTO.getLeavetypeName() == null) {
			throw new BadRequestAlertException("Bad request: missing data", ENTITY_NAME, "missing_leaveType");
		}
		leaveTypeService.create(leaveTypeDTO);
		return ResponseDTO.<LeaveTypeDTO>builder().code(String.valueOf(HttpStatus.OK.value())).data(leaveTypeDTO)
				.build();
	}

	@DeleteMapping("/{id}")
	public ResponseDTO<Void> delete(@CurrentUser UserPrincipal currentUser, @PathVariable(value = "id") String id)
			throws URISyntaxException {
		if (id == null) {
			throw new BadRequestAlertException("Bad request: missing data", ENTITY_NAME, "missing_id");
		}
		leaveTypeService.delete(id);
		return ResponseDTO.<Void>builder().code(String.valueOf(HttpStatus.OK.value())).build();
	}

	@PostMapping("/search")
	public ResponseDTO<List<LeaveTypeDTO>> search(@RequestBody @Valid SearchDTO searchDTO) {
		return leaveTypeService.search(searchDTO);
	}

	@DeleteMapping("/ids")
	public ResponseDTO<List<String>> deletebyListId(@RequestBody @Valid List<String> ids) throws URISyntaxException {

		if (ids.isEmpty()) {
			throw new BadRequestAlertException("Bad request: missing leaveTypes", ENTITY_NAME, "missing_leaveTypes");
		}
		leaveTypeService.deleteAllbyIds(ids);
		return ResponseDTO.<List<String>>builder().code(String.valueOf(HttpStatus.OK.value())).data(ids).build();
	}

	@PutMapping("/")
	public ResponseDTO<LeaveTypeDTO> update(@RequestBody @Valid LeaveTypeDTO leaveTypeDTO) throws IOException {
		leaveTypeService.update(leaveTypeDTO);
		return ResponseDTO.<LeaveTypeDTO>builder().code(String.valueOf(HttpStatus.OK.value())).data(leaveTypeDTO)
				.build();

	}

	@GetMapping("/all")
	public ResponseDTO<List<LeaveTypeDTO>> getAll() throws IOException {
		return ResponseDTO.<List<LeaveTypeDTO>>builder().code(String.valueOf(HttpStatus.OK.value()))
				.data(leaveTypeService.getAll()).build();
	}
}
