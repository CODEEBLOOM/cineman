//package com.codebloom.cineman.model;
//
//import jakarta.persistence.*;
//import lombok.*;
//import lombok.experimental.FieldDefaults;
//import org.hibernate.annotations.Check;
//
//import java.io.Serializable;
//import java.util.List;
//
//@Getter
//@Setter
//@AllArgsConstructor
//@NoArgsConstructor
//@FieldDefaults(level = AccessLevel.PRIVATE)
//@Builder
//@Entity
//@Table(name = "seat_types")
//@Check(constraints = "price >= 0")
//public class SeatTypeEntity implements Serializable {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "seat_type_id")
//    private Integer seatTypeId;
//
//    @Column(name = "name", columnDefinition = "NVARCHAR(200)")
//    private String name;
//
//    @Column(name = "price")
//    private Double price;
//
//    @OneToMany(mappedBy = "seatType")
//    private List<SeatEntity> seats;
//
//}
package com.codebloom.cineman.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Check;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "seat_types")
@Check(constraints = "price >= 0")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SeatTypeEntity implements Serializable {

    @Id
    @Column(name = "id") // ✅ Đây là tên thật trong DB
    private String id;    // ✅ Phù hợp kiểu VARCHAR(25)

    @Column(name = "name", columnDefinition = "NVARCHAR(200)")
    private String name;

    @Column(name = "price")
    private Double price;

    @Column(name = "seat_type")
    private Boolean seatType;

    @OneToMany(mappedBy = "seatType")
    private List<SeatEntity> seats;
}
