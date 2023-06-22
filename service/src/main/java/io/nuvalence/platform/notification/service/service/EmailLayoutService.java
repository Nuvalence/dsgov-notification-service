package io.nuvalence.platform.notification.service.service;

import io.nuvalence.auth.token.UserToken;
import io.nuvalence.platform.notification.service.domain.EmailLayout;
import io.nuvalence.platform.notification.service.repository.EmailLayoutRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;

/**
 * Service for Email Layouts.
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class EmailLayoutService {

    private final EmailLayoutRepository emailLayoutRepository;

    /**
     * Create Email Layout.
     *
     * @param key Email Layout key
     * @param emailLayout Email Layout
     * @return Email Layout
     */
    public EmailLayout createEmailLayout(final String key, final EmailLayout emailLayout) {
        OffsetDateTime now = OffsetDateTime.now();
        Optional<EmailLayout> emailLayoutFound = emailLayoutRepository.findEmailLayoutByKey(key);
        if (emailLayoutFound.isPresent()) {
            emailLayoutFound
        } else {
            emailLayout.setKey(key);
            emailLayout.setStatus("DRAFT");
            emailLayout.setCreatedBy(getCreatedBy().orElse(null));
            emailLayout.setCreatedTimestamp(now);
            emailLayout.setLastUpdatedTimestamp(now);
            return emailLayoutRepository.save(emailLayout);
        }
    }

    /**
     * Get Email Layout by key.
     *
     * @param key Email Layout key
     * @return Email Layout
     */
    public Optional<EmailLayout> getEmailLayoutByKey(final String key) {
        return emailLayoutRepository.findEmailLayoutByKey(key);
    }

    private Optional<String> getCreatedBy() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String createdByUserId = null;
        if ((authentication instanceof UserToken)) {
            final UserToken token = (UserToken) authentication;
            createdByUserId = token.getApplicationUserId();
        }
        return Optional.ofNullable(createdByUserId);
    }
}
