package com.istt.staff_notification_v2.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.istt.staff_notification_v2.entity.Department;

@Repository
public interface DepartmentRepo extends JpaRepository<Department, String> {
	@Query("SELECT u FROM Department u WHERE u.departmentName LIKE :x ")
	Page<Department> searchByDepartmentName(@Param("x") String s, Pageable pageable);

	@Query("SELECT u FROM Department u WHERE u.departmentName = :x ")
	Optional<Department> findByDepartmentName(@Param("x") String name);

	@Query("SELECT u FROM Department u WHERE u.departmentId = :x ")
	Optional<Department> findByDepartmentId(@Param("x") String id);

	Department searchByDepartmentId(String id);

	@Query("SELECT d FROM Department d")
	Optional<List<Department>> getAll();

	@Query("SELECT d FROM Department d WHERE d.departmentId in :ids")
	Optional<List<Department>> findByDepartmentIds(@Param("ids") List<String> departmentIds);

}
