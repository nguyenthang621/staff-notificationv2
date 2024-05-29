package com.istt.staff_notification_v2.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Entity
@Data
@Table(name = "leaveqequest")
public class LeaveRequest {

	@Id
	@Column(name = "leaveqequest_id", updatable = false, nullable = false)
	private String leaveqequest_id;

	@ManyToOne
	@JoinColumn(name = "employee_id", nullable = false)
	private Employee employee;

	@ManyToOne
	@JoinColumn(name = "leavetype_id")
	private LeaveType leavetype;

	@JsonFormat(pattern = "dd/MM/yyyy")
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private Date request_date;

	private float duration;

	@Column(columnDefinition = "TEXT")
	private String reason;

	private String status;

	@Column(columnDefinition = "TEXT")
	private String anrreason;

}
