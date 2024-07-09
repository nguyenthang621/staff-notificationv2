package com.istt.staff_notification_v2.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.istt.staff_notification_v2.entity.GroupRole;


public interface GroupRoleRepo extends JpaRepository<GroupRole, String> {

	Boolean existsByGroupName(String name);
	Optional<GroupRole> findByGroupName(String name);
}
