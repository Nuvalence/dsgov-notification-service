package io.nuvalence.platform.notification.service.service;

import io.nuvalence.platform.notification.service.domain.EmailFormat;
import io.nuvalence.platform.notification.service.domain.EmailFormatContent;
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
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.IllformedLocaleException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.xml.XMLConstants;

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

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (XLIFFWriter writer = new XLIFFWriter()) {

            LocaleId sourceLocale = LocaleId.fromString(defaultLocale);
            LocaleId targetLocale = LocaleId.fromString(localeTag);

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

            writer.close();

            return xmlValidateAndMinify(baos.toString(StandardCharsets.UTF_8));

        } catch (Exception e) {
            // TODO IMPROVE THIS EXCEPTION HANDLING
            throw new RuntimeException("Error generating localization data" + e.getMessage());
        }
    }

    private void writeTemplateGroup(
            XLIFFWriter writer, MessageTemplate template, LocaleId targetLocale) {

        var group = new StartGroup(null, template.getKey());
        group.setName(template.getKey());
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

        writeTextUnit(writer, "subject", langStrings, targetLocale);

        writeEmailContents(writer, emailFormat, targetLocale);

        writer.writeEndGroup();
    }

    private void writeEmailContents(
            XLIFFWriter writer, EmailFormat emailFormat, LocaleId targetLocale) {
        // contents
        var group = new StartGroup(null, "content");
        group.setName("content");
        writer.writeStartGroup(group);

        var formatContents =
                Optional.ofNullable(emailFormat)
                        .map(EmailFormat::getEmailFormatContents)
                        .orElse(List.of());

        for (var formatContent : formatContents) {
            var resourceName = formatContent.getEmailLayoutInput();
            var langStrings =
                    Optional.ofNullable(formatContent)
                            .map(EmailFormatContent::getLocalizedStringTemplate)
                            .map(LocalizedStringTemplate::getLocalizedTemplateStrings)
                            .orElse(List.of());
            writeTextUnit(writer, resourceName, langStrings, targetLocale);
        }

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

        writeTextUnit(writer, "message", langStrings, targetLocale);

        writer.writeEndGroup();
    }

    private void writeTextUnit(
            XLIFFWriter writer,
            String resourceName,
            List<LocalizedStringTemplateLanguage> langStrings,
            LocaleId targetLocale) {

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

        ITextUnit tu = new TextUnit(resourceName);
        tu.setName(resourceName);
        tu.setSourceContent(new TextFragment(sourceValue));
        tu.setTargetContent(targetLocale, new TextFragment(targetValue));
        writer.writeTextUnit(tu);
    }

    /**
    * <p>
    * Verifies the string represents a valid XML file.
    * And returns it minified for transfer convenience.
    * </p>
    * 
    * @param xmlString the XML string to be parsed
    * @return valid XML string in pretty format
    * @throws JDOMException when errors occur in parsing
    * @throws IOException   when an I/O error prevents a document from being fully
    *                       parsed
    */
    public static String xmlValidateAndMinify(String xmlString) throws JDOMException, IOException {

        SAXBuilder saxBuilder = new SAXBuilder();
        saxBuilder.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        saxBuilder.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");

        var reader = new StringReader(xmlString);
        Document xmlDoc = saxBuilder.build(reader);
        reader.close();

        return new XMLOutputter(Format.getCompactFormat()).outputString(xmlDoc);
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
