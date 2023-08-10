package io.nuvalence.platform.notification.service.service;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

public class TwilioSmsProviderTest {

    private MessageCreator messageCreator = mock(MessageCreator.class);

    private TwilioSmsProvider smsProvider;
    private String twilioPhoneNumber = "some_phone_number";

    @BeforeEach
    public void setUp() {
        smsProvider = new TwilioSmsProvider();
        smsProvider.setTwilioPhoneNumber(twilioPhoneNumber);
    }

    @Test
    public void testSendSms() {

        String calledToNumber = "";
        String calledFromNumber = "";
        String calledMessage = "";

        try (MockedStatic<Message> messageMock = mockStatic(Message.class)) {
            messageMock
                    .when(
                            () ->
                                    Message.creator(
                                            any(PhoneNumber.class),
                                            any(PhoneNumber.class),
                                            anyString()))
                    .thenReturn(messageCreator);

            smsProvider.sendSms("to_number", "test_message");

            ArgumentCaptor<PhoneNumber> toNumberCaptor = ArgumentCaptor.forClass(PhoneNumber.class);
            ArgumentCaptor<PhoneNumber> fromNumberCaptor =
                    ArgumentCaptor.forClass(PhoneNumber.class);
            ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

            messageMock.verify(
                    () ->
                            Message.creator(
                                    toNumberCaptor.capture(),
                                    fromNumberCaptor.capture(),
                                    messageCaptor.capture()));

            calledToNumber = toNumberCaptor.getValue().toString();
            calledFromNumber = fromNumberCaptor.getValue().toString();
            calledMessage = messageCaptor.getValue();
        }

        Assertions.assertEquals("to_number", calledToNumber);
        Assertions.assertEquals(twilioPhoneNumber, calledFromNumber);
        Assertions.assertEquals("test_message", calledMessage);
    }
}
