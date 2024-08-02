package com.istt.staff_notification_v2.service;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import com.istt.staff_notification_v2.dto.EmployeeDTO;
import com.istt.staff_notification_v2.dto.LeaveRequestDTO;
import com.istt.staff_notification_v2.dto.MailDTO;
import com.istt.staff_notification_v2.entity.Employee;
import com.istt.staff_notification_v2.repository.EmployeeRepo;
import com.istt.staff_notification_v2.utils.utils;

public interface MailService {
	void sendEmail(LeaveRequestDTO leaveRequestDTO, EmployeeDTO receiver, String subject);

	void sendReponseApprovedEmail(LeaveRequestDTO leaveRequestDTO, EmployeeDTO sender, String subject);

	void sendReponseRejectEmail(LeaveRequestDTO leaveRequestDTO, EmployeeDTO sender, String subject, String reason,
			String status);
}

@Service
class MailServiceImpl implements MailService {

	@Autowired
	private JavaMailSender javaMailSender;

	@Autowired
	private SpringTemplateEngine templateEngine;

	@Autowired
	EmployeeRepo employeeRepo;
	
	private static final Logger logger = LogManager.getLogger(MailServiceImpl.class);
	

	@Async
	@Override
	public void sendEmail(LeaveRequestDTO leaveRequestDTO, EmployeeDTO receiver, String subject) {
		MailDTO mailDTO = new MailDTO();

		try {
			Employee employeeSender = employeeRepo.findByEmployeeId(leaveRequestDTO.getEmployee().getEmployeeId())
					.get();

			String senderEmail = employeeSender.getEmail();
			String senderDepartment = employeeSender.getDepartment().getDepartmentName();
			String senderName = employeeSender.getFullname();
//			System.out.println("---> " + employeeSender.getLevels().iterator().next());

			String senderLevel = "Master";

			Employee employeeReceiver = employeeRepo
					.findByEmployeeIdOrEmail(receiver.getEmployeeId(), receiver.getEmail()).get();
			String receiverName = employeeReceiver.getFullname();
			String receiverEmail = employeeReceiver.getEmail();

			mailDTO.setSubject(subject);

			MimeMessage email = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(email, StandardCharsets.UTF_8.name());

			mailDTO.setContent(leaveRequestDTO.getReason());

			Date endDate = utils.calculatorEndDate(leaveRequestDTO.getStartDate(), leaveRequestDTO.getDuration());
			
			// Load template email with content
			Context context = new Context();
			context.setVariable("senderName", senderName);
			context.setVariable("senderEmail", senderEmail);
			context.setVariable("senderDepartment", senderDepartment);
//			context.setVariable("senderLevel", senderLevel);
			context.setVariable("reason", leaveRequestDTO.getReason());
			context.setVariable("startDate", utils.toStringDate(leaveRequestDTO.getStartDate()));
			context.setVariable("endDate", utils.toStringDate(endDate));
			context.setVariable("receiverName", receiverName);

			String html = templateEngine.process("email", context);

			// Send email
			helper.setTo(receiverEmail);
			helper.setText(html, true);
			helper.setSubject(mailDTO.getSubject());
			helper.setFrom(senderEmail);

			javaMailSender.send(email);
			
//			logger.error(helper.getMimeMessage().getSubject());
			
			
			logger.error("Email sent successful ");

		} catch (MessagingException e) {
			logger.error("Email sent with error: " + e.getMessage());
		}
	}

