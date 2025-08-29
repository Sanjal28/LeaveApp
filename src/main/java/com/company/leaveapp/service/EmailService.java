// src/main/java/com/company/leaveapp/service/EmailService.java
package com.company.leaveapp.service;

import com.company.leaveapp.models.LeaveRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${mail.from}")
    private String fromEmail;

    @Async
    public void sendLeaveAppliedNotification(LeaveRequest leaveRequest) {
        Context context = new Context();
        context.setVariable("managerName", leaveRequest.getManager().getName());
        context.setVariable("employeeName", leaveRequest.getEmployee().getName());
        context.setVariable("leaveType", leaveRequest.getType().toString());
        context.setVariable("startDate", leaveRequest.getStartDate().toString());
        context.setVariable("endDate", leaveRequest.getEndDate().toString());
        context.setVariable("days", leaveRequest.getDays());
        context.setVariable("reason", leaveRequest.getReason());

        String body = templateEngine.process("leave_request_notification.html", context);
        sendEmail(leaveRequest.getManager().getEmail(), "New Leave Request Submitted", body);
    }

    @Async
    public void sendLeaveStatusNotification(LeaveRequest leaveRequest, Double newBalance) {
        Context context = new Context();
        context.setVariable("employeeName", leaveRequest.getEmployee().getName());
        context.setVariable("startDate", leaveRequest.getStartDate().toString());
        context.setVariable("endDate", leaveRequest.getEndDate().toString());
        context.setVariable("status", leaveRequest.getStatus().toString());
        context.setVariable("decisionReason", leaveRequest.getDecisionReason());
        context.setVariable("newBalance", newBalance);

        String body = templateEngine.process("leave_status_notification.html", context);
        sendEmail(leaveRequest.getEmployee().getEmail(), "Your Leave Request has been " + leaveRequest.getStatus(), body);
    }

    private void sendEmail(String to, String subject, String body) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();

            // --- THIS IS THE CORRECTED LINE ---
            // The boolean 'multipart' flag comes before the 'encoding' string.
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true); // true indicates HTML
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            // In a real app, log this error more robustly.
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }
}
