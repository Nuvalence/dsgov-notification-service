package io.nuvalence.platform.notification.service.service;

import static io.nuvalence.platform.notification.service.service.MessageBuilderUtils.getLocalizedTemplate;
import static io.nuvalence.platform.notification.service.service.MessageBuilderUtils.replaceParameterInTemplate;

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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Message provider for email messages.
 */
@Slf4j
@Service
public class EmailMessageProvider implements SendMessageProvider {

    private static final String SUPPORTED_METHOD = "email";

    private final EmailLayoutService emailLayoutService;

    private final EmailProvider emailProvider;

    private final Handlebars handlebars;

    /**
     * Constructor.
     *
     * @param emailLayoutService email layout service
     * @param emailProvider      email provider
     */
    public EmailMessageProvider(
            EmailLayoutService emailLayoutService, EmailProvider emailProvider) {
        this.emailLayoutService = emailLayoutService;
        this.emailProvider = emailProvider;
        this.handlebars = new Handlebars();
    }

    @Override
    public void sendMessage(UserDTO user, Message message, MessageTemplate template)
            throws IOException {
        UserPreferenceDTO userPreferences = user.getPreferences();

        Optional<EmailLayout> emailLayout =
                emailLayoutService.getEmailLayout(template.getEmailLayoutKey());
        if (emailLayout.isEmpty()) {
            log.error(
                    "Message could not be sent. Email layout not found {}",
                    template.getEmailLayoutKey());
            return;
        }

        EmailFormat emailFormat = template.getEmailFormat();
        Optional<LocalizedStringTemplateLanguage> emailSubjectTemplate =
                getLocalizedTemplate(
                        emailFormat.getLocalizedSubjectStringTemplate(),
                        userPreferences.getPreferredLanguage());
        if (emailSubjectTemplate.isEmpty()) {
            log.warn("Could not send {} email to user {}.", template.getKey(), user.getId());
            return;
        }

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

        emailProvider.sendEmail(user.getEmail(), subjectEmail, emailBodyToSend);
    }

    @Override
    public String supportedMethod() {
        return SUPPORTED_METHOD;
    }
}
