package com.istt.staff_notification_v2.apis;

import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.istt.staff_notification_v2.apis.errors.BadRequestAlertException;
import com.istt.staff_notification_v2.dto.AttendanceDTO;
import com.istt.staff_notification_v2.dto.ResponseDTO;
import com.istt.staff_notification_v2.service.AttendanceService;

@RestController
@RequestMapping("/attendance")
public class AttendanceAPI {
	@Autowired
	private AttendanceService attendanceService;

	private static final String ENTITY_NAME = "isttAttendance";

	@PostMapping("")
	public ResponseDTO<AttendanceDTO> create(@RequestBody AttendanceDTO attendanceDTO) throws URISyntaxException {
		if (attendanceDTO.getDate() == null || attendanceDTO.getReason() == null
				 || attendanceDTO.getEmployee() == null || attendanceDTO.getEmployee().getEmail() == null) {
			throw new BadRequestAlertException("Bad request: missing data", ENTITY_NAME, "missing_level");
		}
		attendanceService.create(attendanceDTO);
		return ResponseDTO.<AttendanceDTO>builder().code(String.valueOf(HttpStatus.OK.value())).data(attendanceDTO)
				.build();
	}

}
