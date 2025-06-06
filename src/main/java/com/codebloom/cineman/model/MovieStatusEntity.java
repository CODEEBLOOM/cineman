    package com.codebloom.cineman.model;


    import jakarta.persistence.*;
    import lombok.AllArgsConstructor;
    import lombok.Getter;
    import lombok.NoArgsConstructor;
    import lombok.Setter;

    import java.io.Serializable;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Entity
    @Table(name = "movie_status")
    public class MovieStatusEntity implements Serializable {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "status_id", columnDefinition = "INT")
        private Integer statusId;


        @Column(name = "name" , columnDefinition = "NVARCHAR(100)")
        private String name;

        @Column(name = "description" , columnDefinition = "NVARCHAR(200)")
        private String description;

    }

