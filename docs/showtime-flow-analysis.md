# Phan tich flow tao xuat chieu voi cau truc hien tai

## 1. Ket luan nhanh

- `show_times` dang phu thuoc truc tiep vao `movies`, `cinema_theaters`, `movie_variations`.
- De co `cinema_theater_id` hop le cho showtime, he thong phai di qua chuoi du lieu: `provinces -> movie_theaters -> cinema_theaters -> seats`.
- De co `movie_id` hop le cho showtime, he thong phai co `movie_status`, `genres`, va du lieu phim.
- Da bo sung API master data cho `movie_variations`.
- Da bo sung API master data cho `ticket_types`; phan nay khong bat buoc de tao showtime, nhung bat buoc de booking ve sau do.
- Co mot so API da co route nhung chua dung duoc hoac implement chua dung nghiep vu.

## 2. Quan he du lieu rut gon

```text
provinces
  -> movie_theaters
    -> cinema_theaters
      -> seats
      -> show_times
        -> tickets

movie_status
  -> movies
    -> show_times

movie_variations
  -> show_times

seat_types
  -> seats

ticket_types
  -> tickets

genres
  -> movie_genres
    -> movies

participants + movie_roles
  -> movie_participants
    -> movies
```

## 3. Flow tao xuat chieu

### 3.1. Chuan bi master data

1. Tao `province`.
2. Tao `movie_theater` cho chi nhanh/rap.
3. Tao `cinema_type` cho loai phong chieu.
4. Tao `cinema_theater` voi so hang, so cot, va co cau hang thuong/VIP/double.
5. Tao `seat_type` de dinh nghia phu thu theo loai ghe.
6. Tao danh sach `seat` cho tung phong chieu.
7. Publish `cinema_theater`.
8. Tao `movie_status`, `genre`, `participant`, `movie_role` neu chua co.
9. Tao `movie`.
10. Tao `movie_variation` nhu 2D, 3D, IMAX, Phu de, Long tieng.

### 3.2. Tao showtime

Client admin can cac truong sau:

- `showDate`
- `startTime`
- `originPrice`
- `status`
- `movieId`
- `cinemaTheaterId`
- `movieVariationId`

Backend hien tai validate theo thu tu nghiep vu sau:

1. `showDate` khong duoc o qua khu. Neu la hom nay thi `startTime` phai >= thoi diem hien tai.
2. `cinemaTheater` phai ton tai va dang `PUBLISHED`.
3. `movie` phai ton tai.
4. `movie.releaseDate <= showDate`.
5. `movie.status` khong duoc la `NC` hoac `CNS`.
6. Gio chieu phai nam trong khung 08:00 -> 22:00 sau khi cong them `movie.duration`.
7. Khong duoc trung/cheo khung gio voi showtime khac trong cung phong va cung ngay.
8. `endTime` duoc tinh tu dong = `startTime + movie.duration`.
9. Luu ban ghi vao `show_times`.

### 3.3. Sau khi tao showtime

- He thong hien tai **khong tu sinh danh sach ticket** khi tao showtime.
- Ghe trong/da dat duoc tinh dong tu `seats` cua phong va `tickets` da tao cho showtime do.
- Ticket chi duoc tao khi nguoi dung/staff bat dau giu ghe dat ve.

## 4. Master data can co

