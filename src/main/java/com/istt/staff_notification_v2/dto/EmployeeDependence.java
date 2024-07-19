package com.istt.staff_notification_v2.dto;

import java.util.Date;

import com.istt.staff_notification_v2.entity.Department;

import lombok.Data;
@Data
public class EmployeeDependence {
	private String employeeId;

	private Long staffId;

	private String parentId;

	private String fullname;

	private String address;

	private String phone;

	private String email;

	private Department department;

	private String avatar;

	private String status;

	private Date dateofbirth;

	private Date offdate;
	
	private String workCity;

	private Date hiredate;
	
	private String jobTitle;
	
}
