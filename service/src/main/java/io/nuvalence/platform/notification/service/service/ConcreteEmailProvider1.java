package io.nuvalence.platform.notification.service.service;

import lombok.extern.slf4j.Slf4j;

/**
 * Concrete implementation of email provider.
 */
@Slf4j
public class ConcreteEmailProvider1 implements EmailProvider {
    public void sendEmail(String to, String subject, String body) {
        // send email
        log.info("Sending email to {} with subject {} and message {}", to, subject, body);
    }
}
