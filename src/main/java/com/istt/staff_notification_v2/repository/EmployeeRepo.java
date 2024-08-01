package com.istt.staff_notification_v2.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.istt.staff_notification_v2.entity.Department;
import com.istt.staff_notification_v2.entity.Employee;
import com.istt.staff_notification_v2.entity.Level;

@Repository
public interface EmployeeRepo extends JpaRepository<Employee, String> {
	@Query("SELECT u FROM Employee u WHERE u.fullname LIKE :x AND u.status = :s")
	Page<Employee> searchByFullname(@Param("x") String s, @Param("s") String status, Pageable pageable);

	@Query("SELECT u FROM Employee u WHERE u.email LIKE :x  AND u.status = :s")
	Page<Employee> searchByEmail(@Param("x") String s, @Param("s") String status, Pageable pageable);

	@Query("SELECT u FROM Employee u WHERE u.fullname LIKE :x and u.email LIKE :y AND u.status = :s")
	Page<Employee> searchByFullnameAndEmail(@Param("x") String name, @Param("y") String email,
			@Param("s") String status, Pageable pageable);

	@Query("SELECT u FROM Employee u WHERE u.fullname LIKE :x AND u.status = :s and u.department.departmentName = :y")
	Page<Employee> searchByFullnameAndDepartment(@Param("x") String name, @Param("y") String department,
			@Param("s") String status, Pageable pageable);

	@Query("SELECT u FROM Employee u WHERE u.fullname LIKE :x AND u.status = :s and u.countOfDayOff < :y")
	Page<Employee> searchByFullnameAndCountOff(@Param("x") String name, @Param("y") float countOff,
			@Param("s") String status, Pageable pageable);
	
	@Query("SELECT e FROM Employee e WHERE e.email = :x ")
	Employee findByEmail(@Param("x") String value);

	@Query("SELECT e FROM Employee e WHERE e.employeeId = :x ")
	Optional<Employee> findByEmployeeId(@Param("x") String employeeId);

	@Query("SELECT e FROM Employee e WHERE e.employeeId = :x or e.email = :y order by e.department.departmentId")
	Optional<Employee> findByEmployeeIdOrEmail(@Param("x") String employeeId, @Param("y") String email);

	Boolean existsByEmail(String email);

	Boolean existsByStaffId(Long staffId);

	Boolean existsByEmployeeId(String employeeId);

	@Query("SELECT e FROM Employee e WHERE e.department = :x AND e.status = :y")
	Optional<List<Employee>> findAllByDepartmentId(@Param("x") Department department, @Param("y") String status);

	@Query("SELECT e FROM Employee e WHERE e.employeeId in :ids")
	Optional<List<Employee>> findByEmployeeIds(@Param("ids") List<String> employeeIds);

	@Query("SELECT e FROM Employee e WHERE e.status = :x")
	Optional<List<Employee>> getByEmployeeStatus(@Param("x") String x);

	@Query("SELECT a from Employee a")
	List<Employee> getAll();
	
	@Query("SELECT a from Employee a where a.jobTitle like :x")
	List<Employee> filterLevel(@Param("x") String x);
	
	List<Employee> findByOrderByHiredateAsc();
	
	@Query("SELECT a from Employee a where a.parent.employeeId = :x")
	List<Employee> findByParent(@Param("x") String parentEmployeeId);
	
//	@Query("SELECT e FROM Employee e WHERE e.status = :x and e.countOfDayOff <0")
//	Optional<List<Employee>> getByEmployeeStatusAndCalCountOff(@Param("x") String x);

//	@Query("SELECT u.employeeId, u.fullname, u.phone, u.email, u.department, u.avatar, u.status, u.dateofbirth, u.levels FROM Employee u WHERE u.fullname LIKE :x and u.department.departmentName LIKE :y AND u.status = :s")
//	Page<Employee> searchByCountOff(@Param("x") String name, @Param("y") String department,
//			@Param("s") String status, Pageable pageable);
	
}
