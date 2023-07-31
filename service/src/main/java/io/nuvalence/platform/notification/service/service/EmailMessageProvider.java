package io.nuvalence.platform.notification.service.service;

import com.github.jknack.handlebars.Handlebars;
import io.nuvalence.platform.notification.service.domain.EmailFormat;
import io.nuvalence.platform.notification.service.domain.EmailLayout;
import io.nuvalence.platform.notification.service.domain.LocalizedStringTemplateLanguage;
import io.nuvalence.platform.notification.service.domain.Message;
import io.nuvalence.platform.notification.service.domain.MessageTemplate;
import io.nuvalence.platform.notification.usermanagent.client.generated.models.UserDTO;
import io.nuvalence.platform.notification.usermanagent.client.generated.models.UserPreferenceDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Message provider for email messages.
 */
@Slf4j
@Service
public class EmailMessageProvider extends MessageProvider implements SendMessageProvider {

    private static final String SUPPORTED_METHOD = "email";

    private final EmailLayoutService emailLayoutService;

    /**
     * Constructor.
     *
     * @param emailLayoutService email layout service
     */
    public EmailMessageProvider(EmailLayoutService emailLayoutService) {
        this.emailLayoutService = emailLayoutService;
    }

    @Override
    public void sendMessage(UserDTO user, Message message, MessageTemplate template) {
        UserPreferenceDTO userPreferences = user.getPreferences();

        Optional<EmailLayout> emailLayout =
                emailLayoutService.getEmailLayout(template.getEmailLayoutKey());
        if (emailLayout.isEmpty()) {
            log.error(
                    "Message could not be sent. Email layout not found {}",
                    template.getEmailLayoutKey());
            return;
        }

        Handlebars handlebars = new Handlebars();

        EmailFormat emailFormat = template.getEmailFormat();
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
    }

    @Override
    public String supportedMethod() {
        return SUPPORTED_METHOD;
    }
}
