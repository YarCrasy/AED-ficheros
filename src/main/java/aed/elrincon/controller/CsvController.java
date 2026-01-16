package aed.elrincon.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import aed.elrincon.App;
import aed.elrincon.model.Student;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;

/**
 * Controlador principal para la gestión de estudiantes en formato CSV.
 * Esta clase maneja todas las operaciones CRUD y de archivos CSV.
 */
public class CsvController implements Initializable {

    // Referencias a los elementos de la interfaz gráfica
    @FXML private TableView<Student> tableView;
    @FXML private TableColumn<Student, String> colNombre;
    @FXML private TableColumn<Student, String> colApellido;
    @FXML private TableColumn<Student, String> colEdad;
    @FXML private TableColumn<Student, String> colMatricula;

    // Botones de la interfaz
    @FXML private Button buttonAnadir;
    @FXML private Button buttonEditar;
    @FXML private Button buttonEliminar;
    @FXML private Button buttonGuardar;
    @FXML private Button buttonCargar;
    @FXML private Button buttonVolver;

    // Etiquetas informativas
    @FXML private Label labelContador;
    @FXML private Label labelEstado;

    // Lista observable que contiene los estudiantes
    private ObservableList<Student> listaEstudiantes = FXCollections.observableArrayList();

    /**
     * Método de inicialización que se ejecuta al cargar la vista.
     * Configura la tabla, carga datos de ejemplo y establece los eventos.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Primero configuro la tabla con las columnas correspondientes
        configurarTabla();

        // Luego cargo algunos datos de ejemplo para mostrar la funcionalidad
        cargarDatosEjemplo();

        // Configuro los eventos de los botones
        configurarEventos();

        // Configuro el comportamiento de selección en la tabla
        configurarSeleccionTabla();

        // Actualizo el contador de estudiantes
        actualizarContador();

        // Muestro un estado inicial
        mostrarEstado("Sistema listo. Puedes comenzar a gestionar estudiantes.");
    }

    /**
     * Configura las columnas de la tabla y sus propiedades.
     * Aquí defino qué datos se muestran en cada columna.
     */
    private void configurarTabla() {
        // Configuro cada columna para que se vincule con una propiedad del modelo Student
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colEdad.setCellValueFactory(new PropertyValueFactory<>("edad"));
        colMatricula.setCellValueFactory(new PropertyValueFactory<>("matricula"));

        // Defino anchos preferidos para una mejor visualización
        colNombre.setPrefWidth(200);
        colApellido.setPrefWidth(200);
        colEdad.setPrefWidth(100);
        colMatricula.setPrefWidth(150);

        // Establezco un mensaje cuando la tabla está vacía
        tableView.setPlaceholder(new Label("No hay estudiantes registrados. Puedes añadir nuevos estudiantes o cargar un archivo CSV."));

        // Vinculo la tabla con la lista de estudiantes
        tableView.setItems(listaEstudiantes);
    }

    /**
     * Configura los eventos para cada botón de la interfaz.
     * Cada botón ejecuta una acción específica cuando se hace clic.
     */
    private void configurarEventos() {
        buttonAnadir.setOnAction(e -> anadirEstudiante());
        buttonEditar.setOnAction(e -> editarEstudiante());
        buttonEliminar.setOnAction(e -> eliminarEstudiante());
        buttonGuardar.setOnAction(e -> guardarCSV());
        buttonCargar.setOnAction(e -> cargarCSV());
        buttonVolver.setOnAction(e -> volverAlMenu());

        // Agrego tooltips descriptivos a cada botón
        buttonAnadir.setTooltip(new Tooltip("Agregar un nuevo estudiante a la lista"));
        buttonEditar.setTooltip(new Tooltip("Modificar los datos del estudiante seleccionado"));
        buttonEliminar.setTooltip(new Tooltip("Eliminar el estudiante seleccionado de la lista"));
        buttonGuardar.setTooltip(new Tooltip("Guardar todos los estudiantes en un archivo CSV"));
        buttonCargar.setTooltip(new Tooltip("Cargar estudiantes desde un archivo CSV existente"));
        buttonVolver.setTooltip(new Tooltip("Regresar al menú principal de la aplicación"));
    }

