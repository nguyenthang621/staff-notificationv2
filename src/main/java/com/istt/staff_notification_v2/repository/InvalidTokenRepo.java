package com.istt.staff_notification_v2.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.istt.staff_notification_v2.entity.InvalidToken;

public interface InvalidTokenRepo extends JpaRepository<InvalidToken, String> {
	boolean existsById(String id);
	
	@Query("SELECT a from InvalidToken a where a.expiryTime < :x")
	Optional<List<InvalidToken>> getExpireToken(@Param("x") Date date);
	
}
