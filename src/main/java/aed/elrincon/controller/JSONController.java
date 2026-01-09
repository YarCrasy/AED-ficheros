package aed.elrincon.controller;

import aed.elrincon.model.Student;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class JSONController {

    @FXML
    private TableView<Student> studentsTable;

    @FXML
    private TableColumn<Student, String> colNombre;

    @FXML
    private TableColumn<Student, String> colApellido;

    @FXML
    private TableColumn<Student, String> colEdad;

    @FXML
    private TableColumn<Student, String> colMatricula;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtApellido;

    @FXML
    private TextField txtEdad;

    @FXML
    private TextField txtMatricula;

    @FXML
    private Button btnAgregar;

    @FXML
    private Button btnModificar;

    @FXML
    private Button btnEliminar;

    @FXML
    private Button btnLimpiar;

    @FXML
    private TextArea txtJsonContent;

    @FXML
    private Label lblStatus;

    private ObservableList<Student> studentsList;
    private final String JSON_FILE = "students.json";
    private Gson gson;
    private Student studentSeleccionado;

    @FXML
    public void initialize() {
        gson = new GsonBuilder().setPrettyPrinting().create();
        studentsList = FXCollections.observableArrayList();

        // Configurar columnas de la tabla
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colEdad.setCellValueFactory(new PropertyValueFactory<>("edad"));
        colMatricula.setCellValueFactory(new PropertyValueFactory<>("matricula"));

        studentsTable.setItems(studentsList);

        // Cargar datos al iniciar
        cargarDatos();

        // Listener para selección de fila
        studentsTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> seleccionarStudent(newValue));

        // Deshabilitar botones inicialmente
        btnModificar.setDisable(true);
        btnEliminar.setDisable(true);
    }

    private void cargarDatos() {
        try {
            File file = new File(JSON_FILE);
            if (!file.exists()) {
                // Crear archivo vacío si no existe
                guardarDatos();
                return;
            }

            String json = new String(Files.readAllBytes(Paths.get(JSON_FILE)));
            Type listType = new TypeToken<ArrayList<Student>>() {
            }.getType();
            List<Student> students = gson.fromJson(json, listType);

            if (students != null) {
                studentsList.setAll(students);
                mostrarJSON();
                actualizarStatus("Datos cargados correctamente. Total: " + students.size());
            } else {
                // Si el archivo está vacío o corrupto, inicializar lista vacía
                studentsList.clear();
                mostrarJSON();
                actualizarStatus("Archivo vacío o corrupto. Lista inicializada vacía.");
            }
        } catch (IOException e) {
            mostrarError("Error al cargar el archivo JSON: " + e.getMessage());
        } catch (Exception e) {
            mostrarError("Error en el formato del archivo JSON: " + e.getMessage());
        }
    }

    private void guardarDatos() {
        try (FileWriter writer = new FileWriter(JSON_FILE)) {
            gson.toJson(new ArrayList<>(studentsList), writer);
            mostrarJSON();
            actualizarStatus("Datos guardados correctamente en " + JSON_FILE);
        } catch (IOException e) {
            mostrarError("Error al guardar el archivo JSON: " + e.getMessage());
        }
    }

    @FXML
    private void agregarStudent() {
        if (validarCampos()) {
            // Verificar si la matrícula ya existe
            String matricula = txtMatricula.getText().trim();
            if (matriculaExiste(matricula)) {
                mostrarError("La matrícula " + matricula + " ya existe.");
                return;
            }

            Student student = new Student(
                    txtNombre.getText().trim(),
                    txtApellido.getText().trim(),
                    txtEdad.getText().trim(),
                    matricula);

            studentsList.add(student);
            guardarDatos();
            limpiarCampos();
        }
    }

    @FXML
    private void modificarStudent() {
        if (studentSeleccionado != null && validarCampos()) {
            String nuevaMatricula = txtMatricula.getText().trim();

            // Verificar si la nueva matrícula ya existe (y no es la del estudiante actual)
            if (!studentSeleccionado.getMatricula().equals(nuevaMatricula) && matriculaExiste(nuevaMatricula)) {
                mostrarError("La matrícula " + nuevaMatricula + " ya existe.");
                return;
            }

            studentSeleccionado.setNombre(txtNombre.getText().trim());
            studentSeleccionado.setApellido(txtApellido.getText().trim());
            studentSeleccionado.setEdad(txtEdad.getText().trim());
            studentSeleccionado.setMatricula(nuevaMatricula);

            studentsTable.refresh();
            guardarDatos();
            limpiarCampos();
            deshabilitarBotonesCRUD();
        }
    }

    @FXML
    private void eliminarStudent() {
        if (studentSeleccionado != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmar eliminación");
            alert.setHeaderText("¿Está seguro de eliminar este estudiante?");
            alert.setContentText("Estudiante: " + studentSeleccionado.getNombre() + " " +
                    studentSeleccionado.getApellido() +
                    "\nMatrícula: " + studentSeleccionado.getMatricula());

            if (alert.showAndWait().get() == ButtonType.OK) {
                studentsList.remove(studentSeleccionado);
                guardarDatos();
                limpiarCampos();
                deshabilitarBotonesCRUD();
            }
        }
    }

    @FXML
    private void limpiarCampos() {
        txtNombre.clear();
        txtApellido.clear();
        txtEdad.clear();
        txtMatricula.clear();
        studentsTable.getSelectionModel().clearSelection();
        studentSeleccionado = null;
        deshabilitarBotonesCRUD();
        btnAgregar.setDisable(false);
    }

    @FXML
    private void mostrarJSON() {
        try {
            File file = new File(JSON_FILE);
            if (file.exists() && file.length() > 0) {
                String json = new String(Files.readAllBytes(Paths.get(JSON_FILE)));
                txtJsonContent.setText(json);
            } else {
                txtJsonContent.setText("[]");
            }
        } catch (IOException e) {
            txtJsonContent.setText("Error al leer el archivo JSON: " + e.getMessage());
        }
    }

    @FXML
    private void recargarDatos() {
        cargarDatos();
    }

    private void seleccionarStudent(Student student) {
        if (student != null) {
            studentSeleccionado = student;
            txtNombre.setText(student.getNombre());
            txtApellido.setText(student.getApellido());
            txtEdad.setText(student.getEdad());
            txtMatricula.setText(student.getMatricula());

            // Habilitar botones de modificar/eliminar
            btnModificar.setDisable(false);
            btnEliminar.setDisable(false);
            btnAgregar.setDisable(true);
        }
    }

    private void deshabilitarBotonesCRUD() {
        btnModificar.setDisable(true);
        btnEliminar.setDisable(true);
        btnAgregar.setDisable(false);
    }

    private boolean matriculaExiste(String matricula) {
        return studentsList.stream()
                .anyMatch(student -> student.getMatricula().equals(matricula));
    }

    private boolean validarCampos() {
        // Validar que no haya campos vacíos
        if (txtNombre.getText().trim().isEmpty() ||
                txtApellido.getText().trim().isEmpty() ||
                txtEdad.getText().trim().isEmpty() ||
                txtMatricula.getText().trim().isEmpty()) {
            mostrarError("Todos los campos son obligatorios.");
            return false;
        }

        // Validar matrícula (solo números, longitud específica si lo deseas)
        String matricula = txtMatricula.getText().trim();
        if (!matricula.matches("\\d+")) {
            mostrarError("La matrícula debe contener solo números.");
            return false;
        }

        // Validar edad (solo números)
        String edad = txtEdad.getText().trim();
        if (!edad.matches("\\d+")) {
            mostrarError("La edad debe ser un número.");
            return false;
        }

        // Validar rango de edad
        try {
            int edadInt = Integer.parseInt(edad);
            if (edadInt < 0 || edadInt > 120) {
                mostrarError("La edad debe estar entre 0 y 120 años.");
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarError("La edad debe ser un número válido.");
            return false;
        }

        return true;
    }

    private void mostrarError(String mensaje) {
        lblStatus.setText("Error: " + mensaje);
        lblStatus.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
    }

    private void actualizarStatus(String mensaje) {
        lblStatus.setText(mensaje);
        lblStatus.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
    }
}