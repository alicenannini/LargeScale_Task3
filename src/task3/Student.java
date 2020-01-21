package task3;

import javafx.beans.property.*;

public class Student{
	private final SimpleIntegerProperty id;
    private final SimpleBooleanProperty admin;
    private final SimpleStringProperty username;
    private final SimpleStringProperty password;
    private final SimpleObjectProperty<Degree> degree;
    
    // CONSTRUCTOR
    public Student(int i, String u, String p, Degree d, boolean a) {
        id = new SimpleIntegerProperty(i);
        admin = new SimpleBooleanProperty(a);
        username = new SimpleStringProperty(u);
        password = new SimpleStringProperty(p);
        degree = new SimpleObjectProperty<Degree>(d);		
    }
    
    public int getId(){ return id.get(); }
    
    public void setId(int i){ id.set(i); }
    
    public boolean getAdmin() { return admin.get(); }
    
    public void setAdmin(Boolean b) { admin.set(b); }
    
    public String getUsername() { return username.get(); }
    
    public void setUsername(String n) { username.set(n); }
    
    public String getPassword() { return password.get(); }
    
    public void setPassword(String n) { password.set(n); }

    public Degree getDegree() { return degree.getValue(); }
    
    public void setDegree(Degree d) { degree.set(d); }
	
}