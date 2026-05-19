package com.transkrip.controller;

import com.transkrip.model.User;
import com.transkrip.model.UserDAO;

/**
 * AuthController — Menangani otentikasi pengguna
 * Sesuai proposal: menerima kredensial dari Form Login UI dan memvalidasinya
 * Menyimpan session user yang sedang login (Singleton)
 */
public class AuthController {

    private static AuthController instance;
    private final UserDAO userDAO;

    // Session: menyimpan user yang sedang login
    private User currentUser;

    // ── Singleton ────────────────────────────────────────────────────────────

    private AuthController() {
        this.userDAO = new UserDAO();
    }

    public static AuthController getInstance() {
        if (instance == null) {
            instance = new AuthController();
        }
        return instance;
    }

    // ── Login ─────────────────────────────────────────────────────────────────

    /**
     * Memvalidasi kredensial login
     * Mengembalikan true jika berhasil dan menyimpan session currentUser
     *
     * @param username username yang diinput
     * @param password password yang diinput (akan di-hash sebelum dicek)
     * @return true jika login berhasil
     */
    public boolean login(String username, String password) {
        // Validasi input tidak kosong
        if (username == null || username.isBlank()) return false;
        if (password == null || password.isBlank()) return false;

        User user = userDAO.login(username.trim(), password);
        if (user != null) {
            this.currentUser = user;
            System.out.println("[Auth] Login berhasil: " + user.getUsername());
            return true;
        }
        System.out.println("[Auth] Login gagal untuk username: " + username);
        return false;
    }

    // ── Register ──────────────────────────────────────────────────────────────

    /**
     * Mendaftarkan akun pengguna baru
     *
     * @param username username baru
     * @param password password baru
     * @return pesan hasil: "SUCCESS", "USERNAME_TAKEN", "INPUT_INVALID"
     */
    public String register(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return "INPUT_INVALID";
        }
        if (password.length() < 6) {
            return "PASSWORD_TOO_SHORT";
        }
        if (userDAO.isUsernameExists(username.trim())) {
            return "USERNAME_TAKEN";
        }
        boolean success = userDAO.register(username.trim(), password);
        return success ? "SUCCESS" : "ERROR";
    }

    // ── Logout ────────────────────────────────────────────────────────────────

    /**
     * Menghapus session user (logout)
     */
    public void logout() {
        System.out.println("[Auth] Logout: " + (currentUser != null ? currentUser.getUsername() : "-"));
        this.currentUser = null;
    }

    // ── Getter Session ────────────────────────────────────────────────────────

    /**
     * Mendapatkan user yang sedang login
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Mengecek apakah ada user yang sedang login
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Mendapatkan id_user yang sedang login (shortcut)
     */
    public int getCurrentUserId() {
        return currentUser != null ? currentUser.getIdUser() : -1;
    }

    /**
     * Mendapatkan username yang sedang login (shortcut)
     */
    public String getCurrentUsername() {
        return currentUser != null ? currentUser.getUsername() : "";
    }
}
