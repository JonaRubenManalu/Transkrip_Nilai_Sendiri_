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

    public void loadData() {
        int idUser = authController.getCurrentUserId();
        if (idUser == -1) return;
        listMataKuliah = transkripDAO.getAllMataKuliah(idUser);
    }

    public List<MataKuliah> getAllMataKuliah() {
        return new ArrayList<>(listMataKuliah);
    }

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
    public boolean deleteMataKuliah(int idMk) {
        int idUser = authController.getCurrentUserId();
        if (idUser == -1) return false;

        boolean berhasil = transkripDAO.deleteMataKuliah(idMk, idUser);
        if (berhasil) loadData();
        return berhasil;
    }

    // ── KOMPUTASI IPS & IPK ───────────────────────────────────────────────────
    public double hitungIPS(int semester) {
        return KalkulatorNilai.hitungIPS(listMataKuliah, semester);
    }

    public double hitungIPK() {
        return KalkulatorNilai.hitungIPK(listMataKuliah);
    }

    public int getTotalSKS() {
        return KalkulatorNilai.hitungTotalSKS(listMataKuliah);
    }

    public int getSemesterTerakhir() {
        return KalkulatorNilai.getSemesterTerakhir(listMataKuliah);
    }

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
