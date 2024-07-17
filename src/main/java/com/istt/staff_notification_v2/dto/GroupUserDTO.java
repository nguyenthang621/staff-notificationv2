package com.istt.staff_notification_v2.dto;

import java.util.HashSet;
import java.util.Set;

import lombok.Data;

@Data
public class GroupUserDTO {
	private String groupId;
	private String groupName;
	
	private Set<UserResponse> user = new HashSet<>();
}
