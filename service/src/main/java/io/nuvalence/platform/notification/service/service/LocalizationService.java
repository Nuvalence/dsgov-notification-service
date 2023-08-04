package io.nuvalence.platform.notification.service.service;

import io.nuvalence.platform.notification.service.domain.MessageTemplate;
import io.nuvalence.platform.notification.service.exception.BadDataException;
import io.nuvalence.platform.notification.service.repository.MessageTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.IllformedLocaleException;
import java.util.List;
import java.util.Locale;

@RequiredArgsConstructor
@Slf4j
@Service
public class LocalizationService {

    private final MessageTemplateRepository templateRepository;

    public String getLocalizationData(String language) {

        validateLanguageTag(language);

        List<MessageTemplate> templates = templateRepository.findAll();

        return null;
    }

    public void validateLanguageTag(String languageTag) throws BadDataException {

        if (languageTag == null) {
            throw new BadDataException("Language tag should not be null");
        }

        try {
            // validating language tag
            (new Locale.Builder()).setLanguageTag(languageTag).build();
        } catch (IllformedLocaleException e) {
            throw new BadDataException("Language tag " + languageTag + " is not IETF valid");
        }
    }
}
