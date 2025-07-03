package com.codebloom.cineman.model;

import com.codebloom.cineman.common.enums.GenderUser;
import com.codebloom.cineman.common.enums.UserStatus;
import com.codebloom.cineman.common.enums.UserType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "users")
public class UserEntity implements  Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    Long userId;

    @Column(name = "email", unique = true, nullable = false, length = 150)
    String email;

    @Column(name = "password", length = 250, nullable = false)
    String password;

    @Column(name = "fullname", columnDefinition = "NVARCHAR(100)", nullable = false)
    String fullName;

    @Column(name = "phone_number", length = 20, unique = true, nullable = false)
    String phoneNumber;

    @Column(name = "address", columnDefinition = "NVARCHAR(200)")
    String address;

    @Column(name = "date_of_birth")
    @Temporal(TemporalType.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    Date dateOfBirth;

    @Column(name = "gender", columnDefinition = "TINYINT", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    GenderUser gender;

    @Column(name = "save_point")
    Integer savePoint;

    @Column(name = "facebook_id")
    Integer facebookId;

    @Column(name = "google_id")
    Integer googleId;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    Date updatedAt;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "type", nullable = false, columnDefinition = "TINYINT")
    UserType userType;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status", nullable = false, columnDefinition = "TINYINT")
    UserStatus status;

    @Column(name = "refresh_token")
    String refreshToken;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    @JsonIgnore
    Set<UserRoleEntity> userRoles;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonBackReference
    @JsonIgnore
    List<FeedbackEntity> feedbacks;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    List<SocialAccountEntity> socialAccounts;

    @OneToMany(mappedBy = "staff", cascade = CascadeType.ALL)
    @JsonIgnore
    List<PromotionEntity> promotions;

    @OneToMany(mappedBy = "customer")
    @JsonIgnore
    List<InvoiceEntity> customerInvoices;

    @OneToMany(mappedBy = "staff")
    @JsonIgnore
    List<InvoiceEntity> staffInvoices;


}
