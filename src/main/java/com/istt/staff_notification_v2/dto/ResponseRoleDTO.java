package com.istt.staff_notification_v2.dto;

import com.istt.staff_notification_v2.entity.Feature;

import lombok.Data;
@Data
public class ResponseRoleDTO {
	private String roleId;
	private String role;
	private Boolean isActive = false;
}
