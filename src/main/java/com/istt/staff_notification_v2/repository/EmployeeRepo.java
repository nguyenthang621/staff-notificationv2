package com.istt.staff_notification_v2.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.istt.staff_notification_v2.entity.Employee;

@Repository
public interface EmployeeRepo extends JpaRepository<Employee, String> {
//	@Query("SELECT e FROM Employee e WHERE e.username LIKE :x ")
//	Page<Employee> searchByUsername(@Param("x") String s);
//
//	@Query("SELECT e FROM Employee e WHERE e.username LIKE :x ")
//	Page<Employee> find(@Param("x") String value);

	@Query("SELECT e FROM Employee e WHERE e.email = :x ")
	Employee findByEmail(@Param("x") String value);

	@Query("SELECT e FROM Employee e WHERE e.employeeId = :x ")
	Optional<Employee> findByEmployeeId(@Param("x") String employeeId);

	@Query("SELECT e FROM Employee e WHERE e.employeeId = :x or e.email = :y")
	Optional<Employee> findByEmployeeIdOrEmail(@Param("x") String employeeId, @Param("y") String email);

	Boolean existsByEmail(String email);

}
