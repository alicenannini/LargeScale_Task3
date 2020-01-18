package task3;

import java.util.*;
import javafx.beans.property.*;

public class Degree {

	private final SimpleIntegerProperty id;
    private final SimpleStringProperty name;
	private List<Student> students = new ArrayList<Student>();
	private List<Subject> subjects = new ArrayList<Subject>();
    
    public Degree(int i, String n) {
        id = new SimpleIntegerProperty(i);
        name = new SimpleStringProperty(n);
    }
    
    public int getId(){ return id.get(); }
    public void setId(int i){ id.set(i); }
    public String getName(){ return name.get(); }
    public void setName(String n){ name.set(n); }
    
    @Override
    public String toString(){
        return getName();
    }
}