    /**
     * Configura el comportamiento de selección en la tabla.
     * Los botones de editar y eliminar se activan solo cuando hay un estudiante seleccionado.
     */
    private void configurarSeleccionTabla() {
        tableView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    // Solo habilito los botones de editar y eliminar si hay un estudiante seleccionado
                    buttonEditar.setDisable(newSelection == null);
                    buttonEliminar.setDisable(newSelection == null);

                    if (newSelection != null) {
                        mostrarEstado("Seleccionado: " + newSelection.getNombre() + " " + newSelection.getApellido());
                    }
                });

        // Inicialmente deshabilito los botones porque no hay selección
        buttonEditar.setDisable(true);
        buttonEliminar.setDisable(true);
    }

    /**
     * Carga datos de ejemplo para demostrar la funcionalidad de la aplicación.
     * Estos datos se muestran cuando se inicia la vista por primera vez.
     */
    private void cargarDatosEjemplo() {
        listaEstudiantes.add(new Student("Juan", "Pérez García", "20", "A12345"));
        listaEstudiantes.add(new Student("María", "Gómez López", "22", "B67890"));
        listaEstudiantes.add(new Student("Carlos", "López Martínez", "19", "C24680"));
        listaEstudiantes.add(new Student("Ana", "Rodríguez Sánchez", "21", "D13579"));
        listaEstudiantes.add(new Student("Pedro", "García Fernández", "23", "E97531"));

        mostrarEstado("Datos de ejemplo cargados. Total: " + listaEstudiantes.size() + " estudiantes");
    }

    /**
     * Agrega un nuevo estudiante a la lista.
     * Muestra un diálogo para ingresar los datos del estudiante.
     */
    private void anadirEstudiante() {
        Optional<Student> resultado = mostrarDialogoEstudiante("Agregar Nuevo Estudiante", null);

        resultado.ifPresent(estudiante -> {
            if (!estudiante.getNombre().trim().isEmpty()) {
                listaEstudiantes.add(estudiante);
                // Desplazo la tabla hasta el nuevo estudiante
                tableView.scrollTo(estudiante);
                // Selecciono automáticamente el nuevo estudiante
                tableView.getSelectionModel().select(estudiante);
                mostrarAlerta("Operación Exitosa", "El estudiante ha sido agregado correctamente.", Alert.AlertType.INFORMATION);
                actualizarContador();
                mostrarEstado("Estudiante agregado: " + estudiante.getNombre() + " " + estudiante.getApellido());
            }
        });
    }

    /**
     * Edita los datos de un estudiante existente.
     * Requiere que haya un estudiante seleccionado en la tabla.
     */
    private void editarEstudiante() {
        Student seleccionado = tableView.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Selección Requerida", "Por favor, selecciona un estudiante para editar.", Alert.AlertType.WARNING);
            return;
        }

        Optional<Student> resultado = mostrarDialogoEstudiante("Editar Estudiante", seleccionado);

        resultado.ifPresent(estudianteEditado -> {
            if (!estudianteEditado.getNombre().trim().isEmpty()) {
                int indice = listaEstudiantes.indexOf(seleccionado);
                listaEstudiantes.set(indice, estudianteEditado);
                // Actualizo la visualización de la tabla
                tableView.refresh();
                mostrarAlerta("Operación Exitosa", "Los datos del estudiante han sido actualizados.", Alert.AlertType.INFORMATION);
                mostrarEstado("Estudiante modificado: " + estudianteEditado.getNombre());
            }
        });
    }

    /**
     * Elimina un estudiante de la lista.
     * Solicita confirmación antes de realizar la eliminación.
     */
    private void eliminarEstudiante() {
        Student seleccionado = tableView.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Selección Requerida", "Por favor, selecciona un estudiante para eliminar.", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText("¿Estás seguro de eliminar este estudiante?");
        confirmacion.setContentText("Estudiante: " + seleccionado.getNombre() + " " + seleccionado.getApellido() +
                "\nMatrícula: " + seleccionado.getMatricula());

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            listaEstudiantes.remove(seleccionado);
            mostrarAlerta("Operación Exitosa", "El estudiante ha sido eliminado correctamente.", Alert.AlertType.INFORMATION);
            actualizarContador();
            mostrarEstado("Estudiante eliminado: " + seleccionado.getNombre());
        }
    }

    /**
     * Guarda la lista de estudiantes en un archivo CSV.
     * Permite al usuario seleccionar la ubicación y nombre del archivo.
     */
    private void guardarCSV() {
        if (listaEstudiantes.isEmpty()) {
            mostrarAlerta("Lista Vacía", "No hay estudiantes para guardar en el archivo.", Alert.AlertType.WARNING);
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Archivo CSV");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Archivos CSV", "*.csv"),
                new FileChooser.ExtensionFilter("Todos los archivos", "*.*")
        );
        fileChooser.setInitialFileName("estudiantes_" + System.currentTimeMillis() + ".csv");

        File archivo = fileChooser.showSaveDialog(tableView.getScene().getWindow());

        if (archivo != null) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(archivo))) {
                // Escribo la cabecera del CSV
                writer.println("Nombre,Apellido,Edad,Matricula");

                // Escribo cada estudiante como una línea en el CSV
                for (Student estudiante : listaEstudiantes) {
                    writer.println(String.format("%s,%s,%s,%s",
                            escapeCsv(estudiante.getNombre()),
                            escapeCsv(estudiante.getApellido()),
                            estudiante.getEdad(),
                            estudiante.getMatricula()
                    ));
                }

                mostrarAlerta("Archivo Guardado",
                        "El archivo CSV ha sido guardado exitosamente:\n" +
                                "Ubicación: " + archivo.getAbsolutePath() + "\n" +
                                "Estudiantes guardados: " + listaEstudiantes.size(),
                        Alert.AlertType.INFORMATION);

                mostrarEstado("Archivo CSV guardado: " + archivo.getName());

            } catch (IOException e) {
                mostrarAlerta("Error al Guardar",
                        "No se pudo guardar el archivo:\n" + e.getMessage(),
                        Alert.AlertType.ERROR);
                mostrarEstado("Error al guardar el archivo CSV");
            }
        }
    }

    /**
     * Carga estudiantes desde un archivo CSV.
     * Reemplaza la lista actual con los datos del archivo.
     */
    private void cargarCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Archivo CSV");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Archivos CSV", "*.csv"),
                new FileChooser.ExtensionFilter("Todos los archivos", "*.*")
        );

        File archivo = fileChooser.showOpenDialog(tableView.getScene().getWindow());

        if (archivo != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
                ObservableList<Student> nuevosEstudiantes = FXCollections.observableArrayList();
                String linea;
                int lineasProcesadas = 0;
                boolean tieneCabecera = false;

                while ((linea = reader.readLine()) != null) {
                    // Salto líneas vacías
                    if (linea.trim().isEmpty()) continue;

                    // La primera línea no vacía es la cabecera
                    if (!tieneCabecera) {
                        tieneCabecera = true;
                        continue;
                    }

                    // Proceso cada línea del CSV
                    String[] campos = parseCsvLine(linea);

                    if (campos.length >= 4) {
                        nuevosEstudiantes.add(new Student(
                                campos[0].trim(),
                                campos[1].trim(),
                                campos[2].trim(),
                                campos[3].trim()
                        ));
                        lineasProcesadas++;
                    }
                }

                if (!nuevosEstudiantes.isEmpty()) {
                    listaEstudiantes.setAll(nuevosEstudiantes);

                    mostrarAlerta("Archivo Cargado",
                            "El archivo CSV ha sido cargado exitosamente:\n" +
                                    "Archivo: " + archivo.getName() + "\n" +
                                    "Estudiantes cargados: " + lineasProcesadas,
                            Alert.AlertType.INFORMATION);

                    mostrarEstado("Archivo CSV cargado: " + archivo.getName());
                    actualizarContador();

                } else {
                    mostrarAlerta("Archivo Vacío",
                            "El archivo seleccionado no contiene datos válidos de estudiantes.",
                            Alert.AlertType.WARNING);
                    mostrarEstado("El archivo CSV está vacío o tiene formato incorrecto");
                }

            } catch (IOException e) {
                mostrarAlerta("Error al Cargar",
                        "No se pudo cargar el archivo:\n" + e.getMessage(),
                        Alert.AlertType.ERROR);
                mostrarEstado("Error al cargar el archivo CSV");
            }
        }
    }

    /**
     * Muestra un diálogo para ingresar o editar datos de un estudiante.
     * @param titulo El título del diálogo
     * @param estudianteExistente El estudiante a editar, o null para uno nuevo
     * @return Optional con el estudiante creado o editado
     */
    private Optional<Student> mostrarDialogoEstudiante(String titulo, Student estudianteExistente) {
        Dialog<Student> dialog = new Dialog<>();
        dialog.setTitle(titulo);
        dialog.setHeaderText("Ingresa los datos del estudiante");

        // Creo los campos de texto para cada dato
        TextField txtNombre = new TextField();
        TextField txtApellido = new TextField();
        TextField txtEdad = new TextField();
        TextField txtMatricula = new TextField();

        // Configuro validación para que la edad solo acepte números
        txtEdad.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtEdad.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        // Si estoy editando un estudiante existente, cargo sus datos
        if (estudianteExistente != null) {
            txtNombre.setText(estudianteExistente.getNombre());
            txtApellido.setText(estudianteExistente.getApellido());
            txtEdad.setText(estudianteExistente.getEdad());
            txtMatricula.setText(estudianteExistente.getMatricula());
        }

        // Creo el layout del diálogo
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 10, 10, 10));

        grid.add(new Label("Nombre:"), 0, 0);
        grid.add(txtNombre, 1, 0);
        grid.add(new Label("Apellido:"), 0, 1);
        grid.add(txtApellido, 1, 1);
        grid.add(new Label("Edad:"), 0, 2);
        grid.add(txtEdad, 1, 2);
        grid.add(new Label("Matrícula:"), 0, 3);
        grid.add(txtMatricula, 1, 3);

        // Agrego placeholders para guiar al usuario
        txtNombre.setPromptText("Ejemplo: Juan");
        txtApellido.setPromptText("Ejemplo: Pérez");
        txtEdad.setPromptText("Ejemplo: 20");
        txtMatricula.setPromptText("Ejemplo: A12345");

        dialog.getDialogPane().setContent(grid);

        // Configuro los botones del diálogo
        ButtonType btnAceptar = new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnAceptar, btnCancelar);

        // Configuro la conversión de resultados
        dialog.setResultConverter(buttonType -> {
            if (buttonType == btnAceptar) {
                String nombre = txtNombre.getText().trim();
                String apellido = txtApellido.getText().trim();
                String edad = txtEdad.getText().trim();
                String matricula = txtMatricula.getText().trim();

                // Valido que todos los campos estén completos
                if (nombre.isEmpty() || apellido.isEmpty() || edad.isEmpty() || matricula.isEmpty()) {
                    mostrarAlerta("Datos Incompletos", "Todos los campos son obligatorios.", Alert.AlertType.ERROR);
                    return null;
                }

                // Valido que la edad sea un número válido
                try {
                    int edadNum = Integer.parseInt(edad);
                    if (edadNum < 16 || edadNum > 80) {
                        mostrarAlerta("Edad Inválida", "La edad debe estar entre 16 y 80 años.", Alert.AlertType.ERROR);
                        return null;
                    }
                } catch (NumberFormatException e) {
                    mostrarAlerta("Edad Inválida", "La edad debe ser un número válido.", Alert.AlertType.ERROR);
                    return null;
                }

                // Si todas las validaciones pasan, creo el estudiante
                return new Student(nombre, apellido, edad, matricula);
            }
            return null;
        });

        return dialog.showAndWait();
    }

    /**
     * Vuelve al menú principal de la aplicación.
     */
    private void volverAlMenu() {
        try {
            App.setRoot("launcher");
        } catch (IOException e) {
            mostrarAlerta("Error de Navegación",
                    "No se pudo cargar el menú principal: " + e.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    /**
     * Muestra una alerta con un mensaje al usuario.
     * @param titulo El título de la alerta
     * @param mensaje El mensaje a mostrar
     * @param tipo El tipo de alerta (información, advertencia, error)
     */
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Actualiza la etiqueta de estado con un mensaje.
     * @param mensaje El mensaje a mostrar en la etiqueta de estado
     */
    private void mostrarEstado(String mensaje) {
        if (labelEstado != null) {
            labelEstado.setText("Estado: " + mensaje);
        }
    }

    /**
     * Actualiza el contador de estudiantes.
     * Muestra el número actual de estudiantes en la lista.
     */
    private void actualizarContador() {
        if (labelContador != null) {
            labelContador.setText("Total de estudiantes: " + listaEstudiantes.size());
        }
    }

    /**
     * Escapa un valor para formato CSV.
     * Si el valor contiene comas o comillas, lo envuelve en comillas.
     * @param value El valor a escapar
     * @return El valor escapado para CSV
     */
    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    /**
     * Parsea una línea de CSV, manejando comillas correctamente.
     * @param line La línea de texto CSV
     * @return Array con los campos parseados
     */
    private String[] parseCsvLine(String line) {
        java.util.List<String> fields = new java.util.ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(currentField.toString());
                currentField = new StringBuilder();
            } else {
                currentField.append(c);
            }
        }

        fields.add(currentField.toString());
        return fields.toArray(new String[0]);
    }
}