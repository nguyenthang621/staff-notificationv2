package com.istt.staff_notification_v2.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.istt.staff_notification_v2.entity.User;

@Repository
public interface UserRepo extends JpaRepository<User, String> {

	Optional<User> findByUsername(String username);

//	Optional<User> findByUser_id(String user_id);

	Optional<User> findByAccessToken(String accesstoken);

}