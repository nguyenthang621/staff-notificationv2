package com.istt.staff_notification_v2.dto;

import java.util.HashSet;
import java.util.Set;

import com.istt.staff_notification_v2.entity.Group;
import com.istt.staff_notification_v2.entity.Role;

import lombok.Data;

@Data
public class GroupDTO {
	private String groupId;
	private String groupName;
	private Set<RoleDTO> roles = new HashSet<>();
}
