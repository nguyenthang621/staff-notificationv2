package com.istt.staff_notification_v2.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.istt.staff_notification_v2.entity.Level;

public interface LevelRepo extends JpaRepository<Level, String> {

	Optional<Level> findByLevelId(String levelId);

	@Query("SELECT l FROM Level l WHERE l.levelName = :n or l.levelCode = :c ")
	Optional<Level> findByLevelNameorLevelCode(@Param("n") String levelName, @Param("c") Long levelCode);

	@Query("SELECT l FROM Level l WHERE l.levelName LIKE :x ")
	Page<Level> searchByLevelName(@Param("x") String s, Pageable pageable);

	@Query("SELECT l FROM Level l WHERE l.levelId in :ids")
	Optional<List<Level>> findByLevelIds(@Param("ids") List<String> levelIds);

	@Query("SELECT l FROM Level l")
	Optional<List<Level>> getAll();
	
	@Query("SELECT l FROM Level l WHERE l.description = :c ")
	Optional<Level> findByLevelDes(@Param("c") String des);
	
}
