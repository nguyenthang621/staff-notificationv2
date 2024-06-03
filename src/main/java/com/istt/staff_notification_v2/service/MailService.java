package com.istt.staff_notification_v2.service;

import java.nio.charset.StandardCharsets;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import com.istt.staff_notification_v2.dto.AttendanceDTO;
import com.istt.staff_notification_v2.dto.EmployeeDTO;
import com.istt.staff_notification_v2.dto.MailDTO;
import com.istt.staff_notification_v2.repository.AttendanceRepo;
import com.istt.staff_notification_v2.repository.EmployeeRepo;

public interface MailService {
	void sendEmail(AttendanceDTO attendanceDTO, EmployeeDTO receiver, String subject);
}

@Service
class MailServiceImpl implements MailService {
//	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private JavaMailSender javaMailSender;

	@Autowired
	private SpringTemplateEngine templateEngine;

	@Autowired
	EmployeeRepo employeeRepo;

	@Autowired
	AttendanceRepo attendanceRepo;

	@Override
	public void sendEmail(AttendanceDTO attendanceDTO, EmployeeDTO receiver, String subject) {
		// User user =
		// userRepo.findByUsernameForgotPassword(userDTO.getUsername()).orElseThrow(NoResultException::new);
		MailDTO mailDTO = new MailDTO();

		try {

			String sender = attendanceDTO.getEmployee().getEmail();
			String department_name = attendanceDTO.getEmployee().getDepartment().getDepartment_name();
			String receiver_name = receiver.getEmail();
			String receiver_email = receiver.getEmail();

			System.err.println("department_name " + department_name);
			System.err.println("sender " + sender);
			System.err.println("receiver_name " + receiver_name);
			System.err.println("receiver_email " + receiver_email);

			mailDTO.setSubject(subject);

			System.err.println("subject " + mailDTO.getSubject());
			MimeMessage email = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(email, StandardCharsets.UTF_8.name());

			mailDTO.setContent(attendanceDTO.getReason());
			System.err.println("Content " + mailDTO.getContent());
			// load template email with content
			Context context = new Context();
			context.setVariable("receiver_name", receiver_name);
			context.setVariable("sender_name", sender);
			context.setVariable("department_name", department_name);

			context.setVariable("content", mailDTO.getContent());
			String html = templateEngine.process("email-reason", context);
			/// send email
			System.out.println(receiver_email + "  " + mailDTO.getFrom());
			helper.setTo(receiver_email);
			helper.setText(html, true);
			helper.setSubject(mailDTO.getSubject());
			helper.setFrom(mailDTO.getFrom());
			javaMailSender.send(email);

			System.out.println("END... Email sent success");
		} catch (MessagingException e) {
			System.out.println("Email sent with error: " + e.getMessage());
		}
	}
}