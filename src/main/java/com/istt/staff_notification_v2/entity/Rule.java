//package com.istt.staff_notification_v2.entity;
//
//import java.io.Serializable;
//import java.util.List;
//
//import javax.persistence.CascadeType;
//import javax.persistence.Column;
//import javax.persistence.Convert;
//import javax.persistence.Entity;
//import javax.persistence.FetchType;
//import javax.persistence.Id;
//import javax.persistence.JoinColumn;
//import javax.persistence.OneToOne;
//import javax.persistence.Table;
//
//import com.istt.staff_notification_v2.utils.StringListConverter;
//
//import lombok.Data;
//
//@Data
//@Entity
//@Table(name = "rule")
//public class Rule implements Serializable {
//	private static final long serialVersionUID = 1L;
//	@Id
//	@Column(name = "rule_id", updatable = false, nullable = false)
//	private String ruleId;
//
//	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
//	@JoinColumn(name = "employeeId", referencedColumnName = "employee_id")
//	private Employee employee;
//
//	@Convert(converter = StringListConverter.class)
//	@Column(name = "department_dependence")
//	private List<String> departmentDependence;
//
//	@Convert(converter = StringListConverter.class)
//	@Column(name = "employee_dependence")
//	private List<String> employeeDependenceSpecial;
//
//	@Column(columnDefinition = "TEXT")
//	private String description;
//
//}
