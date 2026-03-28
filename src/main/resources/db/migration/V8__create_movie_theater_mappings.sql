CREATE TABLE IF NOT EXISTS movie_theater_mappings (
    movie_theater_mapping_id SERIAL PRIMARY KEY,
    movie_id INTEGER NOT NULL,
    movie_theater_id INTEGER NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_movie_theater_mappings_movie
        FOREIGN KEY (movie_id) REFERENCES movies (movie_id),
    CONSTRAINT fk_movie_theater_mappings_movie_theater
        FOREIGN KEY (movie_theater_id) REFERENCES movie_theaters (movie_theater_id)
);

CREATE INDEX IF NOT EXISTS idx_movie_theater_mappings_movie_id
    ON movie_theater_mappings (movie_id);

CREATE INDEX IF NOT EXISTS idx_movie_theater_mappings_movie_theater_id
    ON movie_theater_mappings (movie_theater_id);
