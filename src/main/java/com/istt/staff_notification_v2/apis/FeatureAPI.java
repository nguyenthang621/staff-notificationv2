package com.istt.staff_notification_v2.apis;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.istt.staff_notification_v2.dto.FeatureDTO;
import com.istt.staff_notification_v2.dto.ResponseGroupDTO;
import com.istt.staff_notification_v2.dto.ResponseRoleDTO;
import com.istt.staff_notification_v2.service.FeatureService;
import com.istt.staff_notification_v2.service.GroupService;
@RestController
@RequestMapping("/feature")
public class FeatureAPI {
	@Autowired
	private FeatureService featureService;

	private static final String ENTITY_NAME = "isttFeature";
	
//	@GetMapping("/{id}")
//	public Set<FeatureDTO> get(@PathVariable(value = "id") String id) {
//		return featureService.test(id);
//	}
	
	
}
