# Manajemen Transkrip Nilai Pribadi
**Praktikum RPLBO — Universitas Kristen Duta Wacana 2026**

Kelompok:
- 71231048 — Jona Ruben Manalu (Ketua)
- 71241128 — Robin William Hermawan
- 71231138 — Daniel Adi Pramudya

---

## Teknologi
- Java 21
- JavaFX 21
- SQLite (via JDBC, file lokal `transkrip.db`)
- Maven

---

## Struktur Project

```
transkrip-nilai-pribadi/
├── pom.xml
└── src/main/java/com/transkrip/
    ├── MainApp.java                    ← Entry point
    ├── model/
    │   ├── DatabaseConnection.java     ← Koneksi JDBC + inisialisasi DB
    │   ├── User.java                   ← Entity tabel users
    │   ├── MataKuliah.java             ← Entity tabel mata_kuliah + getBobotAngka()
    │   ├── UserDAO.java                ← DAO: login, register
    │   └── TranskripDAO.java           ← DAO: CRUD mata_kuliah
    ├── controller/
    │   ├── AuthController.java         ← Validasi login, session user
    │   ├── TranskripController.java    ← CRUD + hitungIPS() + hitungIPK()
    │   └── KalkulatorNilai.java        ← Konversi nilai huruf, algoritma IPS/IPK
    └── view/
        ├── LoginView.java              ← Halaman login
        ├── DashboardView.java          ← Dashboard utama + grafik preview
        ├── RiwayatNilaiView.java       ← Tabel riwayat + filter semester
        ├── TambahMKView.java           ← Form tambah/edit mata kuliah
        └── GrafikIPSView.java          ← Line chart tren IPS per semester
```

---

## Cara Menjalankan di IntelliJ IDEA

### Prasyarat
- Java 21 sudah terinstall ([Download JDK 21](https://adoptium.net/))
- Maven sudah terinstall (atau gunakan Maven bawaan IntelliJ)

### Langkah Setup

1. **Buka project di IntelliJ IDEA**
   - File → Open → pilih folder `transkrip-nilai-pribadi`
   - Tunggu IntelliJ selesai mengindex dan download dependency Maven

2. **Jalankan aplikasi**
   - Buka file `MainApp.java`
   - Klik tombol ▶ di sebelah `public static void main`
   - Atau via terminal: `mvn javafx:run`

3. **Database otomatis dibuat**
   - File `transkrip.db` akan muncul di root folder project saat pertama kali dijalankan
   - Tidak perlu setup database manual

### Akun Pertama
Karena database kosong, daftarkan akun baru melalui link **"Daftar di sini"** di halaman login.

---

## Fitur Aplikasi (sesuai proposal)

| No | Fitur | Keterangan |
|----|-------|------------|
| 1  | Login & Register | Autentikasi dengan password hash SHA-256 |
| 2  | Dashboard | IPK, Total SKS, Semester, preview grafik |
| 3  | Tambah MK | Form input mata kuliah baru |
| 4  | Riwayat Nilai | Tabel semua MK dengan filter semester |
| 5  | Edit MK | Perbarui data mata kuliah |
| 6  | Hapus MK | Hapus entri dengan konfirmasi |
| 7  | Komputasi Otomatis | Hitung IPS & IPK dari bobot dan SKS |
| 8  | Grafik Tren IPS | Line chart IPS per semester |

---

## Tabel Konversi Nilai

| Nilai Huruf | Bobot Angka |
|-------------|-------------|
| A / A+      | 4.00        |
| B+          | 3.50        |
| B           | 3.00        |
| B-          | 2.70        |
| C+          | 2.30        |
| C           | 2.00        |
| D           | 1.00        |
| E           | 0.00        |

---

## Rumus Perhitungan

**IPS (per semester):**
```
IPS = Σ(Bobot × SKS) / Σ(SKS)  —  untuk semester tertentu
```

**IPK (kumulatif):**
```
IPK = Σ(Bobot × SKS) / Σ(SKS)  —  dari semua semester
```
