package task3;

import java.util.*;
import java.util.Map.*;

import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.types.*;

public class DbManager implements AutoCloseable {
	private final Driver driver;
	private final String uri = "bolt://localhost:7687";
	private final String user = "alice";
	private final String password = "alice";
	
	public DbManager() {
		driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
	}

	
	public void createDegree(String name) {
		try(Session session = driver.session(AccessMode.WRITE)){
			session.run( "CREATE (a:Degree {name: $name});", 
					Values.parameters("name",name) );
		}
	}
	
	public void createProfessor(String name, String surname) {
		
		try(Session session = driver.session(AccessMode.WRITE)){
			session.run( "CREATE (a:Professor {name: $name, surname: $surname});", 
					Values.parameters("name",name,"surname",surname) );
		}
	}
	
	public void createSubject(String name, int credits, String info, int profId, int degreeId) {
		try(Session session = driver.session(AccessMode.WRITE)){
			session.run( 	"MATCH (p:Professor) WHERE id(p) = $profId " + 
							"MATCH (d:Degree) WHERE id(d) = $degreeId " +
							"CREATE (a:Subject {name:$name, cfu:$cfu, info:$info})," +
							"(p)-[:THEACHES]->(a), (a)-[:BELONGS]->(d);", 
					Values.parameters("name",name,"cfu",credits,"info",info,"profId",profId,"degreeId",degreeId) );
		}
	}
	
	public void createComment(String text, Date date, Student student, int subjectId) {
		try(Session session = driver.session(AccessMode.WRITE)){
			session.run( 	"MATCH (e:Student) WHERE id(e) = $idStudent " + 
							"MATCH (s:Subject) WHERE id(s) = $idSubject " +
							"CREATE " + 
							"(a:Comment {text: $text, date: $date})," + 
							"(e)-[:WROTE]->(a)," + 
							"(a)-[:ABOUT]->(s);", 
					Values.parameters("idStudent",student.getId(),"idSubject",subjectId,"text",text,"date",date.toString()) );
		}
	}
	
	public Student checkUser(String username, String password) {
		Student student = null;
		
		try(Session session = driver.session(AccessMode.WRITE)){
			StatementResult sr = session.run( 
					"MATCH (s:Student) WHERE s.username = $username AND s.password = $password "+
					"RETURN ID(s),s.username,s.password,s.admin;", 
					Values.parameters("username",username,"password",password) );
						
			if(sr.hasNext()) {
				Record r = sr.next();
				if(sr.hasNext()) {
					System.err.println("In the Database are present more than one user with the same username");
				}else {
					System.out.println(r.toString());
					student = new Student(r.get("ID(s)").asInt(),r.get("s.username").asString(),
							r.get("s.password").asString(),new Degree(0,""),r.get("s.admin").asBoolean());
				}
			}
		}
		
		return student;
	}
	
	public List<Degree> getDegreeCourses() {
		List<Degree> list = new ArrayList<>();
		
		try(Session session = driver.session(AccessMode.READ)){
			StatementResult sr = session.run( "MATCH (dd:Degree) RETURN ID(dd), dd.name;");
		
			while(sr.hasNext()) {
				Record r = sr.next();
				System.out.println(r.toString());
				
				list.add(new Degree(r.get("ID(dd)").asInt(),r.get("dd.name").asString()));
			}
			/*Map<String,Object> row = sr.next().asMap();
            for ( Entry<String, Object> column : row.entrySet() )
            {
            	System.out.println( column.getKey() + ": " + column.getValue().toString() + "; " );
            }*/
		}
		return list;
	}
	
	public static void main(String[] args) {
		DbManager m = new DbManager();
		
		//m.createComment("commento", new Date(), new Student(1,"A","A",new Degree(0,"D"),false), 34);
		m.createSubject("Prova", 12, "info", 21, 10);
		
		try {
			m.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	@Override
	public void close() throws Exception {
		driver.close();
	}
}
