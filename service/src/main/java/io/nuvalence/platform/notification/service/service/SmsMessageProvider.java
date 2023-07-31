package io.nuvalence.platform.notification.service.service;

import static io.nuvalence.platform.notification.service.service.MessageBuilderUtils.getLocalizedTemplate;
import static io.nuvalence.platform.notification.service.service.MessageBuilderUtils.replaceParameterInTemplate;

import com.github.jknack.handlebars.Handlebars;
import io.nuvalence.platform.notification.service.domain.LocalizedStringTemplateLanguage;
import io.nuvalence.platform.notification.service.domain.Message;
import io.nuvalence.platform.notification.service.domain.MessageTemplate;
import io.nuvalence.platform.notification.service.domain.SmsFormat;
import io.nuvalence.platform.notification.usermanagent.client.generated.models.UserDTO;
import io.nuvalence.platform.notification.usermanagent.client.generated.models.UserPreferenceDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Message provider for SMS messages.
 */
@Slf4j
@Service
public class SmsMessageProvider implements SendMessageProvider {

    private static final String SUPPORTED_METHOD = "sms";

    private final SmsProvider smsProvider;

    public SmsMessageProvider(SmsProvider smsProvider) {
        this.smsProvider = smsProvider;
    }

    @Override
    public void sendMessage(UserDTO user, Message message, MessageTemplate template) {
        UserPreferenceDTO userPreferences = user.getPreferences();

        Handlebars handlebars = new Handlebars();

        SmsFormat smsFormat = template.getSmsFormat();
        Optional<LocalizedStringTemplateLanguage> smsTemplate =
                getLocalizedTemplate(
                        smsFormat.getLocalizedStringTemplate(),
                        userPreferences.getPreferredLanguage());
        if (smsTemplate.isEmpty()) {
            log.error(
                    "No template found for language: {}, templateKey: {}",
                    userPreferences.getPreferredLanguage(),
                    template.getKey());
            return;
        }
        String smsToSend =
                replaceParameterInTemplate(
                        smsTemplate.get().getTemplate(), message.getParameters(), handlebars);
        // send mock sms
        log.info(
                "Processing Message Id: {}. Sending sms to {} with message {}",
                message.getId(),
                user.getPhoneNumber(),
                smsToSend);

        smsProvider.sendSms(user.getPhoneNumber(), smsToSend);
    }

    @Override
    public String supportedMethod() {
        return SUPPORTED_METHOD;
    }
}
