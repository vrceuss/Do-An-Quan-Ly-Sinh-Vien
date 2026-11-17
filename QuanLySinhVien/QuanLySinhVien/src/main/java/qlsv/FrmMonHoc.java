package qlsv;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

/** Form quản lý MÔN HỌC: thêm, sửa, xóa, tìm kiếm. */
public class FrmMonHoc extends JFrame {
    private final JTextField txtMa = new JTextField(12);
    private final JTextField txtTen = new JTextField(20);
    private final JTextField txtTinChi = new JTextField(5);
    private final JTextField txtTim = new JTextField(15);

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Mã môn", "Tên môn", "Số tín chỉ"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);

    public FrmMonHoc() {
        setTitle("Quản lý môn học");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Thông tin môn học"));
        form.add(new JLabel("Mã môn:"));     form.add(txtMa);
        form.add(new JLabel("Tên môn:"));    form.add(txtTen);
        form.add(new JLabel("Số tín chỉ:")); form.add(txtTinChi);

        JButton btnThem = new JButton("Thêm");
        JButton btnSua = new JButton("Sửa");
        JButton btnXoa = new JButton("Xóa");
        JButton btnMoi = new JButton("Làm mới");
        JButton btnTim = new JButton("Tìm kiếm");

        JPanel pnlBtn = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        pnlBtn.add(btnThem); pnlBtn.add(btnSua); pnlBtn.add(btnXoa); pnlBtn.add(btnMoi);
        pnlBtn.add(new JLabel("   Từ khóa:")); pnlBtn.add(txtTim); pnlBtn.add(btnTim);

        JPanel north = new JPanel(new BorderLayout());
        north.add(form, BorderLayout.NORTH);
        north.add(pnlBtn, BorderLayout.SOUTH);

        add(north, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        btnThem.addActionListener(e -> them());
        btnSua.addActionListener(e -> sua());
        btnXoa.addActionListener(e -> xoa());
        btnMoi.addActionListener(e -> lamMoi());
        btnTim.addActionListener(e -> loadData(txtTim.getText().trim()));
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) fillFromRow();
        });

        loadData("");
    }

    private void loadData(String keyword) {
        model.setRowCount(0);
        String sql = "SELECT MaMH, TenMH, SoTinChi FROM MonHoc WHERE MaMH LIKE ? OR TenMH LIKE ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            String kw = "%" + keyword + "%";
            ps.setString(1, kw);
            ps.setString(2, kw);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getInt(3)});
            }
        } catch (SQLException ex) {
            loi(ex);
        }
    }

    private void fillFromRow() {
        int r = table.getSelectedRow();
        if (r < 0) return;
        txtMa.setText(String.valueOf(model.getValueAt(r, 0)));
        txtTen.setText(String.valueOf(model.getValueAt(r, 1)));
        txtTinChi.setText(String.valueOf(model.getValueAt(r, 2)));
        txtMa.setEditable(false);
    }

    /** Trả về số tín chỉ hợp lệ, hoặc -1 nếu dữ liệu sai. */
    private int kiemTra() {
        if (txtMa.getText().trim().isEmpty() || txtTen.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã môn và Tên môn!");
            return -1;
        }
        try {
            int tc = Integer.parseInt(txtTinChi.getText().trim());
            if (tc < 1 || tc > 10) throw new NumberFormatException();
            return tc;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số tín chỉ phải là số nguyên từ 1 đến 10!");
            return -1;
        }
    }

    private void them() {
        int tc = kiemTra();
        if (tc < 0) return;
        String sql = "INSERT INTO MonHoc(MaMH, TenMH, SoTinChi) VALUES(?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, txtMa.getText().trim());
            ps.setString(2, txtTen.getText().trim());
            ps.setInt(3, tc);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Thêm thành công!");
            lamMoi();
        } catch (SQLException ex) {
            loi(ex);
        }
    }

    private void sua() {
        if (txtMa.isEditable()) {
            JOptionPane.showMessageDialog(this, "Hãy chọn một dòng trong bảng để sửa!");
            return;
        }
        int tc = kiemTra();
        if (tc < 0) return;
        String sql = "UPDATE MonHoc SET TenMH=?, SoTinChi=? WHERE MaMH=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, txtTen.getText().trim());
            ps.setInt(2, tc);
            ps.setString(3, txtMa.getText().trim());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Sửa thành công!");
            lamMoi();
        } catch (SQLException ex) {
            loi(ex);
        }
    }

    private void xoa() {
        if (txtMa.isEditable()) {
            JOptionPane.showMessageDialog(this, "Hãy chọn một dòng trong bảng để xóa!");
            return;
        }
        int ok = JOptionPane.showConfirmDialog(this,
                "Xóa môn học này (điểm của môn cũng bị xóa)?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM MonHoc WHERE MaMH=?")) {
            ps.setString(1, txtMa.getText().trim());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Xóa thành công!");
            lamMoi();
        } catch (SQLException ex) {
            loi(ex);
        }
    }

    private void lamMoi() {
        txtMa.setText(""); txtTen.setText(""); txtTinChi.setText(""); txtTim.setText("");
        txtMa.setEditable(true);
        table.clearSelection();
        loadData("");
    }

    private void loi(SQLException ex) {
        JOptionPane.showMessageDialog(this, "Lỗi CSDL: " + ex.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}
