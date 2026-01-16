package aed.elrincon.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Optional;

import aed.elrincon.model.Student;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;

public class ObjectController {

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
    private Label lblStatus;
    @FXML
    private Label lblFileInfo;
    @FXML
    private Label lblRecordCount;

    private final ObservableList<Student> studentList = FXCollections.observableArrayList();
    private final String exportedObjPath = "sampleData";

    @FXML
    public void initialize() {
        // Configurar las columnas de la tabla
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colEdad.setCellValueFactory(new PropertyValueFactory<>("edad"));
        colMatricula.setCellValueFactory(new PropertyValueFactory<>("matricula"));

        tableStudents.setItems(studentList);
    }

    @FXML
    public void handleViewObj() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Abrir Archivo .obj");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Object Files", "*.obj"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        
        // Solo establecer el directorio inicial si existe
        File initialDir = new File(exportedObjPath);
        if (initialDir.exists() && initialDir.isDirectory()) {
            fileChooser.setInitialDirectory(initialDir);
        }

        File file = fileChooser.showOpenDialog(tableStudents.getScene().getWindow());
        if (file != null) {
            loadFromObj(file);
        }
    }

    

    @SuppressWarnings("unchecked")
    private void loadFromObj(File file) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            List<Student> students = (List<Student>) ois.readObject();
            studentList.clear();
            studentList.addAll(students);

            lblStatus.setText("Archivo .obj cargado: " + file.getName());
            lblFileInfo.setText("Archivo: " + file.getAbsolutePath());
            updateRecordCount();

            showAlert("Éxito", "Archivo .obj cargado: " + students.size() + " estudiantes", Alert.AlertType.INFORMATION);

        } catch (Exception e) {
            showAlert("Error", "Error al cargar archivo .obj: " + e.getMessage(), Alert.AlertType.ERROR);
            System.out.println(e.getMessage());
        }
    }

    @FXML
    public void handleAddStudent() {
        Dialog<Student> dialog = createStudentDialog(null);
        Optional<Student> result = dialog.showAndWait();

        result.ifPresent(student -> {
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
            updateRecordCount();
        }
    }

    private Dialog<Student> createStudentDialog(Student student) {
        Dialog<Student> dialog = new Dialog<>();
        dialog.setTitle(student == null ? "Agregar Estudiante" : "Editar Estudiante");
        dialog.setHeaderText(student == null ? "Ingrese los datos del nuevo estudiante" : "Modifique los datos del estudiante");

        ButtonType saveButtonType = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField txtNombre = new TextField(student != null ? student.getNombre() : "");
        TextField txtApellido = new TextField(student != null ? student.getApellido() : "");
        TextField txtEdad = new TextField(student != null ? student.getEdad() : "");
        TextField txtMatricula = new TextField(student != null ? student.getMatricula() : "");

        grid.add(new Label("Nombre:"), 0, 0);
        grid.add(txtNombre, 1, 0);
        grid.add(new Label("Apellido:"), 0, 1);
        grid.add(txtApellido, 1, 1);
        grid.add(new Label("Edad:"), 0, 2);
        grid.add(txtEdad, 1, 2);
        grid.add(new Label("Matrícula:"), 0, 3);
        grid.add(txtMatricula, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return new Student(txtNombre.getText(), txtApellido.getText(), txtEdad.getText(), txtMatricula.getText());
            }
            return null;
        });

        return dialog;
    }

    private void updateRecordCount() {
        lblRecordCount.setText("Registros: " + studentList.size());
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    public void handleBackToLauncher() throws IOException {
        aed.elrincon.App.setRoot("laucher");
    }
}
