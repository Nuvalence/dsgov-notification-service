package io.nuvalence.platform.notification.service.service;

import com.github.jknack.handlebars.Handlebars;
import io.nuvalence.auth.token.UserToken;
import io.nuvalence.platform.notification.service.domain.EmailLayout;
import io.nuvalence.platform.notification.service.exception.BadDataException;
import io.nuvalence.platform.notification.service.model.SearchEmailLayoutFilter;
import io.nuvalence.platform.notification.service.repository.EmailLayoutRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Service for Email Layouts.
 */
@Slf4j
@Service
public class EmailLayoutService {

    private final EmailLayoutRepository emailLayoutRepository;

    private final Handlebars handlebars;

    /**
     * Constructor.
     *
     * @param emailLayoutRepository Email Layout Repository
     */
    public EmailLayoutService(EmailLayoutRepository emailLayoutRepository) {
        this.emailLayoutRepository = emailLayoutRepository;
        this.handlebars = new Handlebars();
    }

    /**
     * Create Email Layout.
     *
     * @param key Email Layout key
     * @param emailLayout Email Layout
     * @return Email Layout
     */
    public EmailLayout createEmailLayout(final String key, final EmailLayout emailLayout) {
        OffsetDateTime now = OffsetDateTime.now();

        Set<String> variableNames =
                MessageBuilderUtils.getVariablesInTemplate(emailLayout.getContent(), handlebars);

        if (!new HashSet<>(emailLayout.getInputs()).containsAll(variableNames)) {
            throw new BadDataException("Invalid inputs");
        }

        Optional<EmailLayout> emailLayoutFound =
                emailLayoutRepository.findFirstByKeyOrderByVersionDesc(key);
        if (emailLayoutFound.isPresent()) {
            EmailLayout existingEmailLayout = emailLayoutFound.get();
            existingEmailLayout.setName(emailLayout.getName());
            existingEmailLayout.setDescription(emailLayout.getDescription());
            existingEmailLayout.setContent(emailLayout.getContent());
            existingEmailLayout.setInputs(emailLayout.getInputs());
            existingEmailLayout.setLastUpdatedTimestamp(now);
            return emailLayoutRepository.save(existingEmailLayout);
        }

        emailLayout.setKey(key);
        emailLayout.setStatus("DRAFT");
        emailLayout.setVersion(0);
        emailLayout.setCreatedBy(getCreatedBy().orElse(null));
        emailLayout.setCreatedTimestamp(now);
        emailLayout.setLastUpdatedTimestamp(now);
        return emailLayoutRepository.save(emailLayout);
    }

    /**
     * Get Email Layout by key.
     *
     * @param key Email Layout key
     * @return Email Layout
     */
    public Optional<EmailLayout> getEmailLayout(final String key) {
        return emailLayoutRepository.findFirstByKeyOrderByVersionDesc(key);
    }

    /**
     * Get Email Layouts.
     *
     * @param filter SearchEmailLayoutFilter
     * @return Page of Email Layouts
     */
    public Page<EmailLayout> getEmailLayouts(final SearchEmailLayoutFilter filter) {
        return emailLayoutRepository.findAll(
                filter.getEmailLayoutSpecifications(), filter.getPageRequest());
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
