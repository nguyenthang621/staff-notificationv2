package com.istt.staff_notification_v2.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.istt.staff_notification_v2.entity.Department;
import com.istt.staff_notification_v2.entity.Feature;


public interface FeatureRepo extends JpaRepository<Feature, String> {
	@Query("SELECT u FROM Feature u WHERE u.featureName = :x ")
	Optional<Feature> findByFeatureName(@Param("x") String name);
	
	@Query("SELECT u FROM Feature u WHERE u.featureId = :x ")
	Optional<Feature> findByFeatureId(@Param("x") String name);
}
