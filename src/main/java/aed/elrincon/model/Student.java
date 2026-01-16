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
<<<<<<< HEAD
=======

>>>>>>> adriBien-v
    public Student(String nombre, String apellido, String edad, String matricula) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.edad = edad;
        this.matricula = matricula;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
<<<<<<< HEAD

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getEdad() { return edad; }
    public void setEdad(String edad) {  this.edad = edad; }

=======
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getEdad() { return edad; }
    public void setEdad(String edad) { this.edad = edad; }
>>>>>>> adriBien-v
    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }
}

