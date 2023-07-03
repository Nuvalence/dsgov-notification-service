package io.nuvalence.platform.notification.service.repository;

import io.nuvalence.platform.notification.service.domain.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for {@link Template} entities.
 */
public interface TemplateRepository
        extends JpaRepository<Template, UUID>, JpaSpecificationExecutor<Template> {

    /**
     * Obtain the latest version of the email layout by key.
     *
     * @param key email layout key
     * @return email layout
     */
    Optional<Template> findFirstByKeyOrderByVersionDesc(String key);
}
