package qlsv;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

/** Form quản lý ĐIỂM: nhập điểm cho sinh viên theo môn, thêm, sửa, xóa, tìm kiếm. */
public class FrmDiem extends JFrame {
    private final JComboBox<String> cboSV = new JComboBox<>();
    private final JComboBox<String> cboMH = new JComboBox<>();
    private final JTextField txtQT = new JTextField(8);
    private final JTextField txtThi = new JTextField(8);
    private final JTextField txtTim = new JTextField(15);

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Mã SV", "Họ tên", "Mã môn", "Tên môn", "Điểm QT (30%)", "Điểm thi (70%)", "Điểm TK"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);

    public FrmDiem() {
        setTitle("Quản lý điểm");
        setSize(950, 540);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel form = new JPanel(new GridLayout(0, 4, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Nhập điểm"));
        form.add(new JLabel("Sinh viên:"));      form.add(cboSV);
        form.add(new JLabel("Môn học:"));        form.add(cboMH);
        form.add(new JLabel("Điểm quá trình:")); form.add(txtQT);
        form.add(new JLabel("Điểm thi:"));       form.add(txtThi);

        JButton btnThem = new JButton("Thêm");
        JButton btnSua = new JButton("Sửa");
        JButton btnXoa = new JButton("Xóa");
        JButton btnMoi = new JButton("Làm mới");
        JButton btnTim = new JButton("Tìm kiếm");

        JPanel pnlBtn = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        pnlBtn.add(btnThem); pnlBtn.add(btnSua); pnlBtn.add(btnXoa); pnlBtn.add(btnMoi);
        pnlBtn.add(new JLabel("   Từ khóa (mã SV / tên / môn):")); pnlBtn.add(txtTim); pnlBtn.add(btnTim);

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

        loadCombo();
        loadData("");
    }

    /** Nạp danh sách sinh viên và môn học vào 2 combobox (dạng "mã - tên"). */
    private void loadCombo() {
        cboSV.removeAllItems();
        cboMH.removeAllItems();
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement()) {
            ResultSet rs = st.executeQuery("SELECT MaSV, HoTen FROM SinhVien ORDER BY MaSV");
            while (rs.next()) cboSV.addItem(rs.getString(1) + " - " + rs.getString(2));
            rs.close();
            rs = st.executeQuery("SELECT MaMH, TenMH FROM MonHoc ORDER BY MaMH");
            while (rs.next()) cboMH.addItem(rs.getString(1) + " - " + rs.getString(2));
            rs.close();
        } catch (SQLException ex) {
            loi(ex);
        }
    }

    /** Lấy phần mã (trước " - ") từ chuỗi của combobox. */
    private String maTuCombo(JComboBox<String> cbo) {
        Object o = cbo.getSelectedItem();
        if (o == null) return null;
        String s = o.toString();
        int idx = s.indexOf(" - ");
        return idx > 0 ? s.substring(0, idx) : s;
    }

    /** Chọn item trong combobox theo mã. */
    private void chonTheoMa(JComboBox<String> cbo, String ma) {
        for (int i = 0; i < cbo.getItemCount(); i++) {
            if (cbo.getItemAt(i).startsWith(ma + " - ")) {
                cbo.setSelectedIndex(i);
                return;
            }
        }
    }

    private void loadData(String keyword) {
        model.setRowCount(0);
        String sql = "SELECT d.MaSV, s.HoTen, d.MaMH, m.TenMH, d.DiemQT, d.DiemThi, d.DiemTK "
                + "FROM Diem d JOIN SinhVien s ON d.MaSV = s.MaSV "
                + "JOIN MonHoc m ON d.MaMH = m.MaMH "
                + "WHERE d.MaSV LIKE ? OR s.HoTen LIKE ? OR m.TenMH LIKE ? "
                + "ORDER BY d.MaSV, d.MaMH";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            String kw = "%" + keyword + "%";
            ps.setString(1, kw);
            ps.setString(2, kw);
            ps.setString(3, kw);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
                        rs.getDouble(5), rs.getDouble(6), rs.getDouble(7)});
            }
        } catch (SQLException ex) {
            loi(ex);
        }
    }

    private void fillFromRow() {
        int r = table.getSelectedRow();
        if (r < 0) return;
        chonTheoMa(cboSV, String.valueOf(model.getValueAt(r, 0)));
        chonTheoMa(cboMH, String.valueOf(model.getValueAt(r, 2)));
        txtQT.setText(String.valueOf(model.getValueAt(r, 4)));
        txtThi.setText(String.valueOf(model.getValueAt(r, 5)));
        cboSV.setEnabled(false); // không cho đổi khóa chính khi sửa
        cboMH.setEnabled(false);
    }

    /** Đọc 2 ô điểm, trả về mảng {qt, thi} hoặc null nếu sai. */
    private double[] docDiem() {
        if (maTuCombo(cboSV) == null || maTuCombo(cboMH) == null) {
            JOptionPane.showMessageDialog(this, "Cần có sinh viên và môn học trước khi nhập điểm!");
            return null;
        }
        try {
            double qt = Double.parseDouble(txtQT.getText().trim().replace(',', '.'));
            double thi = Double.parseDouble(txtThi.getText().trim().replace(',', '.'));
            if (qt < 0 || qt > 10 || thi < 0 || thi > 10) throw new NumberFormatException();
            return new double[]{qt, thi};
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Điểm phải là số từ 0 đến 10!");
            return null;
        }
    }

    private void them() {
        double[] d = docDiem();
        if (d == null) return;
        String sql = "INSERT INTO Diem(MaSV, MaMH, DiemQT, DiemThi) VALUES(?,?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, maTuCombo(cboSV));
            ps.setString(2, maTuCombo(cboMH));
            ps.setDouble(3, d[0]);
            ps.setDouble(4, d[1]);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Thêm điểm thành công!");
            lamMoi();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Không thêm được (sinh viên đã có điểm môn này?)\n" + ex.getMessage());
        }
    }

    private void sua() {
        if (cboSV.isEnabled()) {
            JOptionPane.showMessageDialog(this, "Hãy chọn một dòng trong bảng để sửa!");
            return;
        }
        double[] d = docDiem();
        if (d == null) return;
        String sql = "UPDATE Diem SET DiemQT=?, DiemThi=? WHERE MaSV=? AND MaMH=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDouble(1, d[0]);
            ps.setDouble(2, d[1]);
            ps.setString(3, maTuCombo(cboSV));
            ps.setString(4, maTuCombo(cboMH));
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Sửa điểm thành công!");
            lamMoi();
        } catch (SQLException ex) {
            loi(ex);
        }
    }

    private void xoa() {
        if (cboSV.isEnabled()) {
            JOptionPane.showMessageDialog(this, "Hãy chọn một dòng trong bảng để xóa!");
            return;
        }
        int ok = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa điểm này?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM Diem WHERE MaSV=? AND MaMH=?")) {
            ps.setString(1, maTuCombo(cboSV));
            ps.setString(2, maTuCombo(cboMH));
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Xóa thành công!");
            lamMoi();
        } catch (SQLException ex) {
            loi(ex);
        }
    }

    private void lamMoi() {
        txtQT.setText(""); txtThi.setText(""); txtTim.setText("");
        cboSV.setEnabled(true);
        cboMH.setEnabled(true);
        table.clearSelection();
        loadCombo();
        loadData("");
    }

    private void loi(SQLException ex) {
        JOptionPane.showMessageDialog(this, "Lỗi CSDL: " + ex.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}
