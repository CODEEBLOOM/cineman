package com.codebloom.cineman.model;

import java.io.Serializable;
import java.util.Date;
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
@Table(name = "show_times")
public class ShowTimeEntity implements Serializable {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "show_time_id")
    private Long id;


    @ManyToOne
    @JoinColumn(name = "movie_theater_id", nullable = false)
    private MovieTheatersEntity movieTheater;


    @Column(name = "origin_price")
    private Integer originPrice;

    @Column(name = "show_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date showDate;

    @Column(name = "start_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime;

    @Column(name = "end_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endTime;
    
    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    private MovieEntity movie;

//    @ManyToOne
//    @JoinColumn(name = "id_cinema", nullable = false)
//    private CinemaTheater cinema;

    @OneToMany(mappedBy = "showTime")
    private List<TicketEntity> tickets;
}
