package io.nuvalence.platform.notification.service.service;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import io.nuvalence.platform.notification.service.domain.EmailFormat;
import io.nuvalence.platform.notification.service.domain.EmailLayout;
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

/**
 * Service for sending messages.
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class SendMessageService {

    private static final String EMAIL_PREFERRED_METHOD = "EMAIL";

    private static final String SMS_PREFERRED_METHOD = "SMS";

    private final EmailLayoutService emailLayoutService;

    private final TemplateService templateService;

    private final UserManagementClientService userManagementClientService;

    /**
     * Send a message.
     *
     * @param message message
     * @throws IOException   if an error occurs while sending the message
     * @throws ApiException if an error occurs while querying user management service
     */
    public void sendMessage(Message message) throws ApiException {
        UUID userId = UUID.fromString(message.getUserId());

        // Query user management service for user preferences
        UserDTO user = userManagementClientService.getUser(userId);
        UserPreferenceDTO userPreferences = user.getPreferences();

        if (userPreferences == null) {
            log.error("Message could not be sent. User preferences not found for user {}", userId);
            return;
        }

        Handlebars handlebars = new Handlebars();
        Optional<MessageTemplate> template =
                templateService.getTemplate(message.getMessageTemplateKey());
        if (EMAIL_PREFERRED_METHOD.equalsIgnoreCase(
                userPreferences.getPreferredCommunicationMethod())) {
            Optional<EmailLayout> emailLayout =
                    emailLayoutService.getEmailLayout(template.get().getEmailLayoutKey());
            if (emailLayout.isEmpty()) {
                log.error(
                        "Message could not be sent. Email layout not found {}",
                        template.get().getEmailLayoutKey());
                return;
            }

            EmailFormat emailFormat = template.get().getEmailFormat();
            Optional<LocalizedStringTemplateLanguage> emailSubjectTemplate =
                    getLocalizedTemplate(
                            emailFormat.getLocalizedSubjectStringTemplate(),
                            userPreferences.getPreferredLanguage());
            String subjectEmail =
                    replaceParameterInTemplate(
                            emailSubjectTemplate.get().getTemplate(),
                            message.getParameters(),
                            handlebars);
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
                                        replaceParameterInTemplate(
                                                emailContentTemplate.get().getTemplate(),
                                                message.getParameters(),
                                                handlebars));
                            });
            String emailBodyToSend =
                    replaceParameterInTemplate(
                            emailLayout.get().getContent(), emailLayoutInputToTemplate, handlebars);

            // send mock email
            log.info(
                    "Processing Message Id: {}. Sending email to {} with subject {} and message {}",
                    message.getId(),
                    user.getEmail(),
                    subjectEmail,
                    emailBodyToSend);

        } else if (SMS_PREFERRED_METHOD.equalsIgnoreCase(
                userPreferences.getPreferredCommunicationMethod())) {
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
                            smsTemplate.get().getTemplate(), message.getParameters(), handlebars);
            // send mock sms
            log.info(
                    "Processing Message Id: {}. Sending sms to {} with message {}",
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

    private String replaceParameterInTemplate(
            String template, Map<String, String> parameters, Handlebars handlebars) {
        try {
            Template handleBarTemplate = handlebars.compileInline(template);
            return handleBarTemplate.apply(parameters);
        } catch (Exception e) {
            log.error("Error compiling template: {}", template, e);
            throw new RuntimeException(e);
        }
    }
}
