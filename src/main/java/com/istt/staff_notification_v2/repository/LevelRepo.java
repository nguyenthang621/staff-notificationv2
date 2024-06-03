package com.istt.staff_notification_v2.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.istt.staff_notification_v2.entity.Level;

public interface LevelRepo extends JpaRepository<Level, String> {

	Optional<Level> findByLevelId(String levelId);

	@Query("SELECT l FROM Level l WHERE l.levelName = :n or l.levelCode = :c ")
	Optional<Level> findByLevelNameorLevelCode(@Param("n") String levelName, @Param("c") Long levelCode);
}
