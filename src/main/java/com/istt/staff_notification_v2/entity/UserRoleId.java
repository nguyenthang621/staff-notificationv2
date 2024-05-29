package com.istt.staff_notification_v2.entity;

import java.io.Serializable;
import java.util.Objects;

import org.springframework.stereotype.Component;

@Component
public class UserRoleId implements Serializable {
	private String user_id;
	private String role_id;

	// Default constructor
	public UserRoleId() {
	}

	// Constructor with parameters
	public UserRoleId(String employee_id, String role_id) {
		this.user_id = user_id;
		this.role_id = role_id;
	}

	// hashCode and equals methods
	@Override
	public int hashCode() {
		return Objects.hash(user_id, role_id);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		UserRoleId that = (UserRoleId) o;
		return Objects.equals(user_id, that.user_id) && Objects.equals(role_id, that.role_id);
	}
}
