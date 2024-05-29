package com.istt.staff_notification_v2.dto;

import com.istt.staff_notification_v2.entity.Role;

import lombok.Data;

@Data
public class PayloadToken {

	private String user_id;

	private String username;

	private Role role;

}
