package com.codebloom.cineman.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Check;

import java.io.Serializable;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "snacks")
@Check(constraints = "unit_price >=0")
public class SnackEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "snack_id")
    Integer id;

    @Column(name = "name", columnDefinition = "NVARCHAR(100)")
    String snackName;

    @Column(name = "unit_price")
    Double unitPrice;

    @Column(name = "image", columnDefinition = "VARCHAR(100)")
    String image;

    @Column(name = "description", columnDefinition = "NVARCHAR(250)")
    String description;

    @Column(name = "is_active")
    Boolean isActive;

    @ManyToOne
    @JoinColumn(name = "snack_type_id", nullable = false)
    SnackTypeEntity snackType;

    @OneToMany(mappedBy = "snack")
    @JsonBackReference
    List<DetailBookingSnackEntity> detailBookingSnacks;
}
