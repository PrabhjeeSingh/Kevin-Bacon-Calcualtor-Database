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
	
	/*
	 * Returns true if the actor with given actorId is already present
	 * in the database otherwise false.
	 */
	public boolean hasActor(String actorId) {
        try(Session session = driver.session()){
            Transaction transaction = session.beginTransaction();
            String query = "MATCH (a: actor) WHERE a.actorId = '" + actorId + "' RETURN a";
            StatementResult result = transaction.run(query);
            Boolean alreadyPresent = result.hasNext();
            transaction.success();
            transaction.close();
            session.close();
            
            return alreadyPresent;
        }
    }
	
	/*
	 * Returns true if the movie with given movieId is already present
	 * in the database otherwise false.
	 */
	public boolean hasMovie(String movieId) {
        try(Session session = driver.session()){
            Transaction transaction = session.beginTransaction();
            String query = "MATCH (m: movie) WHERE m.movieId = '" + movieId + "' RETURN m";
            StatementResult result = transaction.run(query);
            Boolean alreadyPresent = result.hasNext();
            transaction.success();
            transaction.close();
            session.close();
            
            return alreadyPresent;
        }
    }
	/*
	 * Returns the name of the actor with given actorId as a String.
	 * If the actor is not present in the database then it returns
	 * an empty string.
	 */
	public String getActor(String actorId) {
		 if(hasActor(actorId) == false)
			 return "";
	       
		 try(Session session = driver.session()){
			 Transaction transaction = session.beginTransaction();
			 String query = "MATCH (a: actor) WHERE a.actorId = '" + actorId + "' RETURN a.name";
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
	
	/*
	 * Returns the name of the movie with given movieId as a String.
	 * If the movie is not present in the database then it returns
	 * an empty string.
	 */
	public String getMovie(String movieId) {
		 if(hasActor(movieId) == false)
			 return "";
	       
		 try(Session session = driver.session()){
			 Transaction transaction = session.beginTransaction();
			 String query = "MATCH (m: movie) WHERE m.movieId = '" + movieId + "' RETURN m.name";
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
