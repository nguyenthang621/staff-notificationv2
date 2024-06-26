package com.istt.staff_notification_v2.dto;

import java.util.List;

import com.istt.staff_notification_v2.entity.Department;

import lombok.Data;

@Data
public class ResponseHierarchy {
	private String employeeId;
	private String fullname;
	private String avatar;
	private ResponseHierarchy parent;
	private String email;
	private String jobTitle;
	private Department department;

	private List<ResponseHierarchy> subordinatesOdoo;

}
