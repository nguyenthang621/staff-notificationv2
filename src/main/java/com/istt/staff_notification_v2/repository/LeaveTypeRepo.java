package com.istt.staff_notification_v2.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.istt.staff_notification_v2.entity.LeaveType;

public interface LeaveTypeRepo extends JpaRepository<LeaveType, String> {
	Optional<LeaveType> findByLeavetypeId(String leavetypeId);

	Optional<LeaveType> findByLeavetypeName(String leavetypeName);

	@Query("SELECT u FROM LeaveType u WHERE u.leavetypeName LIKE :x ")
	Page<LeaveType> searchByLeavetypeName(@Param("x") String s, Pageable pageable);

}
