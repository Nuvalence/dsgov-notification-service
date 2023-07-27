package io.nuvalence.platform.notification.service.service;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import io.nuvalence.platform.notification.service.domain.EmailFormat;
import io.nuvalence.platform.notification.service.domain.LocalizedStringTemplate;
import io.nuvalence.platform.notification.service.domain.LocalizedStringTemplateLanguage;
import io.nuvalence.platform.notification.service.domain.Message;
import io.nuvalence.platform.notification.service.domain.MessageTemplate;
import io.nuvalence.platform.notification.service.domain.SmsFormat;
import io.nuvalence.platform.notification.service.service.usermanagementapi.UserManagementClientService;
import io.nuvalence.platform.notification.usermanagent.client.ApiException;
import io.nuvalence.platform.notification.usermanagent.client.generated.models.UserDTO;
import io.nuvalence.platform.notification.usermanagent.client.generated.models.UserPreferenceDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.transaction.Transactional;

/**
 * Service for sending messages.
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class SendMessageService {

    private static final String EMAIL_PREFERRED_METHOD = "EMAIL";

    private static final String SMS_PREFERRED_METHOD = "SMS";

    private final TemplateService templateService;

    private final UserManagementClientService userManagementClientService;

    /**
     * Send a message.
     *
     * @param message message
     * @throws IOException   if an error occurs while sending the message
     * @throws ApiException if an error occurs while querying user management service
     */
    public void sendMessage(Message message) throws IOException, ApiException {
        UUID userId = UUID.fromString(message.getUserId());

        // Query user management service for user preferences
        UserDTO user = userManagementClientService.getUser(userId);
        UserPreferenceDTO userPreferences = user.getPreferences();

        if (userPreferences == null) {
            log.error("Message could not be sent. User preferences not found for user {}", userId);
            return;
        }

        Optional<MessageTemplate> template =
                templateService.getTemplate(message.getMessageTemplateKey());
        if (EMAIL_PREFERRED_METHOD.equalsIgnoreCase(userPreferences.getPreferredCommunicationMethod())) {
            EmailFormat emailFormat = template.get().getEmailFormat();
            Optional<LocalizedStringTemplateLanguage> emailSubjectTemplate =
                    getLocalizedTemplate(
                            emailFormat.getLocalizedSubjectStringTemplate(),
                            userPreferences.getPreferredLanguage());
            String handleBarSubjectTemplateString =
                    replaceParameterInTemplate(
                            emailSubjectTemplate.get().getTemplate(), message.getParameters());
            Map<String, String> emailLayoutInputToTemplate = new HashMap<>();
            emailFormat
                    .getEmailFormatContents()
                    .forEach(
                            emailFormatContent -> {
                                Optional<LocalizedStringTemplateLanguage> emailContentTemplate =
                                        getLocalizedTemplate(
                                                emailFormatContent.getLocalizedStringTemplate(),
                                                userPreferences.getPreferredLanguage());
                                emailLayoutInputToTemplate.put(
                                        emailFormatContent.getEmailLayoutInput(),
                                        emailContentTemplate.get().getTemplate());
                            });
            // send mock email

        } else if (SMS_PREFERRED_METHOD.equalsIgnoreCase(userPreferences.getPreferredCommunicationMethod())) {
            // send sms
            SmsFormat smsFormat = template.get().getSmsFormat();
            Optional<LocalizedStringTemplateLanguage> smsTemplate =
                    getLocalizedTemplate(
                            smsFormat.getLocalizedStringTemplate(),
                            userPreferences.getPreferredLanguage());
            if (smsTemplate.isEmpty()) {
                log.error(
                        "No template found for language: {}, templateKey: {}",
                        userPreferences.getPreferredLanguage(),
                        template.get().getKey());
                return;
            }
            String smsToSend =
                    replaceParameterInTemplate(
                            smsTemplate.get().getTemplate(), message.getParameters());
            // send mock sms

            log.info("Processing Message Id: {}. Sending sms to {} with message {}",
                    message.getId(),
                    user.getPhoneNumber(),
                    smsToSend);
        }
    }

    private Optional<LocalizedStringTemplateLanguage> getLocalizedTemplate(
            LocalizedStringTemplate localizedStringTemplate, String language) {
        return localizedStringTemplate.getLocalizedTemplateStrings().stream()
                .filter(
                        localizedTemplateString ->
                                localizedTemplateString.getLanguage().equals(language))
                .findFirst();
    }

    private String replaceParameterInTemplate(String template, Map<String, String> parameters)
            throws IOException {
        Handlebars handlebars = new Handlebars();
        Template handleBarTemplate = handlebars.compileInline(template);
        return handleBarTemplate.apply(parameters);
    }
}
