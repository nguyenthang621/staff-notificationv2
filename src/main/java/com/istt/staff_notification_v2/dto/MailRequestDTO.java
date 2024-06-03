package com.istt.staff_notification_v2.dto;

import java.util.List;

import lombok.Data;

@Data
public class MailRequestDTO {
	private AttendanceDTO attendanceDTO;
	private List<EmployeeDTO> recceiverList;
	private String subject;
}
