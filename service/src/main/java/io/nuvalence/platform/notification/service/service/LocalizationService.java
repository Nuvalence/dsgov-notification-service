package io.nuvalence.platform.notification.service.service;

import io.nuvalence.platform.notification.service.domain.EmailFormat;
import io.nuvalence.platform.notification.service.domain.LocalizedStringTemplate;
import io.nuvalence.platform.notification.service.domain.LocalizedStringTemplateLanguage;
import io.nuvalence.platform.notification.service.domain.MessageTemplate;
import io.nuvalence.platform.notification.service.domain.SmsFormat;
import io.nuvalence.platform.notification.service.exception.BadDataException;
import io.nuvalence.platform.notification.service.repository.MessageTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.filterwriter.XLIFFWriter;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.StartGroup;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.IllformedLocaleException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.annotation.PostConstruct;

@RequiredArgsConstructor
@Slf4j
@Service
public class LocalizationService {

    private final MessageTemplateRepository templateRepository;

    @Value("${localization.default-locale}")
    private String defaultLocale;

    @PostConstruct
    public void init() {
        // validate defaultLocale on startup
        validateLocaleTag(defaultLocale);
    }

    public String getLocalizationData(String localeTag) {

        validateLocaleTag(localeTag);

        List<MessageTemplate> templates = templateRepository.findAll();

        try (XLIFFWriter writer = new XLIFFWriter(); ) {
            LocaleId sourceLocale = LocaleId.fromString(defaultLocale);
            LocaleId targetLocale = LocaleId.fromString(localeTag);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            writer.setOutput(baos);

            writer.create(
                    null,
                    null,
                    sourceLocale,
                    targetLocale,
                    "plaintext",
                    "notification-service",
                    " Please use 'resname' attribute on all 'group' and 'trans-unit' elements ");

            templates.forEach(template -> writeTemplateGroup(writer, template, targetLocale));
        }

        // TODO parse string and do XML validation

        return null;
    }

    private void writeTemplateGroup(
            XLIFFWriter writer, MessageTemplate template, LocaleId targetLocale) {

        var group = new StartGroup(null, template.getName());
        group.setName(template.getName());
        writer.writeStartGroup(group);

        writeSmsGroup(writer, template, targetLocale);

        writeEmailGroup(writer, template, targetLocale);

        writer.writeEndGroup();
    }

    private void writeEmailGroup(
            XLIFFWriter writer, MessageTemplate template, LocaleId targetLocale) {
        var group = new StartGroup(null, "email");
        group.setName("email");
        writer.writeStartGroup(group);

        var emailFormat = template.getEmailFormat();

        // subject
        var langStrings =
                Optional.ofNullable(emailFormat)
                        .map(EmailFormat::getLocalizedSubjectStringTemplate)
                        .map(LocalizedStringTemplate::getLocalizedTemplateStrings)
                        .orElse(List.of());

        var pair = getSourceAndTargetValues(langStrings, targetLocale);

        var tu = new TextUnit("subject");
        tu.setName("subject");
        tu.setSourceContent(new TextFragment(pair.getFirst()));
        tu.setTargetContent(targetLocale, new TextFragment(pair.getSecond()));
        writer.writeTextUnit(tu);

        // contents
        group = new StartGroup(null, "content");
        group.setName("content");
        writer.writeStartGroup(group);

        // TODO DAN CONTINUE HERE. READING CONCLUDES WITH THE REAL IMPLEMENTATION OF THE REST OF THE
        // LINES IN THIS METHOD

        tu = new TextUnit("body");
        tu.setName("body");
        tu.setSourceContent(
                new TextFragment("And this seems to be the {{somethingHere}} content of the body"));
        tu.setTargetContent(
                targetLocale, new TextFragment("Ceci est le {{somethingHere}} texte cible"));
        writer.writeTextUnit(tu);
        writer.writeEndGroup();
        writer.writeEndGroup();
    }

    private void writeSmsGroup(
            XLIFFWriter writer, MessageTemplate template, LocaleId targetLocale) {

        var group = new StartGroup(null, "sms");
        group.setName("sms");
        writer.writeStartGroup(group);

        var langStrings =
                Optional.ofNullable(template.getSmsFormat())
                        .map(SmsFormat::getLocalizedStringTemplate)
                        .map(LocalizedStringTemplate::getLocalizedTemplateStrings)
                        .orElse(List.of());

        var pair = getSourceAndTargetValues(langStrings, targetLocale);

        ITextUnit tu = new TextUnit("message");
        tu.setName("message");

        tu.setSourceContent(new TextFragment(pair.getFirst()));
        tu.setTargetContent(targetLocale, new TextFragment(pair.getSecond()));

        writer.writeTextUnit(tu);
        writer.writeEndGroup();
    }

    private Pair<String, String> getSourceAndTargetValues(
            List<LocalizedStringTemplateLanguage> langStrings, LocaleId targetLocale) {
        String sourceValue = "";
        String targetValue = "";

        for (var lang : langStrings) {
            if (lang.getLanguage().equals(defaultLocale)) {
                sourceValue = lang.getTemplate();
            }
            if (lang.getLanguage().equals(targetLocale.toBCP47())) {
                targetValue = lang.getTemplate();
            }
        }
        return Pair.of(sourceValue, targetValue);
    }

    public void validateLocaleTag(String localeTag) throws BadDataException {

        if (localeTag == null) {
            throw new BadDataException("Locale tag should not be null");
        }

        try {
            // validating locale tag
            (new Locale.Builder()).setLanguageTag(localeTag).build();
        } catch (IllformedLocaleException e) {
            throw new BadDataException("Locale tag " + localeTag + " is not IETF valid");
        }
    }
}
