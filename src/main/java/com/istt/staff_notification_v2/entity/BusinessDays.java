package com.istt.staff_notification_v2.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "businessdays")
public class BusinessDays {
	@Id
	@Column(name = "bussinessdays_id", updatable = false, nullable = false)
	private String bussinessdaysId;

//	@DateTimeFormat(pattern = "dd/MM/yyyy")
//	@JsonFormat(pattern = "dd/MM/yyyy")
	private Date startdate;

//	@DateTimeFormat(pattern = "dd/MM/yyyy")
//	@JsonFormat(pattern = "dd/MM/yyyy")
	private Date enddate;

	private String type; 

	@Column(columnDefinition = "TEXT")
	private String description;

}
