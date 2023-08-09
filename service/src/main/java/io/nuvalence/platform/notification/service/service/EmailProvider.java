package io.nuvalence.platform.notification.service.service;

import java.io.IOException;

/**
 * Interface for email providers.
 */
public interface EmailProvider {

    /** Send an email.
     *
     * @param to      recipient
     * @param subject subject
     * @param body    body
     */
    void sendEmail(String to, String subject, String body) throws IOException;
}
