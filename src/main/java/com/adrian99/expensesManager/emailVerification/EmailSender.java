package com.adrian99.expensesManager.emailVerification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
public class EmailSender {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String email, String token, TokenType tokenType) throws MessagingException {

        MimeMessage mailMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mailMessage,"utf-8");

        helper.setFrom("");
        helper.setTo(email);

        String link = "";

        if(tokenType.equals(TokenType.ACCOUNT_ACTIVATION)) {
            link = "Activation link: <a href=\"http://127.0.0.1:8080/registrationConfirm?token=" + token + "\" target=\"_blank\">Confirm</a>";
            helper.setSubject("Expenses Manager Activation Link");
        }
        else {
            link = "Password reset link: <a href=\"http://127.0.0.1:8080/passwordReset?token=" + token + "\" target=\"_blank\">Confirm</a>";
            helper.setSubject("Password Reset Link");
        }

        helper.setText(link, true);

        mailSender.send(mailMessage);
    }
}
