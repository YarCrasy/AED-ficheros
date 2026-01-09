module aed.elrincon {
    requires javafx.controls;
    requires javafx.fxml;

    opens aed.elrincon to javafx.fxml;
    exports aed.elrincon;
}
