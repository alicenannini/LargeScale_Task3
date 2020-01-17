package task3;

import java.util.*;
import org.neo4j.ogm.annotation.*;

@NodeEntity
public class Professor {
	@Id @GeneratedValue Long id;
	@Property private String name;
	@Property private String surname;
	
	@Relationship(type = "TEACHES", direction = Relationship.OUTGOING)
	private Set<Subject> subject = new HashSet<Subject>();
	
	public Professor() {}

	// CONSTRUCTOR
	public Professor(String n, String s) {
		name = n;
		surname = s;
	}

	public Set<Subject> getSubject() { return subject; }

	public void setSubject(Set<Subject> subject) { this.subject = subject; }

	public void setName(String name) { this.name = name; }

	public void setSurname(String surname) { this.surname = surname; }

	public String getName() { return name; }

	public String getSurname() { return surname; }

}
