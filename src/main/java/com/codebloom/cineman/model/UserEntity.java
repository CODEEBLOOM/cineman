package com.codebloom.cineman.model;

import com.codebloom.cineman.common.enums.GenderUser;
import com.codebloom.cineman.common.enums.UserStatus;
import com.codebloom.cineman.common.enums.UserType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class UserEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "email", unique = true, nullable = false, columnDefinition = "VARCHAR(150)")
    private String email;

    @Column(name = "password", columnDefinition = "VARCHAR(100)")
    private String password;

    @Column(name = "fullname", columnDefinition = "NVARCHAR(100)")
    private String fullName;

    @Column(name = "phone_number", nullable = false, columnDefinition = "VARCHAR(20)")
    private String phoneNumber;

    @Column(name = "address", columnDefinition = "NVARCHAR(200)")
    private String address;

    @Column(name = "date_of_birth")
    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;

    @Column(name = "gender", columnDefinition = "TINYINT")
    @Enumerated(EnumType.ORDINAL)
    private GenderUser gender;

    @Column(name = "save_point")
    private Integer savePoint;

    @Column(name = "facebook_id")
    private Integer facebookId;

    @Column(name = "google_id")
    private Integer googleId;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private Date updatedAt;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "type", nullable = false, columnDefinition = "TINYINT")
    private UserType userType;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status", nullable = false, columnDefinition = "TINYINT")
    private UserStatus status;

    @Column(name = "is_active", columnDefinition = "BIT")
    private boolean IsActive ;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserRoleEntity> userRoles; //

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<FeedbacksEntity> feedbacks;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<SocialAccountEntity> socialAccounts;


    @OneToMany(mappedBy = "staff", cascade = CascadeType.ALL)
    private List<PromotionEntity> promotions;

    @OneToMany(mappedBy = "customer")
    private List<InvoiceEntity> customerInvoices;

    @OneToMany(mappedBy = "staff")
    private List<InvoiceEntity> staffInvoices;

}
