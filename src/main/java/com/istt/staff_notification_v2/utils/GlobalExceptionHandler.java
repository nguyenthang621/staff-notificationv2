package com.istt.staff_notification_v2.utils;

import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.istt.staff_notification_v2.apis.errors.BadRequestAlertException;
import com.istt.staff_notification_v2.dto.ResponseDTO;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ConfigDataResourceNotFoundException.class)
	public ResponseEntity<ResponseDTO<Object>> handleResourceNotFoundException(ConfigDataResourceNotFoundException ex) {
		ResponseDTO<Object> response = ResponseDTO.builder().message(ex.getMessage())
				.code(String.valueOf(HttpStatus.NOT_FOUND.value())).build();
		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(BadRequestAlertException.class)
	public ResponseEntity<ResponseDTO<Object>> handleBadRequestException(BadRequestAlertException ex) {
		ResponseDTO<Object> response = ResponseDTO.builder().message(ex.getMessage())
				.code(String.valueOf(HttpStatus.BAD_REQUEST.value())).build();
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ResponseDTO<Object>> handleGenericException(Exception ex) {
		ResponseDTO<Object> response = ResponseDTO.builder().message(ex.getMessage())
				.code(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value())).build();
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
