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
import com.istt.staff_notification_v2.entity.Employee;

@Repository
public interface EmployeeRepo extends JpaRepository<Employee, String> {
	@Query("SELECT u FROM Employee u WHERE u.fullname LIKE :x ")
	Page<Employee> searchByFullname(@Param("x") String s, Pageable pageable);

	@Query("SELECT u FROM Employee u WHERE u.email LIKE :x ")
	Page<Employee> searchByEmail(@Param("x") String s, Pageable pageable);

	@Query("SELECT u FROM Employee u WHERE u.fullname LIKE :x and u.email LIKE :y ")
	Page<Employee> searchByFullnameAndEmail(@Param("x") String name, @Param("y") String email, Pageable pageable);

	@Query("SELECT u FROM Employee u WHERE u.fullname LIKE :x and u.department.departmentName LIKE :y ")
	Page<Employee> searchByFullnameAndDepartment(@Param("x") String name, @Param("y") String department,
			Pageable pageable);

	@Query("SELECT e FROM Employee e WHERE e.email = :x ")
	Employee findByEmail(@Param("x") String value);

	@Query("SELECT e FROM Employee e WHERE e.employeeId = :x ")
	Optional<Employee> findByEmployeeId(@Param("x") String employeeId);

	@Query("SELECT e FROM Employee e WHERE e.employeeId = :x or e.email = :y")
	Optional<Employee> findByEmployeeIdOrEmail(@Param("x") String employeeId, @Param("y") String email);

	Boolean existsByEmail(String email);

	Boolean existsByEmployeeId(String employeeId);

	@Query("SELECT e FROM Employee e WHERE e.department = :x AND e.status = :y")
	Optional<List<Employee>> findAllByDepartmentId(@Param("x") Department department, @Param("y") String status);

	@Query("SELECT e FROM Employee e WHERE e.employeeId in :ids")
	Optional<List<Employee>> findByEmployeeIds(@Param("ids") List<String> employeeIds);

	@Query("SELECT e FROM Employee e WHERE e.status = :x")
	Optional<List<Employee>> getByEmployeeStatus(@Param("x") String x);
}
