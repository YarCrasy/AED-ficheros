package aed.elrincon;

import aed.elrincon.model.Student;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class CsvController implements Initializable {

    @FXML private TableView<Student> tableView;
    @FXML private TableColumn<Student, String> colNombre;
    @FXML private TableColumn<Student, String> colApellido;
    @FXML private TableColumn<Student, String> colEdad;
    @FXML private TableColumn<Student, String> colMatricula;

    @FXML private Button buttonAnadir;
    @FXML private Button buttonEditar;
    @FXML private Button buttonEliminar;
    @FXML private Button buttonGuardar;
    @FXML private Button buttonCargar;
    @FXML private Button buttonVolver;

    private ObservableList<Student> listaEstudiantes = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Configurar tabla
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colEdad.setCellValueFactory(new PropertyValueFactory<>("edad"));
        colMatricula.setCellValueFactory(new PropertyValueFactory<>("matricula"));

        tableView.setItems(listaEstudiantes);

        // Cargar datos de ejemplo
        cargarDatosEjemplo();

        // Configurar eventos de botones
        configurarEventos();
    }

    private void configurarEventos() {
        buttonAnadir.setOnAction(e -> anadirEstudiante());
        buttonEditar.setOnAction(e -> editarEstudiante());
        buttonEliminar.setOnAction(e -> eliminarEstudiante());
        buttonGuardar.setOnAction(e -> guardarCSV());
        buttonCargar.setOnAction(e -> cargarCSV());
        buttonVolver.setOnAction(e -> volverAlMenu());
    }

    private void cargarDatosEjemplo() {
        listaEstudiantes.add(new Student("Juan", "Perez", "20", "A12345"));
        listaEstudiantes.add(new Student("Maria", "Gomez", "22", "B67890"));
        listaEstudiantes.add(new Student("Carlos", "Lopez", "19", "C24680"));
        listaEstudiantes.add(new Student("Ana", "Rodriguez", "21", "D13579"));
        listaEstudiantes.add(new Student("Pedro", "Garcia", "23", "E97531"));
    }

    // ========== MÉTODOS CRUD ==========
    private void anadirEstudiante() {
        Dialog<Student> dialog = crearDialogoEstudiante("Nuevo Estudiante", null);
        dialog.showAndWait().ifPresent(estudiante -> {
            if (!estudiante.getNombre().trim().isEmpty()) {
                listaEstudiantes.add(estudiante);
                mostrarAlerta("Información", "Estudiante añadido correctamente", Alert.AlertType.INFORMATION);
            }
        });
    }

    private void editarEstudiante() {
        Student seleccionado = tableView.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Advertencia", "Selecciona un estudiante para editar", Alert.AlertType.WARNING);
            return;
        }

        Dialog<Student> dialog = crearDialogoEstudiante("Editar Estudiante", seleccionado);
        dialog.showAndWait().ifPresent(estudianteEditado -> {
            if (!estudianteEditado.getNombre().trim().isEmpty()) {
                int indice = listaEstudiantes.indexOf(seleccionado);
                listaEstudiantes.set(indice, estudianteEditado);
                tableView.refresh();
                mostrarAlerta("Información", "Estudiante actualizado", Alert.AlertType.INFORMATION);
            }
        });
    }

    private void eliminarEstudiante() {
        Student seleccionado = tableView.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Advertencia", "Selecciona un estudiante para eliminar", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Eliminar estudiante?");
        confirmacion.setContentText("Vas a eliminar a: " + seleccionado.getNombre() + " " + seleccionado.getApellido());

        if (confirmacion.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            listaEstudiantes.remove(seleccionado);
            mostrarAlerta("Información", "Estudiante eliminado", Alert.AlertType.INFORMATION);
        }
    }

    // ========== MÉTODOS CSV ==========
    private void guardarCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar archivo CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos CSV", "*.csv"));
        fileChooser.setInitialFileName("estudiantes.csv");

        File archivo = fileChooser.showSaveDialog(tableView.getScene().getWindow());
        if (archivo != null) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(archivo))) {
                writer.println("Nombre,Apellido,Edad,Matricula");
                for (Student estudiante : listaEstudiantes) {
                    writer.println(String.format("%s,%s,%s,%s",
                            estudiante.getNombre(),
                            estudiante.getApellido(),
                            estudiante.getEdad(),
                            estudiante.getMatricula()
                    ));
                }
                mostrarAlerta("Éxito", "Archivo guardado: " + archivo.getName(), Alert.AlertType.INFORMATION);
            } catch (IOException e) {
                mostrarAlerta("Error", "Error al guardar: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private void cargarCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Abrir archivo CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos CSV", "*.csv"));

        File archivo = fileChooser.showOpenDialog(tableView.getScene().getWindow());
        if (archivo != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
                ObservableList<Student> nuevosEstudiantes = FXCollections.observableArrayList();
                String linea;
                boolean primeraLinea = true;

                while ((linea = reader.readLine()) != null) {
                    if (primeraLinea) {
                        primeraLinea = false;
                        continue;
                    }

                    String[] campos = linea.split(",");
                    if (campos.length == 4) {
                        nuevosEstudiantes.add(new Student(
                                campos[0].trim(),
                                campos[1].trim(),
                                campos[2].trim(),
                                campos[3].trim()
                        ));
                    }
                }

                listaEstudiantes.setAll(nuevosEstudiantes);
                mostrarAlerta("Éxito", "Archivo cargado: " + archivo.getName() +
                        "\nEstudiantes cargados: " + nuevosEstudiantes.size(), Alert.AlertType.INFORMATION);

            } catch (IOException e) {
                mostrarAlerta("Error", "Error al cargar: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    // ========== MÉTODOS AUXILIARES ==========
    private Dialog<Student> crearDialogoEstudiante(String titulo, Student estudianteExistente) {
        Dialog<Student> dialog = new Dialog<>();
        dialog.setTitle(titulo);
        dialog.setHeaderText("Introduce los datos del estudiante");

        TextField txtNombre = new TextField();
        TextField txtApellido = new TextField();
        TextField txtEdad = new TextField();
        TextField txtMatricula = new TextField();

        if (estudianteExistente != null) {
            txtNombre.setText(estudianteExistente.getNombre());
            txtApellido.setText(estudianteExistente.getApellido());
            txtEdad.setText(estudianteExistente.getEdad());
            txtMatricula.setText(estudianteExistente.getMatricula());
        }

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 10, 10, 10));

        grid.add(new Label("Nombre:"), 0, 0);
        grid.add(txtNombre, 1, 0);
        grid.add(new Label("Apellido:"), 0, 1);
        grid.add(txtApellido, 1, 1);
        grid.add(new Label("Edad:"), 0, 2);
        grid.add(txtEdad, 1, 2);
        grid.add(new Label("Matrícula:"), 0, 3);
        grid.add(txtMatricula, 1, 3);

        dialog.getDialogPane().setContent(grid);

        ButtonType btnAceptar = new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnAceptar, btnCancelar);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == btnAceptar) {
                String nombre = txtNombre.getText().trim();
                String apellido = txtApellido.getText().trim();
                String edad = txtEdad.getText().trim();
                String matricula = txtMatricula.getText().trim();

                if (nombre.isEmpty() || apellido.isEmpty() || edad.isEmpty() || matricula.isEmpty()) {
                    mostrarAlerta("Error", "Todos los campos son obligatorios", Alert.AlertType.ERROR);
                    return null;
                }

                return new Student(nombre, apellido, edad, matricula);
            }
            return null;
        });

        return dialog;
    }

    private void volverAlMenu() {
        try {
            App.setRoot("launcher");
        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo cargar el menú: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}