| Master data | Bang/entity | Vai tro | Muc do can thiet | Trang thai API hien tai |
| --- | --- | --- | --- | --- |
| Province | `provinces` / `ProvinceEntity` | Xac dinh khu vuc cua rap | Bat buoc gian tiep | Da co admin CRUD |
| Movie theater | `movie_theaters` / `MovieTheaterEntity` | Chi nhanh/rap tong | Bat buoc gian tiep | Da co admin CRUD |
| Cinema type | `cinema_types` / `CinemaTypeEntity` | Loai phong chieu | Bat buoc gian tiep | Da co admin CRUD |
| Cinema theater | `cinema_theaters` / `CinemaTheaterEntity` | Phong chieu tao showtime | Bat buoc truc tiep | Da co admin CRUD + publish |
| Seat type | `seat_types` / `SeatTypeEntity` | Phu thu theo loai ghe | Bat buoc cho booking | Da co admin CRUD |
| Seat | `seats` / `SeatEntity` | So do ghe cua phong | Bat buoc cho booking | Da co admin CRUD + add-mul |
| Movie status | `movie_status` / `MovieStatusEntity` | Dieu kien de phim duoc lap lich | Bat buoc gian tiep | Da co admin CRUD |
| Genre | `genres` / `GenresEntity` | Bat buoc khi tao phim theo request hien tai | Bat buoc gian tiep | Da co admin CRUD |
| Participant | `participants` / `ParticipantEntity` | Dien vien/dao dien | Tuy chon cho showtime, huu ich cho movie | Da co admin CRUD |
| Movie role | `movie_roles` / `MovieRoleEntity` | Vai tro participant | Tuy chon cho showtime, huu ich cho movie | Da co admin CRUD |
| Movie-participant | `movie_participants` / `MovieParticipantEntity` | Gan participant vao phim | Tuy chon cho showtime, huu ich cho movie | Da co admin CRUD |
| Movie | `movies` / `MovieEntity` | Nguon phim de lap lich | Bat buoc truc tiep | Da co admin CRUD |
| Movie variation | `movie_variations` / `MovieVariationEntity` | Dinh dang suat chieu | Bat buoc truc tiep | Da co admin CRUD + public list |
| Ticket type | `ticket_types` / `TicketTypeEntity` | Gia theo doi tuong ve | Khong bat buoc cho tao showtime, bat buoc cho booking | Da co admin CRUD + public list |
| Show time status | enum `ShowTimeStatus` | Trang thai `VALID/INVALID/DELETED` | Bat buoc truc tiep | Dang dung enum, khong can bang master data |

## 5. API hien co dang ho tro flow tao showtime

Base path dang dung trong profile `dev`: `/api/v01`

### 5.1. Nhom master data rap/phong/ghe

- `GET/POST/PUT/DELETE /api/v01/admin/province/...`
- `GET/POST/PUT/DELETE /api/v01/admin/movie-theater/...`
- `GET/POST/DELETE /api/v01/admin/cinema-type/...`
- `GET/POST/PUT/DELETE /api/v01/admin/cinema-theater/...`
- `PUT /api/v01/admin/cinema-theater/{id}/published`
- `GET /api/v01/admin/cinema-theater/{id}/seat-map`
- `GET/POST/PUT/DELETE /api/v01/admin/seat-type/...`
- `GET/POST/PUT/DELETE /api/v01/admin/seat/...`
- `POST /api/v01/admin/seat/add-mul`

### 5.2. Nhom master data phim

- `GET/POST/PUT/DELETE /api/v01/admin/movie-status/...`
- `GET/POST/PUT/DELETE /api/v01/admin/genre/...`
- `GET/POST/PUT/DELETE /api/v01/admin/participant/...`
- `GET/POST/PUT/DELETE /api/v01/admin/movie-role/...`
- `POST/PUT/DELETE /api/v01/admin/movie-genre/...`
- `POST/PUT/DELETE /admin/movie-participant/...`
- `GET/POST/PUT/DELETE /api/v01/admin/movie/...`
- `GET/POST/PUT/DELETE /api/v01/admin/movie-variation/...`
- `GET/POST/PUT/DELETE /api/v01/admin/ticket-type/...`
- `GET /api/v01/movie-variation/all`
- `GET /api/v01/ticket-type/all`

### 5.3. Nhom showtime

- `GET /api/v01/admin/show-time/all`
- `GET /api/v01/admin/show-time/{id}`
- `GET /api/v01/admin/show-time/movie/{id}`
- `GET /api/v01/admin/show-time/cinema-theater/{id}`
- `POST /api/v01/admin/show-time/add`
- `PUT /api/v01/admin/show-time/{id}/update`
- `DELETE /api/v01/admin/show-time/{id}/delete`

## 6. API con thieu hoac nen co them

### 6.1. API nen co them de tao showtime de hon

#### 1. API copy/bulk create showtime

Ly do:

- Voi rap co nhieu suat chieu, tao tung showtime se rat ton thao tac.

De xuat:

- `POST /api/v01/admin/show-time/bulk-add`
- `POST /api/v01/admin/show-time/copy-by-date`

## 7. API da co nhung hien tai chua dung duoc hoac chua dung nghiep vu

### 7.1. Danh sach phong theo rap

API:

- `GET /api/v01/admin/cinema-theater/movie-theater/{movieTheaterId}/all`

Trang thai moi:

- Da sua logic lay danh sach phong theo `movieTheaterId`.
- API nay hien co the dung de nap dropdown phong theo rap khi tao showtime.

### 7.2. Seat map theo showtime cho booking

API:

- `GET /api/v01/show-times/{id}/cinema-theater/{cinemaTheaterId}/seat-map`

