package com.istt.staff_notification_v2.apis;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import com.istt.staff_notification_v2.dto.LoginRequest;
import com.istt.staff_notification_v2.dto.ResponseDTO;
import com.istt.staff_notification_v2.entity.User;
import com.istt.staff_notification_v2.repository.UserRepo;
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

	@PostMapping("/signin")
	public ResponseDTO<String> signin(@Valid @RequestBody LoginRequest loginRequest) {

		Optional<User> userOptional = userRepo.findByUsername(loginRequest.getUsername());
		if (userOptional.isEmpty()) {
			throw new BadRequestAlertException("Bad request: User not found.", ENTITY_NAME, "Not Found");
		}

		User user = userOptional.get();

//		Set<Role> roles = user.getGroupRole().getRoles();
//		for (Role role : roles) {
//			System.err.println(role.getRole());
//		}
		
//		System.err.println(user.getGroupRole().getRoles().toString());
		
//		Boolean compare_password = BCrypt.checkpw(loginRequest.getPassword(), user.getPassword());
//		if (!compare_password)
//			throw new BadRequestAlertException("Bad request: Password wrong !!!", ENTITY_NAME, "Password wrong");

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
	
	@GetMapping("/logout")
    public ResponseDTO<String> fetchSignoutSite(HttpServletRequest request, HttpServletResponse response) {        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        String username = "Logout successfull";
//        Object principal = SecurityContextHolder
//        		.getContext().getAuthentication().getPrincipal();
//        if (principal instanceof UserDetails) {
//            username = ((UserDetails) principal).getUsername();
//        } else {
//            username = principal.toString();
//        }
        return ResponseDTO.<String>builder().code(String.valueOf(HttpStatus.OK.value())).data(username).build();
    }
	
	@GetMapping("/getWaiting/{email}")
	public ResponseDTO<Set<EmployeeLeaveDTO>> getWaiting(@PathVariable(value = "email") String email) throws URISyntaxException{
		if (email == null) {
			throw new BadRequestAlertException("Bad request: missing email", ENTITY_NAME, "missing_email");
		}
		return ResponseDTO.<Set<EmployeeLeaveDTO>>builder().code(String.valueOf(HttpStatus.OK.value())).data(leaveRequestService.getApproved(email))
				.build();
	}
	
	@GetMapping("/getLeave")
	public ResponseDTO<Set<EmployeeLeaveDTO>> getLeave() throws URISyntaxException{
		
		return ResponseDTO.<Set<EmployeeLeaveDTO>>builder().code(String.valueOf(HttpStatus.OK.value())).data(attendanceService.getEmployeeLeave())
				.build();
	}
	

}
