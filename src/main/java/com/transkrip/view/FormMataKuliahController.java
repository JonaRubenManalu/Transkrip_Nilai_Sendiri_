package com.transkrip.view;

import com.transkrip.controller.TranskripController;
import com.transkrip.model.MataKuliah;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;


public class FormMataKuliahController implements Initializable, BaseController {

    @FXML private Label              lblJudul;
    @FXML private HBox               bannerEdit;
    @FXML private ComboBox<String>   txtNamaMK;
    @FXML private ComboBox<Integer>  cmbSKS;
    @FXML private Spinner<Integer>   spnSemester;
    @FXML private ComboBox<String>   cmbNilaiHuruf;
    @FXML private Label              lblBobotPreview;
    @FXML private Label              lblPesan;
    @FXML private Button             btnSimpan;

    private MainLayoutController mainLayout;
    private final TranskripController tc = TranskripController.getInstance();
    private MataKuliah mkEdit;
    private boolean isSelecting = false;
    private Timeline debounceTimer;

    // Master list — tidak pernah diubah
    private final ObservableList<String> semuaMK = FXCollections.observableArrayList(
            "Bahasa Indonesia", "Pendidikan Agama Kristen", "Teknologi Komputer",
            "Praktikum Teknologi Komputer", "Matematika Teknik", "Logika Matematika",
            "Interaksi Manusia dan Komputer", "Algoritma dan Pemrograman",
            "Praktikum Algoritma dan Pemrograman", "Matematika Diskrit",
            "Arsitektur dan Organisasi Komputer", "Statistik",
            "Jaringan Komputer", "Praktikum Jaringan Komputer",
            "Pendidikan Kewarganegaraan", "Struktur Data", "Praktikum Struktur Data",
            "Infrastruktur LAN", "Praktikum Infrastruktur LAN",
            "Sistem Basis Data", "Praktikum Sistem Basis Data", "Riset Operasi",
            "Sistem Operasi", "Rekayasa Perangkat Lunak Berorientasi Obyek",
            "Praktikum Rekayasa Perangkat Lunak Berorientasi Obyek",
            "Pemrograman Web", "Praktikum Pemrograman Web", "Kecerdasan Buatan",
            "Keamanan Komputer", "Pendidikan Pancasila",
            "Etika Profesi Teknologi Informasi", "Manajemen Proyek Teknologi Informasi",
            "Riset Teknologi Informasi", "Kuliah Kerja Nyata", "Kerja Praktik", "Skripsi",
            "Cloud Infrastructure", "Enterprise Network", "Pengantar Keamanan Jaringan",
            "Jaringan Nir Kabel", "Otomasi Jaringan", "Teknologi WAN",
            "Keamanan Jaringan", "Internet of Things", "Administrasi Basis Data",
            "Data Warehouse", "Basis Data Terdistribusi", "Keamanan Basis Data",
            "Administrasi Basis Data Non Relasional",
            "Pemrograman Perangkat Bergerak Berbasis Android",
            "Pemrograman Perangkat Bergerak Berbasis iOS",
            "Pemrograman Perangkat Bergerak Berbasis Hybrid",
            "Pemrograman Desktop", "Pemrograman Web Lanjut",
            "Pola Desain Antarmuka Pengguna", "Desain Eksperimental",
            "Desain dan Evaluasi Antarmuka", "Pemodelan Proses Bisnis",
            "Test Engineering", "Machine Learning", "Jaringan Syaraf Tiruan",
            "Knowledge-Based System", "Pemrosesan Bahasa Natural",
            "Pemrosesan Citra Digital", "Pemrosesan Sinyal Digital", "Game Engine",
            "Algoritma Graf", "Analisis Data Statistik", "Analisis Proses Bisnis",
            "Bahasa Inggris Informatika", "Competitive Programming", "Deep Learning",
            "Desain Game", "Digital Humanities", "E-Commerce", "E-Government",
            "Game Audio", "Grafika Game", "Kompresi Data", "Komunikasi Bisnis",
            "Manajemen Kepemimpinan", "Manajemen Konten Web",
            "Pemrograman Berorientasi Layanan", "Praktikum Keahlian Khusus – SAP",
            "Program Kreativitas Mahasiswa", "Proyek Informatika Merdeka",
            "Semantic Web", "Sistem Informasi Geografis", "Sistem Pakar",
            "Technopreneurship dan Manajemen Inovasi", "Teknik Animasi",
            "Forensic Text", "UX Writing dan Storytelling", "Visualisasi Data",
            "Augmented Reality", "Pengenalan Aplikasi Berbasis AI", "AI for Trading",
            "Pemrograman Blockchain Dasar", "Container Orchestration"
    );

