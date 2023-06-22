package io.nuvalence.platform.notification.service.domain;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

/**
 * Base class for all notes.
 */
@Data
@ToString
@Entity
@Table(name = "email_layout")
public class EmailLayout {

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

    @Column(name = "content")
    private String content;

    @Convert(disableConversion = true)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "email_layout_input",
            joinColumns = @JoinColumn(name = "email_layout_id", nullable = false))
    @Column(name = "input")
    private List<String> inputs = new ArrayList<>();

    @Column(name = "createdby", length = 64)
    private String createdBy;

    @Column(name = "created_timestamp", updatable = false)
    private OffsetDateTime createdTimestamp;

    @Column(name = "last_updated_timestamp")
    private OffsetDateTime lastUpdatedTimestamp;

    /**
     * Determine if the given email layout is the same version as this one.
     *
     * @param emailLayout email layout to compare
     * @return true if the email layout is the same version
     */
    public boolean isSameVersion(EmailLayout emailLayout) {
        return this.content.equals(emailLayout.getContent())
                && this.inputs.size() == emailLayout.getInputs().size()
                && this.inputs.containsAll(emailLayout.getInputs());
    }
}
