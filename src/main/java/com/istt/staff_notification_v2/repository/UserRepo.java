package com.istt.staff_notification_v2.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.istt.staff_notification_v2.entity.User;

@Repository
public interface UserRepo extends JpaRepository<User, String> {

	Optional<User> findByUsername(String username);

//	@Query("SELECT u FROM User u WHERE u.userId = :x ")
	@EntityGraph(attributePaths = { "roles", "employee" })
	Optional<User> findByUserId(String s);

	Optional<User> findByAccessToken(String accesstoken);

	Boolean existsByUsername(String username);

}