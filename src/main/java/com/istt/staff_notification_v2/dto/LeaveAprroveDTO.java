package com.istt.staff_notification_v2.dto;

import java.util.Date;

import lombok.Data;

@Data
public class LeaveAprroveDTO {
	private String leaveId;
	private String fullName;
	private String departmentName;
	private Date startDate;
	private Date endDate;
}
