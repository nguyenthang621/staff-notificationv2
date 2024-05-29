package com.istt.staff_notification_v2.dto;

import java.util.Date;

import com.istt.staff_notification_v2.entity.Department;
import com.istt.staff_notification_v2.entity.User;

import lombok.Data;

@Data
public class EmployeeDTO {
	private String employee_id;

	private String fullname;

	private String address;

	private String phone;

	private String email;

	private Department department;

	private String avatar;

	private String status;

	private Date dateofbirth;

//	@JsonBackReference
	private User user;

}
