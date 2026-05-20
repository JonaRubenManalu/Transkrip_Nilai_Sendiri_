package com.transkrip.model;

public class User {

    private int    idUser;
    private String username;
    private String password;

    // ── Konstruktor ──────────────────────────────────────────────────────────

    public User() {}

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(int idUser, String username, String password) {
        this.idUser   = idUser;
        this.username = username;
        this.password = password;
    }

    // ── Getter & Setter ──────────────────────────────────────────────────────

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // ── toString ─────────────────────────────────────────────────────────────

    @Override
    public String toString() {
        return "User{id=" + idUser + ", username='" + username + "'}";
    }
}
