package io.nuvalence.platform.notification.service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.GcpPubSubHeaders;
import io.nuvalence.platform.notification.service.domain.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.MessageHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Service to handle messages from the PubSub subscription, for notification processing.
 */
@Slf4j
@Service
public class NotificationProcessingSubscriber implements MessageHandler {

    private final ObjectMapper mapper;

    private final SendMessageService sendMessageService;

    /**
     * Subscriber constructor.
     *
     * @param mapper                    the object mapper bean
     * @param sendMessageService service to process notifications.
     */
    public NotificationProcessingSubscriber(
            ObjectMapper mapper, SendMessageService sendMessageService) {
        this.mapper = mapper;
        this.sendMessageService = sendMessageService;
    }

    @Override
    public void handleMessage(org.springframework.messaging.Message<?> message) {

        log.trace("Received message for notification processing.");

        Message messageToSend = parseSubscriptionPayload(message);

        try {
            sendMessageService.sendMessage(messageToSend);
        } catch (Exception e) {
            log.error("An error occurred processing request", e);
        }

        acknowledgeMessage(message);
    }

    private void acknowledgeMessage(org.springframework.messaging.Message<?> message) {
        BasicAcknowledgeablePubsubMessage originalMessage =
                message.getHeaders()
                        .get(
                                GcpPubSubHeaders.ORIGINAL_MESSAGE,
                                BasicAcknowledgeablePubsubMessage.class);
        if (originalMessage != null) {
            log.debug("Acknowledging pubsub message");
            originalMessage.ack();
        }
    }

    private Message parseSubscriptionPayload(org.springframework.messaging.Message<?> message) {
        try {
            var payload = (byte[]) message.getPayload();
            String requestWrapperString = new String(payload, StandardCharsets.UTF_8);
            return mapper.readValue(requestWrapperString, Message.class);
        } catch (IOException ex) {
            log.error("Error parsing message from PubSub", ex);
            throw new RuntimeException(ex);
        }
    }
}
