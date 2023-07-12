package io.nuvalence.platform.notification.service.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Entity representing an Email format.
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(exclude = "messageTemplate")
@ToString(exclude = "messageTemplate")
@Entity
@Table(name = "email_format")
public class EmailFormat {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", length = 36, updatable = false, nullable = false)
    private UUID id;

    @OneToOne(mappedBy = "emailFormat")
    private MessageTemplate messageTemplate;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "subject", referencedColumnName = "id")
    private LocalizedStringTemplate localizedSubjectStringTemplate;

    @OneToMany(
            mappedBy = "emailFormat",
            cascade = CascadeType.ALL,
            fetch = javax.persistence.FetchType.EAGER)
    private List<EmailFormatContent> emailFormatContents;
}
