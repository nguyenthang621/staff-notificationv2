package com.istt.staff_notification_v2.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.istt.staff_notification_v2.entity.Department;
import com.istt.staff_notification_v2.entity.Employee;
import com.istt.staff_notification_v2.entity.User;

import lombok.Data;

@Data
public class EmployeeDTO {
	private String employeeId;

	private Long staffId;

	private String parentId;

	private String uOdooId;

	private List<Employee> subordinatesOdoo;

	private String jobTitle;

	private String workCity;

	private float countOfDayOff;

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

	private Set<LevelDTO> levels = new HashSet<>();

	private List<String> employeeDependence;

	private List<EmployeeDTO> subordinates = new ArrayList<>();

	private User user;

}
