# Phần mềm Quản lý sinh viên (Java Swing + SQL Server)

## Chức năng
- Quản lý **Lớp**, **Sinh viên**, **Môn học**, **Điểm**
- Mỗi form có: Thêm, Sửa, Xóa, Làm mới, Tìm kiếm
- Điểm tổng kết tự tính trong CSDL: `DiemTK = DiemQT * 0.3 + DiemThi * 0.7`

## Cấu trúc
```
QuanLySinhVien/
├── database/QuanLySinhVien.sql      <- script tạo CSDL + dữ liệu mẫu
├── pom.xml                          <- Maven (tự tải driver SQL Server)
└── src/main/java/qlsv/
    ├── DBConnection.java            <- kết nối CSDL (sửa user/pass ở đây)
    ├── MainFrame.java               <- form chính (chạy file này)
    ├── FrmLop.java
    ├── FrmSinhVien.java
    ├── FrmMonHoc.java
    └── FrmDiem.java
```

## Cách chạy
1. Cài **SQL Server** (bật SQL Server Authentication, TCP/IP port 1433) và **SSMS**.
2. Mở SSMS, chạy file `database/QuanLySinhVien.sql`.
3. Sửa `USER`, `PASS` trong `DBConnection.java` cho đúng máy của bạn.
4. Mở project bằng **NetBeans / IntelliJ / Eclipse** (dạng Maven), chạy `MainFrame.java`.
   - Nếu không dùng Maven: tải `mssql-jdbc-xx.jre11.jar` và thêm vào Libraries của project.

## Đưa lên GitHub
```
git init
git add .
git commit -m "Do an quan ly sinh vien"
git branch -M main
git remote add origin https://github.com/<ten-ban>/QuanLySinhVien.git
git push -u origin main
```
