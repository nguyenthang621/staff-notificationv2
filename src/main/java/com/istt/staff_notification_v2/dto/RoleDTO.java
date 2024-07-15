package com.istt.staff_notification_v2.dto;

import java.util.HashSet;
import java.util.Set;

import com.istt.staff_notification_v2.entity.Feature;
import com.istt.staff_notification_v2.entity.Group;

import lombok.Data;

@Data
public class RoleDTO {

	private String roleId;
	private String role;
	private String description;
	private Feature feature;

}
