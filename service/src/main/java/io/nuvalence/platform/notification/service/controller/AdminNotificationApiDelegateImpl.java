package io.nuvalence.platform.notification.service.controller;

import io.nuvalence.platform.notification.service.domain.EmailLayout;
import io.nuvalence.platform.notification.service.generated.controllers.AdminNotificationApiDelegate;
import io.nuvalence.platform.notification.service.generated.models.EmailLayoutPageDTO;
import io.nuvalence.platform.notification.service.generated.models.EmailLayoutRequestModel;
import io.nuvalence.platform.notification.service.generated.models.EmailLayoutResponseModel;
import io.nuvalence.platform.notification.service.mapper.EmailLayoutMapper;
import io.nuvalence.platform.notification.service.service.EmailLayoutService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Implementation of AdminNotificationApiDelegate.
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class AdminNotificationApiDelegateImpl implements AdminNotificationApiDelegate {

    private final EmailLayoutService emailLayoutService;
    private final EmailLayoutMapper emailLayoutMapper;

    @Override
    public ResponseEntity<EmailLayoutResponseModel> createEmailLayout(
            String key, EmailLayoutRequestModel emailLayoutRequestModel) {
        EmailLayout emailLayout =
                emailLayoutService.createEmailLayout(
                        key,
                        emailLayoutMapper.emailLayoutRequestModelToEmailLayout(
                                emailLayoutRequestModel));
        return ResponseEntity.ok(
                emailLayoutMapper.emailLayoutToEmailLayoutResponseModel(emailLayout));
    }

    @Override
    public ResponseEntity<EmailLayoutResponseModel> getEmailLayoutByKey(String key) {
        return emailLayoutService
                .getEmailLayoutByKey(key)
                .map(
                        emailLayout ->
                                ResponseEntity.ok(
                                        emailLayoutMapper.emailLayoutToEmailLayoutResponseModel(
                                                emailLayout)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<EmailLayoutPageDTO> getEmailLayouts(
            Integer page, Integer size, String sortOrder, String sortBy) {
        return null;
    }
}
