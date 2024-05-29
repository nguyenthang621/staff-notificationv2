package com.istt.staff_notification_v2.service;

import java.util.UUID;

import javax.persistence.NoResultException;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.istt.staff_notification_v2.dto.RoleDTO;
import com.istt.staff_notification_v2.entity.Role;
import com.istt.staff_notification_v2.repository.RoleRepo;

public interface RoleService {
	RoleDTO create(RoleDTO roleDTO);

	RoleDTO delete(String id);
}

@Service
class RoleServiceImpl implements RoleService {

	@Autowired
	private RoleRepo roleRepo;

	@Transactional
	@Override
	public RoleDTO create(RoleDTO roleDTO) {
		ModelMapper mapper = new ModelMapper();
		Role role = mapper.map(roleDTO, Role.class);
		role.setRole_id(UUID.randomUUID().toString().replaceAll("-", ""));
		roleRepo.save(role);
		return roleDTO;
	}

	@Transactional
	@Override
	public RoleDTO delete(String id) {
		Role role = roleRepo.findById(id).orElseThrow(NoResultException::new);
		if (role != null) {
			roleRepo.deleteById(id);
		}
		return null;
	}

}