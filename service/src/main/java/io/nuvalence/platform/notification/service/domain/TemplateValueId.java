package io.nuvalence.platform.notification.service.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Template value id.
 */
@NoArgsConstructor
@Data
public class TemplateValueId implements java.io.Serializable {

    private static final long serialVersionUID = -4613027838487421521L;

    private UUID templateId;

    private String templateValueType;

    private String templateValueKey;
}
