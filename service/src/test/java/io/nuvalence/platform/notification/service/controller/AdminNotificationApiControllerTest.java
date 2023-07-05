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
import io.nuvalence.platform.notification.service.generated.models.EmailLayoutRequestModel;
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

import java.util.List;

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
        String emailLayoutKey = "emailLayoutKey";
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

    @Test
    void testgetEmailLayoutByKey() throws Exception {
        String emailLayoutKey = "emailLayoutKey";
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

        mockMvc.perform(get("/api/v1/admin/email-layout/{key}", emailLayoutKey))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(notNullValue())))
                .andExpect(jsonPath("$.key", is(emailLayoutKey)))
                .andReturn();
    }

    @Test
    void testgetEmailLayoutByKey_not_found() throws Exception {
        String emailLayoutKey = "notFoundKey";

        mockMvc.perform(get("/api/v1/admin/email-layout/{key}", emailLayoutKey))
                .andExpect(status().isNotFound());
    }
}
