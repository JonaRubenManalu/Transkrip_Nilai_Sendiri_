package com.transkrip.controller;

import com.transkrip.model.MataKuliah;

import java.util.List;


public class KalkulatorNilai {
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

    public static int hitungTotalSKS(List<MataKuliah> listMataKuliah) {
        return listMataKuliah.stream()
                .mapToInt(MataKuliah::getSks)
                .sum();
    }


    public static int getSemesterTerakhir(List<MataKuliah> listMataKuliah) {
        return listMataKuliah.stream()
                .mapToInt(MataKuliah::getSemester)
                .max()
                .orElse(0);
    }


    public static String formatNilai(double nilai) {
        return String.format("%.2f", nilai);
    }
}
