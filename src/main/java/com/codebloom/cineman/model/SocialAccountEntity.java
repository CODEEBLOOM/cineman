package com.codebloom.cineman.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "social_accounts")
public class SocialAccountEntity implements Serializable {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "social_account_id")
    private Integer id;


    @Column(name = "provider", length = 20, nullable = false)
    private String provider;

    @Column(name = "provider_id")
    private Integer providerId;

    @Column(name = "email", length = 150, unique = true)
    private String email;

    @Column(name = "name", length=100, nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

}