    // FilteredList: hanya predikat yang diubah saat filter — tidak buat list baru
    private FilteredList<String> filteredMK;

    @Override
    public void setMainLayout(MainLayoutController ml) { this.mainLayout = ml; }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Setup FilteredList sekali saja
        filteredMK = new FilteredList<>(semuaMK, s -> true);
        txtNamaMK.setItems(filteredMK);
        txtNamaMK.setEditable(true);
        txtNamaMK.setVisibleRowCount(8);

        // ── Listener teks dengan debounce — TANPA Platform.runLater ──────────
        txtNamaMK.getEditor().textProperty().addListener((obs, oldVal, newVal) -> {
            if (isSelecting) return;

            if (debounceTimer != null) debounceTimer.stop();

            debounceTimer = new Timeline(new KeyFrame(Duration.millis(200), e -> {
                String keyword = (newVal == null) ? "" : newVal.trim().toLowerCase();

                if (keyword.isEmpty()) {
                    filteredMK.setPredicate(s -> true);  // tampilkan semua
                    txtNamaMK.hide();
                } else {
                    filteredMK.setPredicate(s -> s.toLowerCase().contains(keyword));
                    if (!filteredMK.isEmpty()) {
                        txtNamaMK.show();
                    } else {
                        txtNamaMK.hide();
                    }
                }
            }));
            debounceTimer.play();
        });

        // Saat item dipilih dari dropdown
        txtNamaMK.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !isSelecting) {
                isSelecting = true;
                txtNamaMK.getEditor().setText(newVal);
                txtNamaMK.getEditor().positionCaret(newVal.length());
                filteredMK.setPredicate(s -> true); // reset filter setelah pilih
                txtNamaMK.hide();
                isSelecting = false;
            }
        });

        // Setup field lainnya
        cmbSKS.getItems().addAll(1, 2, 3, 4, 5, 6);
        cmbNilaiHuruf.getItems().addAll("A", "A-", "B+", "B", "B-", "C+", "C", "D", "E");
        cmbSKS.setValue(3);
        cmbNilaiHuruf.setValue("A");
        lblBobotPreview.setText("4.00");

        spnSemester.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 14, 1));

        // Cek mode edit
        mkEdit = tc.getMataKuliahEdit();
        if (mkEdit != null) {
            lblJudul.setText("Edit Mata Kuliah");
            btnSimpan.setText("Simpan Perubahan");
            btnSimpan.setStyle(
                    "-fx-background-color:#f39c12;-fx-text-fill:#1a1000;" +
                    "-fx-background-radius:6;-fx-font-weight:bold;" +
                    "-fx-cursor:hand;-fx-padding:9 22 9 22;");

            bannerEdit.setVisible(true);
            bannerEdit.setManaged(true);

            isSelecting = true;
            txtNamaMK.getEditor().setText(mkEdit.getNamaMk());
            isSelecting = false;

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
        String  nama     = txtNamaMK.getEditor().getText() == null ? ""
                           : txtNamaMK.getEditor().getText().trim();
        Integer sks      = cmbSKS.getValue();
        String  nilaiH   = cmbNilaiHuruf.getValue();
        Integer semester = spnSemester.getValue();

        if (nama.isEmpty())   { lblPesan.setText("Nama mata kuliah tidak boleh kosong."); return; }
        if (sks == null)      { lblPesan.setText("Pilih jumlah SKS terlebih dahulu."); return; }
        if (nilaiH == null)   { lblPesan.setText("Pilih nilai huruf terlebih dahulu."); return; }
        if (semester == null) { lblPesan.setText("Pilih semester terlebih dahulu."); return; }

        boolean berhasil = (mkEdit != null)
                ? tc.updateMataKuliah(mkEdit.getIdMk(), nama, sks, nilaiH, semester)
                : tc.tambahMataKuliah(nama, sks, nilaiH, semester);

        if (berhasil) {
            tc.setMataKuliahEdit(null);

            String pesanSukses = (mkEdit != null)
                    ? "✓ Data mata kuliah berhasil diperbarui."
                    : "✓ Mata kuliah berhasil ditambahkan.";
            lblPesan.setText(pesanSukses);
            lblPesan.getStyleClass().removeAll("error-label");
            lblPesan.getStyleClass().add("success-label");
            btnSimpan.setDisable(true);

            // Navigasi ke Riwayat setelah 1.5 detik
            new Timeline(new KeyFrame(Duration.millis(1500),
                    e -> { if (mainLayout != null) mainLayout.goRiwayat(); })).play();
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
