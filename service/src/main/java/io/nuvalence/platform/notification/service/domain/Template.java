package io.nuvalence.platform.notification.service.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Template entity.
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
@Entity
@Table(name = "template")
public class Template {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", length = 36, updatable = false, nullable = false)
    private UUID id;

    @Column(name = "key")
    private String key;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "version")
    private Integer version;

    @Column(name = "status")
    private String status;

    @Column(name = "email_layout_key")
    private String emailLayoutKey;

    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyColumn(name = "parameter_name")
    @Column(name = "parameter_value")
    @CollectionTable(
            name = "template_parameter",
            joinColumns = @JoinColumn(name = "template_id", nullable = false))
    private Map<String, String> parameters;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "template_id", updatable = false, insertable = false)
    private List<TemplateValue> templateValues;

    @Column(name = "createdby", length = 64)
    private String createdBy;

    @Column(name = "created_timestamp", updatable = false)
    private OffsetDateTime createdTimestamp;

    @Column(name = "last_updated_timestamp")
    private OffsetDateTime lastUpdatedTimestamp;
}
