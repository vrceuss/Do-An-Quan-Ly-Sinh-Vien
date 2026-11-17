package qlsv;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;

/** Form chính: chứa menu mở các form chức năng. */
public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("PHẦN MỀM QUẢN LÝ SINH VIÊN");
        setSize(420, 380);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JLabel lblTitle = new JLabel("QUẢN LÝ SINH VIÊN", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setForeground(new Color(0, 90, 160));

        JButton btnLop     = new JButton("Quản lý Lớp");
        JButton btnSV      = new JButton("Quản lý Sinh viên");
        JButton btnMH      = new JButton("Quản lý Môn học");
        JButton btnDiem    = new JButton("Quản lý Điểm");
        JButton btnThoat   = new JButton("Thoát");

        btnLop.addActionListener(e -> new FrmLop().setVisible(true));
        btnSV.addActionListener(e -> new FrmSinhVien().setVisible(true));
        btnMH.addActionListener(e -> new FrmMonHoc().setVisible(true));
        btnDiem.addActionListener(e -> new FrmDiem().setVisible(true));
        btnThoat.addActionListener(e -> System.exit(0));

        JPanel pnl = new JPanel(new GridLayout(6, 1, 10, 10));
        pnl.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        pnl.add(lblTitle);
        pnl.add(btnLop);
        pnl.add(btnSV);
        pnl.add(btnMH);
        pnl.add(btnDiem);
        pnl.add(btnThoat);
        add(pnl);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) { }

        SwingUtilities.invokeLater(() -> {
            // Kiểm tra kết nối CSDL khi khởi động
            try (Connection c = DBConnection.getConnection()) {
                // kết nối thành công
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null,
                        "Không kết nối được SQL Server!\n" + ex.getMessage()
                                + "\n\nHãy kiểm tra lại DBConnection.java (user, mật khẩu, port).",
                        "Lỗi kết nối", JOptionPane.ERROR_MESSAGE);
            }
            new MainFrame().setVisible(true);
        });
    }
}
