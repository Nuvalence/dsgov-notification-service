package io.nuvalence.platform.notification.service.service;

import io.nuvalence.platform.notification.service.domain.Message;
import io.nuvalence.platform.notification.service.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Service
public class MessageService {

    private final TemplateService templateService;
    private final MessageRepository messageRepository;

    public Optional<Message> findBy(UUID id) {
        return messageRepository.findById(id);
    }

    public Message save(Message message) {
        //verify user exists
        //verify template exists
        // verify all parameters in message are in template, ignore those which are not
        // validate all parameters are of the type specified in the template
        // queue message for sending



        // from user preferences get locale and send method
        // build message based on template, parameters, and locale
        // TODO: send message based on send method

        return messageRepository.save(message);
    }
}
