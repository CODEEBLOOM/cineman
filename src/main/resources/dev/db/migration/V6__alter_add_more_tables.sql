-- Kiểm tra bảng membership_ranks đã tồn tại hay chưa ?
-- Nếu chưa tồn tại, tạo bảng membership_ranks --
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name = 'membership_ranks' AND xtype = 'U')
    EXEC('
        CREATE TABLE membership_ranks (
            id INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
            name NVARCHAR(50) NOT NULL,
            required_point INT NOT NULL,
            return_points_ticket FLOAT NOT NULL,
            return_points_snack FLOAT NOT NULL,
            priority_level INT NOT NULL DEFAULT(0),
            status BIGINT NOT NULL DEFAULT(1),
            created_at DATETIME NOT NULL DEFAULT(GETDATE()),
            updated_at DATETIME NOT NULL DEFAULT(GETDATE()),
            CHECK (priority_level >= 0),
            CHECK (required_point >= 0),
            CHECK (return_points_ticket >= 0),
            CHECK (return_points_snack >= 0)
        )
    ');

-- Kiểm tra bảng users có cột rank_id ?
-- Nếu chưa có cột rank_id, thêm cột rank_id cho bảng users --
IF COL_LENGTH('users', 'rank_id') IS NULL
    ALTER TABLE users ADD rank_id INT NULL;

-- Kiểm tra bảng membership_ranks có cót bảng users ?
-- Nếu chưa cót bảng users, thêm cót bảng users cho bảng membership_ranks --
IF NOT EXISTS (
    SELECT * FROM sys.foreign_keys WHERE name = 'FK_users_rank'
)
    ALTER TABLE users
        ADD CONSTRAINT FK_users_rank
            FOREIGN KEY (rank_id)
                REFERENCES membership_ranks(id);

-- Kiểm tra bảng user_point_histories đã tồn tại hay chưa ?
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name = 'user_point_histories' AND xtype = 'U')
    EXEC('
        CREATE TABLE user_point_histories (
            id INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
            user_id BIGINT NOT NULL,
            invoice_id BIGINT NULL,
            change_point INT NOT NULL,
            reason NVARCHAR(200) NOT NULL,
            created_at DATETIME NOT NULL DEFAULT(GETDATE()),
            updated_at DATETIME NOT NULL DEFAULT(GETDATE()),
            CONSTRAINT FK_user_point_histories_users FOREIGN KEY (user_id) REFERENCES users(user_id),
            CONSTRAINT FK_user_point_histories_invoices FOREIGN KEY (invoice_id) REFERENCES invoices(invoice_id)
        )
    ');

-- Xóa table social_accounts --
IF EXISTS (
    SELECT * FROM sysobjects WHERE name = 'social_accounts' AND xtype = 'U'
)
    DROP TABLE social_accounts;


-- CHỈNH SỬA THÊM THÔNG TIN CÁC CỘT TRONG CÁC BẢNG --
-- SNACKS --
ALTER TABLE SNACKS
DROP CONSTRAINT CK__snacks__unit_pri__19DFD96B;

ALTER TABLE snacks
ALTER COLUMN unit_price DECIMAL(10,2) NULL;

ALTER TABLE SNACKS
ADD CONSTRAINT CK__snacks__unit_pri__19DFD96B CHECK (unit_price >= 0);

-- DETAIL_BOOKING_SNACKS --
IF COL_LENGTH('detail_booking_snacks', 'total_price') IS NULL
    ALTER TABLE detail_booking_snacks
    ADD total_price DECIMAL(10,2) NOT NULL;

-- invoices --
IF COL_LENGTH('invoices', 'total_amount') IS NULL
    ALTER TABLE invoices
    ADD total_amount DECIMAL(10,2) NULL ;

-- USERS --
IF COL_LENGTH('users', 'user_type') IS NOT NULL
    ALTER TABLE users
    DROP COLUMN user_type;
