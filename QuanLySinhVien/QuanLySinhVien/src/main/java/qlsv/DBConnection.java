package qlsv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String SERVER = "localhost:1433";
    private static final String DB     = "QuanLySinhVien";
    private static final String USER   = "sa";
    private static final String PASS   = "123456";

    public static Connection getConnection() throws SQLException {
        String url = "jdbc:sqlserver://" + SERVER
                + ";databaseName=" + DB
                + ";encrypt=true;trustServerCertificate=true;";
        return DriverManager.getConnection(url, USER, PASS);
    }

    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            if (conn != null) {
                System.out.println("Kết nối SQL Server thành công!");
            }
        } catch (SQLException e) {
            System.out.println("Lỗi kết nối: " + e.getMessage());
            e.printStackTrace();
        }
    }
}