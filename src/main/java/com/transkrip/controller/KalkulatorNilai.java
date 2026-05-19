package com.transkrip.controller;

import com.transkrip.model.MataKuliah;

import java.util.List;

/**
 * KalkulatorNilai — Mengelola algoritma matematis komputasi IPS dan IPK
 * Sesuai proposal: konversi nilai huruf → angka, hitung IPS per semester, hitung IPK kumulatif
 */
public class KalkulatorNilai {

    /**
     * Mengkonversi nilai huruf menjadi bobot angka
     * Sesuai tabel konversi di proposal
     */
    public static double getBobotAngka(String nilaiHuruf) {
        return switch (nilaiHuruf.trim().toUpperCase()) {
            case "A", "A+" -> 4.0;
            case "B+"      -> 3.5;
            case "B"       -> 3.0;
            case "B-"      -> 2.7;
            case "C+"      -> 2.3;
            case "C"       -> 2.0;
            case "D"       -> 1.0;
            case "E"       -> 0.0;
            default        -> 0.0;
        };
    }

    /**
     * Menghitung IPS (Indeks Prestasi Semester) untuk semester tertentu
     * Rumus: IPS = Σ(Bobot × SKS) / Σ(SKS) pada semester tersebut
     *
     * @param listMataKuliah daftar semua mata kuliah user
     * @param semester       semester yang ingin dihitung IPS-nya
     * @return nilai IPS (0.00 - 4.00), 0.0 jika tidak ada data
     */
    public static double hitungIPS(List<MataKuliah> listMataKuliah, int semester) {
        double totalMutu = 0.0;
        int totalSKS = 0;

        for (MataKuliah mk : listMataKuliah) {
            if (mk.getSemester() == semester) {
                totalMutu += getBobotAngka(mk.getNilaiHuruf()) * mk.getSks();
                totalSKS  += mk.getSks();
            }
        }

        if (totalSKS == 0) return 0.0;
        return Math.round((totalMutu / totalSKS) * 100.0) / 100.0;
    }

    /**
     * Menghitung IPK (Indeks Prestasi Kumulatif) dari seluruh semester
     * Rumus: IPK = Σ(Bobot × SKS) / Σ(SKS) dari semua semester
     *
     * @param listMataKuliah daftar semua mata kuliah user
     * @return nilai IPK (0.00 - 4.00), 0.0 jika tidak ada data
     */
    public static double hitungIPK(List<MataKuliah> listMataKuliah) {
        double totalMutu = 0.0;
        int totalSKS = 0;

        for (MataKuliah mk : listMataKuliah) {
            totalMutu += getBobotAngka(mk.getNilaiHuruf()) * mk.getSks();
            totalSKS  += mk.getSks();
        }

        if (totalSKS == 0) return 0.0;
        return Math.round((totalMutu / totalSKS) * 100.0) / 100.0;
    }

    /**
     * Menghitung total SKS yang sudah ditempuh
     *
     * @param listMataKuliah daftar semua mata kuliah user
     * @return total SKS
     */
    public static int hitungTotalSKS(List<MataKuliah> listMataKuliah) {
        return listMataKuliah.stream()
                .mapToInt(MataKuliah::getSks)
                .sum();
    }

    /**
     * Mendapatkan jumlah semester yang sudah ditempuh
     *
     * @param listMataKuliah daftar semua mata kuliah user
     * @return jumlah semester unik
     */
    public static int getSemesterTerakhir(List<MataKuliah> listMataKuliah) {
        return listMataKuliah.stream()
                .mapToInt(MataKuliah::getSemester)
                .max()
                .orElse(0);
    }

    /**
     * Format nilai IPK/IPS menjadi string 2 desimal
     * Contoh: 3.5 → "3.50"
     */
    public static String formatNilai(double nilai) {
        return String.format("%.2f", nilai);
    }
}
