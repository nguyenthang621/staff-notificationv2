package com.istt.staff_notification_v2.dto;

import java.util.List;

import lombok.Data;

@Data
public class Rule {
	private String position;

	private List<Rule> subordinates;

}
