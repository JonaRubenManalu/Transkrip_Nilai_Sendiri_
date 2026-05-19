package com.transkrip.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * TranskripDAO — Pola DAO untuk tabel mata_kuliah
 * Menangani: INSERT, UPDATE, DELETE, SELECT (CRUD lengkap)
 * Sesuai proposal: TranskripController menghubungkan logika ke basis data melalui DAO ini
 */
public class TranskripDAO {

    // ── CREATE ───────────────────────────────────────────────────────────────

    /**
     * Menambahkan data mata kuliah baru ke database
     */
    public boolean tambahMataKuliah(MataKuliah mk) {
        String sql = """
                INSERT INTO mata_kuliah (id_user, nama_mk, sks, nilai_huruf, semester)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, mk.getIdUser());
            stmt.setString(2, mk.getNamaMk());
            stmt.setInt(3, mk.getSks());
            stmt.setString(4, mk.getNilaiHuruf());
            stmt.setInt(5, mk.getSemester());

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("[TranskripDAO] Error tambah MK: " + e.getMessage());
            return false;
        }
    }

    // ── READ (Semua MK milik user) ───────────────────────────────────────────

    /**
     * Mengambil seluruh mata kuliah milik user tertentu
     */
    public List<MataKuliah> getAllMataKuliah(int idUser) {
        List<MataKuliah> list = new ArrayList<>();
        String sql = """
                SELECT id_mk, id_user, nama_mk, sks, nilai_huruf, semester
                FROM mata_kuliah
                WHERE id_user = ?
                ORDER BY semester ASC, nama_mk ASC
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUser);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(new MataKuliah(
                        rs.getInt("id_mk"),
                        rs.getInt("id_user"),
                        rs.getString("nama_mk"),
                        rs.getInt("sks"),
                        rs.getString("nilai_huruf"),
                        rs.getInt("semester")
                ));
            }

        } catch (SQLException e) {
            System.err.println("[TranskripDAO] Error ambil semua MK: " + e.getMessage());
        }
        return list;
    }

    // ── READ (Filter by semester) ─────────────────────────────────────────────

    /**
     * Mengambil mata kuliah berdasarkan semester tertentu
     */
    public List<MataKuliah> getMataKuliahBySemester(int idUser, int semester) {
        List<MataKuliah> list = new ArrayList<>();
        String sql = """
                SELECT id_mk, id_user, nama_mk, sks, nilai_huruf, semester
                FROM mata_kuliah
                WHERE id_user = ? AND semester = ?
                ORDER BY nama_mk ASC
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUser);
            stmt.setInt(2, semester);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(new MataKuliah(
                        rs.getInt("id_mk"),
                        rs.getInt("id_user"),
                        rs.getString("nama_mk"),
                        rs.getInt("sks"),
                        rs.getString("nilai_huruf"),
                        rs.getInt("semester")
                ));
            }

        } catch (SQLException e) {
            System.err.println("[TranskripDAO] Error ambil MK per semester: " + e.getMessage());
        }
        return list;
    }

    // ── READ (Ambil daftar semester yang tersedia) ────────────────────────────

    /**
     * Mengambil daftar semester yang sudah ada (untuk dropdown filter)
     */
    public List<Integer> getSemesterList(int idUser) {
        List<Integer> list = new ArrayList<>();
        String sql = """
                SELECT DISTINCT semester FROM mata_kuliah
                WHERE id_user = ?
                ORDER BY semester ASC
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUser);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(rs.getInt("semester"));
            }

        } catch (SQLException e) {
            System.err.println("[TranskripDAO] Error ambil list semester: " + e.getMessage());
        }
        return list;
    }

    // ── UPDATE ───────────────────────────────────────────────────────────────

    /**
     * Memperbarui data mata kuliah yang sudah ada
     * Sesuai proposal: fitur pembaruan data nilai (misal setelah masa sanggah)
     */
    public boolean updateMataKuliah(MataKuliah mk) {
        String sql = """
                UPDATE mata_kuliah
                SET nama_mk = ?, sks = ?, nilai_huruf = ?, semester = ?
                WHERE id_mk = ? AND id_user = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, mk.getNamaMk());
            stmt.setInt(2, mk.getSks());
            stmt.setString(3, mk.getNilaiHuruf());
            stmt.setInt(4, mk.getSemester());
            stmt.setInt(5, mk.getIdMk());
            stmt.setInt(6, mk.getIdUser());

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("[TranskripDAO] Error update MK: " + e.getMessage());
            return false;
        }
    }

    // ── DELETE ───────────────────────────────────────────────────────────────

    /**
     * Menghapus data mata kuliah berdasarkan id_mk
     */
    public boolean deleteMataKuliah(int idMk, int idUser) {
        String sql = "DELETE FROM mata_kuliah WHERE id_mk = ? AND id_user = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idMk);
            stmt.setInt(2, idUser);
            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("[TranskripDAO] Error delete MK: " + e.getMessage());
            return false;
        }
    }

    // ── Hitung Total SKS ─────────────────────────────────────────────────────

    /**
     * Menghitung total SKS yang sudah ditempuh user
     */
    public int getTotalSKS(int idUser) {
        String sql = "SELECT COALESCE(SUM(sks), 0) AS total FROM mata_kuliah WHERE id_user = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUser);
            ResultSet rs = stmt.executeQuery();
            return rs.getInt("total");

        } catch (SQLException e) {
            System.err.println("[TranskripDAO] Error hitung total SKS: " + e.getMessage());
            return 0;
        }
    }
}
