package com.istt.staff_notification_v2.dto;

import javax.persistence.Id;

import lombok.Data;

@Data
public class LeaveMonthDTO {
	private String id;
	private int countEmployee;
	private Long month;
	private Long year;
}
