package com.istt.staff_notification_v2.dto;

import java.util.Date;

import lombok.Data;

@Data
public class SearchLeaveRequest {
	private String email;

	private String status;

	private Date startDate;

	private String mailReciver;

}
