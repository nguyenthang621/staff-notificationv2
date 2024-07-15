package com.istt.staff_notification_v2.apis;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.istt.staff_notification_v2.apis.errors.BadRequestAlertException;
import com.istt.staff_notification_v2.dto.DepartmentDTO;
import com.istt.staff_notification_v2.dto.FeatureDTO;
import com.istt.staff_notification_v2.dto.ResponseDTO;
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
	
	@PostMapping("")
	public ResponseDTO<FeatureDTO> create(@RequestBody @Valid FeatureDTO featureDTO)
			throws URISyntaxException {
		featureService.create(featureDTO);
		return ResponseDTO.<FeatureDTO>builder().code(String.valueOf(HttpStatus.OK.value())).data(featureDTO)
				.build();
	}
	
	@GetMapping("/getAll")
	public ResponseDTO<List<FeatureDTO>> getAll()
			throws URISyntaxException {
		List<FeatureDTO> featureDTOs = featureService.getAll();
		featureService.getAll();
		return ResponseDTO.<List<FeatureDTO>>builder().code(String.valueOf(HttpStatus.OK.value())).data(featureDTOs)
				.build();
	}
	
	
}
