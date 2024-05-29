package com.istt.staff_notification_v2.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class LoginRequest {
	private String username;

	private String password;

}