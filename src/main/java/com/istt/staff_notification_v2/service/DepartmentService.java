package com.istt.staff_notification_v2.service;

import java.util.UUID;

import javax.persistence.NoResultException;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.istt.staff_notification_v2.dto.DepartmentDTO;
import com.istt.staff_notification_v2.entity.Department;
import com.istt.staff_notification_v2.repository.DepartmentRepo;

public interface DepartmentService {
	DepartmentDTO create(DepartmentDTO roleDTO);

	Boolean delete(String id);

}

@Service
class DepartmentServiceImpl implements DepartmentService {
	@Autowired
	private DepartmentRepo departmentRepo;

	@Transactional
	@Override
	public DepartmentDTO create(DepartmentDTO departmentDTO) {
		ModelMapper mapper = new ModelMapper();
		Department department = mapper.map(departmentDTO, Department.class);
		department.setDepartmentId(UUID.randomUUID().toString().replaceAll("-", ""));
		departmentRepo.save(department);
		return departmentDTO;
	}

	@Transactional
	@Override
	public Boolean delete(String id) {
		Department department = departmentRepo.findById(id).orElseThrow(NoResultException::new);
		if (department != null) {
			departmentRepo.deleteById(id);
			return true;
		}
		return false;
	}

}
