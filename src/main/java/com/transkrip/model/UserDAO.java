package com.transkrip.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * UserDAO — Pola DAO untuk tabel users
 * Menangani: login (SELECT), register (INSERT), enkripsi password SHA-256
 */
public class UserDAO {

    // ── Hash Password SHA-256 ────────────────────────────────────────────────

    /**
     * Mengenkripsi password menggunakan SHA-256
     * Sesuai proposal: password disimpan dalam bentuk hash
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 tidak tersedia", e);
        }
    }

    // ── Login (Validasi Kredensial) ──────────────────────────────────────────

    /**
     * Memvalidasi username dan password saat login
     * Mengembalikan objek User jika berhasil, null jika gagal
     */
    public User login(String username, String password) {
        String sql = "SELECT id_user, username, password FROM users WHERE username = ?";
        String hashedPassword = hashPassword(password);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password");
                if (storedHash.equals(hashedPassword)) {
                    return new User(
                            rs.getInt("id_user"),
                            rs.getString("username"),
                            storedHash
                    );
                }
            }

        } catch (SQLException e) {
            System.err.println("[UserDAO] Error login: " + e.getMessage());
        }
        return null; // Login gagal
    }

    // ── Register (Tambah User Baru) ──────────────────────────────────────────

    /**
     * Mendaftarkan user baru ke tabel users
     * Mengembalikan true jika berhasil
     */
    public boolean register(String username, String password) {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, hashPassword(password));
            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("[UserDAO] Error register: " + e.getMessage());
            return false;
        }
    }

    // ── Cek Username Tersedia ────────────────────────────────────────────────

    /**
     * Mengecek apakah username sudah digunakan
     */
    public boolean isUsernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            return rs.getInt(1) > 0;

        } catch (SQLException e) {
            System.err.println("[UserDAO] Error cek username: " + e.getMessage());
            return false;
        }
    }
}
