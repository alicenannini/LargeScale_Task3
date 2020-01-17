package task3;

import java.util.*;
import org.neo4j.ogm.annotation.*;

@NodeEntity
public class Subject {
	@Id @GeneratedValue Long id;
	@Property private String name;
	@Property("cfu") private int credits;
	@Property private String info;
	
	@Relationship(type = "COVERED IN", direction = Relationship.OUTGOING)
	private Degree deg;
	
	@Relationship(type = "ABOUT", direction = Relationship.INCOMING)
	private List<Comment> comments = new ArrayList<Comment>();
	
	@Relationship(type = "TEACHES", direction = Relationship.INCOMING)
	private Set<Professor> professors = new HashSet<Professor>();
	
	public Subject() {}

	// CONSTRUCTOR
	public Subject(String n, int c, String inf) {
		name = n;
		credits = c;
		info = inf;
	}
	
	public void removeProf(Professor prof) {
        this.professors.remove(prof);
        prof.getSubject().remove(this);
    }

	public Set<Professor> getProfessors() { return professors; }

	public void setProfessors(Set<Professor> professors) { this.professors = professors; }

	public List<Comment> getComments() { return comments; }

	public void setComments(List<Comment> comments) { this.comments = comments; }
	
	public String getName() { return name; }

	public void setName(String name) { this.name = name; }

	public int getCredits() { return credits; }

	public void setCredits(int credits) { this.credits = credits; }
	
	public String getInfo() { return info; }

	public void setInfo(String info) { this.info = info; }

	public Degree getDeg() { return deg; }

	public void setDeg(Degree deg) { this.deg = deg; }

}
