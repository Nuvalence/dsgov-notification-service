package io.nuvalence.platform.notification.service.repository;

import io.nuvalence.platform.notification.service.domain.LocalizedStringTemplateLanguage;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface LocalizedTemplateStringLanguageRepository
        extends CrudRepository<LocalizedStringTemplateLanguage, UUID> {}
