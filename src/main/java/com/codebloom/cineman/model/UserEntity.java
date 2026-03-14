package com.codebloom.cineman.model;

import com.codebloom.cineman.common.enums.GenderUser;
import com.codebloom.cineman.common.enums.UserStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class UserEntity implements  Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "email", unique = true, nullable = false, length = 150)
    private String email;

    @Column(name = "password", length = 250, nullable = false)
    private String password;

    @Column(name = "fullname", length = 100, nullable = false)
    private String fullName;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "address", length = 200)
    private String address;

    @Column(name = "avatar", length = 250)
    private String avatar;

    @Column(name = "date_of_birth")
    @Temporal(TemporalType.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date dateOfBirth;

    @Column(name = "gender", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private GenderUser gender;

    @Column(name = "save_point")
    private Integer savePoint;

    @Column(name = "facebook_id", length = 250)
    private String facebookId;

    @Column(name = "google_id", length = 250)
    private String googleId;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private Date updatedAt;

    @ManyToOne
    @JoinColumn(name = "rank_id")
    private MembershipRankEntity membershipRank;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status", nullable = false)
    private UserStatus status;

    @Column(name = "refresh_token")
    private String refreshToken;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    @JsonIgnore
    private Set<UserRoleEntity> userRoles;

    @OneToMany(mappedBy = "user")
    @JsonBackReference
    @JsonIgnore
    private List<FeedbackEntity> feedbacks;

    @OneToMany(mappedBy = "staff")
    @JsonIgnore
    private List<PromotionEntity> promotions;

    @OneToMany(mappedBy = "customer")
    @JsonIgnore
    private List<InvoiceEntity> customerInvoices;

    @OneToMany(mappedBy = "staff")
    @JsonIgnore
    private List<InvoiceEntity> staffInvoices;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<UserPointHistoryEntity> userPointHistories;

    @OneToOne
    @JoinColumn(name = "movie_theater_id")
    private MovieTheaterEntity movieTheater;

}

