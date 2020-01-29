package task3;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.*;

import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.types.*;

import javafx.collections.FXCollections;

public class DbManager implements AutoCloseable {
	private final Driver driver;
	private final String uri = "bolt://localhost:7687";
	private final String user = "alice";
	private final String password = "alice";
	
	public DbManager() {
		driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
	}

	
	public void createDegree(String name) {
		try(Session session = driver.session()){
			session.writeTransaction( tx -> {
				tx.run( "CREATE (a:Degree {name: $name});", 
						Values.parameters("name",name) );
				return null;
				}
			);
		}
	}
	
	public void createProfessor(String name, String surname) {
		try(Session session = driver.session()){
			session.writeTransaction( tx -> {
				tx.run( "CREATE (a:Professor {name: $name, surname: $surname});", 
					Values.parameters("name",name,"surname",surname) );
				return null;
			});
		}
	}
	
	public void createSubject(String name, int credits, String info, String profIdStr, int degreeId) {
		String[] professorsId = profIdStr.split(",", 5);
		
		try(Session session = driver.session()){
			session.writeTransaction( tx -> {
				StatementResult sr = tx.run( 	
						"MATCH (d:Degree) WHERE id(d) = $degreeId " +
						"CREATE (a:Subject {name:$name, cfu:$cfu, info:$info})," +
						"(a)-[:BELONGS]->(d) RETURN ID(a);", 
					Values.parameters("name",name,"cfu",credits,"info",info,"degreeId",degreeId) );
			
				if(sr.hasNext()) {
					for (String p : professorsId) {
						int profId = Integer.parseInt(p);
						if(profId >= 0) {
							createTeachingRelation(profId, sr.next().get("ID(a)").asInt(),tx);
						}
					}
				}
				return null;
			});
		}
	}
	
	private void createTeachingRelation(int profId, int subjectId, Transaction tx) {
		tx.run( 	"MATCH (p:Professor) WHERE id(p) = $profId " + 
						"MATCH (s:Subject) WHERE id(s) = $subjectId " +
						"CREATE (p)-[:TEACHES]->(s);", 
			Values.parameters("profId",profId,"subjectId",subjectId) );
	}
	
