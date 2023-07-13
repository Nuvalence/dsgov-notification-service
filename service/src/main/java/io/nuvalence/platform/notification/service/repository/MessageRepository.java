package io.nuvalence.platform.notification.service.repository;

import io.nuvalence.platform.notification.service.domain.Message;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

/**
 * Message repository.
 */
public interface MessageRepository extends CrudRepository<Message, UUID> {
}
