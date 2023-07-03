package io.nuvalence.platform.notification.service.repository;

import io.nuvalence.platform.notification.service.domain.TemplateValue;
import io.nuvalence.platform.notification.service.domain.TemplateValueId;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for managing {@link io.nuvalence.platform.notification.service.domain.TemplateValue} entities.
 */
public interface TemplateValueRepository extends JpaRepository<TemplateValue, TemplateValueId> {}
