package io.nuvalence.platform.notification.service.config;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.integration.outbound.PubSubMessageHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;

import java.io.IOException;

/**
 * Configures PubSub IO.
 */
@Slf4j
@Configuration
@IntegrationComponentScan
@RequiredArgsConstructor
public class PubSubConfig {

    @Value("${spring.cloud.gcp.pubsub.topic}")
    private String topic;

    /**
     * Default Interface for outbound messages.
     */
    @MessagingGateway(defaultRequestChannel = "pubSubOutputChannel")
    public interface PubSubOutboundGateway {
        void sendToPubSub(Message<String> message) throws IOException;
    }

    /**
     * Creates a message handler that sends messages to PubSub.
     *
     * @param pubsubTemplate PubSub Message Template
     * @return Message Handler
     */
    @Bean
    @ConditionalOnProperty(
            value = "spring.cloud.gcp.pubsub.enabled",
            havingValue = "true",
            matchIfMissing = true)
    @ServiceActivator(inputChannel = "pubSubOutputChannel")
    public MessageHandler messageSender(PubSubTemplate pubsubTemplate) {

        return new PubSubMessageHandler(pubsubTemplate, topic);
    }

    /**
     * Creates a message handler that sends messages to a spring channel.
     *
     * @return Message Handler
     */
    @Bean
    @ConditionalOnProperty(value = "spring.cloud.gcp.pubsub.enabled", havingValue = "false")
    @ServiceActivator(inputChannel = "pubSubOutputChannel")
    public MessageHandler localMessageSender() {
        return message -> {
            String payload = (String) message.getPayload();
            log.info("Message sent to local channel: {}", payload);
        };
    }
}
