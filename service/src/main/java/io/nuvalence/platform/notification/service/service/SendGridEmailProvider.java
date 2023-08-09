package io.nuvalence.platform.notification.service.service;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;

/**
 * Concrete implementation of email provider.
 */
@Slf4j
public class SendGridEmailProvider implements EmailProvider {

    @Value("${sendgrid.contentType}") String contentType;
    @Value("${sendgrid.sendEmailEndpoint}") String sendEndpoint;
    @Value("${sendgrid.apiKey}") String sendGridApiKey;
    @Value("${sendgrid.from}") String from;


    public void sendEmail(String to, String subject, String body) throws IOException {
        log.info("Sending email to {} with subject {} and message {}", to, subject, body);

        Email sender = new Email(from);
        Email receiver = new Email(to);

        Content content = new Content(contentType, body);

        Mail mail = new Mail(sender, subject, receiver, content);

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();

        request.setMethod(Method.POST);
        request.setEndpoint(sendEndpoint);
        request.setBody(mail.build());

        Response response = sg.api(request);

        System.out.println(response.getStatusCode());
        System.out.println(response.getHeaders());
        System.out.println(response.getBody());
    }
}
