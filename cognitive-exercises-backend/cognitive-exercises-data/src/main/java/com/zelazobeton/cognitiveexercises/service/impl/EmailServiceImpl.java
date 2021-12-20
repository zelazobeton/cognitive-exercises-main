package com.zelazobeton.cognitiveexercises.service.impl;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Service;

import com.sun.mail.smtp.SMTPTransport;
import com.zelazobeton.cognitiveexercises.constant.EmailConstants;
import com.zelazobeton.cognitiveexercises.service.EmailService;

@Service
public class EmailServiceImpl implements EmailService {

    public void sendNewPasswordEmail(String username, String password, String email) throws MessagingException {
        Message message = this.createEmail(username, password, email);
        SMTPTransport smtpTransport = (SMTPTransport) this.getEmailSession().getTransport(EmailConstants.SIMPLE_MAIL_TRANSFER_PROTOCOL);
        smtpTransport.connect(EmailConstants.GMAIL_SMTP_SERVER, EmailConstants.USERNAME, EmailConstants.PASSWORD);
        smtpTransport.sendMessage(message, message.getAllRecipients());
        smtpTransport.close();
    }

    private Message createEmail(String username, String password, String email) throws MessagingException {
        Message message = new MimeMessage(this.getEmailSession());
        message.setFrom(new InternetAddress(EmailConstants.FROM_EMAIL));
        message.setRecipients(MimeMessage.RecipientType.TO, InternetAddress.parse(email, false));
        message.setSubject(EmailConstants.EMAIL_SUBJECT);
        message.setText("Hello " + username + ", \n \nYour new password is:\n \n" + password + "\n \nThe Support Team");
        message.setSentDate(new Date());
        message.saveChanges();
        return message;
    }

    private Session getEmailSession() {
        Properties properties = System.getProperties();
        properties.put(EmailConstants.SMTP_HOST, EmailConstants.GMAIL_SMTP_SERVER);
        properties.put(EmailConstants.SMTP_AUTH, true);
        properties.put(EmailConstants.SMTP_PORT, EmailConstants.DEFAULT_PORT);
        properties.put(EmailConstants.SMTP_STARTTLS_ENABLE, true);
        properties.put(EmailConstants.SMTP_STARTTLS_REQUIRED, true);
        return Session.getInstance(properties, null);
    }

    public boolean validateEmail(String email) {
        EmailValidator validator = EmailValidator.getInstance();
        return validator.isValid(email);
    }
}
