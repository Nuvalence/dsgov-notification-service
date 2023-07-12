package io.nuvalence.platform.notification.service.repository;

import io.nuvalence.platform.notification.service.domain.SmsFormat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Repository for managing {@link SmsFormat} entities.
 */
public interface SmsFormatRepository extends JpaRepository<SmsFormat, UUID> {}
