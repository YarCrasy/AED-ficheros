module aed.elrincon {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires com.google.gson;
    requires com.opencsv;
    requires java.xml.bind;

    opens aed.elrincon to javafx.fxml;
    opens aed.elrincon.controller to javafx.fxml;
    opens aed.elrincon.model to com.google.gson, java.xml.bind;
    
    exports aed.elrincon;
    exports aed.elrincon.controller;
    exports aed.elrincon.model;
}
