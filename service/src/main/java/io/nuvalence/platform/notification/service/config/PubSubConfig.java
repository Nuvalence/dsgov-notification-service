package io.nuvalence.platform.notification.service.config;

import com.google.cloud.spring.pubsub.PubSubAdmin;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.integration.AckMode;
import com.google.cloud.spring.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import com.google.cloud.spring.pubsub.integration.outbound.PubSubMessageHandler;
import io.nuvalence.platform.notification.service.service.NotificationProcessingSubscriber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import java.io.IOException;

/**
 * Configures PubSub IO.
 */
@Slf4j
@Configuration
public class PubSubConfig {

    private static final String OUTPUT_CHANNEL = "pubSubOutputChannel";
    private static final String INPUT_CHANNEL = "inputMessageChannel";

    private final String topic;

    private final String subscription;

    private final boolean createTopicAndSubs;

    private final NotificationProcessingSubscriber subscriber;

    /**
     * PubSub config constructor.
     *
     * @param topic              the name of the topic to publish messages to
     * @param subscription       the name of the subscription to pull messages from
     * @param createTopicAndSubs whether to create the topic and subscription if they don't exist
     * @param subscriber         the subscriber bean
     */
    public PubSubConfig(
            @Value("${spring.cloud.gcp.pubsub.topic}") String topic,
            @Value("${spring.cloud.gcp.pubsub.subscription2}") String subscription,
            @Value("${spring.cloud.gcp.pubsub.enableTopicCreation}") boolean createTopicAndSubs,
            NotificationProcessingSubscriber subscriber) {
        this.subscription = subscription;
        this.topic = topic;
        this.createTopicAndSubs = createTopicAndSubs;
        this.subscriber = subscriber;
    }

    /**
     * Creates a message channel for PubSub.
     *
     * @return Message Channel
     */
    @MessagingGateway(defaultRequestChannel = OUTPUT_CHANNEL)
    public interface PubSubOutboundGateway {
        void sendToPubSub(Message<String> message) throws IOException;
    }

    /**
     * Creates a message handler that sends messages to PubSub.
     *
     * @param pubsubTemplate PubSub Message Template
     * @param admin PubSub Admin
     * @return Message Handler
     */
    @Bean
    @ServiceActivator(inputChannel = OUTPUT_CHANNEL)
    public MessageHandler messageSender(PubSubTemplate pubsubTemplate, PubSubAdmin admin) {
        if (createTopicAndSubs && admin.getTopic(topic) == null) {
            admin.createTopic(topic);
        }
        return new PubSubMessageHandler(pubsubTemplate, topic);
    }

    /**
     * Creates a message channel that receives messages from PubSub.
     *
     * @return Message Channel
     */
    @Bean
    public MessageChannel inputMessageChannel() {
        return new DirectChannel();
    }

    /**
     * Creates a message adapter that receives messages from PubSub.
     *
     * @param messageChannel Message Channel
     * @param pubSubTemplate PubSub Message Template
     * @param admin PubSub Admin
     * @return Message Adapter
     */
    @Bean
    public PubSubInboundChannelAdapter inboundChannelAdapter(
            @Qualifier(INPUT_CHANNEL) MessageChannel messageChannel,
            PubSubTemplate pubSubTemplate,
            PubSubAdmin admin) {
        if (createTopicAndSubs && admin.getTopic(topic) == null) {
            admin.createTopic(topic);
        }
        if (admin.getSubscription(subscription) == null) {
            admin.createSubscription(subscription, topic);
        }

        PubSubInboundChannelAdapter adapter =
                new PubSubInboundChannelAdapter(pubSubTemplate, subscription);
        adapter.setOutputChannel(messageChannel);
        adapter.setAckMode(AckMode.MANUAL);
        adapter.setPayloadType(String.class);
        return adapter;
    }

    /**
     * Creates a message handler that receives messages from PubSub.
     *
     * @return Message Handler
     */
    @ServiceActivator(inputChannel = INPUT_CHANNEL)
    public MessageHandler messageReceiverDocumentProcessing() {
        return subscriber;
    }
}
