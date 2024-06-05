package com.istt.staff_notification_v2.dto;

import lombok.Data;

@Data
public class SearchLeaveRequest {
	private String email;

	private String status;

	private String startDate;

	private String endDate;

}
