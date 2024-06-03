package com.istt.staff_notification_v2.dto;

import java.util.Date;

import com.istt.staff_notification_v2.entity.Employee;

import lombok.Data;

@Data
public class AttendanceDTO {
	private String attendanceId;

	private Date date;

	private Employee employee;

	private String reason;

	private String status;
}
