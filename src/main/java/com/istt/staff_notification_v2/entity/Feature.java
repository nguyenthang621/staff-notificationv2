package com.istt.staff_notification_v2.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
@Data
@Entity
@Table(name = "feature")
//@EqualsAndHashCode(callSuper = false, exclude = {"roles","feature"})
//@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Feature {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "feature_id", updatable = false, nullable = false)
	private String featureId;

	@Column(name = "feature_name", nullable = false, unique = true)
	private String featureName;
	
	@OneToMany(mappedBy = "feature", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	@EqualsAndHashCode.Exclude // không sử dụng trường này trong equals và hashcode
    @ToString.Exclude
    private List<Role> roles = new ArrayList<Role>();

}
