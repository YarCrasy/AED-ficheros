package aed.elrincon.model;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "students")
@XmlAccessorType(XmlAccessType.FIELD)
public class StudentList {

    @XmlElement(name = "student")
    private List<Student> students;

    public StudentList() {}

    public StudentList(List<Student> students) {
        this.students = students;
    }

    public List<Student> getStudents() {
        return students;
    }
}
