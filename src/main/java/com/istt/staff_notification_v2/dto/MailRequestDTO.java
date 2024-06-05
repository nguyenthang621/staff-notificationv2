package com.istt.staff_notification_v2.dto;

import java.util.List;

import lombok.Data;

@Data
public class MailRequestDTO {
	private LeaveRequestDTO leaveRequestDTO;
	private List<EmployeeDTO> recceiverList;
	private String subject;
}
