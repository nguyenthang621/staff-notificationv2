package com.istt.staff_notification_v2.apis;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.istt.staff_notification_v2.security.securityv2.CurrentUser;
import com.istt.staff_notification_v2.security.securityv2.UserPrincipal;
import com.istt.staff_notification_v2.service.FeatureService;
import com.istt.staff_notification_v2.service.GroupService;
@RestController
@RequestMapping("/feature")
public class FeatureAPI {
	@Autowired
	private FeatureService featureService;

	private static final String ENTITY_NAME = "isttFeature";
	
	@PreAuthorize("hasRole('ROLE_FEATURE_VIEW')&&hasRole('ROLE_FEATURE_ACCESS')")
	@GetMapping("/{id}")
	public ResponseDTO<FeatureDTO> get(@PathVariable(value = "id") String id) {
		if(id == null) throw new BadRequestAlertException("Not Found Id", ENTITY_NAME, "exists");
		return ResponseDTO.<FeatureDTO>builder().code(String.valueOf(HttpStatus.OK.value())).data(featureService.get(id))
				.build();
	}
	@PreAuthorize("hasRole('ROLE_FEATURE_VIEW')&&hasRole('ROLE_FEATURE_ACCESS')")
	@PostMapping("")
	public ResponseDTO<FeatureDTO> create(@RequestBody @Valid FeatureDTO featureDTO)
			throws URISyntaxException {
		featureService.create(featureDTO);
		return ResponseDTO.<FeatureDTO>builder().code(String.valueOf(HttpStatus.OK.value())).data(featureDTO)
				.build();
	}
	@PreAuthorize("hasRole('ROLE_FEATURE_VIEW')&&hasRole('ROLE_FEATURE_ACCESS')")
	@GetMapping("/getAll")
	public ResponseDTO<List<FeatureDTO>> getAll()
			throws URISyntaxException {
		List<FeatureDTO> featureDTOs = featureService.getAll();
		featureService.getAll();
		return ResponseDTO.<List<FeatureDTO>>builder().code(String.valueOf(HttpStatus.OK.value())).data(featureDTOs)
				.build();
	}
	
	@PreAuthorize("hasRole('ROLE_FEATURE_VIEW')&&hasRole('ROLE_FEATURE_ACCESS')")
	@GetMapping("/filter/{id}")
	public ResponseDTO<FeatureDTO> filter(@PathVariable(value = "id") String id)
			throws URISyntaxException {
//		featureService.getAll();
		return ResponseDTO.<FeatureDTO>builder().code(String.valueOf(HttpStatus.OK.value())).data(featureService.filterRole(id))
				.build();
	}
	@PreAuthorize("hasRole('ROLE_FEATURE_VIEW')&&hasRole('ROLE_FEATURE_ACCESS')")
	@GetMapping("/filterall")
	public ResponseDTO<List<FeatureDTO>> filterall()
			throws URISyntaxException {
//		featureService.getAll();
		return ResponseDTO.<List<FeatureDTO>>builder().code(String.valueOf(HttpStatus.OK.value())).data(featureService.filterALL())
				.build();
	}
	@PreAuthorize("hasRole('ROLE_FEATURE_VIEW')&&hasRole('ROLE_FEATURE_ACCESS')")
	@GetMapping("/getFeature")
	public ResponseDTO<List<FeatureDTO>> getFeatureFromUser(@CurrentUser UserPrincipal currentuser)
			throws URISyntaxException {
		if(currentuser==null) throw new BadRequestAlertException("not found user", ENTITY_NAME, "missingdata");
		return ResponseDTO.<List<FeatureDTO>>builder().code(String.valueOf(HttpStatus.OK.value())).data(featureService.getFeatureFromUser(currentuser.getUser_id()))
				.build();
	}
	
}
