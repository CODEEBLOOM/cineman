package com.codebloom.cineman.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "feedback_topics")
public class FeedbackTopicEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "topic_id", columnDefinition = "INT")
    private Integer topicId;

    @Column(name = "topic_name", columnDefinition = "NVARCHAR(100)")
    private String topicName;

    @Column(name = "description", columnDefinition = "NVARCHAR(200)")
    private String description;

    @OneToMany(mappedBy = "topic")
    private List<FeedbacksEntity> feedbacks;
}
