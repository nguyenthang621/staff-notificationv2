package com.istt.staff_notification_v2.dto;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.istt.staff_notification_v2.dto.SearchDTO.OrderBy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class SearchLeaveRequest {
	
	public static final int MAX_20 = 20;
	public static final int MAX_200 = 200;
	public static final String ASC = "asc";
	public static final String DESC = "desc";

	@Min(value = 0)
	private int page;

	@Min(value = 1)
	@Max(value = MAX_200)
	@Builder.Default
	private int size = MAX_200;

	@Builder.Default
	private String value = "%%";

	private List<OrderBy> orders;

	@Builder.Default
	private Map<String, String> filterBys = new HashMap<>();

	@Data
	@ToString
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class OrderBy {
		@Builder.Default
		private String order = ASC;// asc, desc
		private String property;
	}
	
	private String email;

	private String status;

	private Date startDate;

	private String mailReciver;

}
