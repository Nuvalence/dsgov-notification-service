package io.nuvalence.platform.notification.service.controller;

import io.nuvalence.platform.notification.service.domain.EmailLayout;
import io.nuvalence.platform.notification.service.domain.Template;
import io.nuvalence.platform.notification.service.generated.controllers.AdminNotificationApiDelegate;
import io.nuvalence.platform.notification.service.generated.models.EmailLayoutPageDTO;
import io.nuvalence.platform.notification.service.generated.models.EmailLayoutRequestModel;
import io.nuvalence.platform.notification.service.generated.models.EmailLayoutResponseModel;
import io.nuvalence.platform.notification.service.generated.models.TemplateRequestModel;
import io.nuvalence.platform.notification.service.generated.models.TemplateResponseModel;
import io.nuvalence.platform.notification.service.mapper.EmailLayoutMapper;
import io.nuvalence.platform.notification.service.mapper.PagingMetadataMapper;
import io.nuvalence.platform.notification.service.mapper.TemplateMapperImpl;
import io.nuvalence.platform.notification.service.model.SearchEmailLayoutFilter;
import io.nuvalence.platform.notification.service.service.EmailLayoutService;
import io.nuvalence.platform.notification.service.service.TemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

/**
 * Implementation of AdminNotificationApiDelegate.
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class AdminNotificationApiDelegateImpl implements AdminNotificationApiDelegate {

    private final EmailLayoutService emailLayoutService;
    private final EmailLayoutMapper emailLayoutMapper;
    private final TemplateService templateService;
    private final TemplateMapperImpl templateMapperImpl;
    private final PagingMetadataMapper pagingMetadataMapper;

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
                .getEmailLayout(key)
                .map(
                        emailLayout ->
                                ResponseEntity.ok(
                                        emailLayoutMapper.emailLayoutToEmailLayoutResponseModel(
                                                emailLayout)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<EmailLayoutPageDTO> getEmailLayouts(
            Integer page, Integer size, String sortOrder, String sortBy, String name) {
        SearchEmailLayoutFilter filter =
                SearchEmailLayoutFilter.builder()
                        .page(page)
                        .size(size)
                        .sortOrder(sortOrder)
                        .sortBy(sortBy)
                        .name(name)
                        .build();
        Page<EmailLayout> result = emailLayoutService.getEmailLayouts(filter);
        EmailLayoutPageDTO response =
                new EmailLayoutPageDTO(
                        result.getContent().stream()
                                .map(emailLayoutMapper::emailLayoutToEmailLayoutResponseModel)
                                .collect(Collectors.toList()),
                        pagingMetadataMapper.toPagingMetadata(result));
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<TemplateResponseModel> createTemplate(
            String key, TemplateRequestModel templateRequestModel) {
        Template template =
                templateService.createOrUpdateTemplate(
                        key,
                        templateMapperImpl.templateRequestModelToTemplate(templateRequestModel));
        return ResponseEntity.ok(templateMapperImpl.templateToTemplateResponseModel(template));
    }

    @Override
    public ResponseEntity<TemplateResponseModel> getTemplateByKey(String key) {
        return templateService
                .getTemplate(key)
                .map(
                        template ->
                                ResponseEntity.ok(
                                        templateMapperImpl.templateToTemplateResponseModel(
                                                template)))
                .orElse(ResponseEntity.notFound().build());
    }
}
