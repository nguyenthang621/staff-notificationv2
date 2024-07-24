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
import lombok.EqualsAndHashCode;

@Entity
@Data
@Table(name = "leaveqequest")
@EqualsAndHashCode(callSuper = false)
public class LeaveRequest {

	@Id
	@Column(name = "leaveqequest_id", updatable = false, nullable = false)
	private String leaveqequestId;

	@ManyToOne
	@JoinColumn(name = "employee_id", nullable = false)
	private Employee employee;

	@ManyToOne
	@JoinColumn(name = "leavetype_id", nullable = false)
	private LeaveType leavetype;

//	@JsonFormat(pattern = "dd/MM/yyyy")
//	@DateTimeFormat(pattern = "dd/MM/yyyy")
	@Column(name = "request_date", nullable = false)
	private Date requestDate;

	@Column(name = "duration", updatable = false, nullable = false)
	private float duration;

	@Column(columnDefinition = "TEXT")
	private String reason;

	private String status;

	@Column(columnDefinition = "TEXT")
	private String anrreason;

//	@JsonFormat(pattern = "dd/MM/yyyy")
//	@DateTimeFormat(pattern = "dd/MM/yyyy")
	@Column(name = "start_date", nullable = false)
	private Date startDate;

	@Column(name = "employee_response")
	private String responseBy;

	@Column(name = "receiver", nullable = false)
	private String receiver;
	
	private Date responseDate;
	
	private String placeOff;

}
