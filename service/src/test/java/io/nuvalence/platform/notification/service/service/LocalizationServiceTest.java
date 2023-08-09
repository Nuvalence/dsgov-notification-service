package io.nuvalence.platform.notification.service.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import io.nuvalence.platform.notification.service.exception.BadDataException;
import io.nuvalence.platform.notification.service.repository.MessageTemplateRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LocalizationServiceTest {

    private LocalizationService service;
    private MessageTemplateRepository templateRepository;

    @BeforeAll
    static void init() {}

    @BeforeEach
    void setUp() {
        templateRepository = mock(MessageTemplateRepository.class);
        service = new LocalizationService(templateRepository);
    }

    @Test
    void locateTagsValidation() {
        service.validateLocaleTag("en");
        service.validateLocaleTag("en-US");
        service.validateLocaleTag("es-US");
        service.validateLocaleTag("es-CO");
        // assert it throws a BadDataException with specific message validation
        assertThrows(BadDataException.class, () -> service.validateLocaleTag("en-US-ES"));
        assertThrows(BadDataException.class, () -> service.validateLocaleTag(""));
        assertThrows(BadDataException.class, () -> service.validateLocaleTag(null));
    }

    @Test
    void tempTest2() throws Exception {
        // var responseString =
        //         "<?xml version=\"1.0\" encoding=\"UTF-8\"?><xliff"
        //             + " xmlns=\"urn:oasis:names:tc:xliff:document:1.2\""
        //             + " xmlns:its=\"http://www.w3.org/2005/11/its\""
        //             + " xmlns:itsxlf=\"http://www.w3.org/ns/its-xliff/\""
        //             + " xmlns:okp=\"okapi-framework:xliff-extensions\" version=\"1.2\""
        //             + " its:version=\"2.0\"><!-- Please use 'resname' attribute on all 'group'
        // and"
        //             + " 'trans-unit' elements --><file original=\"notification-service\""
        //             + " source-language=\"en\" target-language=\"es\""
        //             + " datatype=\"x-plaintext\"><body><group id=\"FbEnUbF9Nq\""
        //             + " resname=\"FbEnUbF9Nq\"><group id=\"sms\" resname=\"sms\"><trans-unit"
        //             + " id=\"message\" resname=\"message\"><source"
        //             + " xml:lang=\"en\">email-sms-english</source><target"
        //             + " xml:lang=\"es\">email-sms-spanish</target></trans-unit></group><group"
        //             + " id=\"email\" resname=\"email\"><trans-unit id=\"subject\""
        //             + " resname=\"subject\"><source"
        //             + " xml:lang=\"en\">email-subject-english</source><target"
        //             + " xml:lang=\"es\">email-subject-spanish</target></trans-unit><group"
        //             + " id=\"content\" resname=\"content\"><trans-unit id=\"body\""
        //             + " resname=\"body\"><source"
        //             + " xml:lang=\"en\">email-body-english</source><target"
        //             + " xml:lang=\"es\">email-body-spanish</target></trans-unit></group>"
        //             + "</group></group></body></file></xliff>";

        // var xmlDoc = XmlUtils.getXmlDocument(responseString);

        // var root = xmlDoc.getRootElement();
        // assertEquals("xliff", root.getName());
        // assertEquals("1.2", root.getAttributeValue("version"));

        // var namespace = root.getNamespace();
        // assertEquals("urn:oasis:names:tc:xliff:document:1.2", namespace.getURI());

        // var file = root.getChild("file", namespace);
        // assertEquals("notification-service", file.getAttributeValue("original"));
        // assertEquals("es", file.getAttributeValue("target-language"));

        // var mainGroups = file.getChild("body", namespace).getChildren("group", namespace);
        // assertEquals(1, mainGroups.size());

        // var group = mainGroups.get(0);
        // assert (!group.getAttributeValue("resname").isBlank());

        // var messageFormats = group.getChildren("group", namespace);
        // assertEquals(2, messageFormats.size());

        // var smsFormat = messageFormats.get(0);
        // assertEquals("sms", smsFormat.getAttributeValue("resname"));

        // var smsMessage = smsFormat.getChild("trans-unit", namespace);
        // assertEquals("message", smsMessage.getAttributeValue("resname"));
        // assertEquals("email-sms-english", smsMessage.getChild("source", namespace).getText());
        // assertEquals("email-sms-spanish", smsMessage.getChild("target", namespace).getText());

        // var emailFormat = messageFormats.get(1);
        // assertEquals("email", emailFormat.getAttributeValue("resname"));
        // var emailSubject = emailFormat.getChild("trans-unit", namespace);
        // assertEquals("subject", emailSubject.getAttributeValue("resname"));

        // assertEquals("email-subject-english", emailSubject.getChild("source",
        // namespace).getText());
        // assertEquals("email-subject-spanish", emailSubject.getChild("target",
        // namespace).getText());

        // var emailContent = emailFormat.getChild("group", namespace);
        // assertEquals("content", emailContent.getAttributeValue("resname"));

        // var emailContentTranslation = emailContent.getChild("trans-unit", namespace);
        // assertEquals("body", emailContentTranslation.getAttributeValue("resname"));

        // assertEquals(
        //         "email-body-english",
        //         emailContentTranslation.getChild("source", namespace).getText());
        // assertEquals(
        //         "email-body-spanish",
        //         emailContentTranslation.getChild("target", namespace).getText());

        // assertEquals(1, 1);
    }
}
