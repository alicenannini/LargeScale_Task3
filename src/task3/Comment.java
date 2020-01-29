package task3;

import java.util.*;
import javafx.beans.property.*;

public class Comment{

    private final SimpleIntegerProperty id;
    private final SimpleStringProperty text;
    private final SimpleObjectProperty<Student> student;
    private final SimpleStringProperty date;
    private final SimpleIntegerProperty subject;

    public Comment(int i, String t, Student s, int sub, String d) {
        id = new SimpleIntegerProperty(i);
        text = new SimpleStringProperty(t);
        student = new SimpleObjectProperty<Student>(s);
        date = new SimpleStringProperty(d);
        subject = new SimpleIntegerProperty(sub);
    }
    
    public int getId(){ return id.get(); }
    public String getText(){ return text.get(); }
    public Student getStudent(){ return student.get(); }
    public String getDate(){ return date.get(); }
    public int getSubject(){ return subject.get(); }
    public void setSubject(int s){ subject.set(s); }
    public void setId(int i){ id.set(i); }
    public void setText(String t){ text.set(t); }
    public void setStudent(Student s){ student.set(s); }
    public void setDate(Date d){ date.set(d.toString()); }
}	