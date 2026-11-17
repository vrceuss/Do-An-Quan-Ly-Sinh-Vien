Phần mềm Quản lý sinh viên (Java Swing + SQL Server)

Chức năng
- Quản lý **Lớp**, **Sinh viên**, **Môn học**, **Điểm**
- Mỗi form có: Thêm, Sửa, Xóa, Làm mới, Tìm kiếm
- Điểm tổng kết tự tính trong CSDL: `DiemTK = DiemQT * 0.3 + DiemThi * 0.7`

Cấu trúc
QuanLySinhVien/
├── database/QuanLySinhVien.sql      <- script tạo CSDL + dữ liệu mẫu
├── pom.xml                          <- Maven
└── src/main/java/qlsv/
    ├── DBConnection.java            <- kết nối CSDL
    ├── MainFrame.java               <- form chính (chạy file này)
    ├── FrmLop.java
    ├── FrmSinhVien.java
    ├── FrmMonHoc.java
    └── FrmDiem.java
