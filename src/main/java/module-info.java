module com.csvreader {
    requires javafx.controls;
    requires javafx.fxml;

    exports com.csvreader;
    exports com.csvreader.controller;

    // IMPORTANTE: Abrir el paquete controller para JavaFX FXML
    opens com.csvreader.controller to javafx.fxml;
}
