module com.example.lab_2_ui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.lab_2_ui to javafx.fxml;
    exports com.example.lab_2_ui;
}