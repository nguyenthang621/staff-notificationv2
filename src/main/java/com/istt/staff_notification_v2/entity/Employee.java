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
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.web.jsf.FacesContextUtils;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.istt.staff_notification_v2.utils.StringListConverter;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "employee")
@EqualsAndHashCode(callSuper = false, exclude = { "levels", "department", "parent", "subordinatesOdoo" })
//@EqualsAndHashCode(callSuper = false, exclude = { "levels", "department", "parent" })
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Employee {
	@Id
	@Column(name = "employee_id", updatable = false, nullable = false)
	private String employeeId;

	@Column(name = "staff_id")
	private Long staffId;

	@ManyToOne
	@JoinColumn(name = "parent_id")
	@JsonBackReference
	private Employee parent;

	@OneToMany(mappedBy = "parent")
	@JsonManagedReference
	private List<Employee> subordinatesOdoo;

	@Column(name = "uodoo_id")
	private String uOdooId;

	private String fullname;

	private float countOfDayOff;

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

	private Date dateofbirth;

	private Date hiredate;

	private Date offdate;
	
	@Column(name ="work_city", nullable = false)
	private String workCity;

	@Convert(converter = StringListConverter.class)
	@Column(name = "employeeDependence")
	private List<String> employeeDependence;

	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name = "employee_level", joinColumns = @JoinColumn(name = "employee_id"), inverseJoinColumns = @JoinColumn(name = "level_id"))
	private Set<Level> levels = new HashSet<>();

}