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
	
	public void createSubject(String name, int credits, String info, String profIdStr, int degreeId) {
		String[] professorsId = profIdStr.split(",", 5);
		
		try(Session session = driver.session(AccessMode.WRITE)){
			StatementResult sr = session.run( 	
							"MATCH (d:Degree) WHERE id(d) = $degreeId " +
							"CREATE (a:Subject {name:$name, cfu:$cfu, info:$info})," +
							"(a)-[:BELONGS]->(d) RETURN ID(a);", 
					Values.parameters("name",name,"cfu",credits,"info",info,"degreeId",degreeId) );
			
			if(sr.hasNext()) {
				for (String p : professorsId) {
					int profId = Integer.parseInt(p);
					if(profId > 0) {
						createTeachingRelation(profId, sr.next().get("ID(a)").asInt());
					}
				}
			}
		}
	}
	
	public void createTeachingRelation(int profId, int subjectId) {
		try(Session session = driver.session(AccessMode.WRITE)){
			session.run( 	"MATCH (p:Professor) WHERE id(p) = $profId " + 
							"MATCH (s:Subject) WHERE id(s) = $subjectId " +
							"CREATE (p)-[:THEACHES]->(s);", 
					Values.parameters("profId",profId,"subjectId",subjectId) );
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
					Values.parameters("idStudent",student.getId(),"idSubject",subjectId,
							"text",text,"date",date.toString()) );
		}
	}
	
	public Student checkUser(String username, String password) {
		Student student = null;
		
		try(Session session = driver.session(AccessMode.READ)){
			StatementResult sr = session.run( 
					"MATCH (s:Student)-[:ATTENDS]->(d:Degree) " +
					"WHERE s.username = $username AND s.password = $password "+
					"RETURN ID(s),s.username,s.password,s.admin, ID(d),d.name;", 
					Values.parameters("username",username,"password",password) );
						
			if(sr.hasNext()) {
				Record r = sr.next();
				if(sr.hasNext()) {
					System.err.println("In the Database are present more than one user with the same username");
				}else {
					System.out.println(r.toString());
					student = new Student(r.get("ID(s)").asInt(),r.get("s.username").asString(),
							r.get("s.password").asString(),new Degree(r.get("ID(d)").asInt(),
									r.get("d.name").asString()),r.get("s.admin").asBoolean());
				}
			}
		}
		return student;
	}
	
	public List<Professor> getProfessors(int subjectId) {

		List<Professor> list = new ArrayList<>();
		
		try(Session session = driver.session(AccessMode.READ)){
			StatementResult sr = session.run( 
					"MATCH (p:Professor)-[:TEACHES]->(s:Subject) \n" + 
					"WHERE id(s) = $subjectId \n" + 
					"RETURN ID(p),p.name,p.surname", 
					Values.parameters("subjectId",subjectId) );
			
			while(sr.hasNext()) {
				Record r = sr.next();
				System.out.println(r.toString());
				
				list.add(new Professor(r.get("ID(p)").asInt(),r.get("p.name").asString(),
						r.get("p.surname").asString()));
			}
		}		
		return list;
	}
	
	public List<Subject> getSubjects(int degree) {

		List<Subject> list = new ArrayList<>();
		
		try(Session session = driver.session(AccessMode.READ)){
			StatementResult sr = session.run( 
					"MATCH (s:Subject)-[:BELONGS]->(d:Degree) \n" + 
					"WHERE id(d) = $idDegree \n" + 
					"RETURN ID(s),s.name,s.cfu,s.info", 
					Values.parameters("idDegree",degree) );
			
			while(sr.hasNext()) {
				Record r = sr.next();
				System.out.println(r.toString());
				
				list.add(new Subject(r.get("ID(s)").asInt(),r.get("s.name").asString(),
						r.get("s.cfu").asInt(),r.get("s.info").asString(),degree));
				
				list.get(list.size()).setProfessors( this.getProfessors(r.get("ID(s)").asInt()) );
			}
		}
		return list;
	}
	
	public List<Comment> getComments(int subjectId) {
		List<Comment> list = new ArrayList<>();
		
		if (subjectId == -1)
			return list;
		
		try(Session session = driver.session(AccessMode.READ)){
			StatementResult sr = session.run( 
					"MATCH (s:Subject) WHERE id(s) = $idSubject\n" + 
					"MATCH (c:Comment)-[:ABOUT]->(s)\n" + 
					"MATCH (st:Student)-[:WROTE]->(c)" +
					"RETURN ID(c),c.text,c.date,ID(st);", 
					Values.parameters("idSubject",subjectId) );
			
			while(sr.hasNext()) {
				Record r = sr.next();
				System.out.println(r.toString());
				
				list.add(new Comment(r.get("ID(c)").asInt(),r.get("c.text").asString(),
						r.get("ID(st)").asInt(),subjectId,r.get("c.date").asString()));
			}
		}		
		return list;
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
		}
		return list;
	}
	
	public void deleteProfessor(int profId) {
		try(Session session = driver.session(AccessMode.WRITE)){
			session.run( "MATCH (n:Professor) WHERE id(n) = $idProf DETACH DELETE n;",
					Values.parameters("idProf",profId));
		}
	}
	
	public void deleteSubject(int subjectId) {
		try(Session session = driver.session(AccessMode.WRITE)){
			session.run( "MATCH (n:Subject) WHERE id(n) = $idSubject DETACH DELETE n;",
					Values.parameters("idSubject",subjectId));
		}
	}
	
	public boolean deleteComment(int commentId, int userId, boolean admin){
		try(Session session = driver.session(AccessMode.WRITE)){
		    StatementResult sr;
			if(admin){
		    	sr = session.run( "MATCH (c:Comment) WHERE id(c) = $idComment DETACH DELETE c RETURN id(c);",
		    			Values.parameters("idComment",commentId) );
		    }else{
		    	sr = session.run("MATCH (s:Student)-[:WROTE]->(c:Comment) " +
				        		"WHERE id(s) = $userId AND id(c) = $idComment " +
				        		"DETACH DELETE c RETURN id(c)",
		    			Values.parameters("userId",userId,"idComment",commentId) );
		    }
		    
		    if(sr.hasNext() && sr.next().get("id(c)").asInt() > 0)
		    	return true;
		    else return false;
		}
	}
	
	public boolean updateSubject(int subjectId, String name, int credits, String info, String profIdStr) {
		try(Session session = driver.session(AccessMode.WRITE)){
			
			session.run("MATCH (s:Subject) WHERE id(s) = $subjectId\n" + 
						"SET s.info = $info, s.name = $name, s.cfu = $cfu",
	    			Values.parameters("subjectId",subjectId,"info",info,"name",name,"cfu",credits) );
			
			if(!profIdStr.isEmpty()) {
				session.run("MATCH (p:Professor)-[r:TEACHES]->(s:Subject) " + 
							"WHERE id(s) = $subjectId DELETE r",
	    			Values.parameters("subjectId",subjectId) );
				
				String[] professorsId = profIdStr.split("," , 5);
				
				for(String p : professorsId) {
					int profId = Integer.parseInt(p);
					if (profId > 0) {
						createTeachingRelation(profId, subjectId);
					}
				}	
			}
		}
		
		return false;
	}
	
	public boolean updateComment(int commentId, String text, int userId) {
		try(Session session = driver.session(AccessMode.WRITE)){
			StatementResult sr = session.run(
						"MATCH (s:Student)-[:WROTE]->(c:Comment) " +
						"WHERE ID(s) = $userId AND ID(c) = $commentId " + 
						"SET c.text = $text, c.date = $date " +
						"RETURN ID(c);",
	    			Values.parameters("userId",userId,"commentId",commentId,
	    					"text",text,"date",new Date().toString()) );
			
			if(sr.hasNext() && sr.next().get("id(c)").asInt() > 0)
		    	return true;
		    else return false;
		}
	}
	
	public void updateProfessor(int profId, String name, String surname) {
		try(Session session = driver.session(AccessMode.WRITE)){
			session.run("MATCH (p:Professor) WHERE ID(s) = $profId\n" + 
						"SET p.name = $name, p.surname = $surname;",
	    			Values.parameters("profId",profId,"name",name,"surname",surname) );
		}
	}

	@Override
	public void close() throws Exception {
		driver.close();
	}
}
