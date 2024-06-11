package com.istt.staff_notification_v2.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class NoPasswordAuthenticationProvider implements AuthenticationProvider {

	@Override
	public boolean supports(Class<?> authentication) {
		return true;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		if (authentication.isAuthenticated()) {
			return authentication;
		} else {
			throw new BadCredentialsException("Invalid credentials");
		}
	}
}
