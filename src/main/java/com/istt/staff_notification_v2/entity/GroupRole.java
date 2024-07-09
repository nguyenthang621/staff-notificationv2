package com.istt.staff_notification_v2.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "group_role")
@EqualsAndHashCode(callSuper = false, exclude = { "roles"})
public class GroupRole {
	
	@Id
	@Column(name = "group_id", updatable = false, nullable = false)
	private String groupId;
	
//	@OneToMany(mappedBy="group")
//    private Set<User> users;
	@Column(unique = true)
	private String groupName;
	
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name = "group_role_role", joinColumns = @JoinColumn(name = "group_id"), 
	inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles = new HashSet<>();
	
}
