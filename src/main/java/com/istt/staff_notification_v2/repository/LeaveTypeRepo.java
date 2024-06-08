package com.istt.staff_notification_v2.repository;

import java.util.List;
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

	@Query("SELECT l FROM LeaveType l WHERE l.leavetypeName LIKE :x ")
	Page<LeaveType> searchByLeavetypeName(@Param("x") String s, Pageable pageable);

	@Query("SELECT l FROM LeaveType l WHERE l.leavetypeId in :ids")
	Optional<List<LeaveType>> findByLeaveTypeIds(@Param("ids") List<String> leaveTypeIds);

	@Query("SELECT l FROM LeaveType l")
	Optional<List<LeaveType>> getAll();

}
