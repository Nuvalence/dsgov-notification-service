package io.nuvalence.platform.notification.service.events.processors;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.nuvalence.events.event.EventMetadata;
import io.nuvalence.events.event.NotificationEvent;
import io.nuvalence.events.exception.EventProcessingException;
import io.nuvalence.platform.notification.service.domain.Message;
import io.nuvalence.platform.notification.service.mapper.MessageMapperImpl;
import io.nuvalence.platform.notification.service.service.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

public class NotificationEventProcessorTest {

    @Mock private MessageMapperImpl messageMapperImpl;

    @Mock private MessageService messageService;

    @InjectMocks private NotificationEventProcessor notificationEventProcessor;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testExecute() throws EventProcessingException {
        NotificationEvent event = mock(NotificationEvent.class);
        EventMetadata eventMetadata = mock(EventMetadata.class);
        when(event.getMetadata()).thenReturn(eventMetadata);
        when(eventMetadata.getId()).thenReturn(UUID.randomUUID());
        when(eventMetadata.getType()).thenReturn("notification");
        Message message = mock(Message.class);

        when(messageMapperImpl.notificationEventToMessage(event)).thenReturn(message);

        notificationEventProcessor.execute(event);

        verify(messageService, times(1)).save(message);
    }
}
