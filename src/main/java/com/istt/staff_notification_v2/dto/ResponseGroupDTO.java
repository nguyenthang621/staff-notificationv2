package com.istt.staff_notification_v2.dto;

import java.util.HashSet;
import java.util.Set;

import com.istt.staff_notification_v2.entity.Feature;

import lombok.Data;

@Data
public class ResponseGroupDTO {
	private String groupId;
	private String groupName;
	private Set<FeatureDTO> features = new HashSet<FeatureDTO>();
}
