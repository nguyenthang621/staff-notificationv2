package com.istt.staff_notification_v2.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@Table(name = "leavetype")
@EqualsAndHashCode(callSuper = false)
public class LeaveType {

	@Id
	@Column(name = "leavetype_id", updatable = false, nullable = false)
	private String leavetypeId;

	@Column(name = "leavetype_name")
	private String leavetypeName;

	private String description;
}
