package io.nuvalence.platform.notification.service.events.processors;

import io.nuvalence.events.event.NotificationEvent;
import io.nuvalence.events.exception.EventProcessingException;
import io.nuvalence.events.subscriber.EventProcessor;
import io.nuvalence.platform.notification.service.mapper.MessageMapperImpl;
import io.nuvalence.platform.notification.service.service.MessageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * This class is responsible for processing notification events.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventProcessor implements EventProcessor<NotificationEvent> {

    private final MessageMapperImpl messageMapperImpl;
    private final MessageService messageService;

    @Override
    @Transactional
    public void execute(NotificationEvent event) throws EventProcessingException {
        try {
            log.debug(
                    "Received event {} of type {}",
                    event.getMetadata().getId(),
                    event.getMetadata().getType());

            messageService.save(messageMapperImpl.notificationEventToMessage(event));
        } catch (Exception e) {
            throw new EventProcessingException(e);
        }
    }

    @Override
    public Class<NotificationEvent> getEventClass() {
        return NotificationEvent.class;
    }
}
