package io.nuvalence.platform.notification.service.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.nuvalence.platform.notification.service.domain.Template;
import io.nuvalence.platform.notification.service.domain.TemplateValue;
import io.nuvalence.platform.notification.service.generated.models.EmailFormatModel;
import io.nuvalence.platform.notification.service.generated.models.EmailFormatModelContent;
import io.nuvalence.platform.notification.service.generated.models.TemplateModel;
import io.nuvalence.platform.notification.service.generated.models.TemplateRequestModel;
import io.nuvalence.platform.notification.service.generated.models.TemplateRequestModelSmsFormat;
import io.nuvalence.platform.notification.service.generated.models.TemplateResponseModel;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

class TemplateMapperImplTest {

    private final TemplateMapperImpl templateMapper = new TemplateMapperImpl();

    @Test
    void testTemplateToTemplateResponseModel() {
        String templateKey = "key";
        Map<String, String> templateParameters = new HashMap<>();
        templateParameters.put("parameter1", "parameter-value1");
        templateParameters.put("parameter2", "parameter-value2");
        templateParameters.put("parameter3", "parameter-value3");

        TemplateValue templateSubjectValue1 =
                TemplateValue.builder()
                        .templateValueType("subject")
                        .templateValueKey("template-subject-key1")
                        .templateValueValue("template-subject-value1")
                        .build();

        TemplateValue templateBodyValue1 =
                TemplateValue.builder()
                        .templateValueType("body")
                        .templateValueKey("template-body-key1")
                        .templateValueValue("template-body-value1")
                        .build();

        TemplateValue templateSmsValue1 =
                TemplateValue.builder()
                        .templateValueType("sms")
                        .templateValueKey("template-sms-key1")
                        .templateValueValue("template-sms-value1")
                        .build();

        List<TemplateValue> templateValues =
                new ArrayList<>(
                        List.of(templateSubjectValue1, templateBodyValue1, templateSmsValue1));

        Template template =
                Template.builder()
                        .id(UUID.randomUUID())
                        .key(templateKey)
                        .name("template name")
                        .description("template description")
                        .parameters(templateParameters)
                        .emailLayoutKey("emailLayoutKey")
                        .templateValues(templateValues)
                        .build();

        TemplateResponseModel templateResponseModel =
                templateMapper.templateToTemplateResponseModel(template);
        assertEquals(templateResponseModel.getId(), template.getId());
        assertEquals(1, templateResponseModel.getSmsFormat().getMessage().getTemplates().size());
        assertEquals(
                template.getEmailLayoutKey(),
                templateResponseModel.getEmailFormat().getLayoutKey());
        assertEquals(1, templateResponseModel.getEmailFormat().getSubject().getTemplates().size());
        assertEquals(
                1,
                templateResponseModel
                        .getEmailFormat()
                        .getContent()
                        .getBody()
                        .getTemplates()
                        .size());
    }

    @Test
    void testTemplateRequestModelToTemplate() {

        Map<String, String> templateParameters = new HashMap<>();
        templateParameters.put("parameter1", "parameter-value1");
        templateParameters.put("parameter2", "parameter-value2");
        templateParameters.put("parameter3", "parameter-value3");

        TemplateRequestModel templateRequestModel = new TemplateRequestModel();
        templateRequestModel.setName("template name");
        templateRequestModel.setDescription("template description");
        templateRequestModel.setParameters(templateParameters);

        TemplateModel subjectModel = new TemplateModel();
        subjectModel.setTemplates(Map.of("template-subject-key1", "template-subject-value1"));
        EmailFormatModelContent contentModel = new EmailFormatModelContent();
        TemplateModel bodyTemplateModel = new TemplateModel();
        bodyTemplateModel.setTemplates(Map.of("template-body-key1", "template-body-value1"));
        contentModel.setBody(bodyTemplateModel);

        EmailFormatModel emailFormat = new EmailFormatModel();
        emailFormat.setLayoutKey("emailLayoutKey");
        emailFormat.setSubject(subjectModel);
        emailFormat.setContent(contentModel);

        TemplateRequestModelSmsFormat smsFormat = new TemplateRequestModelSmsFormat();
        TemplateModel smsTemplateModel = new TemplateModel();
        smsTemplateModel.setTemplates(Map.of("template-sms-key1", "template-sms-value1"));
        smsFormat.setMessage(smsTemplateModel);

        templateRequestModel.setEmailFormat(emailFormat);
        templateRequestModel.setSmsFormat(smsFormat);

        Template mappedTemplate =
                templateMapper.templateRequestModelToTemplate(templateRequestModel);

        assertNotNull(mappedTemplate);
        assertEquals(templateRequestModel.getName(), mappedTemplate.getName());
        assertEquals(templateRequestModel.getDescription(), mappedTemplate.getDescription());
        assertEquals(
                templateRequestModel.getParameters().size(), mappedTemplate.getParameters().size());
        assertEquals(
                templateRequestModel.getEmailFormat().getLayoutKey(),
                mappedTemplate.getEmailLayoutKey());
        assertEquals(3, mappedTemplate.getTemplateValues().size());
    }
}
