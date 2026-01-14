module aed.elrincon {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.opencsv;
    requires com.google.gson;

    opens aed.elrincon to javafx.fxml;
    opens aed.elrincon.controller to javafx.fxml;
    opens aed.elrincon.model to javafx.fxml; // Necesario para PropertyValueFactory

    exports aed.elrincon;
    exports aed.elrincon.controller;
    exports aed.elrincon.model; // Exporta el modelo para TableView
}