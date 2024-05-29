package com.istt.staff_notification_v2.dto;

import java.util.HashSet;
import java.util.Set;

import com.istt.staff_notification_v2.entity.Employee;
import com.istt.staff_notification_v2.entity.Role;

import lombok.Data;

@Data
public class UserDTO {

	private String user_id;

	private String username;

	private String password;

	private Employee employee;

	private Set<Role> roles = new HashSet<>();

	private String accessToken;

	private String refreshToken;

}
