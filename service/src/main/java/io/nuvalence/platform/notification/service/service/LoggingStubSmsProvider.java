package io.nuvalence.platform.notification.service.service;

import lombok.extern.slf4j.Slf4j;

/**
 * Concrete implementation of email provider.
 */
@Slf4j
public class LoggingStubSmsProvider implements SmsProvider {
    public void sendSms(String to, String message) {
        // send sms
        log.info("Sending sms to {} with message {}", to, message);
    }
}
