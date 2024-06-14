package com.istt.staff_notification_v2.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.istt.staff_notification_v2.entity.Attendance;

@Repository
public interface AttendanceRepo extends JpaRepository<Attendance, String> {
	@Query("Select a from Attendance a")
	List<Attendance> getAll();

	@Query("Select a from Attendance a where a.leavetype.leavetypeName = :x ")
	Page<Attendance> findByType(@Param("x") String value, Pageable pageable);

	@Query("Select a from Attendance a where a.employee.fullname like :x OR a.employee.email like :x")
	Page<Attendance> searchByEmployeeName(@Param("x") String value, Pageable pageable);

	@Query("SELECT a FROM Attendance a WHERE (a.employee.fullname LIKE :x OR a.employee.email LIKE :x) AND a.createAt BETWEEN :a AND :b AND a.leavetype.leavetypeName = :t")
	Page<Attendance> searchByMulti(@Param("x") String value, @Param("a") Date start, @Param("b") Date end,
			@Param("t") String leaveType, Pageable pageable);

	@Query("SELECT a FROM Attendance a WHERE (a.employee.fullname LIKE :x OR a.employee.email LIKE :x) AND a.createAt BETWEEN :a AND :b")
	Page<Attendance> searchByMultiAllType(@Param("x") String value, @Param("a") Date start, @Param("b") Date end,
			Pageable pageable);

	@Query("Select a from Attendance a where a.leavetype.leavetypeName = :x ")
	List<Attendance> getType(@Param("x") String value);

}
