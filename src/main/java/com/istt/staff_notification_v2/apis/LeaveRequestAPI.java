package com.istt.staff_notification_v2.apis;

import java.net.URISyntaxException;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.istt.staff_notification_v2.apis.errors.BadRequestAlertException;
import com.istt.staff_notification_v2.dto.LeaveRequestDTO;
import com.istt.staff_notification_v2.dto.MailRequestDTO;
import com.istt.staff_notification_v2.dto.ResponseDTO;
import com.istt.staff_notification_v2.dto.ResponseLeaveRequest;
import com.istt.staff_notification_v2.dto.SearchDTO;
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

//	@PreAuthorize("hasRole('ROLE_LEAVEREQUEST_CREATE')&&hasRole('ROLE_LEAVEREQUEST_ACCESS')")
	@PostMapping("")
	public ResponseDTO<MailRequestDTO> create(@CurrentUser UserPrincipal currentuser, @RequestBody MailRequestDTO mailRequestDTO) throws URISyntaxException {
		if (mailRequestDTO.getLeaveRequestDTO().getRequestDate() == null
				|| mailRequestDTO.getLeaveRequestDTO().getReason() == null
				|| mailRequestDTO.getLeaveRequestDTO().getLeavetype() == null
				|| mailRequestDTO.getLeaveRequestDTO().getEmployee() == null) {
			throw new BadRequestAlertException("Bad request: missing data", ENTITY_NAME, "missing_level");
		}
		User user = userRepo.findById(currentuser.getUser_id()).get();
		if(!mailRequestDTO.getLeaveRequestDTO().getEmployee().getEmployeeId().equals(user.getEmployee().getEmployeeId()))
			throw new BadRequestAlertException("You can only send your leave request", ENTITY_NAME, "missing data");
		leaveRequestService.create(mailRequestDTO);
		return ResponseDTO.<MailRequestDTO>builder().code(String.valueOf(HttpStatus.OK.value())).data(mailRequestDTO)
				.build();
	}
	
	@PutMapping("")
	public ResponseDTO<MailRequestDTO> update(@RequestBody MailRequestDTO mailRequestDTO) throws URISyntaxException {
		if (mailRequestDTO.getLeaveRequestDTO().getRequestDate() == null
				|| mailRequestDTO.getLeaveRequestDTO().getReason() == null
				|| mailRequestDTO.getLeaveRequestDTO().getLeavetype() == null
				|| mailRequestDTO.getLeaveRequestDTO().getEmployee() == null) {
			throw new BadRequestAlertException("Bad request: missing data", ENTITY_NAME, "missing_level");
		}
		leaveRequestService.update(mailRequestDTO);
		return ResponseDTO.<MailRequestDTO>builder().code(String.valueOf(HttpStatus.OK.value())).data(mailRequestDTO)
				.build();
	}
	
	@GetMapping("/{id}")
	public ResponseDTO<LeaveRequestDTO> get(@PathVariable(value = "id") String id){
		if (id == null) {
			throw new BadRequestAlertException("Bad request: missing id", ENTITY_NAME, "missing_id");
		}
		return ResponseDTO.<LeaveRequestDTO>builder().code(String.valueOf(HttpStatus.OK.value())).data(leaveRequestService.get(id)).build();
	}

//	@PreAuthorize("hasRole('ROLE_LEAVEREQUEST_UPDATE')&&hasRole('ROLE_LEAVEREQUEST_ACCESS')")
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

//	@PreAuthorize("hasRole('ROLE_LEAVEREQUEST_VIEW')&&hasRole('ROLE_LEAVEREQUEST_ACCESS')")
	@PostMapping("/getLeaveRequest")
	public ResponseDTO<List<LeaveRequestDTO>> getLeaveRequest(@RequestBody SearchLeaveRequest searchLeaveRequest) {
		return leaveRequestService.searchLeaveRequest(searchLeaveRequest);
	}
	
//	@PreAuthorize("hasRole('ROLE_LEAVEREQUEST_VIEW')&&hasRole('ROLE_LEAVEREQUEST_ACCESS')")
	@PostMapping("/getLeaveThisMonth")
	public ResponseDTO<List<LeaveRequestDTO>> getLeaveThisMonth(@RequestBody @Valid SearchDTO searchDTO) {
		return leaveRequestService.getLeaveThisMonth(searchDTO);
	}
	
//	@PreAuthorize("hasRole('ROLE_LEAVEREQUEST_VIEW')&&hasRole('ROLE_LEAVEREQUEST_ACCESS')")
	@GetMapping("/getAllLeaveThisMonth")
	public ResponseDTO<List<LeaveRequestDTO>> getAllLeaveThisMonth() {
		return leaveRequestService.getAllLeaveThisMonth();
	}
	
//	@PreAuthorize("hasRole('ROLE_LEAVEREQUEST_VIEW')&&hasRole('ROLE_LEAVEREQUEST_ACCESS')")
//	@GetMapping("")
//	public ResponseDTO<List<LeaveRequestDTO>> getLeaveRequest2(@CurrentUser UserPrincipal currentuser) {
//		if(currentuser==null) throw new BadRequestAlertException("User not found", ENTITY_NAME,"missing data");
//		SearchLeaveRequest searchLeaveRequest = new SearchLeaveRequest();
//		User user = userRepo.findById(currentuser.getUser_id()).get();
//		searchLeaveRequest.setMailReciver(user.getEmployee().getEmployeeId());
//		return ResponseDTO.<List<LeaveRequestDTO>>builder().code(String.valueOf(HttpStatus.OK.value()))
//				.data(leaveRequestService.searchLeaveRequest(searchLeaveRequest)).build();
//	}
	
	@PostMapping("/getLeaveHistory")
	public ResponseDTO<List<LeaveRequestDTO>> getLeaveHistory(@CurrentUser UserPrincipal currentuser,SearchLeaveRequest searchLeaveRequest) {
		if(currentuser == null) {
			throw new BadRequestAlertException("Missing id", ENTITY_NAME, "missing data");
		}
		searchLeaveRequest.setEmail(currentuser.getUsername());
		return leaveRequestService.searchLeaveRequest(searchLeaveRequest);
	}
	
}
