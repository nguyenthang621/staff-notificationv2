package com.istt.staff_notification_v2.dto;

import java.util.Date;

import com.istt.staff_notification_v2.entity.Employee;
import com.istt.staff_notification_v2.entity.LeaveRequest;
import com.istt.staff_notification_v2.entity.LeaveType;

import lombok.Data;

@Data
public class AttendanceDTO {
	private String attendanceId;

	private LeaveType leaveType;

	// @DateTimeFormat(pattern = "dd/MM/yyyy")
	// @JsonFormat(pattern = "dd/MM/yyyy")
	private Date startDate;

	// @DateTimeFormat(pattern = "dd/MM/yyyy")
	// @JsonFormat(pattern = "dd/MM/yyyy")
	private Date endDate;

	private String updateBy;

	private float duration;

	private String approvedBy;

	private Employee employee;

	private String note;

	private Date createAt;

	private Date updateAt;

	private LeaveRequest leaveRequest;

}
