package com.istt.staff_notification_v2.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.istt.staff_notification_v2.entity.Employee;
import com.istt.staff_notification_v2.entity.LeaveRequest;

public interface LeaveRequestRepo extends JpaRepository<LeaveRequest, String> {
	Optional<LeaveRequest> findByLeaveqequestId(String findByLeaveRequestId);

	Optional<List<LeaveRequest>> findByEmployee(Employee employee);

	@Query("SELECT l FROM LeaveRequest l WHERE l.employee.email = :x order by l.requestDate DESC")
	Page<LeaveRequest> findEmail(@Param("x") String email, Pageable pageable);

	@Query("SELECT l FROM LeaveRequest l WHERE l.employee.email = :x AND l.status = :t")
	Page<LeaveRequest> findEmailAndStatus(@Param("x") String email, @Param("t") String status,Pageable pageable);

	@Query("SELECT l FROM LeaveRequest l WHERE l.employee.email = :x AND l.status = :t AND l.startDate > :d")
	Page<LeaveRequest> find(@Param("x") String email, @Param("t") String status, @Param("t") Date d, Pageable pageable);

	@Query("SELECT l FROM LeaveRequest l WHERE l.receiver = :x AND l.status = :t AND l.startDate > :d")
	Page<LeaveRequest> findByReceiver(@Param("x") String receiver, @Param("t") String status,
			@Param("t") Date d, Pageable pageable);
	
	@Query("SELECT l FROM LeaveRequest l WHERE l.receiver = :x AND l.status = :t")
	Page<LeaveRequest> findByReceiverStatus(@Param("x") String receiver, @Param("t") String status, Pageable pageable);

	@Query("SELECT l FROM LeaveRequest l WHERE l.receiver = :x AND l.status = :t")
	Optional<List<LeaveRequest>> findByReceiverandStatus(@Param("x") String receiver, @Param("t") String status);

	
	@Query("SELECT l FROM LeaveRequest l WHERE l.status = :t")
	Optional<List<LeaveRequest>> findstatus(@Param("t") String status);
	
	Page<LeaveRequest> findByStatusOrderByResponseDateDesc(@Param("t") String status, Pageable pageable);
	
	@Query("SELECT l FROM LeaveRequest l WHERE l.status like :z and l.requestDate between :x and :y ORDER BY l.requestDate desc")
	Page<LeaveRequest> findByStatusReqdateDesc(@Param("z") String status,@Param("x") Date startDate, @Param("y") Date endDate, Pageable pageable);
	
	@Query("SELECT l FROM LeaveRequest l where l.requestDate between :x and :y")
	Optional<List<LeaveRequest>> findByReqdate(@Param("x") Date startDate, @Param("y") Date endDate);
	
	@Query("SELECT l FROM LeaveRequest l where l.status like :z and l.responseDate between :x and :y")
	Optional<List<LeaveRequest>> findByResponsedate(@Param("x") Date startDate, @Param("y") Date endDate, @Param("z") String status);
	@Query("SELECT COUNT(l) FROM LeaveRequest l")
	Optional<Long> returnCheckCount();
	
	@Query("SELECT l FROM LeaveRequest l where( l.requestDate between :x and :y) and l.employee.employeeId = :e")
	Optional<List<LeaveRequest>> findByReqDateAndEmployeeId(@Param("e") String employeeId,@Param("x") Date requestDate1, @Param("y") Date requestDate2);
}
