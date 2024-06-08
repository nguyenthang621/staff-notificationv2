package com.istt.staff_notification_v2.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.istt.staff_notification_v2.utils.StringListConverter;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "employee")
@EqualsAndHashCode(callSuper = false, exclude = { "levels", "department" })
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Employee {
	@Id
	@Column(name = "employee_id", updatable = false, nullable = false)
	private String employeeId;

	private String fullname;

	private String address;

	@Column(unique = true)
	private String phone;

	@Column(name = "email", unique = true, nullable = false)
	private String email;

	@ManyToOne
	@JoinColumn(name = "department_id")
	private Department department;

	private String avatar;

	private String status;

//	@JsonFormat(pattern = "dd/MM/yyyy")
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private Date dateofbirth;

//	@JsonFormat(pattern = "dd/MM/yyyy")
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private Date hiredate;

//	@JsonFormat(pattern = "dd/MM/yyyy")
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private Date offdate;

	@Convert(converter = StringListConverter.class)
	@Column(name = "employeeDependence")
	private List<String> employeeDependence;

	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name = "employee_level", joinColumns = @JoinColumn(name = "employee_id"), inverseJoinColumns = @JoinColumn(name = "level_id"))
	private Set<Level> levels = new HashSet<>();

}