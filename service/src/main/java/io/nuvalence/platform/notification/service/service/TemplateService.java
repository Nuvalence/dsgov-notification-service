package io.nuvalence.platform.notification.service.service;

import io.nuvalence.auth.token.UserToken;
import io.nuvalence.platform.notification.service.domain.Template;
import io.nuvalence.platform.notification.service.domain.TemplateValue;
import io.nuvalence.platform.notification.service.repository.TemplateRepository;
import io.nuvalence.platform.notification.service.repository.TemplateValueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;

/**
 * Service for managing {@link io.nuvalence.platform.notification.service.domain.Template} entities.
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class TemplateService {

    private final TemplateRepository templateRepository;
    private final TemplateValueRepository templateValueRepository;

    /**
     * Create or update a template.
     *
     * @param key      the template key
     * @param template the template
     * @return the template
     */
    public Template createOrUpdateTemplate(final String key, final Template template) {
        OffsetDateTime now = OffsetDateTime.now();

        Optional<Template> templateFound = templateRepository.findFirstByKeyOrderByVersionDesc(key);

        Template templateToSave = new Template();
        templateToSave.setCreatedTimestamp(now);

        if (templateFound.isPresent()) {
            Template existingTemplate = templateFound.get();
            templateToSave.setId(existingTemplate.getId());
            templateToSave.setCreatedTimestamp(existingTemplate.getCreatedTimestamp());
        }

        templateToSave.setName(template.getName());
        templateToSave.setDescription(template.getDescription());
        templateToSave.setKey(key);
        templateToSave.setStatus("DRAFT");
        templateToSave.setVersion(0);
        templateToSave.setParameters(template.getParameters());
        templateToSave.setEmailLayoutKey(template.getEmailLayoutKey());
        templateToSave.setCreatedBy(getCreatedBy().orElse(null));
        templateToSave.setLastUpdatedTimestamp(now);

        Template createdTemplate = templateRepository.save(templateToSave);
        for (TemplateValue templateValue : template.getTemplateValues()) {
            templateValue.setTemplateId(createdTemplate.getId());
            templateValueRepository.save(templateValue);
        }

        return templateRepository.findById(createdTemplate.getId()).orElse(null);
    }

    public Optional<Template> getTemplate(final String key) {
        return templateRepository.findFirstByKeyOrderByVersionDesc(key);
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
}
