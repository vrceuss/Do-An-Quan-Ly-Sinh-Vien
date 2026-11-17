package qlsv;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

/** Form quản lý SINH VIÊN: thêm, sửa, xóa, tìm kiếm. */
public class FrmSinhVien extends JFrame {
    private final JTextField txtMa = new JTextField(12);
    private final JTextField txtTen = new JTextField(20);
    private final JTextField txtNgaySinh = new JTextField(12);
    private final JTextField txtDiaChi = new JTextField(20);
    private final JTextField txtSdt = new JTextField(12);
    private final JTextField txtTim = new JTextField(15);
    private final JComboBox<String> cboGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ"});
    private final JComboBox<String> cboLop = new JComboBox<>();

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Mã SV", "Họ tên", "Ngày sinh", "Giới tính", "Địa chỉ", "SĐT", "Mã lớp"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);

    public FrmSinhVien() {
        setTitle("Quản lý sinh viên");
        setSize(950, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel form = new JPanel(new GridLayout(0, 4, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Thông tin sinh viên"));
        form.add(new JLabel("Mã SV:"));                 form.add(txtMa);
        form.add(new JLabel("Họ tên:"));                form.add(txtTen);
        form.add(new JLabel("Ngày sinh (yyyy-MM-dd):")); form.add(txtNgaySinh);
        form.add(new JLabel("Giới tính:"));             form.add(cboGioiTinh);
        form.add(new JLabel("Địa chỉ:"));               form.add(txtDiaChi);
        form.add(new JLabel("Số điện thoại:"));         form.add(txtSdt);
        form.add(new JLabel("Lớp:"));                   form.add(cboLop);
        form.add(new JLabel(""));                       form.add(new JLabel(""));

        JButton btnThem = new JButton("Thêm");
        JButton btnSua = new JButton("Sửa");
        JButton btnXoa = new JButton("Xóa");
        JButton btnMoi = new JButton("Làm mới");
        JButton btnTim = new JButton("Tìm kiếm");

        JPanel pnlBtn = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        pnlBtn.add(btnThem); pnlBtn.add(btnSua); pnlBtn.add(btnXoa); pnlBtn.add(btnMoi);
        pnlBtn.add(new JLabel("   Từ khóa (mã / tên):")); pnlBtn.add(txtTim); pnlBtn.add(btnTim);

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

        loadLop();
        loadData("");
    }

    /** Nạp danh sách mã lớp vào combobox. */
    private void loadLop() {
        cboLop.removeAllItems();
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT MaLop FROM Lop ORDER BY MaLop")) {
            while (rs.next()) cboLop.addItem(rs.getString(1));
        } catch (SQLException ex) {
            loi(ex);
        }
    }

    private void loadData(String keyword) {
        model.setRowCount(0);
        String sql = "SELECT MaSV, HoTen, NgaySinh, GioiTinh, DiaChi, SoDienThoai, MaLop "
                + "FROM SinhVien WHERE MaSV LIKE ? OR HoTen LIKE ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            String kw = "%" + keyword + "%";
            ps.setString(1, kw);
            ps.setString(2, kw);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString(1), rs.getString(2), rs.getDate(3), rs.getString(4),
                        rs.getString(5), rs.getString(6), rs.getString(7)});
            }
        } catch (SQLException ex) {
            loi(ex);
        }
    }

    private void fillFromRow() {
        int r = table.getSelectedRow();
        if (r < 0) return;
        txtMa.setText(str(model.getValueAt(r, 0)));
        txtTen.setText(str(model.getValueAt(r, 1)));
        txtNgaySinh.setText(str(model.getValueAt(r, 2)));
        cboGioiTinh.setSelectedItem(str(model.getValueAt(r, 3)));
        txtDiaChi.setText(str(model.getValueAt(r, 4)));
        txtSdt.setText(str(model.getValueAt(r, 5)));
        cboLop.setSelectedItem(str(model.getValueAt(r, 6)));
        txtMa.setEditable(false); // không cho sửa khóa chính
    }

    private String str(Object o) { return o == null ? "" : o.toString(); }

    /** Kiểm tra dữ liệu nhập. Trả về true nếu hợp lệ. */
    private boolean kiemTra() {
        if (txtMa.getText().trim().isEmpty() || txtTen.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã SV và Họ tên!");
            return false;
        }
        if (cboLop.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Chưa có lớp nào. Hãy thêm lớp trước!");
            return false;
        }
        String ns = txtNgaySinh.getText().trim();
        if (!ns.isEmpty()) {
            try {
                Date.valueOf(ns);
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this, "Ngày sinh phải đúng định dạng yyyy-MM-dd (VD: 2004-03-15)!");
                return false;
            }
        }
        return true;
    }

    private void setParams(PreparedStatement ps, boolean laThem) throws SQLException {
        String ns = txtNgaySinh.getText().trim();
        Date ngay = ns.isEmpty() ? null : Date.valueOf(ns);
        int i = 1;
        if (laThem) ps.setString(i++, txtMa.getText().trim());
        ps.setString(i++, txtTen.getText().trim());
        ps.setDate(i++, ngay);
        ps.setString(i++, (String) cboGioiTinh.getSelectedItem());
        ps.setString(i++, txtDiaChi.getText().trim());
        ps.setString(i++, txtSdt.getText().trim());
        ps.setString(i++, (String) cboLop.getSelectedItem());
        if (!laThem) ps.setString(i, txtMa.getText().trim());
    }

    private void them() {
        if (!kiemTra()) return;
        String sql = "INSERT INTO SinhVien(MaSV, HoTen, NgaySinh, GioiTinh, DiaChi, SoDienThoai, MaLop) "
                + "VALUES(?,?,?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            setParams(ps, true);
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
        if (!kiemTra()) return;
        String sql = "UPDATE SinhVien SET HoTen=?, NgaySinh=?, GioiTinh=?, DiaChi=?, SoDienThoai=?, MaLop=? "
                + "WHERE MaSV=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            setParams(ps, false);
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
                "Xóa sinh viên này (điểm của sinh viên cũng bị xóa)?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM SinhVien WHERE MaSV=?")) {
            ps.setString(1, txtMa.getText().trim());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Xóa thành công!");
            lamMoi();
        } catch (SQLException ex) {
            loi(ex);
        }
    }

    private void lamMoi() {
        txtMa.setText(""); txtTen.setText(""); txtNgaySinh.setText("");
        txtDiaChi.setText(""); txtSdt.setText(""); txtTim.setText("");
        cboGioiTinh.setSelectedIndex(0);
        if (cboLop.getItemCount() > 0) cboLop.setSelectedIndex(0);
        txtMa.setEditable(true);
        table.clearSelection();
        loadLop();
        loadData("");
    }

    private void loi(SQLException ex) {
        JOptionPane.showMessageDialog(this, "Lỗi CSDL: " + ex.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}
