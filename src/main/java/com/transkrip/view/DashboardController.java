package com.transkrip.view;

import com.transkrip.controller.AuthController;
import com.transkrip.controller.KalkulatorNilai;
import com.transkrip.controller.TranskripController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class DashboardController implements Initializable, BaseController {

    @FXML private Label lblSambutan;
    @FXML private Label lblIPK;
    @FXML private Label lblSKS;
    @FXML private Label lblSemester;
    @FXML private LineChart<Number, Number> chartIPS;
    @FXML private Label lblKosongGrafik;

    private MainLayoutController mainLayout;
    private final TranskripController tc = TranskripController.getInstance();

    @Override
    public void setMainLayout(MainLayoutController ml) {
        this.mainLayout = ml;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        String username = AuthController.getInstance().getCurrentUsername();
        lblSambutan.setText("Selamat Datang, " + username + "!");

        double ipk = tc.hitungIPK();
        lblIPK.setText(KalkulatorNilai.formatNilai(ipk));
        lblSKS.setText(String.valueOf(tc.getTotalSKS()));

        int sem = tc.getSemesterTerakhir();
        lblSemester.setText(sem == 0 ? "-" : String.valueOf(sem));

        muatGrafik();
    }

    private void muatGrafik() {
        Map<Integer, Double> data = tc.getDataGrafik();

        if (data.isEmpty()) {
            chartIPS.setVisible(false);
            chartIPS.setManaged(false);
            lblKosongGrafik.setVisible(true);
            lblKosongGrafik.setManaged(true);
            return;
        }

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("IPS");
        data.forEach((sem, ips) ->
            series.getData().add(new XYChart.Data<>(sem, ips)));
        chartIPS.getData().add(series);
    }

    @FXML private void goTambah()  { if (mainLayout != null) mainLayout.goTambah(); }
    @FXML private void goRiwayat() { if (mainLayout != null) mainLayout.goRiwayat(); }
    @FXML private void goGrafik()  { if (mainLayout != null) mainLayout.goGrafik(); }
}
