package io.nuvalence.platform.notification.service;

import io.nuvalence.auth.access.AuthorizationHandler;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class NotificationServiceTest {

    @MockBean AuthorizationHandler authorizationHandler;

    @Test
    void testSpringBootContext() {
        assertTrue(true);
    }
}
