package io.nuvalence.platform.notification.service.repository;

import io.nuvalence.platform.notification.service.domain.EmailFormatContent;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface EmailFormatContentRepository extends CrudRepository<EmailFormatContent, UUID> {}
