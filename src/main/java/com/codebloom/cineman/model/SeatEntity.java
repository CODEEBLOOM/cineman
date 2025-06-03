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
@Table(name = "seats")
public class SeatEntity implements Serializable {
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "id_seat")
	    private Long id;

	    @Column(name = "seat_number")
	    private Integer seatNumber;

	    @Column(name = "row")
	    private Integer row;

	    @ManyToOne
	    @JoinColumn(name = "id_seat_type" ,nullable = false )
	    private SeatTypeEntity seatType;

//	    @ManyToOne
//	    @JoinColumn(name = "id_room", nullable = false )
//	    private CinemaTheater room;

}
