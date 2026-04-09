package com.codebloom.cineman.model;

import com.codebloom.cineman.common.enums.StatusPromotion;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "promotions")
public class PromotionEntity implements Serializable  {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "promotion_id")
    private Long id;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(length = 500)
    private String content;

    @Column(name = "code", length=100, nullable = false)
    private String code;

    @Column(name = "start_day", nullable = false)
    private LocalDateTime startDay;

    @Column(name = "end_day", nullable = false)
    private LocalDateTime endDay;

    @Column(name = "discount", nullable = false)
    private Double discount;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "limit_amount", nullable = false)
    private Double limitAmount;

    @Column(name = "status", nullable = false)
    private StatusPromotion status;

    @ManyToOne
    @JoinColumn(name = "promotion_type_id")
    private PromotionTypeEntity promotionType;

    @ManyToOne
    @JoinColumn(name = "staff_id", nullable = false)
    @JsonIgnore
    private UserEntity staff;

    @ManyToMany
    @JoinTable(
            name = "promotion_membership_ranks",
            joinColumns = @JoinColumn(name = "promotion_id", referencedColumnName = "promotion_id"),
            inverseJoinColumns = @JoinColumn(name = "membership_rank_id", referencedColumnName = "id")
    )
    @Builder.Default
    private List<MembershipRankEntity> membershipRanks = new ArrayList<>();

    @OneToMany(mappedBy = "promotion")
    @JsonIgnore
    private List<InvoiceEntity> invoices;


}

