package com.istt.staff_notification_v2.apis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.istt.staff_notification_v2.dto.EmployeeDTO;
import com.istt.staff_notification_v2.dto.MailRequestDTO;
import com.istt.staff_notification_v2.service.MailService;

@RestController
@RequestMapping("/email")
public class MailAPI {

	@Autowired
	private MailService mailService;

	@PostMapping("")
	public ResponseEntity<String> sendNotification(@RequestBody MailRequestDTO mailRequestDTO) {

		try {
			System.err.println(mailRequestDTO.getRecceiverList().size());
			for (int i = 0; i < mailRequestDTO.getRecceiverList().size(); i++) {
				EmployeeDTO receiver = new EmployeeDTO();
				receiver = mailRequestDTO.getRecceiverList().get(i);
				mailService.sendEmail(mailRequestDTO.getAttendanceDTO(), receiver, mailRequestDTO.getSubject());

			}
			return ResponseEntity.status(HttpStatus.OK).body("Email sent successfully");

		} catch (Exception e) {
			// TODO: handle exception
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error sending email");

		}

	}

}
