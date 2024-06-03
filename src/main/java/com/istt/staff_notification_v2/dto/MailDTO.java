package com.istt.staff_notification_v2.dto;

import lombok.Data;

@Data
public class MailDTO {
	private String from = "nthang621@gmail.com";
	private String to;
	private String toName;
	private String subject;
	private String content;

}