package com.istt.staff_notification_v2.apis;

import java.util.Optional;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.istt.staff_notification_v2.apis.errors.BadRequestAlertException;
import com.istt.staff_notification_v2.dto.LoginRequest;
import com.istt.staff_notification_v2.dto.ResponseDTO;
import com.istt.staff_notification_v2.dto.UserDTO;
import com.istt.staff_notification_v2.entity.User;
import com.istt.staff_notification_v2.repository.UserRepo;
import com.istt.staff_notification_v2.service.AuthService;
import com.istt.staff_notification_v2.service.UserService;

@RestController
@RequestMapping("/auth")
public class AuthAPI {
	@Autowired
	UserRepo userRepo;

	@Autowired
	UserService userService;

	@Autowired
	AuthService authService;

	private static final String ENTITY_NAME = "isttAuth";

	@PostMapping("/signin")
	public ResponseDTO<String> signin(@Valid @RequestBody LoginRequest loginRequest) {

		Optional<User> userOptional = userRepo.findByUsername(loginRequest.getUsername());
		if (userOptional.isEmpty()) {
			throw new BadRequestAlertException("Bad request: User not found.", ENTITY_NAME, "Not Found");
		}

		User user = userOptional.get();

		Boolean compare_password = BCrypt.checkpw(loginRequest.getPassword(), user.getPassword());

		if (!compare_password)
			throw new BadRequestAlertException("Bad request: Password wrong !!!", ENTITY_NAME, "Password wrong");

		return authService.signin(loginRequest, user);

	}

	@PostMapping("/signup")
	public ResponseDTO<String> signup(@Valid @RequestBody UserDTO userSignUp) {
		Optional<User> userOptional = userRepo.findByUsername(userSignUp.getUsername());
		User user = userOptional.get();

		if (user != null)
			throw new BadRequestAlertException("user " + user.getUsername() + " already exists", ENTITY_NAME,
					"Password wrong");

		UserDTO userDTO = new ModelMapper().map(user, UserDTO.class);
		userService.create(userDTO);

		LoginRequest loginRequest = new LoginRequest();
		loginRequest.setUsername(userSignUp.getUsername());
		loginRequest.setUsername(userSignUp.getPassword());

		return authService.signup(loginRequest, user);

	}

//	@PostMapping("/refreshToken")
//	public ResponseDTO<String> handleRefreshToken(
//			@RequestParam(value = "refreshtoken", required = true) String refreshtoken) {
//		try {
//			return authService.handleRefreshToken(refreshtoken);
//
//		} catch (Exception e) {
//			throw Problem.builder().withStatus(Status.INTERNAL_SERVER_ERROR).withDetail("SERVER ERROR").build();
//		}
//	}

}
