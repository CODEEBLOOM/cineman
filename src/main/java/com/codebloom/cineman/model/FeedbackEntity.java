package com.codebloom.cineman.model;

import com.codebloom.cineman.common.enums.SatisfactionLevel;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "feedbacks")
public class FeedbackEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_id")
    Integer feedbackId;

    @Column(name = "content", columnDefinition = "NVARCHAR(500)", nullable = true)
    String content;

    @Column(name = "satisfaction_level", columnDefinition = "TINYINT")
    @Enumerated(EnumType.ORDINAL)
    SatisfactionLevel satisfactionLevel;
    
    @Column(name = "reason_for_review", columnDefinition = "NVARCHAR(250)", nullable = false)
    String reasonForReview;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_feedback", columnDefinition = "DATETIME")
    @CreationTimestamp
    Date dateFeedback;

    @ManyToOne
    @JoinColumn(name = "feedback_topic", nullable = false)
    FeedbackTopicEntity topic;

    @Column(name = "is_active")
    Boolean isActive;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @OneToOne
    @JoinColumn(name = "invoice_id", nullable = false, unique = true)
    InvoiceEntity invoice;
}
