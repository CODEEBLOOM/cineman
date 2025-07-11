
-- BẢNG movie_variations CHỨA DANH SÁCH CÁC PHIÊN BẢN PHIM --
-- name: tên phiên bản phim
-- status: trạng thái của phiên bản (1: hoạt động, 0: không hoạt động)
IF OBJECT_ID('movie_variations', 'U') IS NULL
BEGIN
CREATE TABLE movie_variations
(
    id   INT IDENTITY(1,1) PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    status TINYINT DEFAULT 1
);
END
GO

-- Thêm cột movie_variation_id vào bảng movies để trỏ đến bảng movie_variations --
IF COL_LENGTH('movies', 'movie_variation_id') IS NULL
BEGIN
    ALTER TABLE movies
    ADD movie_variation_id INT NULL;
END
GO

-- Thêm foreign key cho bảng movies trỏ đến bảng movie_variations --
IF OBJECT_ID('FK_movies_movie_variations', 'F') IS NULL
BEGIN
ALTER TABLE movies
    ADD CONSTRAINT FK_movies_movie_variations FOREIGN KEY (movie_variation_id) REFERENCES movie_variations (id);
END
GO
