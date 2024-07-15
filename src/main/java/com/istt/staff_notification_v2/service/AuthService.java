package com.istt.staff_notification_v2.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import com.istt.staff_notification_v2.apis.errors.BadRequestAlertException;
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

	private List<String> getOdooSession(LoginRequest loginRequest) {
		try {
			RestTemplate restTemplate = new RestTemplate();
			String url = "http://localhost:5000/authOdoo"; // Thay đổi URL nếu cần

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<LoginRequest> entity = new HttpEntity<>(loginRequest, headers);

			ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

			List<String> result = new ArrayList<>();
			if (response.getStatusCode().is2xxSuccessful()) {
				Map<String, Object> responseBody = response.getBody();
				if (responseBody != null && responseBody.containsKey("session_id")
						&& responseBody.containsKey("expired")) {
					result.add(responseBody.get("session_id").toString());
					result.add(responseBody.get("expired").toString());
					return result;
				} else {
					return null;
				}
			} else {
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public ResponseDTO<String> signin(LoginRequest loginRequest, User user) {
		try {
//			System.err.println("saveuser");

			Authentication authentication = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), null));

			SecurityContextHolder.getContext().setAuthentication(authentication);

			String accessToken = tokenProvider.generateAccessToken(authentication);
			String refreshToken = tokenProvider.generateRefreshToken(user.getUsername());
//			List<String> response = getOdooSession(loginRequest);
//			if (response == null)
//				throw new BadRequestAlertException("Bad request: Password wrong !!!", ENTITY_NAME, "Password wrong");
//			String session_id = response.get(0);
//			Long expired = Long.valueOf(response.get(1));
//
//			if (session_id == null || session_id.isEmpty()) {
//				throw new BadRequestAlertException("Bad request: Password wrong !!!", ENTITY_NAME, "Password wrong");
//			}

			user.setAccessToken(accessToken);
			user.setRefreshToken(refreshToken);
			userRepo.save(user);

//			return ResponseDTO.<String>builder().code(String.valueOf(HttpStatus.OK.value())).accessToken(accessToken)
//					.sessionId(session_id).refreshToken(refreshToken).build();
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
					new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), user.getUserId()));
			// Set thông tin authentication vào Security Context
			SecurityContextHolder.getContext().setAuthentication(authentication);

			String accessToken = tokenProvider.generateAccessToken(authentication);
			String refreshToken = tokenProvider.generateRefreshToken(user.getUsername());

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

			String username = tokenProvider.getUserIdFromJWT(refreshToken_in);
			if (username.isEmpty())
				throw new AccessDeniedException("Access Denied");

			User user = userRepo.findByUsername(username).orElseThrow(NoResultException::new);

			Authentication authentication = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), null));

			String accessToken = tokenProvider.generateAccessToken(authentication);
			String refreshToken = tokenProvider.generateRefreshToken(user.getUsername());

//			user.setAccessToken(accessToken);
//			user.setRefreshToken(refreshToken);
//			userRepo.save(user);

			return ResponseDTO.<String>builder().code(String.valueOf(HttpStatus.OK.value())).accessToken(accessToken)
					.refreshToken(refreshToken).build();

		} catch (ResourceAccessException e) {
			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
		} catch (HttpServerErrorException | HttpClientErrorException e) {
			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
		}
	}

}
