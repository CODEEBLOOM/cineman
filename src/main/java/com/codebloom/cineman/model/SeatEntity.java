package com.codebloom.cineman.model;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.*;
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
	    @Column(name = "seat_id")
	    private Long id;

	    @Column(name = "seat_number")
	    private Integer seatNumber;

	    @Column(name = "row")
	    private Integer row;

	    @ManyToOne
	    @JoinColumn(name = "seat_type_id" ,nullable = false )
	    private SeatTypeEntity seatType;


	@OneToMany(mappedBy = "seat")
	private List<TicketEntity> tickets;
//	    @ManyToOne
//	    @JoinColumn(name = "id_room", nullable = false )
//	    private CinemaTheater room;

}
