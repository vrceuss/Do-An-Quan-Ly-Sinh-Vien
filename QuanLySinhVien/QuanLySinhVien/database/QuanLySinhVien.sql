USE QuanLySinhVien;
GO

DROP TABLE IF EXISTS Diem;
DROP TABLE IF EXISTS SinhVien;
DROP TABLE IF EXISTS MonHoc;
DROP TABLE IF EXISTS Lop;
GO

CREATE TABLE Lop (
    MaLop   VARCHAR(10)   NOT NULL,
    TenLop  NVARCHAR(50)  NOT NULL,
    Khoa    NVARCHAR(50)  NULL,
    CONSTRAINT PK_Lop PRIMARY KEY (MaLop)
);
GO

CREATE TABLE SinhVien (
    MaSV         VARCHAR(10)    NOT NULL,
    HoTen        NVARCHAR(100)  NOT NULL,
    NgaySinh     DATE           NULL,
    GioiTinh     NVARCHAR(5)    NOT NULL DEFAULT N'Nam',
    DiaChi       NVARCHAR(200)  NULL,
    SoDienThoai  VARCHAR(15)    NULL,
    MaLop        VARCHAR(10)    NOT NULL,
    CONSTRAINT PK_SinhVien PRIMARY KEY (MaSV),
    CONSTRAINT CK_SV_GioiTinh CHECK (GioiTinh IN (N'Nam', N'Nữ')),
    CONSTRAINT FK_SV_Lop FOREIGN KEY (MaLop) REFERENCES Lop(MaLop)
);
GO

CREATE TABLE MonHoc (
    MaMH      VARCHAR(10)    NOT NULL,
    TenMH     NVARCHAR(100)  NOT NULL,
    SoTinChi  INT            NOT NULL,
    CONSTRAINT PK_MonHoc PRIMARY KEY (MaMH),
    CONSTRAINT CK_MH_TinChi CHECK (SoTinChi BETWEEN 1 AND 10)
);
GO

CREATE TABLE Diem (
    MaSV     VARCHAR(10)   NOT NULL,
    MaMH     VARCHAR(10)   NOT NULL,
    DiemQT   DECIMAL(4, 2) NOT NULL,
    DiemThi  DECIMAL(4, 2) NOT NULL,
    DiemTK   AS CAST(ROUND(DiemQT * 0.3 + DiemThi * 0.7, 2) AS DECIMAL(4, 2)) PERSISTED,
    CONSTRAINT PK_Diem PRIMARY KEY (MaSV, MaMH),
    CONSTRAINT FK_Diem_SV FOREIGN KEY (MaSV) REFERENCES SinhVien(MaSV) ON DELETE CASCADE,
    CONSTRAINT FK_Diem_MH FOREIGN KEY (MaMH) REFERENCES MonHoc(MaMH) ON DELETE CASCADE,
    CONSTRAINT CK_Diem_QT CHECK (DiemQT BETWEEN 0 AND 10),
    CONSTRAINT CK_Diem_Thi CHECK (DiemThi BETWEEN 0 AND 10)
);
GO

INSERT INTO Lop VALUES
('CNTT01', N'Công nghệ thông tin 01', N'Công nghệ thông tin'),
('CNTT02', N'Công nghệ thông tin 02', N'Công nghệ thông tin'),
('KT01', N'Kế toán 01', N'Kế toán');

INSERT INTO SinhVien VALUES
('SV001', N'Trần Anh Tuất', '2007-09-06', N'Nam', N'Long Biên', '0912345678', 'CNTT01'),
('SV002', N'Lê Nguyễn Phi', '2007-06-09', N'Nam', N'Tuyên Quang', '0987654321', 'CNTT01'),
('SV003', N'Nguyễn Thấp Quân', '2007-07-06', N'Nam', N'Đông Anh', '0924681012', 'CNTT02'),
('SV004', N'Lê Minh Hương', '2007-06-07', N'Nữ', N'Ninh Bình', '0912108642', 'KT01');

INSERT INTO MonHoc VALUES
('MH01', N'Lập trình Java', 3),
('MH02', N'Cơ sở dữ liệu', 3),
('MH03', N'Cấu trúc dữ liệu', 2);

INSERT INTO Diem (MaSV, MaMH, DiemQT, DiemThi) VALUES
('SV001', 'MH01', 8.0, 7.5),
('SV001', 'MH02', 7.0, 8.0),
('SV002', 'MH01', 9.0, 8.5),
('SV003', 'MH03', 6.5, 7.0);
GO

SELECT * FROM Lop;
SELECT * FROM SinhVien;
SELECT * FROM MonHoc;
SELECT * FROM Diem;