package io.nuvalence.platform.notification.service.service;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

@ExtendWith(MockitoExtension.class)
public class SendGridEmailProviderTest {

    @Mock private SendGrid sendGrid;

    @Mock private Response response;

    private SendGridEmailProvider emailProvider;

    @BeforeEach
    public void setUp() throws IOException {
        emailProvider = new SendGridEmailProvider(sendGrid);
        when(sendGrid.api(any(Request.class))).thenReturn(response);
    }

    @Test
    public void testSendEmail() throws IOException {
        String to = "test@example.com";
        String subject = "Subject";
        String body = "Body";

        int expectedStatusCode = 200;
        emailProvider.sendEmail(to, subject, body);

        verify(sendGrid).api(any(Request.class));
    }
}