	public void createComment(String text, Student student, int subjectId) {
		try(Session session = driver.session()){
			session.writeTransaction( tx -> {
				tx.run( 	"MATCH (e:Student) WHERE id(e) = $idStudent " + 
							"MATCH (s:Subject) WHERE id(s) = $idSubject " +
							"CREATE " + 
							"(a:Comment {text: $text, date: $date})," + 
							"(e)-[:WROTE]->(a)," + 
							"(a)-[:ABOUT]->(s);", 
					Values.parameters("idStudent",student.getId(),"idSubject",subjectId,"text",text,
							"date",new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date())) );
				return null;
			});
		}
	}
	
	public Student checkUser(String username, String password) {
		
		
		try(Session session = driver.session()){
			return session.readTransaction( tx -> {
				Student student = null;
				StatementResult sr = tx.run( 
					"MATCH (s:Student)-[:ATTENDS]->(d:Degree) " +
					"WHERE s.username = $username AND s.password = $password "+
					"RETURN ID(s),s.username,s.password,s.admin, ID(d),d.name;", 
					Values.parameters("username",username,"password",password) );
						
				if(sr.hasNext()) {
					Record r = sr.next();
					if(sr.hasNext()) {
						System.err.println("In the Database are present more than one user with the same username");
					}else {
						boolean admin = r.get("s.admin").isNull()? false : r.get("s.admin").asBoolean();
						
						student = new Student(r.get("ID(s)").asInt(),r.get("s.username").asString(),
									new Degree(r.get("ID(d)").asInt(),r.get("d.name").asString()), admin);
						student.setFriends(this.getFriends(student, tx));
					}
				}
				return student;
			});
		}
	}
	
	public List<Professor> getProfessorsBySubject(int subjectId) {
		
		try(Session session = driver.session()){
			return session.readTransaction( tx -> {
				List<Professor> professors = FXCollections.observableArrayList();
				StatementResult sr = tx.run( 
					"MATCH (p:Professor)-[:TEACHES]->(s:Subject) \n" + 
					"WHERE id(s) = $subjectId \n" + 
					"RETURN ID(p),p.name,p.surname", 
					Values.parameters("subjectId",subjectId) );
						
				while(sr.hasNext()) {
					Record r = sr.next();
					professors.add(new Professor(r.get("ID(p)").asInt(),r.get("p.name").asString(),r.get("p.surname").asString()));
				}
				return professors;
			});
		}
	}
	
	public List<Professor> getProfessorsByDegree(int degreeId) {
		try(Session session = driver.session()){
			return session.readTransaction( tx -> {
				List<Professor> professors = FXCollections.observableArrayList();
				StatementResult sr;
				if(degreeId >= 0) {
					sr = tx.run( 
							"MATCH (p:Professor)-[:TEACHES]->(s:Subject)-[:BELONGS]->(d:Degree) " + 
							"WHERE id(d) = $degreeId \n" + 
							"RETURN DISTINCT ID(p),p.name,p.surname", 
							Values.parameters("degreeId",degreeId) );
				}else {
					sr = tx.run( 
							"MATCH (p:Professor) " + 
							"RETURN ID(p),p.name,p.surname" );
				}
				
				while(sr.hasNext()) {
					Record r = sr.next();
					professors.add(new Professor(r.get("ID(p)").asInt(),r.get("p.name").asString(),r.get("p.surname").asString()));
				}
				return professors;
			});
		}
	}
	
	public List<Subject> getSubjects(int degree) {
		try(Session session = driver.session()){
			return session.readTransaction( tx -> {
				List<Subject> list = new ArrayList<>();
				StatementResult sr;
				if(degree >= 0) {
					sr = tx.run( 
							"MATCH (s:Subject)-[:BELONGS]->(d:Degree) \n" + 
							"WHERE id(d) = $idDegree \n" + 
							"RETURN ID(s),s.name,s.cfu,s.info", 
							Values.parameters("idDegree",degree) );
				}else {
					sr = tx.run( 
							"MATCH (s:Subject) " + 
							"RETURN ID(s),s.name,s.cfu,s.info", 
							Values.parameters("idDegree",degree) );
				}
				
				while(sr.hasNext()) {
					Record r = sr.next();
					list.add(new Subject(r.get("ID(s)").asInt(),r.get("s.name").asString(),
							r.get("s.cfu").asInt(),r.get("s.info").asString(),degree));
					
					list.get(list.size()-1).setProfessors( this.getProfessorsBySubject(r.get("ID(s)").asInt()) );
				}
				return list;
			});
		}
	}
	
	public List<Comment> getComments(int subjectId, int userId) {
		
		if (subjectId == -1)
			return null;
		
		try(Session session = driver.session()){
			return session.readTransaction( tx -> {
				List<Comment> list = new ArrayList<>();
				StatementResult sr;
				if(userId < 0) {
					sr = tx.run( 
							"MATCH (st:Student)-[:WROTE]->" + 
							"(c:Comment)-[:ABOUT]->(s:Subject) " + 
							"WHERE id(s) = $idSubject " +
							"RETURN ID(c),c.text,c.date,ID(st),st.username;", 
							Values.parameters("idSubject",subjectId) );
				}else {
					sr = tx.run( 
							"MATCH (user:Student)-[:KNOWS]->" +
							"(st:Student)-[:WROTE]->" + 
							"(c:Comment)-[:ABOUT]->(s:Subject) " +
							"WHERE id(s) = $idSubject " +
							"AND id(user) = $idUser " +
							"RETURN ID(c),c.text,c.date,ID(st),st.username;", 
							Values.parameters("idSubject",subjectId,"idUser",userId) );
				}
				
				while(sr.hasNext()) {
					Record r = sr.next();
					list.add(new Comment(r.get("ID(c)").asInt(),r.get("c.text").asString(),
							new Student(r.get("ID(st)").asInt(),r.get("st.username").asString(),null, false),
							subjectId,r.get("c.date").asString()));
				}
				return list;
			});
		}		
	}
	
	public List<Degree> getDegreeCourses() {
		try(Session session = driver.session()){
			return session.readTransaction( tx -> {
				List<Degree> list = new ArrayList<>();
				StatementResult sr = tx.run( "MATCH (dd:Degree) RETURN ID(dd), dd.name;");
			
				while(sr.hasNext()) {
					Record r = sr.next();
					list.add(new Degree(r.get("ID(dd)").asInt(),r.get("dd.name").asString()));
				}
				return list;
			});
		}
	}
	
	public void deleteProfessor(int profId) {
		try(Session session = driver.session()){
			session.writeTransaction( tx -> {
				tx.run( "MATCH (n:Professor) WHERE id(n) = $idProf DETACH DELETE n;",
						Values.parameters("idProf",profId));
				return null;
			});
		}
	}
	
	public void deleteSubject(int subjectId) {
		try(Session session = driver.session()){
			session.writeTransaction( tx -> {
				tx.run( "MATCH (n:Subject) WHERE id(n) = $idSubject DETACH DELETE n;",
						Values.parameters("idSubject",subjectId));
				return null;
			});
		}
	}
	
	public boolean deleteComment(int commentId, int userId, boolean admin){
		try(Session session = driver.session()){
			return session.writeTransaction( tx -> {
			    StatementResult sr;
				if(admin){
			    	sr = tx.run( "MATCH (c:Comment) WHERE id(c) = $idComment DETACH DELETE c RETURN id(c);",
			    			Values.parameters("idComment",commentId) );
			    }else{
			    	sr = tx.run("MATCH (s:Student)-[:WROTE]->(c:Comment) " +
					        		"WHERE id(s) = $userId AND id(c) = $idComment " +
					        		"DETACH DELETE c RETURN id(c)",
			    			Values.parameters("userId",userId,"idComment",commentId) );
			    }
			    
			    if(sr.hasNext() && sr.next().get("id(c)").asInt() >= 0)
			    	return true;
			    else return false;
			});
		}
	}
	
	public boolean updateSubject(int subjectId, String name, int credits, String info, String profIdStr) {
		try(Session session = driver.session()){
			return session.writeTransaction( tx -> {
			
				StatementResult sr = tx.run(
							"MATCH (s:Subject) WHERE id(s) = $subjectId\n" + 
							"SET s.info = $info, s.name = $name, s.cfu = $cfu " +
							"RETURN ID(s);",
		    			Values.parameters("subjectId",subjectId,"info",info,"name",name,"cfu",credits) );
				
				if(!profIdStr.isEmpty()) {
					tx.run("MATCH (p:Professor)-[r:TEACHES]->(s:Subject) " + 
								"WHERE id(s) = $subjectId DELETE r",
		    			Values.parameters("subjectId",subjectId) );
					
					String[] professorsId = profIdStr.split("," , 5);
					
					for(String p : professorsId) {
						int profId = Integer.parseInt(p);
						if (profId >= 0) {
							createTeachingRelation(profId, subjectId,tx);
						}
					}	
				}
				if(sr.hasNext() && sr.next().get("ID(s)").asInt() >= 0)
			    	return true;
			    else return false;
			});
		}
	}
	
	public boolean updateComment(int commentId, String text, int userId) {
		try(Session session = driver.session()){
			return session.writeTransaction( tx -> {
				StatementResult sr = session.run(
							"MATCH (s:Student)-[:WROTE]->(c:Comment) " +
							"WHERE ID(s) = $userId AND ID(c) = $commentId " + 
							"SET c.text = $text, c.date = $date " +
							"RETURN ID(c);",
		    			Values.parameters("userId",userId,"commentId",commentId,"text",text,
		    					"date",new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date())) );
				
				if(sr.hasNext() && sr.next().get("ID(c)").asInt() >= 0)
			    	return true;
			    else return false;
			});
		}
	}
	
	public void updateProfessor(int profId, String name, String surname) {
		try(Session session = driver.session()){
			session.writeTransaction( tx -> {
				tx.run("MATCH (p:Professor) WHERE ID(p) = $profId\n" + 
							"SET p.name = $name, p.surname = $surname;",
		    			Values.parameters("profId",profId,"name",name,"surname",surname) );
				return null;
			});
		}
	}

	public List<Student> getSuggestedFriends(Student user){
		try(Session session = driver.session()){
			return session.readTransaction( tx -> {
				List<Student> list = FXCollections.observableArrayList();
				StatementResult sr = tx.run(
						"MATCH (friend:Student)-[*2..4]-(s:Student) \n" + 
						"WHERE ID(s) = $idUser AND \n" + 
						"NOT (s)-[:KNOWS]-(friend)  AND \n" + 
						"NOT ID(friend) = ID(s) \n" + 
						"RETURN DISTINCT ID(friend), friend.username, friend.admin;",
					Values.parameters("idUser", user.getId()) );
			
				while(sr.hasNext()) {
					Record r = sr.next();
					list.add(new Student(r.get("ID(friend)").asInt(),r.get("friend.username").asString(),
							user.getDegree(), false));
				}
				return list;
			});
		}
	}
	
	private List<Student> getFriends(Student s, Transaction tx) {
		List<Student> list = FXCollections.observableArrayList();
		StatementResult sr = tx.run( 
				"MATCH (s:Student)-[:KNOWS]->(friend:Student) " +
				"WHERE id(s) = $studentId " + 
				"RETURN ID(friend), friend.username;", 
				Values.parameters("studentId", s.getId()) );
		while(sr.hasNext()) {
			Record r = sr.next();
			list.add(new Student(r.get("ID(friend)").asInt(),r.get("friend.username").asString(),
					s.getDegree(), false));
		}
		return list;
	}
	
	public void addFriend(Student user, Student friend) {
		try(Session session = driver.session()){
			session.writeTransaction( tx -> {
				tx.run( 	"MATCH (s:Student) WHERE id(s) = $idUser " + 
							"MATCH (friend:Student) WHERE id(friend) = $idFriend " +
							"CREATE (s)-[:KNOWS]->(friend);", 
					Values.parameters("idUser",user.getId(),"idFriend", friend.getId()) );
				return null;
			});
		}
	}


	@Override
	public void close() throws Exception {
		driver.close();
	}
	
}
