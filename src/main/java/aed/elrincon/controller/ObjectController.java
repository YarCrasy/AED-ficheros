package aed.elrincon.controller;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;

import aed.elrincon.App;
import aed.elrincon.FileCRUD;
import aed.elrincon.model.Student;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class ObjectController extends FileCRUD {

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
    private Label lblRecordCount;

    private final ObservableList<Student> studentList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colEdad.setCellValueFactory(new PropertyValueFactory<>("edad"));
        colMatricula.setCellValueFactory(new PropertyValueFactory<>("matricula"));
        tableStudents.setItems(studentList);
        updateRecordCount();
    }

    @FXML
    public void handleViewObj() {
        File file = openFileChooser(tableStudents.getScene().getWindow(), 
                "Abrir Archivo .obj", "Object Files", "*.obj", "All Files", "*.*");
        if (file != null) {
            loadFromFile(file, FileType.OBJ);
            studentList.clear();
            studentList.addAll(tempStudents);
            updateRecordCount();
        }
    }

    @FXML
    public void handleSaveObj() {
        saveToFile(tableStudents.getScene().getWindow(), FileType.OBJ);
    }

    @FXML
    public void handleAddStudent() {
        Dialog<Student> dialog = createStudentDialog(null);
        Optional<Student> result = dialog.showAndWait();

        result.ifPresent(student -> {
            Set<String> existingMatriculas = collectMatriculas(tempStudents, null);
            if (!student.isValid(existingMatriculas)) {
                showAlert("Error", "Datos inválidos o matrícula duplicada", Alert.AlertType.ERROR);
                return;
            }
            tempStudents.add(student);
            studentList.add(student);
            updateRecordCount();
        });
    }

    @FXML
    public void handleEditStudent() {
        Student selected = tableStudents.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Advertencia", "Seleccione un estudiante para editar", Alert.AlertType.WARNING);
            return;
        }

        Dialog<Student> dialog = createStudentDialog(selected);
        Optional<Student> result = dialog.showAndWait();

        result.ifPresent(student -> {
            Set<String> existingMatriculas = collectMatriculas(tempStudents, selected);
            if (!student.isValid(existingMatriculas)) {
                showAlert("Error", "Datos inválidos o matrícula duplicada", Alert.AlertType.ERROR);
                return;
            }
            selected.setNombre(student.getNombre());
            selected.setApellido(student.getApellido());
            selected.setEdad(student.getEdad());
            selected.setMatricula(student.getMatricula());
            tableStudents.refresh();
        });
    }

    @FXML
    public void handleDeleteStudent() {
        Student selected = tableStudents.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Advertencia", "Seleccione un estudiante para eliminar", Alert.AlertType.WARNING);
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar eliminación");
        confirm.setHeaderText("¿Eliminar estudiante?");
        confirm.setContentText("¿Está seguro de eliminar a " + selected.getNombre() + " " + selected.getApellido() + "?");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            studentList.remove(selected);
            tempStudents.remove(selected);
            updateRecordCount();
        }
    }

    @FXML
    public void handleBackToLauncher() throws IOException {
        App.setRoot("launcher");
    }

    private void updateRecordCount() {
        lblRecordCount.setText("Registros: " + studentList.size());
    }
}
