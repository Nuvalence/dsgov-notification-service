package io.nuvalence.platform.notification.service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.nuvalence.platform.notification.service.domain.EmailLayout;
import io.nuvalence.platform.notification.service.domain.Template;
import io.nuvalence.platform.notification.service.domain.TemplateValue;
import io.nuvalence.platform.notification.service.model.SearchTemplateFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test")
class TemplateServiceTest {

    @Autowired private EmailLayoutService emailLayoutService;
    @Autowired private TemplateService service;

    private Template createdTemplate;

    @BeforeEach
    void setUp() {
        final String key = "emailLayoutKey";
        List<String> inputs =
                new ArrayList<>() {
                    {
                        add("input1");
                        add("input2");
                        add("input3");
                    }
                };
        EmailLayout emailLayout = new EmailLayout();
        emailLayout.setName("name");
        emailLayout.setDescription("description");
        emailLayout.setContent("content");
        emailLayout.setInputs(inputs);

        EmailLayout createdEmailLayout = emailLayoutService.createEmailLayout(key, emailLayout);

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
                        .name("template name")
                        .description("template description")
                        .parameters(templateParameters)
                        .emailLayoutKey(createdEmailLayout.getKey())
                        .templateValues(templateValues)
                        .build();

        String templateKey = "key";
        createdTemplate = service.createOrUpdateTemplate(templateKey, template);
    }

    @Test
    void testCreateOrUpdateTemplate() {
        assertNotNull(createdTemplate);
    }

    @Test
    void testCreateOrUpdateTemplate_update() {
        createdTemplate.setDescription("updated description");

        Template updateTemplate =
                service.createOrUpdateTemplate(createdTemplate.getKey(), createdTemplate);

        assertNotNull(createdTemplate);
        assertEquals(createdTemplate.getDescription(), updateTemplate.getDescription());
    }

    @Test
    void testGetTemplate() {
        Optional<Template> foundTemplate = service.getTemplate(createdTemplate.getKey());

        assertTrue(foundTemplate.isPresent());
    }

    @Test
    void testGetTemplates() {
        SearchTemplateFilter filter =
                SearchTemplateFilter.builder().name(createdTemplate.getName()).build();
        Page<Template> result = service.getTemplates(filter);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(createdTemplate.getId(), result.getContent().get(0).getId());
    }

    @Test
    void testGetTemplates_not_found() {
        SearchTemplateFilter filter = SearchTemplateFilter.builder().name("unknown").build();
        Page<Template> result = service.getTemplates(filter);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
    }
}
