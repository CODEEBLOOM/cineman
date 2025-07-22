-- chuyển trường origin_price trong table movie sang kiểu DECIMAL(10,2) --
ALTER TABLE show_times
ALTER
COLUMN origin_price DECIMAL(10,2) NOT NULL;

ALTER TABLE show_times
DROP
CONSTRAINT CK__show_time__origin_price__71D1E811

ALTER TABLE SHOW_TIMES
    ADD CONSTRAINT CK__show_time__origin_price__71D1E811
        CHECK (origin_price >= 0);

-- CHUYỂN KIỂU DỮ LIỆU CỦA PRICE TRONG BẢNG SEAT_TYPES SANG KIỂU DỮ LIỆU DECIMAL(10,2) --
ALTER TABLE seat_types
DROP
CONSTRAINT CK__seat_type__price__68487DD7

ALTER TABLE seat_types
ALTER
COLUMN price DECIMAL(10,2) NOT NULL;

ALTER TABLE seat_types
    ADD CONSTRAINT CK__seat_type__price__68487DD7
        CHECK (price >= 0);

-- CHUYỂN ĐỔI NOT NULL TRONG BẢNG SEAT_TYPES --
ALTER TABLE seat_types
DROP CONSTRAINT UQ_seat_types_name

ALTER TABLE seat_types
ALTER
COLUMN name VARCHAR(200) NOT NULL;

ALTER TABLE seat_types
ADD CONSTRAINT UQ_seat_types_name UNIQUE (name);

