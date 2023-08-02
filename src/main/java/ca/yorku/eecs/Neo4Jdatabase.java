package ca.yorku.eecs;
import com.sun.net.httpserver.*;
import org.neo4j.driver.v1.*;
import org.json.*;
import java.net.*;
import java.util.*;
import java.io.*;
import java.lang.*;

public class Neo4Jdatabase {
	private Driver driver;
	private String uriDb;
	
	public Neo4Jdatabase() {
		uriDb = "bolt://localhost:7687"; // may need to change if you used a different port for your DBMS
		Config config = Config.builder().withoutEncryption().build();
		driver = GraphDatabase.driver(uriDb, AuthTokens.basic("neo4j","12345678"), config);
	}
	
	public boolean hasActor(String actorID) {
        try(Session session = driver.session()){
            Transaction transaction = session.beginTransaction();
            String query = "MATCH (a: Actor) WHERE a.id = '" + actorID + "' RETURN a";
            StatementResult result = transaction.run(query);
            Boolean alreadyPresent = result.hasNext();
            transaction.success();
            transaction.close();
            session.close();
            
            return alreadyPresent;
        }
    }
	
	public String getActor(String actorID) {
		 if(hasActor(actorID) == false)
			 return "";
	       
		 try(Session session = driver.session()){
			 Transaction transaction = session.beginTransaction();
			 String query = "MATCH (a: Actor) WHERE a.id = '" + actorID + "' RETURN a.Name";
			 StatementResult result = transaction.run(query);
			 String name = result.next().values().get(0).asString();
			 transaction.success();
			 transaction.close();
			 session.close();
			 
			 return name;
		 }
		 
		 catch(Exception e) {
			 e.printStackTrace();
			 return "500 INTERNAL SERVER ERROR";
		 }
	}
}
