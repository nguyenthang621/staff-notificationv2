package com.istt.staff_notification_v2.service;

import javax.persistence.NoResultException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import com.istt.staff_notification_v2.dto.LoginRequest;
import com.istt.staff_notification_v2.dto.ResponseDTO;
import com.istt.staff_notification_v2.entity.User;
import com.istt.staff_notification_v2.repository.UserRepo;
import com.istt.staff_notification_v2.security.securityv2.JwtTokenProvider;
import com.istt.staff_notification_v2.utils.exception.AccessDeniedException;

public interface AuthService {
	ResponseDTO<String> signin(LoginRequest loginRequest, User user);

	ResponseDTO<String> handleRefreshToken(String refreshToken_in);

	ResponseDTO<String> signup(LoginRequest loginRequest, User user);

}

@Service
class AuthServiceImpl implements AuthService {

	@Autowired
	UserRepo userRepo;

	@Autowired
	UserService userService;

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	JwtTokenProvider tokenProvider;

	private static final String ENTITY_NAME = "isttAuth";

	@Override
	public ResponseDTO<String> signin(LoginRequest loginRequest, User user) {
		try {

			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
			// Set thông tin authentication vào Security Context
			SecurityContextHolder.getContext().setAuthentication(authentication);

			String accessToken = tokenProvider.generateAccessToken(authentication);
			String refreshToken = tokenProvider.generateRefreshToken(authentication);

			user.setAccessToken(accessToken);
			user.setRefreshToken(refreshToken);
			userRepo.save(user);

			return ResponseDTO.<String>builder().code(String.valueOf(HttpStatus.OK.value())).accessToken(accessToken)
					.refreshToken(refreshToken).build();

		} catch (ResourceAccessException e) {
			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
		} catch (HttpServerErrorException | HttpClientErrorException e) {
			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
		}
	}

	@Override
	public ResponseDTO<String> signup(LoginRequest loginRequest, User user) {
		try {

			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
			// Set thông tin authentication vào Security Context
			SecurityContextHolder.getContext().setAuthentication(authentication);

			String accessToken = tokenProvider.generateAccessToken(authentication);
			String refreshToken = tokenProvider.generateRefreshToken(authentication);

			user.setAccessToken(accessToken);
			user.setRefreshToken(refreshToken);

			userRepo.save(user);

			return ResponseDTO.<String>builder().code(String.valueOf(HttpStatus.OK.value())).accessToken(accessToken)
					.refreshToken(refreshToken).build();

		} catch (ResourceAccessException e) {
			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
		} catch (HttpServerErrorException | HttpClientErrorException e) {
			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
		}
	}

	@Override
	public ResponseDTO<String> handleRefreshToken(String refreshToken_in) {
		try {

			String user_id = tokenProvider.getUserIdFromJWT(refreshToken_in);
			System.out.println(1);
			if (user_id.isEmpty())
				throw new AccessDeniedException("Access Denied");

			System.out.println(2);
			User user = userRepo.findByUserId(user_id).orElseThrow(NoResultException::new);
			Authentication authentication = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
			System.out.println(4);

			String accessToken = tokenProvider.generateAccessToken(authentication);
			String refreshToken = tokenProvider.generateRefreshToken(authentication);

			user.setAccessToken(accessToken);
			user.setRefreshToken(refreshToken);

			return ResponseDTO.<String>builder().code(String.valueOf(HttpStatus.OK.value())).accessToken(accessToken)
					.refreshToken(refreshToken).build();

		} catch (ResourceAccessException e) {
			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
		} catch (HttpServerErrorException | HttpClientErrorException e) {
			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
		}
	}

}
