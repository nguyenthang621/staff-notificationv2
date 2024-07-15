package com.istt.staff_notification_v2.dto;

import java.util.HashSet;
import java.util.Set;

import com.istt.staff_notification_v2.entity.Employee;
import com.istt.staff_notification_v2.entity.Group;

import lombok.Data;

@Data
public class UserDTO {

	private String userId;
	private String username;
	private String password;
	private Employee employee;
	private Group group;
	private String refreshToken;
	private Long expired;

}
