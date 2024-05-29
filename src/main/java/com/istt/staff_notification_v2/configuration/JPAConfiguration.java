package com.istt.staff_notification_v2.configuration;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.istt.staff_notification_v2.entity.User;
import com.istt.staff_notification_v2.repository.UserRepo;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JPAConfiguration {
	@Autowired
	UserRepo userRepo;

	@Bean
	AuditorAware<User> auditorProvider() {
		return new AuditorAware<User>() {
			@Override
			public Optional<User> getCurrentAuditor() {
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				Optional<User> user = null;
				if (auth != null && !(auth instanceof AnonymousAuthenticationToken))
					user = userRepo.findByUsername(auth.getName());
				return user;
			}
		};
	}
}
