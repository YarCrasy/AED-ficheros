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

<<<<<<< HEAD
    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

=======
>>>>>>> adriBien-v
    public static void main(String[] args) {
        launch();
    }
}
