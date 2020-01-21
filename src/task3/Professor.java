package task3;

import javafx.beans.property.*;

public class Professor{

    private final SimpleIntegerProperty id;
    private final SimpleStringProperty name;
    private final SimpleStringProperty surname;
	
    // CONSTRUCTOR
    public Professor(int i, String n, String s) {
        name = new SimpleStringProperty(n);
        surname = new SimpleStringProperty(s);
        id = new SimpleIntegerProperty(i);
    }
    
    public int getId(){ return id.get(); }
    
    public void setId(int i){ id.set(i); }
    
    public String getName() { return name.get(); }
    
    public void setName(String n) { name.set(n); }

    public String getSurname() { return surname.get(); }
    
    public void setSurname(String s) { surname.set(s); }
    
    @Override
    public String toString() {
    	return getName() +" "+ getSurname();
    }

}
	