package com.codebloom.cineman.model;

import com.codebloom.cineman.common.enums.ShowTimeStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Check;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "show_times")
@Check(constraints = "origin_price >= 0")
public class ShowTimeEntity implements Serializable {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "show_time_id")
    private Long id;

    @Column(name = "origin_price", nullable = false)
    private Double originPrice;

    @Column(name = "show_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date showDate;

    @Column(name = "start_time", nullable = false)
    @Temporal(TemporalType.TIME)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    @Temporal(TemporalType.TIME)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime endTime;

    @Column(name = "status", columnDefinition = "TINYINT", nullable = false)
    private ShowTimeStatus status;

    @OneToMany(mappedBy = "showTime")
    @JsonIgnore
    private List<TicketEntity> tickets;

    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    @JsonIgnore
    private MovieEntity movie;

    @ManyToOne
    @JoinColumn(name = "cinema_theater_id", nullable = false)
    @JsonIgnore
    private CinemaTheaterEntity cinemaTheater;

}
