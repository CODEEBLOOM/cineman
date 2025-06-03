package com.codebloom.cineman.model;


import com.codebloom.cineman.common.enums.SatisfactionLevel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "feedbacks")
public class FeedbacksEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_id", columnDefinition = "INT")
    private Integer feedbackId;


    @Column(name = "content", columnDefinition = "NVARCHAR(500)")
    private String content;

    @Column(name = "satisfaction_level", nullable = false, columnDefinition = "TINYINT")
    @Enumerated(EnumType.ORDINAL)
    private SatisfactionLevel satisfactionLevel;

    @Column(name = "reason_for_review", columnDefinition = "NVARCHAR(100)")
    private String reasonForReview;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_feedback", columnDefinition = "DATETIME")
    private Date dateFeedback;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "feedback_topic", nullable = false)
    private FeedbackTopicEntity topic;

}
