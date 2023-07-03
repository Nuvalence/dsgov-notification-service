package io.nuvalence.platform.notification.service.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

/**
 * TemplateValue entity.
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
@Entity
@Table(name = "template_value")
@IdClass(TemplateValueId.class)
public class TemplateValue implements Serializable {

    private static final long serialVersionUID = -9082037792338811608L;

    @Id
    @Column(name = "template_id")
    private UUID templateId;

    @Id
    @Column(name = "template_value_type")
    private String templateValueType;

    @Id
    @Column(name = "template_value_key")
    private String templateValueKey;

    @Column(name = "template_value_value")
    private String templateValueValue;
}
