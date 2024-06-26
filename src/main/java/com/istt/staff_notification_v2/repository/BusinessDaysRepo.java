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

import com.istt.staff_notification_v2.entity.BusinessDays;

@Repository
public interface BusinessDaysRepo extends JpaRepository<BusinessDays, String> {
	@Query("Select u from BusinessDays u")
	List<BusinessDays> getAll();

	@Query("Select u from BusinessDays u where u.bussinessdaysId = :x")
	Optional<BusinessDays> findByBussinessdaysId(@Param("x") String id);

	@Query("Select a from BusinessDays a where a.type = :x ")
	Page<BusinessDays> findByType(@Param("x") String value, Pageable pageable);

	@Query("select a from BusinessDays a where (a.startdate BETWEEN :x AND :y"
			+ " OR a.enddate  BETWEEN :x AND :y) and a.type like %:z%")
	Page<BusinessDays> search(@Param("x") Date x, @Param("y") Date y, @Param("z") String z, Pageable pageable);

	@Query("select startdate from BusinessDays b where b.startdate between :x and :y")
	List<BusinessDays> getListStartDateCheck(@Param("x") Date startDate, @Param("y") Date endDate);

	Boolean existsByStartdate(Date startDate);
}