package com.istt.staff_notification_v2.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import com.istt.staff_notification_v2.service.JwtTokenService;

//chay truoc filter cua spring security
@Service
public class JwtTokenFilter extends OncePerRequestFilter {

	private static final List<String> ALLOWED_URLS = Arrays.asList("/auth/signin");

	@Autowired
	private JwtTokenService jwtTokenProvider;

//	@Override
//	protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
//			FilterChain filterChain) throws ServletException, IOException {
//		// doc token tu header
//		String token = resolveToken(httpServletRequest);
//		System.out.println("path: " + httpServletRequest.getRequestURI());
//
//		try {
//			// verify token
//			if (token != null && jwtTokenProvider.validateToken(token)) {
////			if (token != null) {
//				// co token roi thi lay username, gọi db len user
//				String username = jwtTokenProvider.getUsername(token);
//				System.out.println(username);
//				if (username != null) {
//					Authentication auth = jwtTokenProvider.getAuthentication(username);
//					System.out.println(jwtTokenProvider.getAuthentication(username));
//
//					// set vao context de co dang nhap roi
//					SecurityContextHolder.getContext().setAuthentication(auth);
//				} else {
//					// this is very important, since it guarantees the user is not authenticated at
//					// all
//					SecurityContextHolder.clearContext();
//					httpServletResponse.sendError(401, "No Auth");
//					System.out.println("ko có quyền");
//
//					return;
//				}
//			}
//		} catch (Exception ex) {
//			System.out.println("Cannot set user authentication: " + ex.getMessage());
////			logger.error("Cannot set user authentication: {}", ex.getMessage());
//		}
//
//		filterChain.doFilter(httpServletRequest, httpServletResponse);
//	}

	@Override
	protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			FilterChain filterChain) throws ServletException, IOException {
		// Đọc token từ header
		String token = resolveToken(httpServletRequest);
		String requestURI = httpServletRequest.getRequestURI();
		System.out.println("path: " + httpServletRequest.getRequestURI());

		try {
			if (ALLOWED_URLS.contains(requestURI)) {
				filterChain.doFilter(httpServletRequest, httpServletResponse);
				System.out.println("----pass-----");
				return;
			}

			// Verify token
			if (token != null && jwtTokenProvider.validateToken(token)) {
				String username = jwtTokenProvider.getUsername(token);
				System.out.println(username);
				if (username != null) {
					Authentication auth = jwtTokenProvider.getAuthentication(username);
					System.out.println(jwtTokenProvider.getAuthentication(username));

					// Set vào context để có đăng nhập rồi
					SecurityContextHolder.getContext().setAuthentication(auth);
				} else {
					// Bảo đảm người dùng không được xác thực
					SecurityContextHolder.clearContext();
					httpServletResponse.sendError(401, "No Auth");
					System.out.println("Không có quyền");
					return;
				}
			}
		} catch (Exception ex) {
			System.out.println("Cannot set user authentication: " + ex.getMessage());
			// logger.error("Cannot set user authentication: {}", ex.getMessage());
		}

		filterChain.doFilter(httpServletRequest, httpServletResponse);
	}

	// lay token tu request gui len
	public String resolveToken(HttpServletRequest req) {
		String bearerToken = req.getHeader("Authorization");
		System.out.println("-----------");
		System.out.println("bearer Token: " + bearerToken);
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}
}