Van de:

- `ShowTimeServiceImpl.findSeatMapByShowTimeIdAndCinemaTheaterId()` dang `return null`.

Tac dong:

- Flow booking sau khi tao showtime chua dung duoc day du.

### 7.3. API filter showtime admin

API:

- `GET /api/v01/admin/show-time/all`

Van de:

- Co nhan `ShowTimeRequestNew`, nhung service dang `findAll()` va bo qua filter.

Tac dong:

- Man hinh quan ly showtime khong loc dung theo rap/ngay/trang thai.

### 7.4. API showtime public theo phim va rap

- `GET /api/v01/admin/show-time/cinema-theater/{id}/occupied-slots?showDate=yyyy-MM-dd`

Trang thai moi:

- Da bo sung xong de FE ve scheduler theo phong/ngay.
- API tra danh sach showtime trong ngay cua 1 phong, sap xep theo `startTime`.

### 7.5. API showtime public theo phim va rap

API:

- `GET /api/v01/show-times/movie/{movieId}/movie-theater/{movieTheaterId}`
- `GET /api/v01/show-times/movie/{movieId}/movie-theater/{movieTheaterId}/by-date`

Van de:

- Query `findAllShowTimeByMovieIdAndMovieTheaterId` khong dung filter `status`.
- Query `findAllShowTimeByMovieIdAndMovieTheaterIdAndShowDateEqual` dang dung `showDate >= :showDate`, khong phai `=` nhu ten ham/API.

Tac dong:

- Client co the nhan showtime sai trang thai hoac sai ngay.

## 8. Khoang trong nghiep vu khong phai API nhung anh huong truc tiep den flow

### 8.1. Chua thay seed data cho bang master data

Khong thay migration `INSERT` cho cac bang danh muc. Neu DB moi hoan toan, rat de thieu du lieu cho:

- `movie_status`
- `cinema_types`
- `seat_types`
- `movie_variations`
- `ticket_types`

Khuyen nghi:

- Bo sung seed migration cho cac bang danh muc co tinh on dinh.

### 8.2. Publish phong chieu chua validate so do ghe

Van de:

- Co the publish `cinema_theater` du chua co ghe nao.
- Co the tao showtime cho phong da publish nhung phong khong co du lieu ghe.

Tac dong:

- Showtime tao thanh cong nhung booking khong van hanh dung.

Khuyen nghi:

- Khi publish phong, validate so luong ghe > 0.
- Tot hon nua la validate du so ghe theo `numberOfRows * numberOfColumns` hoac theo quy tac nghiep vu cho phep.

### 8.3. Cac hang ghe thuong/VIP/double chua duoc enforce khi tao ghe

Van de:

- `cinema_theaters` co `regularSeatRow`, `vipSeatRow`, `doubleSeatRow`.
- Nhung khi tao `seat`, he thong chi check row/column nam trong gioi han phong, chua check hang do co dung loai ghe hay khong.

Tac dong:

- So do phong va gia ghe co the lech nghiep vu thiet ke ban dau.

## 9. Flow de xuat toi thieu de tao 1 showtime trong admin

1. Seed hoac tao `movie_status`, `cinema_types`, `seat_types`, `movie_variations`, `ticket_types`.
2. Tao `province`.
3. Tao `movie_theater`.
4. Tao `cinema_theater`.
5. Tao ghe cho phong.
6. Publish phong.
7. Tao `movie`.
8. Chon `movieVariation`.
9. Goi `POST /api/v01/admin/show-time/add`.

Payload mau:

```json
{
  "showDate": "2026-03-20",
  "startTime": "19:30:00",
  "originPrice": 90000,
  "status": "VALID",
  "movieId": 12,
  "cinemaTheaterId": 5,
  "movieVariationId": 2
}
```

## 10. Tong hop ngan gon

- Neu chi nhin vao DB, de tao showtime can toi thieu: `movie`, `cinema_theater`, `movie_variation`.
- Neu nhin dung theo flow van hanh, can them: `province`, `movie_theater`, `cinema_type`, `seat_type`, `seat`, `movie_status`, `genre`.
- Da bo sung xong API cho `movie_variation`.
- Da bo sung xong API cho `ticket_type`.
- Da sua xong logic room-by-movie-theater.
- Cac diem da sua them: occupied-slots, admin filter showtime, public filter showtime theo ngay/status.
- Diem can uu tien tiep theo: bulk create/copy showtime va neu can thi lam richer response cho seat-map theo showtime.
