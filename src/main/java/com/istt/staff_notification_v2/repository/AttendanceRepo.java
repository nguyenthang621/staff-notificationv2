package com.istt.staff_notification_v2.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

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

	@Query("Select a from Attendance a where a.leaveType.leavetypeName = :x ")
	Page<Attendance> findByType(@Param("x") String value, Pageable pageable);

	@Query("Select a from Attendance a where a.employee.fullname like :x OR a.employee.email like :x")
	Page<Attendance> searchByEmployeeName(@Param("x") String value, Pageable pageable);

	@Query("SELECT a FROM Attendance a WHERE (a.employee.fullname LIKE :x OR a.employee.email LIKE :x) AND a.createAt BETWEEN :a AND :b AND a.leaveType.leavetypeName = :t")
	Page<Attendance> searchByMulti(@Param("x") String value, @Param("a") Date start, @Param("b") Date end,
			@Param("t") String leaveType, Pageable pageable);

	@Query("SELECT a FROM Attendance a WHERE (a.employee.fullname LIKE :x OR a.employee.email LIKE :x) AND a.startDate <= :b AND a.endDate >= :a")
	Page<Attendance> searchByMultiAllType(@Param("x") String value, @Param("a") Date start, @Param("b") Date end,
			Pageable pageable);

	@Query("SELECT a FROM Attendance a WHERE "
			+ "(a.year > :startYear OR (a.year = :startYear AND (a.month > :startMonth OR (a.month = :startMonth AND a.day >= :startDay)))) AND "
			+ "(a.year < :endYear OR (a.year = :endYear AND (a.month < :endMonth OR (a.month = :endMonth AND a.day <= :endDay)))) AND a.employee.fullname LIKE :x")
	Page<Attendance> searchByIndex(@Param("x") String value, @Param("startYear") Long startYear,
			@Param("startMonth") Long startMonth, @Param("startDay") Long startDay, @Param("endYear") Long endYear,
			@Param("endMonth") Long endMonth, @Param("endDay") Long endDay, Pageable pageable);

	@Query("SELECT a FROM Attendance a WHERE "
			+ "(a.year > :startYear OR (a.year = :startYear AND (a.month > :startMonth OR (a.month = :startMonth AND a.day >= :startDay)))) AND "
			+ "(a.year < :endYear OR (a.year = :endYear AND (a.month < :endMonth OR (a.month = :endMonth AND a.day <= :endDay))))")
	List<Attendance> findByIndex(@Param("startYear") Long startYear, @Param("startMonth") Long startMonth,
			@Param("startDay") Long startDay, @Param("endYear") Long endYear, @Param("endMonth") Long endMonth,
			@Param("endDay") Long endDay);

	@Query("Select a from Attendance a where a.leaveType.leavetypeName = :x ")
	List<Attendance> getType(@Param("x") String value);

	@Query("Select a from Attendance a where a.startDate = :d and a.employee.employeeId = :y ")
	Optional<Attendance> findByStartDate(@Param("d") Date startDate, @Param("y") String employee_id);

}
