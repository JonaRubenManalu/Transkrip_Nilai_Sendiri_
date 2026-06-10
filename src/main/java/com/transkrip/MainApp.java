package com.transkrip;

import com.transkrip.model.DatabaseConnection;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // 1. Init database di background
        Thread dbThread = new Thread(DatabaseConnection::initializeDatabase, "db-init");
        dbThread.setDaemon(true);
        dbThread.start();

        // 2. Load halaman Login
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/com/transkrip/fxml/Login.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 480, 560);

        // 3. Preload CSS agar tidak ada flash saat pertama tampil
        String css = getClass().getResource("/com/transkrip/css/style.css") != null
                ? getClass().getResource("/com/transkrip/css/style.css").toExternalForm()
                : null;
        if (css != null) {
            scene.getStylesheets().add(css);
        }

        primaryStage.setTitle("Transkrip Nilai Pribadi — UKDW 2026");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(420);
        primaryStage.setMinHeight(480);
        primaryStage.show();

        // 4. Tutup koneksi DB saat aplikasi ditutup
        primaryStage.setOnCloseRequest(e -> {
            DatabaseConnection.closeConnection();
            Platform.exit();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
