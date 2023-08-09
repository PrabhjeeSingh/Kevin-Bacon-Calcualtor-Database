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
            String query = "MATCH (a: actor) WHERE a.actorId = '" + actorId + "' RETURN a;";
            StatementResult result = transaction.run(query);
            
            boolean actorAlreadyPresent = result.hasNext();
            
            transaction.success();
            transaction.close();
            session.close();
            
            return actorAlreadyPresent;
        }
    }
	
	/*
	 * Returns true if the movie with given movieId is already present
	 * in the database otherwise false.
	 */
	public boolean hasMovie(String movieId) {
        try(Session session = driver.session()){
            Transaction transaction = session.beginTransaction();
            String query = "MATCH (m: movie) WHERE m.movieId = '" + movieId + "' RETURN m;";
            StatementResult result = transaction.run(query);
            //System.out.println("result  = " + result);
            boolean movieAlreadyPresent = result.hasNext();
            //System.out.println("check has next : "+ movieAlreadyPresent);
            transaction.success();
            transaction.close();
            session.close();
            
            return movieAlreadyPresent;
        }
		
		
    }
	/*
	 * Returns the name of the actor with given actorId as a String.
	 * If the actor is not present in the database then it returns
	 * an empty string.
	 */
	public String getActorName(String actorId) {
		 if(hasActor(actorId) == false)
			 return "";
	       
		 try(Session session = driver.session()){
			 Transaction transaction = session.beginTransaction();
			 String query = "MATCH (a: actor) WHERE a.actorId = '" + actorId + "' RETURN a.name AS name;";
			 StatementResult result = transaction.run(query);
			 
			 String name = result.next().get("name").asString();
			 
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
	public String getMovieName(String movieId) {
		 if(hasMovie(movieId) == false) {
		 	//System.out.println("movie not found");
			 return "";
		 }
		 try(Session session = driver.session()){
			 Transaction transaction = session.beginTransaction();
			 String query = "MATCH (m: movie) WHERE m.movieId = '" + movieId + "' RETURN m.name AS name;";
			 StatementResult result = transaction.run(query);
			 
			 String name = result.next().get("name").asString();
			 
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
	 * Returns the list of movies of an actor with given actorId has acted in.
	 */
	public List<String> getMoviesOfActor(String actorId){
		try(Session session = driver.session()){
			Transaction transaction = session.beginTransaction();
			String query =  "MATCH (a: actor {actorId: '" + actorId + "'})-[:ACTED_IN]->(fof) RETURN DISTINCT fof.movieId AS movieId;";
			StatementResult result = transaction.run(query);
			
			List<String> listOfMovies = new ArrayList<>();
			
			while(result.hasNext()) {
				listOfMovies.add(result.next().get("movieId").asString());
			}
			
			transaction.success();
			transaction.close();
			session.close();
			 
			return listOfMovies;
		 }
	}
	
	/*
	 * Returns the list of movies of an actor with given actorId has acted in.
	 */
	public List<String> getActorsOfMovie(String movieId){
		try(Session session = driver.session()){
			Transaction transaction = session.beginTransaction();
			String query =  "MATCH (a: actor)-[:ACTED_IN]->(m: movie {movieId : '" + movieId + "'}) RETURN DISTINCT a.actorId AS actorId;";
			StatementResult result = transaction.run(query);
			
			List<String> listOfActors = new ArrayList<>();
			
			while(result.hasNext()) {
				listOfActors.add(result.next().get("actorId").asString());
			}
			
			transaction.success();
			transaction.close();
			session.close();
			 
			return listOfActors;
		 }
	}
	
	/*
	 * Checks whether the relationship between actor with given actorId
	 * and movie with given movieId exists or not.
	 */
	public String hasRelationship(String actorId, String movieId) {
		if(!hasActor(actorId) || !hasMovie(movieId))
			return "404 NOT FOUND";
		
		try(Session session = driver.session()){
			Transaction transaction = session.beginTransaction();
			String query =  "MATCH (a: actor {actorId: '"+ actorId + "'})-[:ACTED_IN]->(m: movie {movieId: '" + movieId + "'}) RETURN EXISTS((a)-[:ACTED_IN]->(m));";
			StatementResult result = transaction.run(query);
			
			boolean isRelationshipPresent = result.next().get(0).asBoolean();
			
			transaction.success();
			transaction.close();
			session.close();
			
			if(isRelationshipPresent)
				return "true";
			else
				return "false";
		}
		catch(Exception e) {
			e.printStackTrace();
			return "500 INTERNAL SERVER ERROR";
		}
	}
	
	/*
	 * Adds the actor with given name and actorId to the database.
	 */
	public String addActor(String name, String actorId) {
		
		if(hasActor(actorId))
			return "400 BAD REQUEST";
		
		try(Session session = driver.session()){
			Transaction transaction = session.beginTransaction();
			String query = "CREATE (a: actor {name: '" + name + "', actorId: '" + actorId + "'});";
			//StatementResult result = transaction.run(query);
			transaction.run(query);
			
			transaction.success();
			transaction.close();
			session.close();
			
			return "200 OK";
		}
		catch(Exception e) {
			e.printStackTrace();
			return "500 INTERNAL SERVER ERROR";
		}
	}
	
	/*
	 * Adds the movie with given name and movieId to the database.
	 */
	public String addMovie(String name, String movieId) {
		
		if(hasMovie(movieId))
			return "400 BAD REQUEST";
		
		try(Session session = driver.session()){
			Transaction transaction = session.beginTransaction();
			String query = "CREATE (m: movie {name: '" + name + "', movieId: '" + movieId + "'});";
			//StatementResult result = transaction.run(query);
			transaction.run(query);
			
			transaction.success();
			transaction.close();
			session.close();
			
			return "200 OK";
		}
		catch(Exception e) {
			e.printStackTrace();
			return "500 INTERNAL SERVER ERROR";
		}
	}
	
	/*
	 * Adds the directed relationship of
	 * actor ACTED_IN movie
	 * to the database.
	 */
	public String addRelationship(String actorId, String movieId) {
		
		if(!hasActor(actorId) || !hasMovie(movieId))
			return "404 NOT FOUND";
		
		if(hasRelationship(actorId, movieId).equals("true"))
			return "400 BAD REQUEST";
			
		try(Session session = driver.session()){
			Transaction transaction = session.beginTransaction();
			String query = "MATCH (a: actor), (m: movie) WHERE a.actorId = '" + actorId + "' AND m.movieId = '" + movieId + "' CREATE (a)-[r:ACTED_IN]->(m);";
			//StatementResult result = transaction.run(query);
			transaction.run(query);
			
			transaction.success();
			transaction.close();
			session.close();
			
			return "200 OK";
		}
		catch(Exception e) {
			e.printStackTrace();
			return "500 INTERNAL SERVER ERROR";
		}
	}
	
	/*
	 * Computes the bacon number of the actor with given actorId
	 * with respect to Kevin Bacon with actorId "nm0000102".
	 */
	public String computeBaconNumber(String actorId) {
		if(!hasActor(actorId))
			return "404 NOT FOUND";
		
		if(actorId.equals("nm0000102"))
			return "0";
		
		try(Session session = driver.session()){
			Transaction transaction = session.beginTransaction();
			String query = "MATCH path = shortestPath((kb: actor {actorId: 'nm0000102'})-[*]-(a: actor {actorId: '" + actorId +"'})) RETURN length(path)/2 AS baconNumber;";
			StatementResult result = transaction.run(query);
			
			String baconNumber = result.next().get("baconNumber").asString();
			transaction.success();
			transaction.close();
			session.close();
			
			return baconNumber;
		}
		catch(Exception e) {
			e.printStackTrace();
			return "500 INTERNAL SERVER ERROR";
		}
	}
}
