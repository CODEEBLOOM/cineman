package com.codebloom.cineman.model;

import java.io.Serializable;

import java.util.Date;
import java.util.List;

import jakarta.persistence.*;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "show_times")
public class ShowTimeEntity implements Serializable {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "show_time_id")
    private Long id;

    @Column(name = "origin_price", nullable = false)
    private Integer originPrice;

    @Column(name = "show_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date showDate;

    @Column(name = "start_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime;

    @Column(name = "end_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date endTime;

    @Column(name = "total_cinema")
    Integer totalCinema;
    
    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    private MovieEntity movie;

    @OneToMany(mappedBy = "showTime")
    private List<TicketEntity> tickets;

    @ManyToOne
    @JoinColumn(name = "movie_theater_id", nullable = false)
    private MovieTheatersEntity movieTheater;

}
