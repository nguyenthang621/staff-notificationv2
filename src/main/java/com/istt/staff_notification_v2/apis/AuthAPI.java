package com.istt.staff_notification_v2.apis;

import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import com.istt.staff_notification_v2.apis.errors.BadRequestAlertException;
import com.istt.staff_notification_v2.dto.EmployeeLeaveDTO;
import com.istt.staff_notification_v2.dto.LeaveAprroveDTO;
import com.istt.staff_notification_v2.dto.LoginRequest;
import com.istt.staff_notification_v2.dto.ResponseDTO;
import com.istt.staff_notification_v2.dto.TokenDTO;
import com.istt.staff_notification_v2.entity.User;
import com.istt.staff_notification_v2.repository.UserRepo;
import com.istt.staff_notification_v2.security.securityv2.CurrentUser;
import com.istt.staff_notification_v2.security.securityv2.UserPrincipal;
import com.istt.staff_notification_v2.service.AttendanceService;
import com.istt.staff_notification_v2.service.AuthService;
import com.istt.staff_notification_v2.service.LeaveRequestService;
import com.istt.staff_notification_v2.service.UserService;

@RestController
@RequestMapping("/auth")
public class AuthAPI {
	@Autowired
	UserRepo userRepo;

	@Autowired
	UserService userService;

	@Autowired
	AuthService authService;
	
	@Autowired
	LeaveRequestService leaveRequestService;
	
	@Autowired
	AttendanceService attendanceService;

	private static final String ENTITY_NAME = "isttAuth";
	private static final Logger logger = LogManager.getLogger(AuthAPI.class);

	@PostMapping("/signin")
	public ResponseDTO<String> signin(@Valid @RequestBody LoginRequest loginRequest) {

		Optional<User> userOptional = userRepo.findByUsername(loginRequest.getUsername());
		if (userOptional.isEmpty()) {
			throw new BadRequestAlertException("Login Failed!!!", ENTITY_NAME, "missing data");
		}
		

		User user = userOptional.get();
		
		return authService.signin(loginRequest, user);

	}

//	@PostMapping("/signup")
//	public ResponseDTO<String> signup(@Valid @RequestBody UserDTO userSignUp) {
//		Optional<User> userOptional = userRepo.findByUsername(userSignUp.getUsername());
//		User user = userOptional.get();
//
//		if (user != null)
//			throw new BadRequestAlertException("user " + user.getUsername() + " already exists", ENTITY_NAME,
//					"Password wrong");
//
//		UserDTO userDTO = new ModelMapper().map(user, UserDTO.class);
//		userService.create(userDTO);
//
//		LoginRequest loginRequest = new LoginRequest();
//		loginRequest.setUsername(userSignUp.getUsername());
//		loginRequest.setUsername(userSignUp.getPassword());
//
//		return authService.signup(loginRequest, user);
//
//	}

	@PostMapping("/refreshToken")
	public ResponseDTO<String> handleRefreshToken(
			@RequestParam(value = "refreshtoken", required = true) String refreshtoken) {
		try {
			return authService.handleRefreshToken(refreshtoken);

		} catch (Exception e) {
			throw Problem.builder().withStatus(Status.INTERNAL_SERVER_ERROR).withDetail("SERVER ERROR").build();
		}
	}
	
	@PostMapping("/logout")
    public ResponseDTO<Void> fetchSignoutSite(@RequestBody @Valid TokenDTO tokenDTO ) {        
		if(tokenDTO==null) throw new BadRequestAlertException("Not found token", ENTITY_NAME, "missing data");
		
		authService.logout(tokenDTO.getAccessToken(), tokenDTO.getRefreshToken());
        return ResponseDTO.<Void>builder().code(String.valueOf(HttpStatus.OK.value())).build();
    }
	
	@GetMapping("/getWaiting/{email}")
	public ResponseDTO<Set<EmployeeLeaveDTO>> getWaiting(@PathVariable(value = "email") String email) throws URISyntaxException{
		if (email == null) {
			throw new BadRequestAlertException("Bad request: missing email", ENTITY_NAME, "missing_email");
		}
		
		Set<EmployeeLeaveDTO> employeeLeaveDTOs = leaveRequestService.getApproved(email);
		
//		for (EmployeeLeaveDTO employeeLeaveDTO : employeeLeaveDTOs) {
//			logger.error("employee:"+employeeLeaveDTO.getEmployeeName());
//		}
		
		return ResponseDTO.<Set<EmployeeLeaveDTO>>builder().code(String.valueOf(HttpStatus.OK.value())).data(employeeLeaveDTOs)
				.build();
	}
	
	@GetMapping("/getLeave")
	public ResponseDTO<List<LeaveAprroveDTO>> getLeave() throws URISyntaxException{
		return leaveRequestService.getApproveCurrentDay();
	}
	

}
