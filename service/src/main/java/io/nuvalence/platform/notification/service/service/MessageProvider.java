package io.nuvalence.platform.notification.service.service;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import io.nuvalence.platform.notification.service.domain.LocalizedStringTemplate;
import io.nuvalence.platform.notification.service.domain.LocalizedStringTemplateLanguage;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Optional;

/**
 * Base class for message providers.
 */
@Slf4j
public abstract class MessageProvider {

    protected Optional<LocalizedStringTemplateLanguage> getLocalizedTemplate(
            LocalizedStringTemplate localizedStringTemplate, String language) {
        return localizedStringTemplate.getLocalizedTemplateStrings().stream()
                .filter(
                        localizedTemplateString ->
                                localizedTemplateString.getLanguage().equals(language))
                .findFirst();
    }

    protected String replaceParameterInTemplate(
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
