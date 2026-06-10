package com.transkrip.controller;

import com.transkrip.model.MataKuliah;
import com.transkrip.model.TranskripDAO;

import java.util.*;
import java.util.stream.Collectors;


public class TranskripController {

    private static volatile TranskripController instance;
    private final TranskripDAO transkripDAO;
    private final AuthController authController;

    private List<MataKuliah> listMataKuliah;
    private MataKuliah mataKuliahEdit;

    // Cache hasil komputasi agar tidak dihitung ulang tiap akses
    private double cachedIPK = -1;
    private int    cachedTotalSKS = -1;
    private Map<Integer, Double> cachedDataGrafik = null;

    private TranskripController() {
        this.transkripDAO   = new TranskripDAO();
        this.authController = AuthController.getInstance();
        this.listMataKuliah = new ArrayList<>();
    }

    public static TranskripController getInstance() {
        if (instance == null) {
            synchronized (TranskripController.class) {
                if (instance == null) {
                    instance = new TranskripController();
                }
            }
        }
        return instance;
    }

    // ── Load / Refresh ────────────────────────────────────────────────────────

    public void loadData() {
        int idUser = authController.getCurrentUserId();
        if (idUser == -1) return;
        listMataKuliah = transkripDAO.getAllMataKuliah(idUser);
        invalidateComputedCache();  // buang cache kalkulasi lama
    }

    public void resetCache() {
        listMataKuliah = new ArrayList<>();
        mataKuliahEdit = null;
        invalidateComputedCache();
    }

    private void invalidateComputedCache() {
        cachedIPK        = -1;
        cachedTotalSKS   = -1;
        cachedDataGrafik = null;
    }

    // ── READ (dari cache, tidak hit DB) ──────────────────────────────────────

    public List<MataKuliah> getAllMataKuliah() {
        return new ArrayList<>(listMataKuliah);
    }

    public List<MataKuliah> getMataKuliahBySemester(int semester) {
        // Filter langsung dari cache — tidak query DB
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
        if (namaMk == null || namaMk.isBlank()) return false;
        if (sks < 1 || sks > 6) return false;
        if (nilaiHuruf == null || nilaiHuruf.isBlank()) return false;
        if (semester < 1) return false;

        MataKuliah mk = new MataKuliah(idUser, namaMk.trim(), sks, nilaiHuruf.trim(), semester);
        boolean berhasil = transkripDAO.tambahMataKuliah(mk);
        if (berhasil) loadData();
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

    // ── KOMPUTASI (lazy-cached, dari in-memory list) ──────────────────────────

    public double hitungIPS(int semester) {
        return KalkulatorNilai.hitungIPS(listMataKuliah, semester);
    }

    public double hitungIPK() {
        if (cachedIPK < 0) {
            cachedIPK = KalkulatorNilai.hitungIPK(listMataKuliah);
        }
        return cachedIPK;
    }

    public int getTotalSKS() {
        if (cachedTotalSKS < 0) {
            cachedTotalSKS = KalkulatorNilai.hitungTotalSKS(listMataKuliah);
        }
        return cachedTotalSKS;
    }

    public int getSemesterTerakhir() {
        return KalkulatorNilai.getSemesterTerakhir(listMataKuliah);
    }

    public Map<Integer, Double> getDataGrafik() {
        if (cachedDataGrafik != null) return cachedDataGrafik;

        Map<Integer, Double> map = new TreeMap<>();
        listMataKuliah.stream()
                .map(MataKuliah::getSemester)
                .distinct()
                .sorted()
                .forEach(sem -> map.put(sem, hitungIPS(sem)));
        cachedDataGrafik = map;
        return cachedDataGrafik;
    }

    public List<Integer> getDaftarSemester() {
        return listMataKuliah.stream()
                .map(MataKuliah::getSemester)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    // ── Edit state ────────────────────────────────────────────────────────────
    public MataKuliah getMataKuliahEdit() {
        return mataKuliahEdit;
    }

    public void       setMataKuliahEdit(MataKuliah mk) {
        this.mataKuliahEdit = mk;
    }
}
