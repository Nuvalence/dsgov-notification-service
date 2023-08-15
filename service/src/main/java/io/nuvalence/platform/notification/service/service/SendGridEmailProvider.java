package io.nuvalence.platform.notification.service.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import io.nuvalence.platform.notification.service.exception.UnprocessableNotificationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;

/**
 * Concrete implementation of email provider.
 */
@Slf4j
@RequiredArgsConstructor
public class SendGridEmailProvider implements EmailProvider {

    @Value("${sendgrid.contentType}")
    String contentType;

    @Value("${sendgrid.sendEmailEndpoint}")
    String sendEndpoint;

    @Value("${sendgrid.apiKey}")
    String sendGridApiKey;

    @Value("${sendgrid.from}")
    String from;

    private final SendGrid sg;

    /**
     * Sends an email via SendGrid.
     * @param to      recipient of the email.
     * @param subject subject for the email.
     * @param body    body for the email.
     * @throws IOException possibly thrown by api.
     */
    public void sendEmail(String to, String subject, String body) throws IOException {
        Email sender = new Email(from);
        Email receiver = new Email(to);

        Content content = new Content(contentType, body);

        Mail mail = new Mail(sender, subject, receiver, content);

        Request request = new Request();

        request.setMethod(Method.POST);
        request.setEndpoint(sendEndpoint);
        request.setBody(mail.build());

        Response response = sg.api(request);

        if(response.getStatusCode() >= 400 && response.getStatusCode() < 500) {
            String sendGridBadRequest = String.format("Bad request response obtained from SendGrid with code %d, could send email to %s", response.getStatusCode(), to);
            log.error(sendGridBadRequest);
            throw new UnprocessableNotificationException(sendGridBadRequest);
        }

        log.trace("Email sent to {} with status code {}", to, response.getStatusCode());
    }
}
