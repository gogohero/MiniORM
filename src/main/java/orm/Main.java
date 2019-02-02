package orm;

 
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Date;
import java.sql.SQLException;
 

import entities.User;

public class Main {
	public static void main(String[] args) throws SQLException, IllegalAccessException, IOException {
		  Connector connector = new Connector();
		  connector.createConnection("root", "admin", "soft_uni");
	User user = new User("pesho", 20, new Date(2,3,2016));
	orm.EntityManager<User> em = new EntityManager(connector.getConnection());
	em.persist(user);
	
	BufferedReader buffer=new BufferedReader(new InputStreamReader(System.in));
	String line=buffer.readLine();
	  user = new User("Tosho", 20, new Date(2,3,2016));
	em.persist(user);
	System.out.print(user.getAge());
	}
}
