package aed.elrincon.view;

import aed.elrincon.model.Student;
import aed.elrincon.model.StudentList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javax.xml.bind.*;
import java.io.File;

public class StudentView {

    @FXML private TableView<Student> tableStudents;
    @FXML private TableColumn<Student, String> colNombre;
    @FXML private TableColumn<Student, String> colApellido;
    @FXML private TableColumn<Student, String> colEdad;
    @FXML private TableColumn<Student, String> colMatricula;

    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private TextField txtEdad;
    @FXML private TextField txtMatricula;

    private final ObservableList<Student> students = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colNombre.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getNombre()));
        colApellido.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getApellido()));
        colEdad.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getEdad()));
        colMatricula.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getMatricula()));
        tableStudents.setItems(students);
    }

    // -------- CRUD --------

    @FXML
    private void addStudent() {
        Student s = new Student(
                txtNombre.getText(),
                txtApellido.getText(),
                txtEdad.getText(),
                txtMatricula.getText()
        );
        students.add(s);
        clearFields();
    }

    @FXML
    private void deleteStudent() {
        Student s = tableStudents.getSelectionModel().getSelectedItem();
        if (s != null) {
            students.remove(s);
        }
    }

    private void clearFields() {
        txtNombre.clear();
        txtApellido.clear();
        txtEdad.clear();
        txtMatricula.clear();
    }

    // -------- XML --------

    @FXML
    private void saveXML() {
        try {
            JAXBContext context = JAXBContext.newInstance(StudentList.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(new StudentList(students), new File("students.xml"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void loadXML() {
        try {
            JAXBContext context = JAXBContext.newInstance(StudentList.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            StudentList list = (StudentList) unmarshaller.unmarshal(new File("students.xml"));
            students.setAll(list.getStudents());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

