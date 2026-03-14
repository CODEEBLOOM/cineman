ALTER TABLE cinema_theaters
DROP CONSTRAINT IF EXISTS cinema_theaters_check;

ALTER TABLE cinema_theaters
ADD CONSTRAINT cinema_theaters_check
CHECK (
    number_of_rows > 0
    AND number_of_columns > 0
    AND regular_seat_row > 0
    AND vip_seat_row >= 0
    AND double_seat_row >= 0
    AND regular_seat_row + vip_seat_row + double_seat_row = number_of_rows
) NOT VALID;
