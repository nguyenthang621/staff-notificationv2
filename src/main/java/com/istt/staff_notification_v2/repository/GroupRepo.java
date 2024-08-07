package com.istt.staff_notification_v2.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.istt.staff_notification_v2.entity.Group;


public interface GroupRepo extends JpaRepository<Group, String> {

	Boolean existsByGroupName(String name);
	Optional<Group> findByGroupName(String name);
}
