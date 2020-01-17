package task3;

import java.util.*;
import org.neo4j.ogm.annotation.*;

@NodeEntity
public class Degree {
	@Id @GeneratedValue Long id;
	@Property private String name;
	@Relationship(type = "ATTENDS", direction = Relationship.INCOMING)
	private List<Student> students = new ArrayList<Student>();
	@Relationship(type = "COVERED IN", direction = Relationship.INCOMING)
	private List<Subject> subjects = new ArrayList<Subject>();

	public Degree() {}

	public Degree(int i, String n) {
		name = n;
	}

	public List<Student> getStudent() { return students; }

	public void setStudent(List<Student> student) { this.students = student; }

	public List<Subject> getSubject() { return subjects; }

	public void setSubject(List<Subject> subjects) { this.subjects = subjects; }

	public String getName() { return name; }

	public void setName(String name) { this.name = name; }

	@Override
	public String toString() {
		return getName();
	}
}
