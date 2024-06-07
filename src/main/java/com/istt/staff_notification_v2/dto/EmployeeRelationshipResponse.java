package com.istt.staff_notification_v2.dto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.istt.staff_notification_v2.entity.Department;

import lombok.Data;

@Data
public class EmployeeRelationshipResponse {
	private String employeeId;

	private String fullname;

	private String email;

	private Department department;

	private String avatar;

	private Set<LevelDTO> levels = new HashSet<>();

	private List<EmployeeRelationshipResponse> subordinates = new ArrayList<>();

}
