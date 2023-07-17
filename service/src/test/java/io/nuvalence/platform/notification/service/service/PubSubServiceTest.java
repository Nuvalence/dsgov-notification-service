package io.nuvalence.platform.notification.service.service;

import static org.mockito.ArgumentMatchers.any;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.nuvalence.platform.notification.service.domain.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.MessageChannel;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("PMD.BeanMembersShouldSerialize")
public class PubSubServiceTest {

    @Mock private MessageChannel pubsubOutputChannel;
    @Mock private ObjectMapper mockMapper;

    private PubSubService service;

    @BeforeEach
    public void beforeEach() {
        service = new PubSubService(pubsubOutputChannel, mockMapper);
    }

    @Test
    public void testPublish() throws JsonProcessingException {
        Message message = new Message();
        message.setId(UUID.randomUUID());
        message.setUserId(UUID.randomUUID().toString());
        message.setMessageTemplateKey("messageTemplateKey");
        message.setStatus("QUEUED");
        message.setParameters(Map.of("parameter-key", "parameter-value"));
        message.setRequestedTimestamp(OffsetDateTime.now());

        Mockito.when(mockMapper.writeValueAsString(any(Message.class)))
                .thenReturn("serialized-string");

        service.publish(message);

        Mockito.verify(pubsubOutputChannel).send(any());
    }
}
