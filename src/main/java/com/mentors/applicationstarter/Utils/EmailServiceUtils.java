package com.mentors.applicationstarter.Utils;

import com.mentors.applicationstarter.Service.Impl.AuthenticationServiceImpl;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;
import java.util.Properties;

@Service
@RequiredArgsConstructor
public class EmailServiceUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceUtils.class);

    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String username;
    @Value("${spring.mail.password}")
    private String password;
    @Value("${spring.mail.host}")
    private String smtpServer;
    @Value("${spring.mail.port}")
    private String smptPort;

    @Async
    public void sendEmail(String to, String subject, Map<String, String> templateVariables, String templateFilePath) {
        LOGGER.info("Sending email using template - {} ", templateFilePath);

        //Configure properties for smtp mail sender
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", smtpServer); // SMTP server address
        props.put("mail.smtp.port", smptPort); // SMTP server port

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        // Load and process the template with variables from templateVariables
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);

            //Construct the html template and replace variables with values from templateVariables
            String htmlContent = loadAndProcessTemplate(templateVariables, templateFilePath);
            message.setContent(htmlContent, "text/html; charset=utf-8");

            //Send the message
            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    // loadAndProcessTemplate is used to load a correct template and replace variables with values from templateVariables
    private String loadAndProcessTemplate(Map<String, String> templateVariables, String templateFilePath) {
        Context context = new Context();

        //Step through all items in templateVariables map and set them as variable for thymeleaf template
        for (Map.Entry<String, String> entry : templateVariables.entrySet()) {
            context.setVariable(entry.getKey(), entry.getValue());
        }

        // Process the template with variables
        return templateEngine.process(templateFilePath, context);
    }
}
