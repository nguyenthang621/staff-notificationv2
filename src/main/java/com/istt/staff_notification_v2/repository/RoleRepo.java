package com.istt.staff_notification_v2.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.istt.staff_notification_v2.entity.Group;
import com.istt.staff_notification_v2.entity.Role;

@Repository
public interface RoleRepo extends JpaRepository<Role, String> {

	Optional<Role> findByRoleId(String roleId);

	@Query("SELECT u FROM Role u WHERE u.role LIKE :x ")
	Page<Role> searchByName(@Param("x") String s, Pageable pageable);

	@Query("SELECT r FROM Role r WHERE r.roleId in :ids")
	Optional<List<Role>> findByRoleIds(@Param("ids") List<String> roleIds);

	@Query("SELECT u FROM Role u WHERE u.role = :x ")
	Optional<Role> findByRoleName(@Param("x") String role);

	@Query("SELECT r FROM Role r")
	Optional<List<Role>> getAll();
	
//	@Query("SELECT u FROM Role u WHERE u.feature = :x ")
//	List<Role> findByGroupRole(@Param("x") Group group);
//	Optional<List<Role>> findByFeature(@Param("x") feature);
	
	@Query("SELECT u FROM Role u WHERE u.role LIKE :x")
	List<Role> findByFeature(@Param("x") String feature);
}
