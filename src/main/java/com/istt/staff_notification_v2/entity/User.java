package com.istt.staff_notification_v2.entity;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "user")
@EqualsAndHashCode(callSuper = false, exclude = "employee")
public class User {

	@Id
	@Column(name = "user_id", updatable = false, nullable = false)
	private String user_id;

	@Column(name = "username", unique = true)
	private String username;

	@Column(name = "password", unique = true)
	private String password;

	@OneToMany(mappedBy = "user")
	Set<UserRole> role;

	private String accessToken;

	private String refreshToken;

	@Override
	public String toString() {
		return "User{" + "user_id=" + user_id + ", username='" + username + '\'' + ", password='" + password + '\''
				+ '\'' + ", accessToken=" + accessToken + ", refreshToken='" + refreshToken + '}';
	}
}
