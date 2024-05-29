package com.istt.staff_notification_v2.dto;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDTO<T> {

	@Builder.Default
	private String code = String.valueOf(HttpStatus.OK.value());

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String message;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Long totalElements;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Long numberOfElements;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Long totalPages;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private T data;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String refreshToken; // Thêm trường token

}
