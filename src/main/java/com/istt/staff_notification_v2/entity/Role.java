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
@Table(name = "role")
@Entity
@EqualsAndHashCode(callSuper = false)
public class Role {
	@Id
	@Column(name = "role_id", updatable = false, nullable = false)
	private String role_id;

	private String role;

	@Column(columnDefinition = "TEXT")
	private String description;

	@OneToMany(mappedBy = "role")
	Set<UserRole> user;

}