package io.nuvalence.platform.notification.service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.GcpPubSubHeaders;
import io.nuvalence.platform.notification.service.domain.EmailFormat;
import io.nuvalence.platform.notification.service.domain.EmailFormatContent;
import io.nuvalence.platform.notification.service.domain.EmailLayout;
import io.nuvalence.platform.notification.service.domain.LocalizedStringTemplate;
import io.nuvalence.platform.notification.service.domain.LocalizedStringTemplateLanguage;
import io.nuvalence.platform.notification.service.domain.MessageTemplate;
import io.nuvalence.platform.notification.service.domain.SmsFormat;
import io.nuvalence.platform.notification.service.service.usermanagementapi.UserManagementClientService;
import io.nuvalence.platform.notification.usermanagent.client.ApiException;
import io.nuvalence.platform.notification.usermanagent.client.generated.models.UserDTO;
import io.nuvalence.platform.notification.usermanagent.client.generated.models.UserPreferenceDTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.ActiveProfiles;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NotificationProcessingSubscriberTest {

    @Autowired private ObjectMapper objectMapper;

    @Autowired private EmailLayoutService emailLayoutService;

    @Autowired private TemplateService templateService;

    @Autowired private MessageService messageService;

    @Autowired private NotificationProcessingSubscriber service;

    @MockBean private UserManagementClientService userManagementClientService;

    private MessageTemplate createdTemplate;

    @BeforeAll
    void setUp() {
        final String emailLayoutKeykey = "emailLayoutKey";
        List<String> inputs =
                new ArrayList<>() {
                    private static final long serialVersionUID = 4861793309100343408L;

                    {
                        add("greeting");
                        add("body");
                        add("footer");
                    }
                };
        EmailLayout emailLayout = new EmailLayout();
        emailLayout.setName("name");
        emailLayout.setDescription("description");
        emailLayout.setContent(
                "<html>\n"
                        + "  <head>\n"
                        + "    <title>Application Submission Confirmation</title>\n"
                        + "  </head>\n"
                        + "  <body>\n"
                        + "    <div id=\\\"greeting\\\">\n"
                        + "           <p>{{greeting}}</p>\n"
                        + "           </div>\n"
                        + "           <div id=\\\"body\\\">\n"
                        + "           <p>{{body}}</p>\n"
                        + "           </div>\n"
                        + "           <div id=\\\"footer\\\">\n"
                        + "           <p>{{footer}}</p>\n"
                        + "           </div>\n"
                        + "           </body>\n"
                        + "  </html>");
        emailLayout.setInputs(inputs);

        final String templateKey = "key";
        Map<String, String> templateParameters = new HashMap<>();
        templateParameters.put("transactionId", "String");
        templateParameters.put("name", "String");

        final LocalizedStringTemplateLanguage localizedSmsStringTemplateLanguage1 =
                LocalizedStringTemplateLanguage.builder()
                        .language("en")
                        .template(
                                "Your financial benefits application, {{transactionId}}, has been"
                                        + " approved")
                        .build();

        final LocalizedStringTemplateLanguage localizedSmsStringTemplateLanguage2 =
                LocalizedStringTemplateLanguage.builder()
                        .language("es")
                        .template(
                                "Su solicitud de beneficios financieros, {{transactionId}}, ha"
                                        + " sido aprobada.")
                        .build();

        LocalizedStringTemplateLanguage localizedGreetingStringTemplateLanguage1 =
                LocalizedStringTemplateLanguage.builder()
                        .language("en")
                        .template("Hi {{name}}")
                        .build();

        LocalizedStringTemplateLanguage localizedGreetingStringTemplateLanguage2 =
                LocalizedStringTemplateLanguage.builder()
                        .language("es")
                        .template("Hola {{name}}")
                        .build();

        LocalizedStringTemplate localizedGreetingStringTemplate =
                LocalizedStringTemplate.builder()
                        .localizedTemplateStrings(
                                List.of(
                                        localizedGreetingStringTemplateLanguage1,
                                        localizedGreetingStringTemplateLanguage2))
                        .build();
        localizedGreetingStringTemplateLanguage1.setLocalizedStringTemplate(
                localizedGreetingStringTemplate);
        localizedGreetingStringTemplateLanguage2.setLocalizedStringTemplate(
                localizedGreetingStringTemplate);

        final EmailFormatContent emailFormatGreeting1 =
                EmailFormatContent.builder()
                        .emailLayoutInput("greeting")
                        .localizedStringTemplate(localizedGreetingStringTemplate)
                        .build();

        LocalizedStringTemplateLanguage localizedContentStringTemplateLanguage1 =
                LocalizedStringTemplateLanguage.builder()
                        .language("en")
                        .template(
                                "Your financial benefits application, {{transactionId}}, has been"
                                        + " approved.")
                        .build();

        LocalizedStringTemplateLanguage localizedContentStringTemplateLanguage2 =
                LocalizedStringTemplateLanguage.builder()
                        .language("es")
                        .template(
                                "Su solicitud de beneficios financieros, {{transactionId}}, ha"
                                        + " sido aprobada.")
                        .build();

        LocalizedStringTemplate localizedContentStringTemplate =
                LocalizedStringTemplate.builder()
                        .localizedTemplateStrings(
                                List.of(
                                        localizedContentStringTemplateLanguage1,
                                        localizedContentStringTemplateLanguage2))
                        .build();
        localizedContentStringTemplateLanguage1.setLocalizedStringTemplate(
                localizedContentStringTemplate);
        localizedContentStringTemplateLanguage2.setLocalizedStringTemplate(
                localizedContentStringTemplate);

        final EmailFormatContent emailFormatContent1 =
                EmailFormatContent.builder()
                        .emailLayoutInput("body")
                        .localizedStringTemplate(localizedContentStringTemplate)
                        .build();

        LocalizedStringTemplateLanguage localizedFooterStringTemplateLanguage1 =
                LocalizedStringTemplateLanguage.builder()
                        .language("en")
                        .template("Best regards,\\n DSGov")
                        .build();

        LocalizedStringTemplateLanguage localizedFooterStringTemplateLanguage2 =
                LocalizedStringTemplateLanguage.builder()
                        .language("es")
                        .template("Cordialmente,\\n DSGov")
                        .build();

        LocalizedStringTemplate localizedFooterStringTemplate =
                LocalizedStringTemplate.builder()
                        .localizedTemplateStrings(
                                List.of(
                                        localizedFooterStringTemplateLanguage1,
                                        localizedFooterStringTemplateLanguage2))
                        .build();
        localizedFooterStringTemplateLanguage1.setLocalizedStringTemplate(
                localizedFooterStringTemplate);
        localizedFooterStringTemplateLanguage2.setLocalizedStringTemplate(
                localizedFooterStringTemplate);

        EmailFormatContent emailFormatFooter1 =
                EmailFormatContent.builder()
                        .emailLayoutInput("footer")
                        .localizedStringTemplate(localizedFooterStringTemplate)
                        .build();

        SmsFormat smsFormat =
                SmsFormat.builder()
                        .localizedStringTemplate(
                                LocalizedStringTemplate.builder()
                                        .localizedTemplateStrings(
                                                List.of(
                                                        localizedSmsStringTemplateLanguage1,
                                                        localizedSmsStringTemplateLanguage2))
                                        .build())
                        .build();
        localizedSmsStringTemplateLanguage1.setLocalizedStringTemplate(
                smsFormat.getLocalizedStringTemplate());
        localizedSmsStringTemplateLanguage2.setLocalizedStringTemplate(
                smsFormat.getLocalizedStringTemplate());

        LocalizedStringTemplateLanguage localizedSubjectStringTemplateLanguage1 =
                LocalizedStringTemplateLanguage.builder()
                        .language("en")
                        .template("Your Financial Benefits Application has been Approved")
                        .build();

        LocalizedStringTemplateLanguage localizedSubjectStringTemplateLanguage2 =
                LocalizedStringTemplateLanguage.builder()
                        .language("es")
                        .template("Su Solicitud de Beneficios Financieros ha sido Aprobada")
                        .build();

        EmailFormat emailFormat =
                EmailFormat.builder()
                        .localizedSubjectStringTemplate(
                                LocalizedStringTemplate.builder()
                                        .localizedTemplateStrings(
                                                List.of(
                                                        localizedSubjectStringTemplateLanguage1,
                                                        localizedSubjectStringTemplateLanguage2))
                                        .build())
                        .emailFormatContents(
                                List.of(
                                        emailFormatGreeting1,
                                        emailFormatContent1,
                                        emailFormatFooter1))
                        .build();
        localizedSubjectStringTemplateLanguage1.setLocalizedStringTemplate(
                emailFormat.getLocalizedSubjectStringTemplate());
        localizedSubjectStringTemplateLanguage2.setLocalizedStringTemplate(
                emailFormat.getLocalizedSubjectStringTemplate());
        emailFormatGreeting1.setEmailFormat(emailFormat);
        emailFormatContent1.setEmailFormat(emailFormat);
        emailFormatFooter1.setEmailFormat(emailFormat);

        EmailLayout createdEmailLayout =
                emailLayoutService.createEmailLayout(emailLayoutKeykey, emailLayout);

        MessageTemplate template =
                MessageTemplate.builder()
                        .key(templateKey)
                        .name("template name")
                        .description("template description")
                        .parameters(templateParameters)
                        .emailLayoutKey(createdEmailLayout.getKey())
                        .smsFormat(smsFormat)
                        .emailFormat(emailFormat)
                        .build();

        createdTemplate = templateService.createOrUpdateTemplate(templateKey, template);
    }

    @Test
    void testHandleMessage_sms() throws JsonProcessingException, ApiException {
        UUID userId = UUID.randomUUID();
        BasicAcknowledgeablePubsubMessage ack =
                Mockito.mock(BasicAcknowledgeablePubsubMessage.class);
        Message<?> message =
                MessageBuilder.withPayload(generateJsonMessage(userId))
                        .setHeader(GcpPubSubHeaders.ORIGINAL_MESSAGE, ack)
                        .build();
        Mockito.when(userManagementClientService.getUser(Mockito.any()))
                .thenReturn(createUser(userId, "en", "sms", false));

        service.handleMessage(message);

        Mockito.verify(ack).ack();
    }

    @Test
    void testHandleMessage_email() throws JsonProcessingException, ApiException {
        UUID userId = UUID.randomUUID();
        BasicAcknowledgeablePubsubMessage ack =
                Mockito.mock(BasicAcknowledgeablePubsubMessage.class);
        Message<?> message =
                MessageBuilder.withPayload(generateJsonMessage(userId))
                        .setHeader(GcpPubSubHeaders.ORIGINAL_MESSAGE, ack)
                        .build();
        Mockito.when(userManagementClientService.getUser(Mockito.any()))
                .thenReturn(createUser(userId, "en", "email", false));

        service.handleMessage(message);

        Mockito.verify(ack).ack();
    }

    private byte[] generateJsonMessage(UUID userId) throws JsonProcessingException {
        Map<String, String> parameters =
                Map.of(
                        "name", "Deibys Parra",
                        "transactionId", "38dh38");
        io.nuvalence.platform.notification.service.domain.Message message =
                io.nuvalence.platform.notification.service.domain.Message.builder()
                        .messageTemplateKey(createdTemplate.getKey())
                        .userId(userId.toString())
                        .status("DRAFT")
                        .parameters(parameters)
                        .build();
        messageService.save(message);
        return objectMapper.writeValueAsString(message).getBytes(StandardCharsets.UTF_8);
    }

    private UserDTO createUser(
            UUID id,
            String preferredLanguage,
            String preferredCommunicationMethod,
            boolean nullUserPreferences) {
        UserPreferenceDTO userPreferences = null;
        if (!nullUserPreferences) {
            userPreferences = new UserPreferenceDTO();
            userPreferences.setPreferredLanguage(preferredLanguage);
            userPreferences.setPreferredCommunicationMethod(preferredCommunicationMethod);
        }

        UserDTO user = new UserDTO();
        user.setId(id);
        user.setEmail("test@nobody.com");
        user.setPhoneNumber("1234567890");
        user.setPreferences(userPreferences);

        return user;
    }
}
