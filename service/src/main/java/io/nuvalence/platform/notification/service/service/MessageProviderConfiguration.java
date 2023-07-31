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

    private final EmailLayoutService emailLayoutService;

    /**
     * Constructor.
     *
     * @param emailLayoutService email layout service
     */
    public MessageProviderConfiguration(EmailLayoutService emailLayoutService) {
        this.emailLayoutService = emailLayoutService;
    }

    /**
     * Create a message provider for email.
     *
     * @return email message provider
     */
    @Bean
    public SendMessageProvider emailMessageProvider() {
        return new EmailMessageProvider(emailLayoutService);
    }

    /**
     * Create a message provider for SMS.
     *
     * @return SMS message provider
     */
    @Bean
    public SendMessageProvider smsMessageProvider() {
        return new SmsMessageProvider();
    }
}
