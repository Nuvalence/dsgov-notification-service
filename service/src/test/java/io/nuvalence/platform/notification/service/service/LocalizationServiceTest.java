package io.nuvalence.platform.notification.service.service;

import static org.mockito.Mockito.mock;

import io.nuvalence.platform.notification.service.repository.MessageTemplateRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LocalizationServiceTest {

    private LocalizationService service;
    private MessageTemplateRepository templateRepository;

    @BeforeAll
    static void init() {}

    @BeforeEach
    void setUp() {
        templateRepository = mock(MessageTemplateRepository.class);
        service = new LocalizationService(templateRepository);
    }

    @Test
    void tempTest() {
        service.validateLocaleTag("en-US");
    }
}
