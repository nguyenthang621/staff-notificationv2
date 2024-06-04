package com.istt.staff_notification_v2.apis;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.istt.staff_notification_v2.apis.errors.BadRequestAlertException;
import com.istt.staff_notification_v2.dto.ResponseDTO;
import com.istt.staff_notification_v2.dto.SearchDTO;
import com.istt.staff_notification_v2.dto.UserDTO;
import com.istt.staff_notification_v2.dto.UserResponse;
import com.istt.staff_notification_v2.service.UserService;

@RestController
@RequestMapping("/user")
public class UserAPI {

	@Autowired
	private UserService userService;

	private static final String ENTITY_NAME = "isttUser";

	@PostMapping("")
	public ResponseDTO<UserDTO> create(@RequestBody UserDTO userDTO) throws URISyntaxException {
		System.out.println("=========================username: " + userDTO.getUsername());
		if (userDTO.getUsername() == null || userDTO.getPassword() == null || userDTO.getRoles() == null) {
			throw new BadRequestAlertException("Bad request: missing data", ENTITY_NAME, "missing");
		}
		userService.create(userDTO);
		return ResponseDTO.<UserDTO>builder().code(String.valueOf(HttpStatus.OK.value())).data(userDTO).build();

	}

	@GetMapping("/{id}")
	// @PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseDTO<UserResponse> get(@PathVariable(value = "id") String id) {
		return ResponseDTO.<UserResponse>builder().code(String.valueOf(HttpStatus.OK.value())).data(userService.get(id))
				.build();
	}

	@PutMapping("/update-password")
	public ResponseDTO<Void> updatePassword(@ModelAttribute @Valid UserDTO userDTO) throws IOException {
		userService.updatePassword(userDTO);
		return ResponseDTO.<Void>builder().code(String.valueOf(HttpStatus.OK.value())).build();
	}

	@PostMapping("/search")
//	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseDTO<List<UserResponse>> search(@RequestBody @Valid SearchDTO searchDTO) {
		return ResponseDTO.<List<UserResponse>>builder().code(String.valueOf(HttpStatus.OK.value()))
				.data(userService.search(searchDTO)).build();
//		return userService.search(searchDTO);
	}

}
