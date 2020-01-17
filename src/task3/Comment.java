package task3;

import java.util.*;
import org.neo4j.ogm.annotation.*;

@NodeEntity
public class Comment {
	@Id @GeneratedValue Long id;
	@Property private String text;
	@Property private String date;
	
	@Relationship(type = "WROTE", direction = Relationship.INCOMING)
	private Student stud;
	
	@Relationship(type = "ABOUT", direction = Relationship.OUTGOING)
	private Subject subj;
        
    //private static String format = "yyyy-MM-dd HH:mm:ss";
        
	public Comment() {}

	public Comment(String t, Date d) {
		text = t;
		date = d.toString();//new SimpleDateFormat(format).format(d);
	}

	public void setText(String text) { this.text = text; }

	public void setDate(Date date) {
		this.date = date.toString();//new SimpleDateFormat(format).format(date);
	}

	public String getText() { return text; }

	public String getDate() { return date; }
	
	public Subject getSubj() { return subj; }

	public void setSubj(Subject subj) { this.subj = subj; }

	public Student getStud() { return stud; }

	public void setStud(Student stud) { this.stud = stud; }
}
