package task3;

import java.util.List;

import javafx.beans.property.*;
import javafx.collections.ObservableList;

public class Student{
	private final SimpleIntegerProperty id;
    private final SimpleBooleanProperty admin;
    private final SimpleStringProperty username;
    private final SimpleObjectProperty<Degree> degree;
    private final SimpleListProperty<Student> friends;
    
    // CONSTRUCTOR
    public Student(int i, String u, Degree d, boolean a) {
        id = new SimpleIntegerProperty(i);
        admin = new SimpleBooleanProperty(a);
        username = new SimpleStringProperty(u);
        degree = new SimpleObjectProperty<Degree>(d);
        friends = new SimpleListProperty<Student>(null);
    }
    //GETTERS AND SETTERS
    
    public int getId(){ return id.get(); }
    
    public void setId(int i){ id.set(i); }
    
    public boolean getAdmin() { return admin.get(); }
    
    public void setAdmin(Boolean b) { admin.set(b); }
    
    public String getUsername() { return username.get(); }
    
    public void setUsername(String n) { username.set(n); }

    public Degree getDegree() { return degree.getValue(); }
    
    public void setDegree(Degree d) { degree.set(d); }
    
    public List<Student> getFriends(){ return friends.getValue(); }
    
	public void setFriends(List<Student> f) { 
		friends.set((ObservableList<Student>) f); 
	}
	
	@Override
	public String toString() {
		return this.getUsername();
	}
}