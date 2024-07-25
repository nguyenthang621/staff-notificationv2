package com.istt.staff_notification_v2.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "invalidToken")
public class InvalidToken {
	@Id
	private String id;
	private Date expiryTime;
}
