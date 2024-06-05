package com.istt.staff_notification_v2.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

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

	@Column(name = "special_type", nullable = false)
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean specialType;

}
