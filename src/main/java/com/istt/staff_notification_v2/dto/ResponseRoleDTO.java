package com.istt.staff_notification_v2.dto;

import lombok.Data;
@Data
public class ResponseRoleDTO {
	private String roleId;
	private String role;
	private Boolean isActive = false;
}
