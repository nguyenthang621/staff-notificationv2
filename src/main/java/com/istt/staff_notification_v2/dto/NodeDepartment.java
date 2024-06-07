package com.istt.staff_notification_v2.dto;

import lombok.Data;

@Data
public class NodeDepartment {
	private DepartmentDTO department;

	private EmployeeDTO leader;

	private EmployeeRelationship employeeRelationship;

}
