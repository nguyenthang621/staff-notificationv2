package com.istt.staff_notification_v2.dto;

import java.util.List;

import com.istt.staff_notification_v2.entity.Employee;

import lombok.Data;

@Data
public class EmployeeRelationship {
	private Employee employee;

	private List<Employee> employeeRelationships;

}
