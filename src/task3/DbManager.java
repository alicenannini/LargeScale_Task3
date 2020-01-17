package task3;

import org.neo4j.driver.v1.*;

public class DbManager implements AutoCloseable {
	private final Driver driver;
	
	private final String uri = "bolt://localhost:7687";
	private final String user = "root";
	private final String password = "";
	
	public DbManager() {
		driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
	}

	
	public void addProfessor(String name, String surname) {
		try(Session session = driver.session(AccessMode.WRITE)){
			session.run( "CREATE a:Professor {name: $name, surname: $surname})", 
					Values.parameters(name,surname) );
		}
	}
	
	public static void main(String[] args) {
		DbManager m = new DbManager();
		m.addProfessor("Marco", "Avvenuti");
	}


	@Override
	public void close() throws Exception {
		driver.close();
	}
}
