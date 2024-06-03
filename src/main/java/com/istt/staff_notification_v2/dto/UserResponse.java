package com.istt.staff_notification_v2.dto;

import java.util.HashSet;
import java.util.Set;

import com.istt.staff_notification_v2.entity.Employee;

import lombok.Data;

@Data
public class UserResponse {
	private String id;
	private String username;
	private Employee employee;
	private Set<RoleDTO> roles = new HashSet<>();
}
