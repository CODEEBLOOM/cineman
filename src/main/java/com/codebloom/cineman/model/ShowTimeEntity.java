package com.codebloom.cineman.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

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

    @OneToMany(mappedBy = "showTime")
    private List<TicketEntity> tickets;

    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    private MovieEntity movie;

    @ManyToOne
    @JoinColumn(name = "cinema_theater_id", nullable = false)
    private CinemaTheaterEntity cinemaTheater;

}
