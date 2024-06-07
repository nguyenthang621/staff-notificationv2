package com.istt.staff_notification_v2.dto;

import java.util.List;

import com.istt.staff_notification_v2.entity.Level;

import lombok.Data;

@Data
public class Rule {

	private Level level;

//	private List<Rule> subordinates;
	private List<String> subordinates;

}
