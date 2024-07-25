package com.istt.staff_notification_v2.dto;

import lombok.Data;

@Data
public class TokenDTO {
	private String accessToken;
	private String refreshToken;
}
