package com.codebloom.cineman.model;

import java.io.Serializable;
import java.util.Date;

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
@Table(name = "movies")
public class MovieEntity implements Serializable {
	
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "id_movie")
	    private Long movieId;
	 
	    @Column(name = "id_status", nullable = false, columnDefinition = "VARCHAR(25)")
	    private String statusId;

	    @Column(name = "title_movie", columnDefinition = "NVARCHAR(100)")
	    private String title;

	    @Column(name = "synopsis", columnDefinition = "NVARCHAR(500)")
	    private String synopsis;

	    @Column(name = "detail_description", columnDefinition = "NVARCHAR(500)")
	    private String detailDescription;

	    @Column(name = "release_date")
	    @Temporal(TemporalType.DATE)
	    private Date releaseDate;

	    @Column(name = "language", columnDefinition = "NVARCHAR(50)")
	    private String language;

	    @Column(name = "duration")
	    private Integer duration;

	    @Column(name = "rating")
	    private Integer rating;

	    @Column(name = "age")
	    private Integer age;

	    @Column(name = "trailer_link", columnDefinition = "VARCHAR(200)")
	    private String trailerLink;

	    @Column(name = "poster_image", columnDefinition = "VARCHAR(100)")
	    private String posterImage;

	    @Column(name = "banner_image", columnDefinition = "VARCHAR(100)")
	    private String bannerImage;

	    @Column(name = "created_at")
	    @Temporal(TemporalType.TIMESTAMP)
	    private Date createdAt;

	    @Column(name = "updated_at")
	    @Temporal(TemporalType.TIMESTAMP)
	    private Date updatedAt;
	    
//		 @ManyToOne
//		 @JoinColumn(name = "id_status", referencedColumnName = "id_status", insertable = false, updatable = false)
//		 private MovieStatus movieStatus;
		 


}
