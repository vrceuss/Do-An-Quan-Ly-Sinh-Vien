package qlsv;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

/** Form quản lý LỚP: thêm, sửa, xóa, tìm kiếm. */
public class FrmLop extends JFrame {
    private final JTextField txtMa = new JTextField(12);
    private final JTextField txtTen = new JTextField(20);
    private final JTextField txtKhoa = new JTextField(20);
    private final JTextField txtTim = new JTextField(15);

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Mã lớp", "Tên lớp", "Khoa"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);

    public FrmLop() {
        setTitle("Quản lý lớp");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Thông tin lớp"));
        form.add(new JLabel("Mã lớp:"));  form.add(txtMa);
        form.add(new JLabel("Tên lớp:")); form.add(txtTen);
        form.add(new JLabel("Khoa:"));    form.add(txtKhoa);

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
        String sql = "SELECT MaLop, TenLop, Khoa FROM Lop WHERE MaLop LIKE ? OR TenLop LIKE ? OR Khoa LIKE ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            String kw = "%" + keyword + "%";
            ps.setString(1, kw);
            ps.setString(2, kw);
            ps.setString(3, kw);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getString(3)});
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
        Object khoa = model.getValueAt(r, 2);
        txtKhoa.setText(khoa == null ? "" : khoa.toString());
        txtMa.setEditable(false); // không cho sửa khóa chính
    }

    private void them() {
        if (txtMa.getText().trim().isEmpty() || txtTen.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã lớp và Tên lớp!");
            return;
        }
        String sql = "INSERT INTO Lop(MaLop, TenLop, Khoa) VALUES(?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, txtMa.getText().trim());
            ps.setString(2, txtTen.getText().trim());
            ps.setString(3, txtKhoa.getText().trim());
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
        String sql = "UPDATE Lop SET TenLop=?, Khoa=? WHERE MaLop=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, txtTen.getText().trim());
            ps.setString(2, txtKhoa.getText().trim());
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
        int ok = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa lớp này?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM Lop WHERE MaLop=?")) {
            ps.setString(1, txtMa.getText().trim());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Xóa thành công!");
            lamMoi();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Không xóa được (lớp đang có sinh viên?)\n" + ex.getMessage());
        }
    }

    private void lamMoi() {
        txtMa.setText(""); txtTen.setText(""); txtKhoa.setText(""); txtTim.setText("");
        txtMa.setEditable(true);
        table.clearSelection();
        loadData("");
    }

    private void loi(SQLException ex) {
        JOptionPane.showMessageDialog(this, "Lỗi CSDL: " + ex.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}
