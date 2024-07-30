package com.istt.staff_notification_v2.entity;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.springframework.core.serializer.Deserializer;
import org.springframework.core.serializer.Serializer;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@Table(name = "leavetype")
@EqualsAndHashCode(callSuper = false)
public class LeaveType{
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "leavetype_id", updatable = false, nullable = false)
	private String leavetypeId;

	@Column(name = "leavetype_name")
	private String leavetypeName;

	private String description;

	@Column(name = "special_type", nullable = false)
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean specialType;
	
//	private void writeObject(ObjectOutputStream oos) throws IOException {
//        oos.defaultWriteObject();
//    }
//
//    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
//        ois.defaultReadObject();
//    }


}
