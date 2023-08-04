package io.nuvalence.platform.notification.service.service;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import io.nuvalence.platform.notification.service.domain.LocalizedStringTemplate;
import io.nuvalence.platform.notification.service.domain.LocalizedStringTemplateLanguage;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Optional;

/**
 * Utility class for message builders.
 */
@Slf4j
public class MessageBuilderUtils {

    private MessageBuilderUtils() {
        // private constructor
    }

    /**
     * Get the localized template for a given language.
     *
     * @param localizedStringTemplate localized string template
     * @param language                language
     * @return localized template
     */
    public static Optional<LocalizedStringTemplateLanguage> getLocalizedTemplate(
            LocalizedStringTemplate localizedStringTemplate, String language) {
        return localizedStringTemplate.getLocalizedTemplateStrings().stream()
                .filter(
                        localizedTemplateString ->
                                localizedTemplateString.getLanguage().equals(language))
                .findFirst();
    }

    /**
     * Replace parameters in a template.
     *
     * @param template    template
     * @param parameters  parameters
     * @param handlebars  handlebars
     * @return template with parameters replaced
     */
    public static String replaceParameterInTemplate(
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
