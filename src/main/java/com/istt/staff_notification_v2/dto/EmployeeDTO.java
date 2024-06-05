package com.istt.staff_notification_v2.dto;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.istt.staff_notification_v2.entity.Department;
import com.istt.staff_notification_v2.entity.Level;
import com.istt.staff_notification_v2.entity.User;

import lombok.Data;

@Data
public class EmployeeDTO {
	private String employeeId;

	private String fullname;

	private String address;

	private String phone;

	private String email;

	private Department department;

	private String avatar;

	private String status;

	private Date dateofbirth;

	private Date offdate;

	private Date hiredate;

	private Set<Level> levels = new HashSet<>();

	private List<String> employeeDependence;

//	@JsonBackReference
	private User user;

}
