CREATE TABLE IF NOT EXISTS movie_reviews (
    review_id BIGSERIAL PRIMARY KEY,
    rating_score INTEGER NOT NULL CHECK (rating_score BETWEEN 1 AND 5),
    comment VARCHAR(1000),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id BIGINT NOT NULL,
    movie_id INTEGER NOT NULL,
    invoice_id BIGINT NOT NULL,
    CONSTRAINT uq_movie_reviews_user_movie UNIQUE (user_id, movie_id),
    CONSTRAINT fk_movie_reviews_user FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT fk_movie_reviews_movie FOREIGN KEY (movie_id) REFERENCES movies(movie_id),
    CONSTRAINT fk_movie_reviews_invoice FOREIGN KEY (invoice_id) REFERENCES invoices(invoice_id)
);

CREATE INDEX IF NOT EXISTS idx_movie_reviews_movie_id ON movie_reviews(movie_id);
CREATE INDEX IF NOT EXISTS idx_movie_reviews_user_id ON movie_reviews(user_id);
