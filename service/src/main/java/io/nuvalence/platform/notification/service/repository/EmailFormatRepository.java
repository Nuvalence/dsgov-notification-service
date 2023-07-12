package io.nuvalence.platform.notification.service.repository;

import io.nuvalence.platform.notification.service.domain.EmailFormat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Repository for managing {@link EmailFormat} entities.
 */
public interface EmailFormatRepository extends JpaRepository<EmailFormat, UUID> {}
