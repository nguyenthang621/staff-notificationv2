package com.istt.staff_notification_v2.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "employee")
@EqualsAndHashCode(callSuper = false, exclude = "user")
public class Employee {
	@Id
	@Column(name = "id", updatable = false, nullable = false)
	private String id;

	private String fullname;

	private String address;

	private String phone;

	@Column(name = "email", unique = true)
	private String email;

	@ManyToOne
	@JoinColumn(name = "department_id")
	private Department department;

	private String avatar;

	private String status;

	@JsonFormat(pattern = "dd/MM/yyyy")
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private Date dateofbirth;

	@JsonFormat(pattern = "dd/MM/yyyy")
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private Date hiredate;

	@JsonFormat(pattern = "dd/MM/yyyy")
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private Date offdate;

	@OneToOne
	@JoinColumn(name = "user_id", referencedColumnName = "user_id")
	private User user;

	@Override
	public String toString() {
		return "Employee{" + "id=" + id + ", fullname='" + fullname + '\'' + ", address='" + address + '\''
				+ ", avatar='" + avatar + '\'' + ", phone=" + phone + ", department=" + department + ", email='" + email
				+ '\'' + ", dateofbirth='" + dateofbirth + '\'' + ", user='" + user + '\'' + ", status=" + status + '}';
	}

}