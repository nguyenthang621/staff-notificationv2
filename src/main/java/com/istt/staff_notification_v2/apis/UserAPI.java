package com.istt.staff_notification_v2.apis;

import java.net.URISyntaxException;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.istt.staff_notification_v2.apis.errors.BadRequestAlertException;
import com.istt.staff_notification_v2.dto.ResponseDTO;
import com.istt.staff_notification_v2.dto.UserDTO;
import com.istt.staff_notification_v2.service.UserService;

@RestController
@RequestMapping("/user")
public class UserAPI {

	@Autowired
	private UserService userService;

	private static final String ENTITY_NAME = "isttUser";

	@PostMapping("")
	public ResponseDTO<UserDTO> create(@RequestBody @Valid UserDTO userDTO) throws URISyntaxException {
		System.out.println("username: " + userDTO.getUsername());
		if (userDTO.getUsername() == null || userDTO.getPassword() == null || userDTO.getRoles() == null) {
			throw new BadRequestAlertException("Bad request: missing data", ENTITY_NAME, "missing");
		}
		userService.create(userDTO);
		return ResponseDTO.<UserDTO>builder().code(String.valueOf(HttpStatus.OK.value())).data(userDTO).build();
	}

}
