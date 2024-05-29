package com.istt.staff_notification_v2.configuration;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import lombok.Data;

@Service
@Data
public class ApplicationProperties {
	private String defaultStatus = "INACTIVE";

	@PostConstruct
	protected void init() {
		System.out.println(" == Application Reloaded: " + this);
	}

}
