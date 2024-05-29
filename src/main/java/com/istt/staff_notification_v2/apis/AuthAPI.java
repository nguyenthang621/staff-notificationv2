package com.istt.staff_notification_v2.apis;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.istt.staff_notification_v2.dto.LoginRequest;
import com.istt.staff_notification_v2.dto.ResponseDTO;
import com.istt.staff_notification_v2.entity.User;
import com.istt.staff_notification_v2.repository.UserRepo;
import com.istt.staff_notification_v2.security.JwtTokenProvider;
import com.istt.staff_notification_v2.service.JwtTokenService;
import com.istt.staff_notification_v2.service.UserService;

@RestController
@RequestMapping("/auth")
public class AuthAPI {
	@Autowired
	UserRepo userRepo;

	@Autowired
	UserService userService;

	@Autowired
	JwtTokenService jwtTokenService;

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	private JwtTokenProvider tokenProvider;

	@PostMapping("/signin")
	public ResponseDTO<String> signin(@Valid @RequestBody LoginRequest loginRequest) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
		System.out.println(123123);
		// Nếu không xảy ra exception tức là thông tin hợp lệ
		// Set thông tin authentication vào Security Context
		SecurityContextHolder.getContext().setAuthentication(authentication);

		User user = userRepo.findByUsername(loginRequest.getUsername()).get();
		if (user == null)
			return null;

		String jwt = tokenProvider.generateToken((User) authentication.getPrincipal());
		System.out.println("jwt: " + jwt);

//		RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.get().getId());
//        return ResponseDTO.<String>builder().code(String.valueOf(HttpStatus.OK.value())).data(jwtTokenService.createToken(loginRequest.getUsername(),authentication.getAuthorities())).build();
//		return ResponseDTO.<String>builder().code(String.valueOf(HttpStatus.OK.value()))
//				.data(jwtTokenService.createToken(loginRequest.getUsername(), authentication.getAuthorities()))
//				.refreshToken(refreshToken.getToken()).build();
		return null;
	}

}
