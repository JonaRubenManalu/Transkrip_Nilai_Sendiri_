package com.transkrip.controller;

import com.transkrip.model.MataKuliah;
import com.transkrip.model.TranskripDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class TranskripController {

    private static TranskripController instance;
    private final TranskripDAO transkripDAO;
    private final AuthController authController;

    // Cache data mata kuliah sesuai proposal: listMataKuliah (ArrayList)
    private List<MataKuliah> listMataKuliah;
    private MataKuliah mataKuliahEdit; // untuk pass data edit antar halaman

    // ── Singleton ────────────────────────────────────────────────────────────

    private TranskripController() {
        this.transkripDAO   = new TranskripDAO();
        this.authController = AuthController.getInstance();
        this.listMataKuliah = new ArrayList<>();
    }

    public static TranskripController getInstance() {
        if (instance == null) {
            instance = new TranskripController();
        }
        return instance;
    }

    // ── Load / Refresh Data ───────────────────────────────────────────────────

    /**
     * Memuat ulang semua data mata kuliah dari database ke listMataKuliah
     * Dipanggil setiap kali View perlu data terbaru
     */
    public void loadData() {
        int idUser = authController.getCurrentUserId();
        if (idUser == -1) return;
        listMataKuliah = transkripDAO.getAllMataKuliah(idUser);
    }

    /**
     * Mendapatkan semua mata kuliah (dari cache)
     */
    public List<MataKuliah> getAllMataKuliah() {
        return new ArrayList<>(listMataKuliah);
    }

    /**
     * Mendapatkan mata kuliah berdasarkan semester (dari cache)
     */
    public List<MataKuliah> getMataKuliahBySemester(int semester) {
        List<MataKuliah> hasil = new ArrayList<>();
        for (MataKuliah mk : listMataKuliah) {
            if (mk.getSemester() == semester) {
                hasil.add(mk);
            }
        }
        return hasil;
    }

    // ── CREATE ────────────────────────────────────────────────────────────────

    /**
     * Menambahkan mata kuliah baru
     * Validasi input sebelum menyimpan ke database
     */
    public boolean tambahMataKuliah(String namaMk, int sks, String nilaiHuruf, int semester) {
        int idUser = authController.getCurrentUserId();
        if (idUser == -1) return false;

        // Validasi
        if (namaMk == null || namaMk.isBlank()) return false;
        if (sks < 1 || sks > 6) return false;
        if (nilaiHuruf == null || nilaiHuruf.isBlank()) return false;
        if (semester < 1) return false;

        MataKuliah mk = new MataKuliah(idUser, namaMk.trim(), sks, nilaiHuruf.trim(), semester);
        boolean berhasil = transkripDAO.tambahMataKuliah(mk);
        if (berhasil) loadData(); // refresh cache
        return berhasil;
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    /**
     * Memperbarui data mata kuliah yang sudah ada
     */
    public boolean updateMataKuliah(int idMk, String namaMk, int sks, String nilaiHuruf, int semester) {
        int idUser = authController.getCurrentUserId();
        if (idUser == -1) return false;

        if (namaMk == null || namaMk.isBlank()) return false;
        if (sks < 1 || sks > 6) return false;
        if (nilaiHuruf == null || nilaiHuruf.isBlank()) return false;
        if (semester < 1) return false;

        MataKuliah mk = new MataKuliah(idMk, idUser, namaMk.trim(), sks, nilaiHuruf.trim(), semester);
        boolean berhasil = transkripDAO.updateMataKuliah(mk);
        if (berhasil) loadData();
        return berhasil;
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    /**
     * Menghapus mata kuliah berdasarkan id_mk
     */
    public boolean deleteMataKuliah(int idMk) {
        int idUser = authController.getCurrentUserId();
        if (idUser == -1) return false;

        boolean berhasil = transkripDAO.deleteMataKuliah(idMk, idUser);
        if (berhasil) loadData();
        return berhasil;
    }

    // ── KOMPUTASI IPS & IPK ───────────────────────────────────────────────────

    /**
     * Menghitung IPS untuk semester tertentu
     * Sesuai proposal: fungsi hitungIPS(semester)
     */
    public double hitungIPS(int semester) {
        return KalkulatorNilai.hitungIPS(listMataKuliah, semester);
    }

    /**
     * Menghitung IPK kumulatif dari semua semester
     * Sesuai proposal: fungsi hitungIPK()
     */
    public double hitungIPK() {
        return KalkulatorNilai.hitungIPK(listMataKuliah);
    }

    /**
     * Menghitung total SKS yang sudah ditempuh
     */
    public int getTotalSKS() {
        return KalkulatorNilai.hitungTotalSKS(listMataKuliah);
    }

    /**
     * Mendapatkan semester terakhir yang sudah ditempuh
     */
    public int getSemesterTerakhir() {
        return KalkulatorNilai.getSemesterTerakhir(listMataKuliah);
    }

    /**
     * Mendapatkan data IPS per semester untuk grafik line chart
     * Mengembalikan Map<semester, nilaiIPS> yang sudah terurut
     */
    public Map<Integer, Double> getDataGrafik() {
        Map<Integer, Double> dataGrafik = new TreeMap<>();
        // Ambil semua semester unik
        listMataKuliah.stream()
                .map(MataKuliah::getSemester)
                .distinct()
                .sorted()
                .forEach(sem -> dataGrafik.put(sem, hitungIPS(sem)));
        return dataGrafik;
    }

    /**
     * Mendapatkan daftar semester yang tersedia (untuk dropdown filter)
     */
    public List<Integer> getDaftarSemester() {
        return listMataKuliah.stream()
                .map(MataKuliah::getSemester)
                .distinct()
                .sorted()
                .collect(java.util.stream.Collectors.toList());
    }
    // ── Edit state (pass data RiwayatNilai → FormMataKuliah) ──────────────────
    public MataKuliah getMataKuliahEdit() { return mataKuliahEdit; }
    public void setMataKuliahEdit(MataKuliah mk) { this.mataKuliahEdit = mk; }
}
