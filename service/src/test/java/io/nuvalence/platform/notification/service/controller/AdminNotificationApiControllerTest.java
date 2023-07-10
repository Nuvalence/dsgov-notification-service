package io.nuvalence.platform.notification.service.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.nuvalence.auth.access.AuthorizationHandler;
import io.nuvalence.platform.notification.service.generated.models.EmailFormatModel;
import io.nuvalence.platform.notification.service.generated.models.EmailLayoutRequestModel;
import io.nuvalence.platform.notification.service.generated.models.LocalizedTemplateModel;
import io.nuvalence.platform.notification.service.generated.models.TemplateRequestModel;
import io.nuvalence.platform.notification.service.generated.models.TemplateRequestModelSmsFormat;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class AdminNotificationApiControllerTest {

    @Autowired private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockBean private AuthorizationHandler authorizationHandler;

    @BeforeEach
    void setup() {
        when(authorizationHandler.isAllowed(any(), (Class<?>) any())).thenReturn(true);
        when(authorizationHandler.isAllowed(any(), (String) any())).thenReturn(true);
        when(authorizationHandler.isAllowedForInstance(any(), any())).thenReturn(true);
        when(authorizationHandler.getAuthFilter(any(), any())).thenReturn(element -> true);
    }

    @Test
    void testCreateEmailLayout() throws Exception {
        String emailLayoutKey = RandomStringUtils.randomAlphanumeric(10);
        createEmailLayout(emailLayoutKey);
    }

    @Test
    void testgetEmailLayoutByKey() throws Exception {
        String emailLayoutKey = RandomStringUtils.randomAlphanumeric(10);
        createEmailLayout(emailLayoutKey);

        mockMvc.perform(get("/api/v1/admin/email-layout/{key}", emailLayoutKey))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(notNullValue())))
                .andExpect(jsonPath("$.key", is(emailLayoutKey)))
                .andReturn();
    }

    @Test
    void testGetEmailLayoutByKey_not_found() throws Exception {
        String emailLayoutKey = RandomStringUtils.randomAlphanumeric(5);

        mockMvc.perform(get("/api/v1/admin/email-layout/{key}", emailLayoutKey))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateTemplate() throws Exception {
        String emailLayoutKey = RandomStringUtils.randomAlphanumeric(10);
        createEmailLayout(emailLayoutKey);

        String templateKey = RandomStringUtils.randomAlphanumeric(10);

        Map<String, String> templateParameters =
                Map.of(
                        "parameter1", "Date",
                        "parameter2", "String",
                        "parameter3", "Number",
                        "parameter4", "DateTime");

        LocalizedTemplateModel emailFormatSubject =
                new LocalizedTemplateModel(
                        Map.of(
                                "en", "email-subject-english",
                                "es", "email-subject-spanish",
                                "ca", "email-subject-catalan",
                                "f1", "email-subject-finnish"));

        Map<String, LocalizedTemplateModel> emailFormatContentBody = new HashMap<>();
        emailFormatContentBody.put(
                "body",
                new LocalizedTemplateModel(
                        Map.of(
                                "en", "email-body-english",
                                "es", "email-body-spanish",
                                "ca", "email-body-catalan",
                                "f1", "email-body-finnish")));

        EmailFormatModel emailFormat =
                new EmailFormatModel(emailLayoutKey, emailFormatSubject, emailFormatContentBody);

        TemplateRequestModelSmsFormat smsFormat = new TemplateRequestModelSmsFormat();
        LocalizedTemplateModel smsFormatMessage =
                new LocalizedTemplateModel(
                        Map.of(
                                "en", "email-sms-english",
                                "es", "email-sms-spanish",
                                "ca", "email-sms-catalan",
                                "f1", "email-sms-finnish"));
        smsFormat.setMessage(smsFormatMessage);

        TemplateRequestModel templateRequestModel =
                new TemplateRequestModel(
                        "templateName",
                        "templateDescription",
                        templateParameters,
                        emailFormat,
                        smsFormat);

        mockMvc.perform(
                        put("/api/v1/admin/templates/{key}", templateKey)
                                .content(objectMapper.writeValueAsString(templateRequestModel))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(notNullValue())))
                .andExpect(jsonPath("$.key", is(templateKey)))
                .andReturn();
    }

    private void createEmailLayout(String emailLayoutKey) throws Exception {
        EmailLayoutRequestModel emailLayoutRequestModel =
                new EmailLayoutRequestModel(
                        "name",
                        "description",
                        "content",
                        new java.util.ArrayList<>(List.of("inputs")));

        mockMvc.perform(
                        put("/api/v1/admin/email-layout/{key}", emailLayoutKey)
                                .content(objectMapper.writeValueAsString(emailLayoutRequestModel))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(notNullValue())))
                .andExpect(jsonPath("$.key", is(emailLayoutKey)))
                .andReturn();
    }
}
