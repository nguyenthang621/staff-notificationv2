package com.istt.staff_notification_v2.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "department")
@EqualsAndHashCode(callSuper = false)
public class Department implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "department_id", updatable = false, nullable = false)
	private String departmentId;

	@Column(name = "department_name", nullable = false, unique = true)
	private String departmentName;

}