	@Async
	@Override
	public void sendReponseApprovedEmail(LeaveRequestDTO leaveRequestDTO, EmployeeDTO sender, String subject) {
		MailDTO mailDTO = new MailDTO();

		try {
			Employee employeeSender = employeeRepo.findByEmployeeId(sender.getEmployeeId()).get();

			String senderEmail = employeeSender.getEmail();
			String senderDepartment = employeeSender.getDepartment().getDepartmentName();
			String senderName = employeeSender.getFullname();
//			System.out.println("---> " + employeeSender.getLevels().iterator().next());

			String senderLevel = "Master";

			Employee employeeReceiver = employeeRepo.findByEmployeeId(leaveRequestDTO.getEmployee().getEmployeeId())
					.get();
			String receiverName = employeeReceiver.getFullname();
			String receiverEmail = employeeReceiver.getEmail();

			mailDTO.setSubject(subject);

			MimeMessage email = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(email, StandardCharsets.UTF_8.name());

			mailDTO.setContent(leaveRequestDTO.getReason());

			Date endDate = utils.calculatorEndDate(leaveRequestDTO.getStartDate(), leaveRequestDTO.getDuration());

			
			// Load template email with content
			Context context = new Context();
			context.setVariable("senderName", senderName);
			context.setVariable("senderEmail", senderEmail);
			context.setVariable("senderDepartment", senderDepartment);
//			context.setVariable("senderLevel", senderLevel);

			context.setVariable("startDate", utils.toStringDate(leaveRequestDTO.getStartDate()));
			context.setVariable("endDate", utils.toStringDate(endDate));
			context.setVariable("requestDate", utils.toStringDate(leaveRequestDTO.getRequestDate()));
			context.setVariable("receiverName", receiverName);
			
//			System.err.println(utils.toStringDate(leaveRequestDTO.getStartDate()));
//			System.err.println( utils.toStringDate(endDate));
//			System.err.println( utils.toStringDate(leaveRequestDTO.getRequestDate()));
			
			String html = templateEngine.process("responseEmail", context);

			// Send email
			helper.setTo(receiverEmail);
			helper.setText(html, true);
			helper.setSubject(mailDTO.getSubject());
			helper.setFrom(senderEmail);

			javaMailSender.send(email);

		} catch (MessagingException e) {
			System.out.println("Email sent with error: " + e.getMessage());
		}

	}

	@Async
	@Override
	public void sendReponseRejectEmail(LeaveRequestDTO leaveRequestDTO, EmployeeDTO sender, String subject,
			String reason, String status) {
		MailDTO mailDTO = new MailDTO();

		try {
			Date date = new Date();
			Employee employeeSender = employeeRepo.findByEmployeeId(sender.getEmployeeId()).get();

			String senderEmail = employeeSender.getEmail();
			String senderDepartment = employeeSender.getDepartment().getDepartmentName();
			String senderName = employeeSender.getFullname();
//			System.out.println("---> " + employeeSender.getLevels().iterator().next());

			String senderLevel = "Master";

			Employee employeeReceiver = employeeRepo.findByEmployeeId(leaveRequestDTO.getEmployee().getEmployeeId())
					.get();
			String receiverName = employeeReceiver.getFullname();
			String receiverEmail = employeeReceiver.getEmail();

			mailDTO.setSubject(subject);

			MimeMessage email = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(email, StandardCharsets.UTF_8.name());

			mailDTO.setContent(leaveRequestDTO.getReason());
			
			Date endDate = utils.calculatorEndDate(leaveRequestDTO.getStartDate(), leaveRequestDTO.getDuration());

			// Load template email with content
			Context context = new Context();
			context.setVariable("senderName", senderName);
			context.setVariable("senderEmail", senderEmail);
			context.setVariable("senderDepartment", senderDepartment);
//			context.setVariable("senderLevel", senderLevel);

			context.setVariable("status", status);
			context.setVariable("reason", reason);
			context.setVariable("startDate", utils.toStringDate(leaveRequestDTO.getStartDate()));
			context.setVariable("endDate", utils.toStringDate(endDate));
			context.setVariable("requestDate", utils.toStringDate(leaveRequestDTO.getRequestDate()));
			
			context.setVariable("receiverName", receiverName);
			
//			System.err.println(utils.toStringDate(leaveRequestDTO.getStartDate()));
//			System.err.println( utils.toStringDate(endDate));
//			System.err.println( utils.toStringDate(leaveRequestDTO.getRequestDate()));
			
			
			String html = templateEngine.process("responseReject", context);

			// Send email
			helper.setTo(receiverEmail);
			helper.setText(html, true);
			helper.setSubject(mailDTO.getSubject());
			helper.setFrom(senderEmail);

			javaMailSender.send(email);

		} catch (MessagingException e) {
			System.out.println("Email sent with error: " + e.getMessage());
		}

	}
}