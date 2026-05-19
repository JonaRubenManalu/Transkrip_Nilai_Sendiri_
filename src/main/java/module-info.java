module com.transkrip {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires org.xerial.sqlitejdbc;

    opens com.transkrip            to javafx.fxml;
    opens com.transkrip.view       to javafx.fxml;
    opens com.transkrip.controller to javafx.fxml;
    opens com.transkrip.model      to javafx.fxml;

    exports com.transkrip;
    exports com.transkrip.view;
    exports com.transkrip.controller;
    exports com.transkrip.model;
}
