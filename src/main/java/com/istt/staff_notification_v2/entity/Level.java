package com.istt.staff_notification_v2.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "level")
@EqualsAndHashCode(callSuper = false)
public class Level {

	@Id
	@Column(name = "level_id", updatable = false, nullable = false)
	private String levelId;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Column(name = "levelname", nullable = false)
	private String levelName;

	@Column(name = "levelcode", nullable = false)
	private Long levelCode;

	@JsonIgnore
	@ManyToMany(mappedBy = "levels")
	private Set<Employee> employees = new HashSet<>();

}
