module aed.elrincon {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires com.opencsv;
    requires com.google.gson;
    requires java.xml.bind;

    opens aed.elrincon to javafx.fxml;
    opens aed.elrincon.controller to javafx.fxml;
    opens aed.elrincon.model to com.google.gson, java.xml.bind, javafx.base;

    exports aed.elrincon;
    exports aed.elrincon.controller;
    exports aed.elrincon.model;
}
