package task3;

import java.util.*;
import javafx.beans.property.*;

public class Subject {

	private final SimpleIntegerProperty id;
	private final SimpleStringProperty name;
	private final SimpleIntegerProperty credits;
	private final SimpleStringProperty info;
	private final SimpleIntegerProperty degree;
	private final SimpleStringProperty professors;
	
	// CONSTRUCTOR
	public Subject(int i, String n, int c, String inf, int d) {
		id = new SimpleIntegerProperty(i);
		name = new SimpleStringProperty(n);
		credits = new SimpleIntegerProperty(c);
		info = new SimpleStringProperty(inf);
		degree = new SimpleIntegerProperty(d);
		professors = new SimpleStringProperty("");
	}
	
	public int getId() { return id.getValue(); }
	public void setId(int i) { id.set(i); }
	
	public String getName() { return name.getValue(); }
	public void setName(String n) { name.set(n); }
	
	public int getCredits() { return credits.getValue(); }
	public void setCredits(int c) { credits.set(c); }
	
	public String getInfo() { return info.getValue(); }
	public void setInfo(String i) { info.set(i); }
	
	public int getDegree() { return degree.getValue(); }
	public void setDegree(int d) { degree.set(d); }
	
	public String getProfessors(){ return professors.getValue(); }
	public void setProfessors(String profs) { professors.set(profs);; }
}
	