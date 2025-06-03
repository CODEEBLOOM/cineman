package com.codebloom.cineman.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "social_accounts")
public class SocialAccountEntity implements Serializable {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "social_account_id")
    private Integer id;


    @Column(name = "provider", columnDefinition = "VARCHAR(20)")
    private String provider;

    @Column(name = "provider_id")
    private Integer providerId;

    @Column(name = "email", columnDefinition = "VARCHAR(150)", unique = true)
    private String email;

    @Column(name = "name", columnDefinition = "VARCHAR(100)")
    private String name;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

}
