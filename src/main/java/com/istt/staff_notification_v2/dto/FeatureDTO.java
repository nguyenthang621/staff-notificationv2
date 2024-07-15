package com.istt.staff_notification_v2.dto;

import java.util.HashSet;
import java.util.Set;

import lombok.Data;

@Data
public class FeatureDTO {

	private String featureId;
	private String featureName;
	private Set<ResponseRoleDTO> roles = new HashSet<ResponseRoleDTO>();
}
