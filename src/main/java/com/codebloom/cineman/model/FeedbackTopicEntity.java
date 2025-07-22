package com.codebloom.cineman.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "feedback_topics")
public class FeedbackTopicEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "topic_id")
    private Integer topicId;

    @Column(name = "topic_name", columnDefinition = "NVARCHAR(100)", nullable = false)
    private String topicName;

    @Column(name = "description", columnDefinition = "NVARCHAR(200)")
    private String description;

    @OneToMany(mappedBy = "topic")
    @JsonIgnore
    private List<FeedbackEntity> feedbacks;
}
