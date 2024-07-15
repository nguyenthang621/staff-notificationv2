package com.istt.staff_notification_v2.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.istt.staff_notification_v2.dto.FeatureDTO;
import com.istt.staff_notification_v2.dto.ResponseRoleDTO;
import com.istt.staff_notification_v2.entity.Feature;
import com.istt.staff_notification_v2.entity.Group;
import com.istt.staff_notification_v2.entity.Role;
import com.istt.staff_notification_v2.repository.FeatureRepo;
import com.istt.staff_notification_v2.repository.GroupRepo;
import com.istt.staff_notification_v2.repository.RoleRepo;

public interface FeatureService {
	Set<FeatureDTO> getFeaturesFromRoles(Set<Role> roles);
//	Set<FeatureDTO> test(String id);
	FeatureDTO create(FeatureDTO featureDTO);
}

@Service
class FeatureServiceImpl implements FeatureService{

	@Autowired
	private FeatureRepo featureRepo;
	
	@Autowired
	private GroupRepo groupRepo;
	@Autowired
	private RoleRepo roleRepo;
	
	@Override
	public Set<FeatureDTO> getFeaturesFromRoles(Set<Role> roles) {
		ModelMapper mapper= new ModelMapper();
		Set<ResponseRoleDTO> resRoles = roles.stream().map(role -> mapper.map(role, ResponseRoleDTO.class))
				.collect(Collectors.toSet());
		Set<FeatureDTO> featureDTOs = featureRepo.findAll().stream().map(feature -> mapper.map(feature, FeatureDTO.class))
				.collect(Collectors.toSet());
		for (FeatureDTO featureDTO : featureDTOs) {
			for (ResponseRoleDTO resRole : featureDTO.getRoles()) {
				if(resRoles.contains(resRole)) {
					resRole.setIsActive(true);
					System.err.println(resRole.getRole());
				}
			}
		}
		return featureDTOs;
	}

	@Override
	public FeatureDTO create(FeatureDTO featureDTO) {
		Feature feature = new Feature();
		feature.setFeatureId(UUID.randomUUID().toString().replaceAll("-", ""));
		feature.setFeatureName(featureDTO.getFeatureName());
		System.err.println(feature.getFeatureName().toUpperCase());
		List<Role> roles= roleRepo.findByFeature("%"+feature.getFeatureName().toUpperCase()+"%");
//		feature.setRoles(roles);
//		System.err.print(roles.size());
		featureRepo.save(feature);
		for (Role role : roles) {
			role.setFeature(feature);
			roleRepo.save(role);
		}
		return new ModelMapper().map(feature, FeatureDTO.class);
	}
	
//	@Override
//	public Set<FeatureDTO> test(String id) {
//		ModelMapper mapper= new ModelMapper();
//		Group group = groupRepo.findById(id).get();
//		Set<ResponseRoleDTO> resRoles = group.getRoles().stream().map(role -> mapper.map(role, ResponseRoleDTO.class))
//				.collect(Collectors.toSet());
//		Set<FeatureDTO> featureDTOs = featureRepo.findAll().stream().map(feature -> mapper.map(feature, FeatureDTO.class))
//				.collect(Collectors.toSet());
//		for (FeatureDTO featureDTO : featureDTOs) {
//			for (ResponseRoleDTO resRole : featureDTO.getRoles()) {
//				if(resRoles.contains(resRole)) {
//					resRole.setIsActive(true);
//					System.err.println(resRole.getRole());
//				}
//			}
//		}
//		return featureDTOs;
//	}
	
}
