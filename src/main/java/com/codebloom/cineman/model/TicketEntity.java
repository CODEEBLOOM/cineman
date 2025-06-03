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
	    @Column(name = "ticket_id")
	    private Long id;
	 
	    @Column(name = "status")
	    private Byte status;

	    @Column(name = "price")
	    private Double price;


	    @ManyToOne
	    @JoinColumn(name = "show_time_id", nullable = false)
	    private ShowTimeEntity showTime;

	    @ManyToOne
	    @JoinColumn(name = "ticket_type_id", nullable = false)
	    private TicketTypeEntity ticketType;

	    @ManyToOne
	    @JoinColumn(name = "seat_id", nullable = false)
	    private SeatEntity seat;

	@ManyToOne
	@JoinColumn(name = "invoice_id")
	private InvoiceEntity invoice;


}
