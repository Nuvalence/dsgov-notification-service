package io.nuvalence.platform.notification.service.mapper;

import io.nuvalence.platform.notification.service.domain.Template;
import io.nuvalence.platform.notification.service.domain.TemplateValue;
import io.nuvalence.platform.notification.service.generated.models.EmailFormatModel;
import io.nuvalence.platform.notification.service.generated.models.EmailFormatModelContent;
import io.nuvalence.platform.notification.service.generated.models.TemplateModel;
import io.nuvalence.platform.notification.service.generated.models.TemplateRequestModel;
import io.nuvalence.platform.notification.service.generated.models.TemplateRequestModelSmsFormat;
import io.nuvalence.platform.notification.service.generated.models.TemplateResponseModel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mapper for template models.
 */
@Component
public class TemplateMapperImpl {

    /**
     * Map a template to a template response model.
     *
     * @param template template
     * @return template response model
     */
    public TemplateResponseModel templateToTemplateResponseModel(Template template) {
        Map<String, String> subjectTemplates = new HashMap<>();
        Map<String, String> bodyTemplates = new HashMap<>();
        Map<String, String> smsTemplates = new HashMap<>();

        template.getTemplateValues()
                .forEach(
                        templateValue -> {
                            switch (templateValue.getTemplateValueType()) {
                                case "subject":
                                    subjectTemplates.put(
                                            templateValue.getTemplateValueKey(),
                                            templateValue.getTemplateValueValue());
                                    break;
                                case "body":
                                    bodyTemplates.put(
                                            templateValue.getTemplateValueKey(),
                                            templateValue.getTemplateValueValue());
                                    break;
                                case "sms":
                                    smsTemplates.put(
                                            templateValue.getTemplateValueKey(),
                                            templateValue.getTemplateValueValue());
                                    break;
                                default:
                                    break;
                            }
                        });

        TemplateModel subject = new TemplateModel(subjectTemplates);
        TemplateModel smsMessage = new TemplateModel(smsTemplates);

        TemplateRequestModelSmsFormat smsTemplate = new TemplateRequestModelSmsFormat();
        smsTemplate.setMessage(smsMessage);

        EmailFormatModelContent contentTemplate =
                new EmailFormatModelContent().body(new TemplateModel(bodyTemplates));

        return new TemplateResponseModel(
                template.getId(),
                template.getKey(),
                template.getVersion(),
                template.getStatus(),
                template.getName(),
                template.getDescription(),
                template.getParameters(),
                new EmailFormatModel(template.getEmailLayoutKey(), subject, contentTemplate),
                smsTemplate,
                template.getCreatedBy());
    }

    /**
     * Map a template request model to a template.
     *
     * @param templateRequestModel template request model
     * @return template
     */
    public Template templateRequestModelToTemplate(TemplateRequestModel templateRequestModel) {
        List<TemplateValue> templateValues = new ArrayList<>();

        if (templateRequestModel.getEmailFormat() != null) {
            templateRequestModel
                    .getEmailFormat()
                    .getSubject()
                    .getTemplates()
                    .forEach(
                            (key, value) ->
                                    templateValues.add(
                                            TemplateValue.builder()
                                                    .templateValueType("subject")
                                                    .templateValueKey(key)
                                                    .templateValueValue(value)
                                                    .build()));
            templateRequestModel
                    .getEmailFormat()
                    .getContent()
                    .getBody()
                    .getTemplates()
                    .forEach(
                            (key, value) ->
                                    templateValues.add(
                                            TemplateValue.builder()
                                                    .templateValueType("body")
                                                    .templateValueKey(key)
                                                    .templateValueValue(value)
                                                    .build()));
        }

        if (templateRequestModel.getSmsFormat() != null) {
            templateRequestModel
                    .getSmsFormat()
                    .getMessage()
                    .getTemplates()
                    .forEach(
                            (key, value) ->
                                    templateValues.add(
                                            TemplateValue.builder()
                                                    .templateValueType("sms")
                                                    .templateValueKey(key)
                                                    .templateValueValue(value)
                                                    .build()));
        }

        return Template.builder()
                .name(templateRequestModel.getName())
                .description(templateRequestModel.getDescription())
                .parameters(templateRequestModel.getParameters())
                .emailLayoutKey(templateRequestModel.getEmailFormat().getLayoutKey())
                .templateValues(templateValues)
                .build();
    }
}
