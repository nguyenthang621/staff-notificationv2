package com.istt.staff_notification_v2.dto;

import lombok.Data;

@Data
public class UpdatePassword {
	private String userId;
	private String oldPassword;
	private String newPassword;
	private String confirmPassword;

}
