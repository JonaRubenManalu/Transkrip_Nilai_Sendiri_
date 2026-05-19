package com.transkrip.view;

import com.transkrip.controller.TranskripController;
import com.transkrip.model.MataKuliah;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ResourceBundle;

public class FormMataKuliahController implements Initializable, BaseController {

    @FXML private Label              lblJudul;
    @FXML private HBox               bannerEdit;
    @FXML private TextField          txtNamaMK;
    @FXML private ComboBox<Integer>  cmbSKS;
    @FXML private Spinner<Integer>   spnSemester;
    @FXML private ComboBox<String>   cmbNilaiHuruf;
    @FXML private Label              lblBobotPreview;
    @FXML private Label              lblPesan;
    @FXML private Button             btnSimpan;

    private MainLayoutController mainLayout;
    private final TranskripController tc = TranskripController.getInstance();
    private MataKuliah mkEdit;

    @Override
    public void setMainLayout(MainLayoutController ml) {
        this.mainLayout = ml;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // --- 1. Isi daftar pilihan Dropdown (ComboBox) ---
        cmbSKS.getItems().addAll(1, 2, 3, 4, 5, 6);
        cmbNilaiHuruf.getItems().addAll("A", "A-", "B+", "B", "B-", "C+", "C", "D", "E");

        // --- 2. Set nilai default (Setelah item diisi) ---
        cmbSKS.setValue(3);
        cmbNilaiHuruf.setValue("A");
        lblBobotPreview.setText("4.00");

        // --- 3. Tambahkan Value Factory agar Spinner tidak null ---
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 14, 1);
        spnSemester.setValueFactory(valueFactory);

        // --- 4. Cek mode edit (Pass data dari RiwayatNilai) ---
        mkEdit = tc.getMataKuliahEdit();

        if (mkEdit != null) {
            // Mode Edit
            lblJudul.setText("Edit Mata Kuliah");
            btnSimpan.setText("Simpan Perubahan");
            btnSimpan.setStyle(
                    "-fx-background-color:#f39c12;" +
                            "-fx-text-fill:#1a1000;" +
                            "-fx-background-radius:6;" +
                            "-fx-font-weight:bold;" +
                            "-fx-cursor:hand;" +
                            "-fx-padding:9 22 9 22;"
            );

            // Tampilkan banner kuning
            bannerEdit.setVisible(true);
            bannerEdit.setManaged(true);

            // Isi form dengan data yang ada
            txtNamaMK.setText(mkEdit.getNamaMk());
            cmbSKS.setValue(mkEdit.getSks());
            cmbNilaiHuruf.setValue(mkEdit.getNilaiHuruf());
            spnSemester.getValueFactory().setValue(mkEdit.getSemester());
            lblBobotPreview.setText(String.format("%.2f", mkEdit.getBobotAngka()));
        }
    }

    @FXML
    private void updateBobotPreview() {
        String nilai = cmbNilaiHuruf.getValue();
        if (nilai == null) return;

        MataKuliah dummy = new MataKuliah();
        dummy.setNilaiHuruf(nilai);
        lblBobotPreview.setText(String.format("%.2f", dummy.getBobotAngka()));
    }

    @FXML
    private void handleSimpan() {
        String  nama     = txtNamaMK.getText().trim();
        Integer sks      = cmbSKS.getValue();
        String  nilaiH   = cmbNilaiHuruf.getValue();

        // --- 5. Gunakan tipe objek 'Integer' agar aman dari null ---
        Integer semester = spnSemester.getValue();

        // Validasi input
        if (nama.isEmpty()) {
            lblPesan.setText("Nama mata kuliah tidak boleh kosong.");
            return;
        }
        if (sks == null) {
            lblPesan.setText("Pilih jumlah SKS terlebih dahulu.");
            return;
        }
        if (nilaiH == null) {
            lblPesan.setText("Pilih nilai huruf terlebih dahulu.");
            return;
        }
        if (semester == null) {
            lblPesan.setText("Pilih semester terlebih dahulu.");
            return;
        }

        boolean berhasil;
        if (mkEdit != null) {
            berhasil = tc.updateMataKuliah(mkEdit.getIdMk(), nama, sks, nilaiH, semester);
        } else {
            berhasil = tc.tambahMataKuliah(nama, sks, nilaiH, semester);
        }

        if (berhasil) {
            tc.setMataKuliahEdit(null);

            Alert info = new Alert(Alert.AlertType.INFORMATION);
            info.setTitle("Berhasil");
            info.setHeaderText(null);
            info.setContentText(mkEdit != null
                    ? "Data mata kuliah berhasil diperbarui."
                    : "Mata kuliah berhasil ditambahkan.");
            info.showAndWait();

            if (mainLayout != null) mainLayout.goRiwayat();
        } else {
            lblPesan.setText("Gagal menyimpan data. Silakan coba lagi.");
        }
    }

    @FXML
    private void handleBatal() {
        tc.setMataKuliahEdit(null);
        if (mainLayout != null) mainLayout.goRiwayat();
    }
}