package io.nuvalence.platform.notification.service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for message providers.
 */
@Slf4j
@Configuration
public class MessageProviderConfiguration {

    /**
     * Create a message provider for email.
     *
     * @return email message provider
     */
    @Bean
    public EmailProvider emailMessageProvider() {
        return new SendGridEmailProvider();
    }

    /**
     * Create a message provider for SMS.
     *
     * @return SMS message provider
     */
    @Bean
    public SmsProvider smsMessageProvider() {
        return new LoggingStubSmsProvider();
    }
}
