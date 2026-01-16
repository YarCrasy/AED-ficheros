package aed.elrincon.controller;

import aed.elrincon.model.Student;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
    private TextArea txtJsonContent;  // Lo añadí para evitar error

    @FXML
    private Label lblStatus;

    private ObservableList<Student> studentsList;
    private final String JSON_FILE = "students.json";
    private Gson gson;
    private Student selectedStudent;

    @FXML
    public void initialize() {
        gson = new Gson();
        studentsList = FXCollections.observableArrayList();

        // Configurar tabla
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colEdad.setCellValueFactory(new PropertyValueFactory<>("edad"));
        colMatricula.setCellValueFactory(new PropertyValueFactory<>("matricula"));

        studentsTable.setItems(studentsList);

        // Cargar datos
        loadData();

        // Selección en tabla
        studentsTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> selectStudent(newSelection)
        );
    }

    private void loadData() {
        try {
            File file = new File(JSON_FILE);
            if (file.exists()) {
                String json = new String(Files.readAllBytes(Paths.get(JSON_FILE)));
                List<Student> students = gson.fromJson(json, new TypeToken<List<Student>>(){}.getType());
                if (students != null) {
                    studentsList.setAll(students);
                }
                setStatus("Datos cargados: " + studentsList.size() + " estudiantes");
                txtJsonContent.setText(json);  // Mostrar contenido JSON en el TextArea
            } else {
                setStatus("Archivo creado: " + JSON_FILE);
                saveData();
            }
        } catch (Exception e) {
            setStatus("Error al cargar: " + e.getMessage());
        }
    }

    private void saveData() {
        try (FileWriter writer = new FileWriter(JSON_FILE)) {
            gson.toJson(new ArrayList<>(studentsList), writer);
            setStatus("Guardado: " + studentsList.size() + " estudiantes");
            txtJsonContent.setText(gson.toJson(new ArrayList<>(studentsList))); // Actualizar contenido JSON
        } catch (IOException e) {
            setStatus("Error al guardar: " + e.getMessage());
        }
    }

    @FXML
    private void agregarStudent() {
        if (validateFields()) {
            String matricula = txtMatricula.getText().trim();

            // Verificar matrícula única
            for (Student s : studentsList) {
                if (s.getMatricula().equals(matricula)) {
                    setStatus("Error: Matrícula ya existe");
                    return;
                }
            }

            Student student = new Student(
                txtNombre.getText().trim(),
                txtApellido.getText().trim(),
                txtEdad.getText().trim(),
                matricula
            );

            studentsList.add(student);
            saveData();
            limpiarCampos();
        }
    }

    @FXML
    private void modificarStudent() {
        if (selectedStudent != null && validateFields()) {
            String nuevaMatricula = txtMatricula.getText().trim();

            // Verificar matrícula única (excepto para el estudiante actual)
            for (Student s : studentsList) {
                if (s != selectedStudent && s.getMatricula().equals(nuevaMatricula)) {
                    setStatus("Error: Matrícula ya existe");
                    return;
                }
            }

            selectedStudent.setNombre(txtNombre.getText().trim());
            selectedStudent.setApellido(txtApellido.getText().trim());
            selectedStudent.setEdad(txtEdad.getText().trim());
            selectedStudent.setMatricula(nuevaMatricula);

            studentsTable.refresh();
            saveData();
            limpiarCampos();
            selectedStudent = null;
        }
    }

    @FXML
    private void eliminarStudent() {
        if (selectedStudent != null) {
            studentsList.remove(selectedStudent);
            saveData();
            limpiarCampos();
            selectedStudent = null;
            setStatus("Estudiante eliminado");
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
        setStatus("Campos limpiados");
    }

    private void selectStudent(Student student) {
        if (student != null) {
            selectedStudent = student;
            txtNombre.setText(student.getNombre());
            txtApellido.setText(student.getApellido());
            txtEdad.setText(student.getEdad());
            txtMatricula.setText(student.getMatricula());
            setStatus("Seleccionado: " + student.getNombre());
        }
    }

    private boolean validateFields() {
        if (txtNombre.getText().trim().isEmpty() ||
            txtApellido.getText().trim().isEmpty() ||
            txtEdad.getText().trim().isEmpty() ||
            txtMatricula.getText().trim().isEmpty()) {
            setStatus("Error: Todos los campos son obligatorios");
            return false;
        }

        String matricula = txtMatricula.getText().trim();
        if (!matricula.matches("\\d+")) {
            setStatus("Error: Matrícula debe contener solo números");
            return false;
        }

        try {
            int edad = Integer.parseInt(txtEdad.getText().trim());
            if (edad < 0 || edad > 120) {
                setStatus("Error: Edad debe ser entre 0 y 120");
                return false;
            }
        } catch (NumberFormatException e) {
            setStatus("Error: Edad debe ser un número");
            return false;
        }

        return true;
    }

    private void setStatus(String message) {
        lblStatus.setText(message);
    }
}
