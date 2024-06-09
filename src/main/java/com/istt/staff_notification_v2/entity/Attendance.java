package com.istt.staff_notification_v2.entity;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "attendance")
public class Attendance {
	@Id
	@Column(name = "attendance_id", updatable = false, nullable = false)
	private String attendanceId;

	@Column(name = "type", updatable = false, nullable = false)
	private String type;

	@Column(name = "start_date", updatable = false, nullable = false)
	private Date startDate;

	@Column(name = "end_date", updatable = false, nullable = false)
	private Date endDate;

	private String updateBy;

	@Column(name = "approved_by", updatable = false, nullable = false)
	private String approvedBy;

	@ManyToOne
	@JoinColumn(name = "employee_id", updatable = false, nullable = false)
	private Employee employee;

	@Column(columnDefinition = "TEXT")
	private String note;

	@Column(name = "create_at")
	private Date createAt;

	@Column(name = "update_at")
	private Date updateAt;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "leaveqequestId", referencedColumnName = "leaveqequest_id")
	private LeaveRequest leaveRequest;

}