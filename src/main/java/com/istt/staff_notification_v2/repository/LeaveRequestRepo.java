package com.istt.staff_notification_v2.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.istt.staff_notification_v2.entity.Employee;
import com.istt.staff_notification_v2.entity.LeaveRequest;

public interface LeaveRequestRepo extends JpaRepository<LeaveRequest, String> {
	Optional<LeaveRequest> findByLeaveqequestId(String findByLeaveRequestId);

	Optional<List<LeaveRequest>> findByEmployee(Employee employee);

	@Query("SELECT COUNT(l) FROM LeaveRequest l")
	Optional<Long> returnCheckCount();

}
