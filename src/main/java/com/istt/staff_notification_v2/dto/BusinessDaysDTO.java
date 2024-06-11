package com.istt.staff_notification_v2.dto;

import java.util.Date;

import lombok.Data;

@Data
public class BusinessDaysDTO {
	private String bussinessdaysId;

//	@DateTimeFormat(pattern = "dd/MM/yyyy")
//	@JsonFormat(pattern = "dd/MM/yyyy")
	private Date startdate;

//	@JsonFormat(pattern = "dd/MM/yyyy")
//	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private Date enddate;

	private String type;

	private String description;
}
