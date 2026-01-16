package aed.elrincon.controller;

import aed.elrincon.model.Student;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class JSONController {

    @FXML private TableView<Student> studentsTable;
    @FXML private TableColumn<Student, String> colNombre;
    @FXML private TableColumn<Student, String> colApellido;
    @FXML private TableColumn<Student, String> colEdad;
    @FXML private TableColumn<Student, String> colMatricula;

    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private TextField txtEdad;
    @FXML private TextField txtMatricula;
    @FXML private TextArea txtJsonContent;
    @FXML private Label lblStatus;

    private ObservableList<Student> studentsList;
    private Gson gson;
    private Student selectedStudent;

    @FXML
    public void initialize() {
        gson = new GsonBuilder().setPrettyPrinting().create();
        studentsList = FXCollections.observableArrayList();

        // Configurar tabla
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colEdad.setCellValueFactory(new PropertyValueFactory<>("edad"));
        colMatricula.setCellValueFactory(new PropertyValueFactory<>("matricula"));

        studentsTable.setItems(studentsList);

        // Cargar datos iniciales (si hay archivo por defecto)
        cargarAutomaticamente();
    }

    private void cargarAutomaticamente() {
        try {
            File defaultFile = new File("students.json");
            if (defaultFile.exists() && defaultFile.length() > 0) {
                cargarDesdeArchivo(defaultFile.getAbsolutePath());
            }
        } catch (Exception e) {
            // No hacer nada, simplemente no cargar
        }
    }

    @FXML
    private void cargarJSON() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo JSON para cargar");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos JSON", "*.json")
        );

        // Mostrar diálogo para elegir archivo
        File archivo = fileChooser.showOpenDialog(studentsTable.getScene().getWindow());

        if (archivo != null) {
            cargarDesdeArchivo(archivo.getAbsolutePath());
        }
    }

    private void cargarDesdeArchivo(String rutaArchivo) {
        try {
            String contenido = new String(Files.readAllBytes(Paths.get(rutaArchivo)));
            List<Student> estudiantes = gson.fromJson(contenido, new TypeToken<List<Student>>(){}.getType());

            if (estudiantes != null) {
                studentsList.setAll(estudiantes);
                txtJsonContent.setText(contenido);
                lblStatus.setText("✓ Cargado: " + estudiantes.size() + " estudiantes desde " +
                        new File(rutaArchivo).getName());
            } else {
                lblStatus.setText("✗ Archivo vacío o formato incorrecto");
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo cargar el archivo", e.getMessage());
        }
    }

    @FXML
    private void guardarJSON() {
        if (studentsList.isEmpty()) {
            mostrarAlerta("Advertencia", "Sin datos", "No hay estudiantes para guardar.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar archivo JSON");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos JSON", "*.json")
        );

        // Sugerir nombre por defecto
        fileChooser.setInitialFileName("estudiantes.json");

        // Mostrar diálogo para guardar
        File archivo = fileChooser.showSaveDialog(studentsTable.getScene().getWindow());

        if (archivo != null) {
            // Asegurar que tenga extensión .json
            String ruta = archivo.getAbsolutePath();
            if (!ruta.toLowerCase().endsWith(".json")) {
                ruta += ".json";
                archivo = new File(ruta);
            }

            guardarEnArchivo(ruta);
        }
    }

    private void guardarEnArchivo(String rutaArchivo) {
        try (FileWriter writer = new FileWriter(rutaArchivo)) {
            String json = gson.toJson(new ArrayList<>(studentsList));
            writer.write(json);

            // Actualizar vista
            txtJsonContent.setText(json);
            lblStatus.setText("✓ Guardado: " + studentsList.size() +
                    " estudiantes en " + new File(rutaArchivo).getName());
        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo guardar el archivo", e.getMessage());
        }
    }

    @FXML
    private void agregarStudent() {
        if (validarCampos()) {
            String matricula = txtMatricula.getText().trim();

            // Verificar matrícula única
            for (Student s : studentsList) {
                if (s.getMatricula().equals(matricula)) {
                    lblStatus.setText("✗ Matrícula ya existe");
                    return;
                }
            }

            Student estudiante = new Student(
                    txtNombre.getText().trim(),
                    txtApellido.getText().trim(),
                    txtEdad.getText().trim(),
                    matricula
            );

            studentsList.add(estudiante);
            limpiarCampos();
            lblStatus.setText("✓ Agregado: " + estudiante.getNombre());

            // Actualizar vista JSON
            actualizarVistaJSON();
        }
    }

    @FXML
    private void modificarStudent() {
        if (selectedStudent != null && validarCampos()) {
            String nuevaMatricula = txtMatricula.getText().trim();

            // Verificar matrícula única
            for (Student s : studentsList) {
                if (s != selectedStudent && s.getMatricula().equals(nuevaMatricula)) {
                    lblStatus.setText("✗ Matrícula ya existe");
                    return;
                }
            }

            selectedStudent.setNombre(txtNombre.getText().trim());
            selectedStudent.setApellido(txtApellido.getText().trim());
            selectedStudent.setEdad(txtEdad.getText().trim());
            selectedStudent.setMatricula(nuevaMatricula);

            studentsTable.refresh();
            limpiarCampos();
            lblStatus.setText("✓ Modificado: " + selectedStudent.getNombre());

            // Actualizar vista JSON
            actualizarVistaJSON();
        }
    }

    @FXML
    private void eliminarStudent() {
        if (selectedStudent != null) {
            studentsList.remove(selectedStudent);
            limpiarCampos();
            lblStatus.setText("✓ Eliminado: " + selectedStudent.getNombre());

            // Actualizar vista JSON
            actualizarVistaJSON();
        }
    }

    @FXML
    private void limpiarCampos() {
        txtNombre.clear();
        txtApellido.clear();
        txtEdad.clear();
        txtMatricula.clear();
        studentsTable.getSelectionModel().clearSelection();
        selectedStudent = null;
    }

    @FXML
    private void volver() throws IOException {
        aed.elrincon.App.setRoot("launcher");
    }

    private void actualizarVistaJSON() {
        String json = gson.toJson(new ArrayList<>(studentsList));
        txtJsonContent.setText(json);
    }

    private boolean validarCampos() {
        if (txtNombre.getText().trim().isEmpty() ||
                txtApellido.getText().trim().isEmpty() ||
                txtEdad.getText().trim().isEmpty() ||
                txtMatricula.getText().trim().isEmpty()) {
            lblStatus.setText("✗ Todos los campos son obligatorios");
            return false;
        }

        if (!txtMatricula.getText().trim().matches("\\d+")) {
            lblStatus.setText("✗ Matrícula solo números");
            return false;
        }

        try {
            int edad = Integer.parseInt(txtEdad.getText().trim());
            if (edad < 0 || edad > 120) {
                lblStatus.setText("✗ Edad 0-120");
                return false;
            }
        } catch (NumberFormatException e) {
            lblStatus.setText("✗ Edad debe ser número");
            return false;
        }

        return true;
    }

    private void mostrarAlerta(String titulo, String cabecera, String contenido) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(cabecera);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    // Este método se llama cuando seleccionas un estudiante en la tabla
    @FXML
    private void seleccionarStudent() {
        selectedStudent = studentsTable.getSelectionModel().getSelectedItem();
        if (selectedStudent != null) {
            txtNombre.setText(selectedStudent.getNombre());
            txtApellido.setText(selectedStudent.getApellido());
            txtEdad.setText(selectedStudent.getEdad());
            txtMatricula.setText(selectedStudent.getMatricula());
        }
    }
}