package com.istt.staff_notification_v2.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name = "leavetype")
public class LeaveType {

	@Id
	@Column(name = "leavetype_id", updatable = false, nullable = false)
	private String leavetype_id;

	private String leavetype_name;

	private String description;
}
