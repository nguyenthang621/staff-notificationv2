package com.istt.staff_notification_v2.dto;

import java.util.Date;

import com.istt.staff_notification_v2.entity.Employee;
import com.istt.staff_notification_v2.entity.LeaveRequest;

import lombok.Data;

@Data
public class AttendanceDTO {
	private String attendanceId;

	private String type;

	private Date startDate;

	private Date endDate;

	private String updateBy;

	private String approvedBy;

	private Employee employee;

	private String note;

	private Date createAt;

	private Date updateAt;

	private LeaveRequest leaveRequest;

}
