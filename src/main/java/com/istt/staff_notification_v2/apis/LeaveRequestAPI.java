package com.istt.staff_notification_v2.apis;

import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.istt.staff_notification_v2.apis.errors.BadRequestAlertException;
import com.istt.staff_notification_v2.dto.LeaveRequestDTO;
import com.istt.staff_notification_v2.dto.ResponseDTO;
import com.istt.staff_notification_v2.service.LeaveRequestService;

@RestController
@RequestMapping("/leaveRequest")
public class LeaveRequestAPI {
	@Autowired
	private LeaveRequestService leaveRequestService;

	private static final String ENTITY_NAME = "isttLeaveRequest";

	@PostMapping("")
	public ResponseDTO<LeaveRequestDTO> create(@RequestBody LeaveRequestDTO leaveRequestDTO) throws URISyntaxException {
		if (leaveRequestDTO.getRequestDate() == null || leaveRequestDTO.getReason() == null
				|| leaveRequestDTO.getLeavetype() == null || leaveRequestDTO.getEmployee() == null
				|| leaveRequestDTO.getEmployee().getEmail() == null) {
			throw new BadRequestAlertException("Bad request: missing data", ENTITY_NAME, "missing_level");
		}
		leaveRequestService.create(leaveRequestDTO);
		return ResponseDTO.<LeaveRequestDTO>builder().code(String.valueOf(HttpStatus.OK.value())).data(leaveRequestDTO)
				.build();
	}

}
