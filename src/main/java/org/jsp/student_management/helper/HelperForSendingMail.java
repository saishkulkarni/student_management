package org.jsp.student_management.helper;

import java.io.UnsupportedEncodingException;

import org.jsp.student_management.dto.MyUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Component
public class HelperForSendingMail {

    @Autowired
    JavaMailSender mailSender;

    public void sendEmail(MyUser myUser) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        try {
            helper.setFrom("saishkulkarni7@gmail.com", "Student Management System");
            helper.setTo(myUser.getEmail());
            helper.setSubject("OTP Verification");
            helper.setText("<h1>Hello, " + myUser.getName() + " Your OTP is : " + myUser.getOtp()+"</h1>",true);
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        mailSender.send(mimeMessage);
    }

}
