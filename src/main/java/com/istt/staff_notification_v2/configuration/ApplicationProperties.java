package com.istt.staff_notification_v2.configuration;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import lombok.Data;

@Service
@Data
public class ApplicationProperties {
	private String defaultStatus = "INACTIVE";

	private List<String> STATUS_EMPLOYEE = Arrays.asList("SUSPEND", "STOP", "ACTIVE");

	public enum StatusEmployeeRef {
		SUSPEND, STOP, ACTIVE
	}

	@PostConstruct
	protected void init() {
		System.out.println(" == Application Reloaded: " + this);
	}

}
