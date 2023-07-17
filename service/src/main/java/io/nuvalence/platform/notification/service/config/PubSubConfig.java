package io.nuvalence.platform.notification.service.config;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.integration.outbound.PubSubMessageHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

/**
 * Configures PubSub IO.
 */
@Configuration
@IntegrationComponentScan
@RequiredArgsConstructor
public class PubSubConfig {

    @Value("${spring.cloud.gcp.pubsub.topic}")
    private String topic;

    @Primary
    @Bean
    public MessageChannel pubSubOutputChannel() {
        return new DirectChannel();
    }

    @Bean
    @ConditionalOnProperty(
            value = "spring.cloud.gcp.pubsub.enabled",
            havingValue = "true",
            matchIfMissing = true)
    @ServiceActivator(inputChannel = "pubSubOutputChannel")
    public MessageHandler messageSender(PubSubTemplate pubsubTemplate) {

        return new PubSubMessageHandler(pubsubTemplate, topic);
    }
}
