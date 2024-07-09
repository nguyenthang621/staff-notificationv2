package com.istt.staff_notification_v2.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "role")
@EqualsAndHashCode(callSuper = false, exclude = {"groupRoles"})
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Role {
	@Id
	@Column(name = "role_id", updatable = false, nullable = false)
	private String roleId;

//	@Column(unique = true)
	private String role;

	@Column(columnDefinition = "TEXT")
	private String description;

//	@JsonIgnore
//	@ManyToMany(mappedBy = "roles")
//	private Set<User> users = new HashSet<>();
//	@JsonIgnore
//	@ManyToMany(mappedBy = "roles")
//	private Set<GroupRole> users = new HashSet<>();
	
	@JsonIgnore
	@ManyToMany(mappedBy = "roles")
	private Set<GroupRole> groupRoles = new HashSet<>();

}
