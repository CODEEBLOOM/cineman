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
@Table(name = "tickets")
public class TicketEntity implements Serializable{
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "id_ticket")
	    private Long id;
	 
	    @Column(name = "status")
	    private Byte status;

	    @Column(name = "price")
	    private Double price;

//	    @ManyToOne
//	    @JoinColumn(name = "id_invoice", nullable = false)
//	    private Invoice invoice;

	    @ManyToOne
	    @JoinColumn(name = "id_show_time", nullable = false)
	    private Show_timeEntity showTime;

	    @ManyToOne
	    @JoinColumn(name = "id_ticket_type", nullable = false)
	    private Ticket_TypeEntity ticketType;

	    @ManyToOne
	    @JoinColumn(name = "id_seat", nullable = false)
	    private SeatEntity seat;
}
