module aed.elrincon {
    requires javafx.controls;
    requires javafx.fxml;

    requires java.xml.bind;

    opens aed.elrincon to javafx.fxml;
    opens aed.elrincon.model to java.xml.bind;

    exports aed.elrincon;
}
