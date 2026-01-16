package aed.elrincon;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                App.class.getResource("/aed/elrincon/student.fxml")
        );

        Scene scene = new Scene(loader.load(), 1000, 600);
        stage.setTitle("Gestión de Estudiantes - XML");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
