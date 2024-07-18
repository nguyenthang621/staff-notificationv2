package com.istt.staff_notification_v2.apis;

import java.net.URISyntaxException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.istt.staff_notification_v2.apis.errors.BadRequestAlertException;
import com.istt.staff_notification_v2.dto.LeaveRequestDTO;
import com.istt.staff_notification_v2.dto.MailRequestDTO;
import com.istt.staff_notification_v2.dto.ResponseDTO;
import com.istt.staff_notification_v2.dto.ResponseLeaveRequest;
import com.istt.staff_notification_v2.dto.SearchLeaveRequest;
import com.istt.staff_notification_v2.entity.User;
import com.istt.staff_notification_v2.repository.UserRepo;
import com.istt.staff_notification_v2.security.securityv2.CurrentUser;
import com.istt.staff_notification_v2.security.securityv2.UserPrincipal;
import com.istt.staff_notification_v2.service.LeaveRequestService;

@RestController
@RequestMapping("/leaveRequest")
public class LeaveRequestAPI {
	@Autowired
	private LeaveRequestService leaveRequestService;
	
	@Autowired
	private UserRepo userRepo ;

	private static final String ENTITY_NAME = "isttLeaveRequest";

//	@PreAuthorize("hasRole('ROLE_LEAVEREQUEST_CREATE')")
	@PostMapping("")
	public ResponseDTO<MailRequestDTO> create(@RequestBody MailRequestDTO mailRequestDTO) throws URISyntaxException {
		if (mailRequestDTO.getLeaveRequestDTO().getRequestDate() == null
				|| mailRequestDTO.getLeaveRequestDTO().getReason() == null
				|| mailRequestDTO.getLeaveRequestDTO().getLeavetype() == null
				|| mailRequestDTO.getLeaveRequestDTO().getEmployee() == null) {
			throw new BadRequestAlertException("Bad request: missing data", ENTITY_NAME, "missing_level");
		}
		leaveRequestService.create(mailRequestDTO);
		return ResponseDTO.<MailRequestDTO>builder().code(String.valueOf(HttpStatus.OK.value())).data(mailRequestDTO)
				.build();
	}

//	@PreAuthorize("hasRole('ROLE_LEAVEREQUEST_CHANGESTATUS')")
	@PostMapping("/changeStatusLeaveRequest")
	public ResponseDTO<ResponseLeaveRequest> changeStatusLeaveRequest(
			@RequestBody ResponseLeaveRequest responseLeaveRequest) throws URISyntaxException {
		if (responseLeaveRequest.getLeaveqequestId() == null || responseLeaveRequest.getStatus() == null) {
			throw new BadRequestAlertException("Bad request: missing data", ENTITY_NAME, "missing_data");
		}
		leaveRequestService.changeStatusLeaveRequest(responseLeaveRequest);
		return ResponseDTO.<ResponseLeaveRequest>builder().code(String.valueOf(HttpStatus.OK.value()))
				.data(responseLeaveRequest).build();
	}

//	@PreAuthorize("hasRole('ROLE_LEAVEREQUEST_GET')")
	@PostMapping("/getLeaveRequest")
	public ResponseDTO<List<LeaveRequestDTO>> getLeaveRequest(@RequestBody SearchLeaveRequest searchLeaveRequest) {
		return ResponseDTO.<List<LeaveRequestDTO>>builder().code(String.valueOf(HttpStatus.OK.value()))
				.data(leaveRequestService.searchLeaveRequest(searchLeaveRequest)).build();
	}
//	@PostMapping("/test")
//	public ResponseDTO<List<LeaveRequestDTO>> test(@RequestBody SearchLeaveRequest searchLeaveRequest) {
//		return ResponseDTO.<List<LeaveRequestDTO>>builder().code(String.valueOf(HttpStatus.OK.value()))
//				.data(leaveRequestService.test(searchLeaveRequest.getStatus())).build();
//	}
	
	@GetMapping("/test")
	public ResponseDTO<List<LeaveRequestDTO>> test(@CurrentUser UserPrincipal currentUser) {
		if(currentUser==null) throw new BadRequestAlertException("Not Found User", ENTITY_NAME, "missing data");
		return ResponseDTO.<List<LeaveRequestDTO>>builder().code(String.valueOf(HttpStatus.OK.value()))
				.data(leaveRequestService.testPheduyet(currentUser.getUser_id())).build();
	}
	
//	@PreAuthorize("hasRole('ROLE_LEAVEREQUEST_ACCESS')")
	@GetMapping("")
	public ResponseDTO<List<LeaveRequestDTO>> getLeaveRequest2(@CurrentUser UserPrincipal currentuser) {
		if(currentuser==null) throw new BadRequestAlertException("User not found", ENTITY_NAME,"missing data");
		SearchLeaveRequest searchLeaveRequest = new SearchLeaveRequest();
		User user = userRepo.findById(currentuser.getUser_id()).get();
		searchLeaveRequest.setMailReciver(user.getEmployee().getEmail());
		return ResponseDTO.<List<LeaveRequestDTO>>builder().code(String.valueOf(HttpStatus.OK.value()))
				.data(leaveRequestService.searchLeaveRequest(searchLeaveRequest)).build();
	}
}
