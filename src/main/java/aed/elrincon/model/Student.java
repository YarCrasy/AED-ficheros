package aed.elrincon.model;

import java.io.Serializable;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "student")
@XmlAccessorType(XmlAccessType.FIELD)
public class Student implements Serializable {

    private static final long serialVersionUID = 1L;
    private String nombre;
    private String apellido;
    private String edad;
    private String matricula;

    public Student() {}
    public Student(String nombre, String apellido, String edad, String matricula) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.edad = edad;
        this.matricula = matricula;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getEdad() { return edad; }
    public void setEdad(String edad) {  this.edad = edad; }

    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }

    private boolean isBlank(String value) { return value == null || value.trim().isEmpty(); }
    public boolean isValid() { return isValid(null); }
    public boolean isValid(java.util.Set<String> existingMatriculas) {
        if (isBlank(nombre) || isBlank(apellido) || isBlank(edad) || isBlank(matricula)) return false;

        if (existingMatriculas != null && existingMatriculas.contains(matricula.trim())) return false;

        try {
            int age = Integer.parseInt(edad.trim());
            return age >= 0 && age <= 200;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}

