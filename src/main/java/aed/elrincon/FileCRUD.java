package aed.elrincon;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import aed.elrincon.model.Student;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Window;

public class FileCRUD {
    public static enum FileType {
        CSV, JSON, XML, OBJ
    }

    protected static final String SAMPLE_DATA_PATH = "sampleData";
    protected static final ArrayList<Student> tempStudents = new ArrayList<>();

    //--------------------------------Utils------------------------------------
    protected void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    protected Dialog<Student> createStudentDialog(Student student) {
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

    protected Set<String> collectMatriculas(List<Student> students, Student toIgnore) {
        Set<String> set = new HashSet<>();
        for (Student s : students) {
            if (s == toIgnore) {
                continue;
            }
            if (s.getMatricula() != null) {
                set.add(s.getMatricula().trim());
            }
        }
        return set;
    }

    //--------------------------------File I/O------------------------------------
    protected void loadFromObj(File file) {
        loadFromFile(file, FileType.OBJ);
    }

    protected void saveToObj(Window owner) {
        saveToFile(owner, FileType.OBJ);
    }

    @SuppressWarnings("unchecked")
    protected void loadFromFile(File file, FileType type) {
        try {
            List<Student> students = null;
            
            switch (type) {
                case OBJ -> {
                    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                        students = (List<Student>) ois.readObject();
                    }
                }
                case CSV, JSON, XML -> throw new UnsupportedOperationException("Tipo de archivo no implementado aún: " + type);
            }

            if (students != null) {
                Set<String> existingMatriculas = new HashSet<>();
                for (Student s : students) {
                    if (!s.isValid(existingMatriculas)) {
                        showAlert("Error", "Estudiante inválido en el archivo (campos vacíos, edad fuera de rango o matrícula duplicada)", Alert.AlertType.ERROR);
                        return;
                    }
                    existingMatriculas.add(s.getMatricula().trim());
                }

                tempStudents.clear();
                tempStudents.addAll(students);
                showAlert("Éxito", "Archivo " + type.name().toLowerCase() + " cargado: " + students.size() + " estudiantes", Alert.AlertType.INFORMATION);
            }
        } catch (IOException | ClassNotFoundException | UnsupportedOperationException e) {
            showAlert("Error", "Error al cargar archivo " + type.name().toLowerCase() + ": " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    protected void saveToFile(Window owner, FileType type) {
        if (tempStudents.isEmpty()) {
            showAlert("Advertencia", "No hay estudiantes para guardar", Alert.AlertType.WARNING);
            return;
        }

        String extension = type.name().toLowerCase();
        String description = getFileTypeDescription(type);
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Archivo " + extension);
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(description, "*." + extension),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm");
        String defaultFilename = extension + "-" + now.format(formatter) + "." + extension;
        fileChooser.setInitialFileName(defaultFilename);

        File initialDir = new File(SAMPLE_DATA_PATH);
        if (initialDir.exists() && initialDir.isDirectory()) {
            fileChooser.setInitialDirectory(initialDir);
        }

        File file = fileChooser.showSaveDialog(owner);
        if (file != null) {
            try {
                switch (type) {
                    case OBJ -> {
                        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                            oos.writeObject(new ArrayList<>(tempStudents));
                        }
                    }
                    case CSV, JSON, XML -> throw new UnsupportedOperationException("Tipo de archivo no implementado aún: " + type);
                }
                showAlert("Éxito", "Archivo guardado: " + file.getName(), Alert.AlertType.INFORMATION);
            } catch (IOException | UnsupportedOperationException e) {
                showAlert("Error", "Error al guardar archivo " + extension + ": " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private String getFileTypeDescription(FileType type) {
        return switch (type) {
            case CSV -> "CSV Files";
            case JSON -> "JSON Files";
            case XML -> "XML Files";
            case OBJ -> "Object Files";
            default -> "Files";
        };
    }

    protected File openFileChooser(Window owner, String title, String... extensionFilters) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);

        for (int i = 0; i < extensionFilters.length; i += 2) {
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter(extensionFilters[i], extensionFilters[i + 1])
            );
        }

        File initialDir = new File(SAMPLE_DATA_PATH);
        if (initialDir.exists() && initialDir.isDirectory()) {
            fileChooser.setInitialDirectory(initialDir);
        }

        return fileChooser.showOpenDialog(owner);
    }

    protected File openFileChooserByType(Window owner, FileType type) {
        String extension = type.name().toLowerCase();
        String description = getFileTypeDescription(type);
        return openFileChooser(owner, "Abrir Archivo " + extension, 
                description, "*." + extension, "All Files", "*.*");
    }
}
