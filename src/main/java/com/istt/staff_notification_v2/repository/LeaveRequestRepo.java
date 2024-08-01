package com.istt.staff_notification_v2.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.istt.staff_notification_v2.entity.Employee;
import com.istt.staff_notification_v2.entity.LeaveRequest;

public interface LeaveRequestRepo extends JpaRepository<LeaveRequest, String> {
	Optional<LeaveRequest> findByLeaveqequestId(String findByLeaveRequestId);

	Optional<List<LeaveRequest>> findByEmployee(Employee employee);

	@Query("SELECT l FROM LeaveRequest l WHERE l.employee.email = :x")
	Optional<List<LeaveRequest>> findEmail(@Param("x") String email);

	@Query("SELECT l FROM LeaveRequest l WHERE l.employee.email = :x AND l.status = :t")
	Optional<List<LeaveRequest>> findEmailAndStatus(@Param("x") String email, @Param("t") String status);

	@Query("SELECT l FROM LeaveRequest l WHERE l.employee.email = :x AND l.status = :t AND l.startDate > :d")
	Optional<List<LeaveRequest>> find(@Param("x") String email, @Param("t") String status, @Param("t") Date d);

	@Query("SELECT l FROM LeaveRequest l WHERE l.receiver = :x AND l.status = :t AND l.startDate > :d")
	Optional<List<LeaveRequest>> findByReceiver(@Param("x") String receiver, @Param("t") String status,
			@Param("t") Date d);
	
	@Query("SELECT l FROM LeaveRequest l WHERE l.receiver = :x AND l.status = :t")
	Optional<List<LeaveRequest>> findByReceiverStatus(@Param("x") String receiver, @Param("t") String status);

	@Query("SELECT l FROM LeaveRequest l WHERE l.status = :t")
	Optional<List<LeaveRequest>> findstatus(@Param("t") String status);
	
	Optional<List<LeaveRequest>> findByStatusOrderByResponseDateDesc(@Param("t") String status);
	
//	@Query("SELECT l FROM LeaveRequest l WHERE l.status = :t AND (l.responseDate between :x and :y) ORDER BY l.responseDate desc")
	@Query("SELECT l FROM LeaveRequest l WHERE l.status like :z and l.requestDate between :x and :y ORDER BY l.requestDate desc")
	Optional<List<LeaveRequest>> findByStatusReqdateDesc(@Param("x") Date startdate,@Param("y") Date enddate, @Param("z") String status);

	@Query("SELECT COUNT(l) FROM LeaveRequest l")
	Optional<Long> returnCheckCount();

}
