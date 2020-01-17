package task3;

import java.util.*;
import org.neo4j.ogm.annotation.*;

@NodeEntity
public class Student {
	@Id @GeneratedValue Long id;
	@Property private final boolean admin;
	@Property private String username;
	@Property private String password;

	// relation with Degree
	@Relationship(type = "ATTENDS", direction = Relationship.OUTGOING)
	private Degree deg;

	// relation with Comments
	@Relationship(type = "WROTE", direction = Relationship.OUTGOING)
	private List<Comment> comments = new ArrayList<Comment>();

	public Student() {
		admin = false;
		username = "";
	}

	public Student(int i, String u, String p, boolean a) {
		this.admin = a;
		this.username = u;
		this.password = p;
	}

	public List<Comment> getComments() { return comments; }

	public void setSubjectComments(List<Comment> comments) {
		this.comments = comments;
	}

	public Degree getDeg() { return deg; }

	public void setDeg(Degree deg) { this.deg = deg; }

	public Student getStudent() { return this; }

	public boolean getAdmin() { return admin; }

	public String getUsername() { return username; }

	public String getPassword() { return password; }

	public void setPassword(String password) { this.password = password; }

	public void setUsername(String username) { this.username = username; }
}
