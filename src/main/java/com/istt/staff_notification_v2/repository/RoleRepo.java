package com.istt.staff_notification_v2.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.istt.staff_notification_v2.entity.Role;

@Repository
public interface RoleRepo extends JpaRepository<Role, String> {

	Optional<Role> findByRoleId(String roleId);

	@Query("SELECT u FROM Role u WHERE u.role LIKE :x ")
	Page<Role> searchByName(@Param("x") String s, Pageable pageable);

}
