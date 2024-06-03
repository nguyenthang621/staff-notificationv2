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
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import com.istt.staff_notification_v2.dto.LoginRequest;
import com.istt.staff_notification_v2.dto.ResponseDTO;
import com.istt.staff_notification_v2.dto.UserDTO;
import com.istt.staff_notification_v2.entity.User;
import com.istt.staff_notification_v2.repository.UserRepo;
import com.istt.staff_notification_v2.service.AuthService;
import com.istt.staff_notification_v2.service.UserService;
import com.istt.staff_notification_v2.utils.exception.AccessDeniedException;

@RestController
@RequestMapping("/auth")
public class AuthAPI {
	@Autowired
	UserRepo userRepo;

	@Autowired
	UserService userService;

	@Autowired
	AuthService authService;

	@PostMapping("/signin")
	public ResponseDTO<String> signin(@Valid @RequestBody LoginRequest loginRequest) {
		try {
			Optional<User> userOptional = userRepo.findByUsername(loginRequest.getUsername());
			User user = userOptional.get();

			if (user == null)
				throw new AccessDeniedException("User not found!!!");

			Boolean compare_password = BCrypt.checkpw(loginRequest.getPassword(), user.getPassword());

			if (!compare_password)
				throw new AccessDeniedException("Password wrong !!!");

			return authService.signin(loginRequest, user);

		} catch (Exception e) {
			throw Problem.builder().withStatus(Status.INTERNAL_SERVER_ERROR).withDetail("SERVER ERROR").build();
		}
	}

	@PostMapping("/signup")
	public ResponseDTO<String> signup(@Valid @RequestBody UserDTO userSignUp) {
		try {
			Optional<User> userOptional = userRepo.findByUsername(userSignUp.getUsername());
			User user = userOptional.get();

			if (user != null)
				throw new AccessDeniedException("user " + user.getUsername() + " already exists");

			UserDTO userDTO = new ModelMapper().map(user, UserDTO.class);
			userService.create(userDTO);

			LoginRequest loginRequest = new LoginRequest();
			loginRequest.setUsername(userSignUp.getUsername());
			loginRequest.setUsername(userSignUp.getPassword());

			return authService.signin(loginRequest, user);

		} catch (Exception e) {
			throw Problem.builder().withStatus(Status.INTERNAL_SERVER_ERROR).withDetail("SERVER ERROR").build();
		}
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
