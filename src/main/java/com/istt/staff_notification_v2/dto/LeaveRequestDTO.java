package com.istt.staff_notification_v2.dto;

import java.util.Date;

import com.istt.staff_notification_v2.entity.Employee;
import com.istt.staff_notification_v2.entity.LeaveType;

import lombok.Data;

@Data
public class LeaveRequestDTO {
	private String leaveqequestId;

	private Employee employee;

	private LeaveType leavetype;

	private Date requestDate;

	private float duration;

	private String reason;

	private String status;

	private String anrreason;
	
	private String leavePlace;

	private Date startDate;

	private String receiver;

}
