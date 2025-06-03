package com.codebloom.cineman.model;

import java.io.Serializable;
import java.sql.Date;

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
public class Show_timeEntity implements Serializable {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_show_time")
    private Long id;

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
    @JoinColumn(name = "id_movie", nullable = false)
    private MovieEntity movie;

//    @ManyToOne
//    @JoinColumn(name = "id_cinema", nullable = false)
//    private CinemaTheater cinema;
}
