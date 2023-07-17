package io.nuvalence.platform.notification.service.service;

import io.nuvalence.platform.notification.service.domain.Message;
import io.nuvalence.platform.notification.service.domain.MessageTemplate;
import io.nuvalence.platform.notification.service.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for managing {@link Message} entities.
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class MessageService {

    private static final String QUEUED_STATUS = "QUEUED";
    private final PubSubService pubSubService;
    private final TemplateService templateService;
    private final MessageRepository messageRepository;

    /**
     * Get a message by id.
     *
     * @param id the message id
     * @return the message
     */
    public Optional<Message> findBy(UUID id) {
        return messageRepository.findById(id);
    }

    /**
     * Save a message.
     *
     * @param message the message
     * @return the saved message
     */
    public Message save(Message message) {
        // TODO: verify user exists
        // verify template exists
        MessageTemplate messageTemplate =
                templateService
                        .getTemplate(message.getMessageTemplateKey())
                        .orElseThrow(() -> new RuntimeException("Template not found"));
        // verify all parameters in message are in template, ignore those which are not
        messageTemplate
                .getParameters()
                .forEach(
                        (key, value) -> {
                            if (!message.getParameters().containsKey(key)) {
                                log.warn(
                                        "Parameter {} not found in template {}",
                                        key,
                                        messageTemplate.getKey());
                                throw new RuntimeException("Parameter not found in template");
                            } else {
                                // validate the type of the parameters matches with the one
                                // specified in template
                                // if
                                // (!message.getParameters().get(key).getClass().equals(value.getClass())) {
                                //    log.warn("Parameter {} type {} does not match template type
                                // {}",
                                //            key, message.getParameters().get(key).getClass(),
                                // value.getClass());
                                //    throw new RuntimeException("Parameter type does not match
                                // template type");
                                // }
                            }
                        });
        // queue message for sending
        OffsetDateTime now = OffsetDateTime.now();

        message.setStatus(QUEUED_STATUS);
        message.setRequestedTimestamp(now);
        Message savedMessaged = messageRepository.save(message);

        pubSubService.publish(savedMessaged);

        return savedMessaged;
    }
}
