package com.istt.staff_notification_v2.dto;

import java.util.Date;

import lombok.Data;

@Data
public class SearchAttendence {

	private SearchDTO search;

	private Date startDate;

	private Date endDate;

	private String type;

}
