package com.transkrip.model;

/**
 * Class MataKuliah — Entity sesuai class diagram proposal
 * Atribut: id_mk, nama_mk, sks, nilai_huruf, semester
 * Fitur: getBobotAngka() — konversi nilai huruf ke bobot angka
 * Relasi: Many-to-One dengan User
 */
public class MataKuliah {

    private int    idMk;
    private int    idUser;
    private String namaMk;
    private int    sks;
    private String nilaiHuruf;
    private int    semester;

    // ── Konstruktor ──────────────────────────────────────────────────────────

    public MataKuliah() {}

    public MataKuliah(int idUser, String namaMk, int sks, String nilaiHuruf, int semester) {
        this.idUser     = idUser;
        this.namaMk     = namaMk;
        this.sks        = sks;
        this.nilaiHuruf = nilaiHuruf;
        this.semester   = semester;
    }

    public MataKuliah(int idMk, int idUser, String namaMk, int sks, String nilaiHuruf, int semester) {
        this.idMk       = idMk;
        this.idUser     = idUser;
        this.namaMk     = namaMk;
        this.sks        = sks;
        this.nilaiHuruf = nilaiHuruf;
        this.semester   = semester;
    }

    // ── Fungsi utama: konversi nilai huruf → bobot angka ────────────────────

    public double getBobotAngka() {
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
     * Menghitung mutu (bobot × SKS) untuk keperluan perhitungan IPS/IPK
     */
    public double getMutu() {
        return getBobotAngka() * sks;
    }

    // ── Getter & Setter ──────────────────────────────────────────────────────

    public int getIdMk() {
        return idMk;
    }

    public void setIdMk(int idMk) {
        this.idMk = idMk;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getNamaMk() {
        return namaMk;
    }

    public void setNamaMk(String namaMk) {
        this.namaMk = namaMk;
    }

    public int getSks() {
        return sks;
    }

    public void setSks(int sks) {
        this.sks = sks;
    }

    public String getNilaiHuruf() {
        return nilaiHuruf;
    }

    public void setNilaiHuruf(String nilaiHuruf) {
        this.nilaiHuruf = nilaiHuruf;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    // ── toString ─────────────────────────────────────────────────────────────

    @Override
    public String toString() {
        return "MataKuliah{id=" + idMk
                + ", nama='" + namaMk + "'"
                + ", sks=" + sks
                + ", nilai=" + nilaiHuruf
                + ", bobot=" + getBobotAngka()
                + ", semester=" + semester + "}";
    }
}
