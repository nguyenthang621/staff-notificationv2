package com.istt.staff_notification_v2.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.web.jsf.FacesContextUtils;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Entity
@Table(name = "role")
//@EqualsAndHashCode(callSuper = false, exclude = {"groups", "feature"})
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Role {
	@Id
	@Column(name = "role_id", updatable = false, nullable = false)
	private String roleId;
	
	private String role;

	@Column(columnDefinition = "TEXT")
	private String description;
	
	@JsonIgnore
	@ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
	@EqualsAndHashCode.Exclude // không sử dụng trường này trong equals và hashcode
    @ToString.Exclude
	private Set<Group> groups = new HashSet<>();
	
	@ManyToOne
	@JoinColumn(name = "feature_id")
	@JsonBackReference
	@EqualsAndHashCode.Exclude // không sử dụng trường này trong equals và hashcode
    @ToString.Exclude
	private Feature feature;

}
