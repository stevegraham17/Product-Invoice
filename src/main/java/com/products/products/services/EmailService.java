package com.products.products.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.File;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // Send PDF invoice for e-sign
    public void sendInvoiceForESign(String to, String subject, String body, String pdfFilePath) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, true); // HTML content possible

        if (pdfFilePath != null) {
            FileSystemResource file = new FileSystemResource(new File(pdfFilePath));
            helper.addAttachment(file.getFilename(), file);
        }

        mailSender.send(message);
    }
}
