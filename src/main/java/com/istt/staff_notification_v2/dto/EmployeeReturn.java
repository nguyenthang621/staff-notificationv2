package com.istt.staff_notification_v2.dto;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.istt.staff_notification_v2.entity.Department;

import lombok.Data;

@Data
public class EmployeeReturn {

	private String employeeId;

	private Long staffId;

	private String fullname;

	private String address;

	private String email;

	private Department department;

	private String avatar;

	private String status;

	private Date dateofbirth;

	private Set<LevelDTO> levels = new HashSet<>();

	private List<String> employeeDependence;

}
