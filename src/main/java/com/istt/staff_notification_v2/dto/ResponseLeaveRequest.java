package com.istt.staff_notification_v2.dto;

import lombok.Data;

@Data
public class ResponseLeaveRequest {
	private String byEmployeeId;

	private String leaveqequestId;

	private String status;

	private String anrreason;
}
