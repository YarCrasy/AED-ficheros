package aed.elrincon.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import aed.elrincon.model.Student;
import aed.elrincon.model.StudentList;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

public class XMLController {

    @FXML
    private TableView<Student> tableStudents;
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

    private final ObservableList<Student> students = FXCollections.observableArrayList();
    private final FileChooser fileChooser = new FileChooser();

    @FXML
    public void initialize() {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colEdad.setCellValueFactory(new PropertyValueFactory<>("edad"));
        colMatricula.setCellValueFactory(new PropertyValueFactory<>("matricula"));

        tableStudents.setItems(students);

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML Files", "*.xml"));
        fileChooser.setInitialFileName("students.xml");
    }

    @FXML
    private void addStudent() {
        if (!validateInputs()) {
            return;
        }

        if (matriculaExists(txtMatricula.getText().trim())) {
            showAlert("Matrícula duplicada", "Ya existe un estudiante con esa matrícula", Alert.AlertType.WARNING);
            return;
        }

        Student student = new Student(
                txtNombre.getText().trim(),
                txtApellido.getText().trim(),
                txtEdad.getText().trim(),
                txtMatricula.getText().trim()
        );

        students.add(student);
        clearInputs();
    }

    @FXML
    private void deleteStudent() {
        Student selected = tableStudents.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Sin selección", "Selecciona un estudiante para eliminar", Alert.AlertType.INFORMATION);
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "¿Eliminar al estudiante seleccionado?", ButtonType.OK, ButtonType.CANCEL);
        confirm.showAndWait().filter(ButtonType.OK::equals).ifPresent(btn -> students.remove(selected));
    }

    @FXML
    private void saveXML() {
        File file = fileChooser.showSaveDialog(tableStudents.getScene().getWindow());
        if (file == null) {
            return;
        }

        try {
            JAXBContext context = JAXBContext.newInstance(StudentList.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            StudentList wrapper = new StudentList(new ArrayList<>(students));
            marshaller.marshal(wrapper, file);
            showAlert("Éxito", "Archivo guardado en: " + file.getAbsolutePath(), Alert.AlertType.INFORMATION);
        } catch (Exception ex) {
            showAlert("Error", "No se pudo guardar el archivo XML: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void loadXML() {
        File file = fileChooser.showOpenDialog(tableStudents.getScene().getWindow());
        if (file == null) {
            return;
        }

        try {
            JAXBContext context = JAXBContext.newInstance(StudentList.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            StudentList wrapper = (StudentList) unmarshaller.unmarshal(file);
            List<Student> loaded = wrapper != null && wrapper.getStudents() != null ? wrapper.getStudents() : List.of();
            students.setAll(loaded);
            showAlert("Éxito", "Archivo cargado: " + file.getAbsolutePath(), Alert.AlertType.INFORMATION);
        } catch (Exception ex) {
            showAlert("Error", "No se pudo cargar el archivo XML: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private boolean validateInputs() {
        if (txtNombre.getText().trim().isEmpty() ||
                txtApellido.getText().trim().isEmpty() ||
                txtEdad.getText().trim().isEmpty() ||
                txtMatricula.getText().trim().isEmpty()) {
            showAlert("Campos vacíos", "Completa todos los campos", Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }

    private boolean matriculaExists(String matricula) {
        return students.stream().anyMatch(s -> s.getMatricula().equalsIgnoreCase(matricula));
    }

    private void clearInputs() {
        txtNombre.clear();
        txtApellido.clear();
        txtEdad.clear();
        txtMatricula.clear();
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
