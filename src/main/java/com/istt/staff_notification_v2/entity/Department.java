package com.istt.staff_notification_v2.entity;

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
public class Department {
	@Id
	@Column(name = "department_id", updatable = false, nullable = false)
	private String department_id;

	private String department_name;
}
