package io.nuvalence.platform.notification.service.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;

/**
 * Implementation to send sms messages through Twilio.
 */
@Slf4j
@Setter
public class TwilioSmsProvider implements SmsProvider {

    @Value("${twilio.phoneNumber}")
    private String twilioPhoneNumber;

    @Value("${twilio.accountSID}")
    private String twilioAccountSid;

    @Value("${twilio.authToken}")
    private String twilioAuthToken;

    @PostConstruct
    public void initialize() {
        Twilio.init(twilioAccountSid, twilioAuthToken);
    }

    public void sendSms(String to, String message) {
        Message.creator(new PhoneNumber(to), new PhoneNumber(twilioPhoneNumber), message).create();
    }
}
