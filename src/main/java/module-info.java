module aed.elrincon {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.opencsv;
    requires com.google.gson;
    requires java.xml.bind; // Si usas JAXB

    opens aed.elrincon to javafx.fxml;
    opens aed.elrincon.controller to javafx.fxml;

    exports aed.elrincon;
}