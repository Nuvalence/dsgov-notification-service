package io.nuvalence.platform.notification.service.service;

import io.nuvalence.auth.token.UserToken;
import io.nuvalence.platform.notification.service.domain.EmailFormatContent;
import io.nuvalence.platform.notification.service.domain.LocalizedStringTemplateLanguage;
import io.nuvalence.platform.notification.service.domain.MessageTemplate;
import io.nuvalence.platform.notification.service.model.SearchTemplateFilter;
import io.nuvalence.platform.notification.service.repository.MessageTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;

/**
 * Service for managing {@link io.nuvalence.platform.notification.service.domain.MessageTemplate} entities.
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class TemplateService {

    private final MessageTemplateRepository templateRepository;

    /**
     * Create or update a template.
     *
     * @param key      the template key
     * @param template the template
     * @return the template
     */
    public MessageTemplate createOrUpdateTemplate(
            final String key, final MessageTemplate template) {
        OffsetDateTime now = OffsetDateTime.now();

        Optional<MessageTemplate> templateFound =
                templateRepository.findFirstByKeyOrderByVersionDesc(key);

        MessageTemplate templateToSave =
                new MessageTemplate(key, template, "DRAFT", getCreatedBy().orElse(null), now);

        if (templateFound.isPresent()) {
            MessageTemplate existingTemplate = templateFound.get();
            updateIdsInTemplate(templateToSave, existingTemplate);
        }

        return templateRepository.save(templateToSave);
    }

    /**
     * Get a template by key.
     *
     * @param key the template key
     * @return the first template found by key (latest version)
     */
    public Optional<MessageTemplate> getTemplate(final String key) {
        return templateRepository.findFirstByKeyOrderByVersionDesc(key);
    }

    /**
     * Get templates.
     *
     * @param filter the filter
     * @return the templates
     */
    public Page<MessageTemplate> getTemplates(final SearchTemplateFilter filter) {
        return templateRepository.findAll(
                filter.getTemplateSpecifications(), filter.getPageRequest());
    }

    private Optional<String> getCreatedBy() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String createdByUserId = null;
        if ((authentication instanceof UserToken)) {
            final UserToken token = (UserToken) authentication;
            createdByUserId = token.getApplicationUserId();
        }
        return Optional.ofNullable(createdByUserId);
    }

    private void updateIdsInTemplate(
            MessageTemplate templateToSave, MessageTemplate existingTemplate) {
        templateToSave.setId(existingTemplate.getId());
        templateToSave.setCreatedTimestamp(existingTemplate.getCreatedTimestamp());

        templateToSave.getSmsFormat().setId(existingTemplate.getSmsFormat().getId());
        templateToSave.getSmsFormat().setMessageTemplate(templateToSave);
        templateToSave
                .getSmsFormat()
                .getLocalizedStringTemplate()
                .setId(existingTemplate.getSmsFormat().getLocalizedStringTemplate().getId());
        templateToSave
                .getSmsFormat()
                .getLocalizedStringTemplate()
                .getLocalizedTemplateStrings()
                .forEach(
                        localizedTemplateString -> {
                            // search in existing template if there is and localizedTemplateString
                            // with the same language
                            Optional<LocalizedStringTemplateLanguage> matchedLanguage =
                                    existingTemplate
                                            .getSmsFormat()
                                            .getLocalizedStringTemplate()
                                            .getLocalizedTemplateStrings()
                                            .stream()
                                            .filter(
                                                    lts ->
                                                            localizedTemplateString
                                                                    .getLanguage()
                                                                    .equals(lts.getLanguage()))
                                            .findFirst();
                            matchedLanguage.ifPresent(
                                    localizedTemplateString1 ->
                                            localizedTemplateString.setId(
                                                    localizedTemplateString1.getId()));
                        });

        templateToSave.getEmailFormat().setId(existingTemplate.getEmailFormat().getId());
        templateToSave.getEmailFormat().setMessageTemplate(templateToSave);
        templateToSave
                .getEmailFormat()
                .getLocalizedSubjectStringTemplate()
                .setId(
                        existingTemplate
                                .getEmailFormat()
                                .getLocalizedSubjectStringTemplate()
                                .getId());
        templateToSave
                .getEmailFormat()
                .getLocalizedSubjectStringTemplate()
                .getLocalizedTemplateStrings()
                .forEach(
                        localizedTemplateString -> {
                            // search in existing template if there is and localizedTemplateString
                            // with the same language
                            Optional<LocalizedStringTemplateLanguage> matchedLanguage =
                                    existingTemplate
                                            .getEmailFormat()
                                            .getLocalizedSubjectStringTemplate()
                                            .getLocalizedTemplateStrings()
                                            .stream()
                                            .filter(
                                                    lts ->
                                                            localizedTemplateString
                                                                    .getLanguage()
                                                                    .equals(lts.getLanguage()))
                                            .findFirst();
                            matchedLanguage.ifPresent(
                                    localizedTemplateString1 ->
                                            localizedTemplateString.setId(
                                                    localizedTemplateString1.getId()));
                        });

        templateToSave
                .getEmailFormat()
                .getEmailFormatContents()
                .forEach(
                        emailFormatContent -> {
                            // search in existing template if there is and emailFormatContent
                            // with the same emailLayoutInput
                            Optional<EmailFormatContent> matchedLayoutInput =
                                    existingTemplate
                                            .getEmailFormat()
                                            .getEmailFormatContents()
                                            .stream()
                                            .filter(
                                                    efc ->
                                                            emailFormatContent
                                                                    .getEmailLayoutInput()
                                                                    .equals(
                                                                            efc
                                                                                    .getEmailLayoutInput()))
                                            .findFirst();
                            matchedLayoutInput.ifPresent(
                                    emailFormatContent1 ->
                                            emailFormatContent.setId(emailFormatContent1.getId()));
                        });
        templateToSave.setCreatedBy(existingTemplate.getCreatedBy());
    }
}
