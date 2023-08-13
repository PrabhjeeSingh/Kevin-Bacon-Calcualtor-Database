package ca.yorku.eecs;
import com.sun.net.httpserver.*;
import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.types.Node;
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
	 * Returns true if the actor with given id is already present
	 * in the database otherwise false.
	 */
	public boolean hasActor(String id) {
        try(Session session = driver.session()){
            Transaction transaction = session.beginTransaction();
            String query = "MATCH (a: actor) WHERE a.id = '" + id + "' RETURN a;";
            StatementResult result = transaction.run(query);
            
            boolean actorAlreadyPresent = result.hasNext();
            
            transaction.success();
            transaction.close();
            session.close();
            
            return actorAlreadyPresent;
        }
    }
	
	/*
	 * Returns true if the movie with given id is already present
	 * in the database otherwise false.
	 */
	public boolean hasMovie(String id) {
        try(Session session = driver.session()){
            Transaction transaction = session.beginTransaction();
            String query = "MATCH (m: movie) WHERE m.id = '" + id + "' RETURN m;";
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
	 * Returns the name of the actor with given id as a String.
	 * If the actor is not present in the database then it returns
	 * an empty string.
	 */
	public String getActorName(String id) {
		 if(hasActor(id) == false)
			 return "";
	       
		 try(Session session = driver.session()){
			 Transaction transaction = session.beginTransaction();
			 String query = "MATCH (a: actor) WHERE a.id = '" + id + "' RETURN a.name AS name;";
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
	 * Returns the name of the movie with given id as a String.
	 * If the movie is not present in the database then it returns
	 * an empty string.
	 */
	public String getMovieName(String id) {
		 if(hasMovie(id) == false) {
		 	//System.out.println("movie not found");
			 return "";
		 }
		 try(Session session = driver.session()){
			 Transaction transaction = session.beginTransaction();
			 String query = "MATCH (m: movie) WHERE m.id = '" + id + "' RETURN m.name AS name;";
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
	 * Returns the list of movies of an actor with given id has acted in.
	 */
	public List<String> getMoviesOfActor(String id){
		try(Session session = driver.session()){
			Transaction transaction = session.beginTransaction();
			String query =  "MATCH (a: actor {id: '" + id + "'})-[:ACTED_IN]->(fof) RETURN DISTINCT fof.id AS id;";
			StatementResult result = transaction.run(query);
			
			List<String> listOfMovies = new ArrayList<>();
			
			while(result.hasNext()) {
				listOfMovies.add(result.next().get("id").asString());
			}
			
			transaction.success();
			transaction.close();
			session.close();
			 
			return listOfMovies;
		 }
	}
	
	/*
	 * Returns the list of movies of an actor with given id has acted in.
	 */
	public List<String> getActorsOfMovie(String id){
		try(Session session = driver.session()){
			Transaction transaction = session.beginTransaction();
			String query =  "MATCH (a: actor)-[:ACTED_IN]->(m: movie {id : '" + id + "'}) RETURN DISTINCT a.id AS id;";
			StatementResult result = transaction.run(query);
			
			List<String> listOfActors = new ArrayList<>();
			
			while(result.hasNext()) {
				listOfActors.add(result.next().get("id").asString());
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
			String query =  "MATCH (a: actor {id: '"+ actorId + "'})-[:ACTED_IN]->(m: movie {id: '" + movieId + "'}) RETURN EXISTS((a)-[:ACTED_IN]->(m));";
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
	 * Adds the actor with given name and id to the database.
	 */
	public String addActor(String name, String id) {
		
		if(hasActor(id))
			return "400 BAD REQUEST";
		
		try(Session session = driver.session()){
			Transaction transaction = session.beginTransaction();
			String query = "CREATE (a: actor {name: '" + name + "', id: '" + id + "'});";
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
	 * Adds the movie with given name and id to the database.
	 */
	public String addMovie(String name, String id) {
		
		if(hasMovie(id))
			return "400 BAD REQUEST";
		
		try(Session session = driver.session()){
			Transaction transaction = session.beginTransaction();
			String query = "CREATE (m: movie {name: '" + name + "', id: '" + id + "'});";
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
			String query = "MATCH (a: actor), (m: movie) WHERE a.id = '" + actorId + "' AND m.id = '" + movieId + "' CREATE (a)-[r:ACTED_IN]->(m);";
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
	 * Computes the bacon number of the actor with given id
	 * with respect to Kevin Bacon with id "nm0000102".
	 */
	public String computeBaconNumber(String id) {
		if(!hasActor(id))
			return "404 NOT FOUND";
		
		if(id.equals("nm0000102"))
			return "0";
		
		try(Session session = driver.session()){
			Transaction transaction = session.beginTransaction();
			String query = "MATCH path = shortestPath((kb: actor {id: 'nm0000102'})-[*]-(a: actor {id: '" + id +"'})) RETURN length(path)/2 AS baconNumber;";
			StatementResult result = transaction.run(query);
			
			int baconNumber = result.next().get("baconNumber").asInt();
			transaction.success();
			transaction.close();
			session.close();
			
			return baconNumber + "";
		}
		catch(Exception e) {
			e.printStackTrace();
			return "500 INTERNAL SERVER ERROR";
		}
	}
	
	/*
	 * Computes the bacon path of the actor with given id
	 * with respect to Kevin Bacon with id "nm0000102".
	 */
	public List<String> computeBaconPath(String id){
		if(!hasActor(id))
			return new ArrayList<>(Arrays.asList(new String[]{"404 NOT FOUND"}));
		
		if(id.equals("nm0000102"))
			return new ArrayList<>(Arrays.asList(new String[] {"nm0000102"}));
		 
		try(Session session = driver.session()){
			Transaction transaction = session.beginTransaction();
			String query = "MATCH path = shortestPath((a: actor {id: '" + id + "'})-[*]-(kb: actor {id: 'nm0000102'})) RETURN nodes(path) AS path";
			StatementResult result = transaction.run(query);
			
			//List<Object> pathObject = result.next().get("path").asList();
			List<String> baconPath = new ArrayList<>();
	
			List<Node> pathNode = result.next().get("path").asList(Value::asNode);
			
			for(Node data_entity : pathNode) {
				//Map<String, Object> properties = ((Value) year).asMap();
				
					baconPath.add(data_entity.get("id").asString());
					/*if(data_entity.hasLabel("actor")) {
						baconPath.add(data_entity.get("actorId").asString());
					}
					else if(data_entity.hasLabel("movie")) {
						baconPath.add(data_entity.get("movieId").asString());
					}*/
			}
			
			transaction.success();
			transaction.close();
			session.close();
			
			return baconPath;
		}
		catch(Exception e) {
			e.printStackTrace();
			return new ArrayList<>(Arrays.asList(new String[]{"500 INTERNAL SERVER ERROR"}));
		}
	}
	
	//METHODS FOR NEW FEATURE STARTS FROM HERE.
	
	/*
	 * Returns true if the year node with given year is already present
	 * in the database otherwise false.
	 */
	public boolean hasYear(String year) {
        try(Session session = driver.session()){
            Transaction transaction = session.beginTransaction();
            String query = "MATCH (y: year) WHERE y.year = '" + year + "' RETURN y;";
            StatementResult result = transaction.run(query);
            
            boolean yearAlreadyPresent = result.hasNext();
            
            transaction.success();
            transaction.close();
            session.close();
            
            return yearAlreadyPresent;
        }
    }
	
	/*
	 * Checks whether the relationship between movie with given id
	 * and year node with given year exists or not.
	 */
	public String hasRelationshipBtwnMovieYear(String id, String year) {
		if(!hasMovie(id) || !hasYear(year))
			return "404 NOT FOUND";
		
		try(Session session = driver.session()){
			Transaction transaction = session.beginTransaction();
			String query =  "MATCH (m: movie {id: '"+ id + "'})-[:RELEASED_IN]->(y: year {year: '" + year + "'}) RETURN EXISTS((m)-[:RELEASED_IN]->(y));";
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
	 * Adds the year node with given year to the database.
	 */
	public String addYear(String year) {
		
		if(hasYear(year))
			return "400 BAD REQUEST";
		
		try(Session session = driver.session()){
			Transaction transaction = session.beginTransaction();
			String query = "CREATE (y: year {year: '" + year + "'});";
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
	 * movie RELEASED_IN year
	 * to the database.
	 */
	public String addRelationshipBtwnMovieYear(String id, String year) {
		
		if(!hasMovie(id) || !hasYear(year))
			return "404 NOT FOUND";
		
		if(hasRelationshipBtwnMovieYear(id, year).equals("true"))
			return "400 BAD REQUEST";
			
		try(Session session = driver.session()){
			Transaction transaction = session.beginTransaction();
			String query = "MATCH (m: movie), (y: year) WHERE m.id = '" + id + "' AND y.year = '" + year + "' CREATE (m)-[rmy:RELEASED_IN]->(y);";
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
	 * Returns the list of movies released on a specific year.
	 */
	public List<String> getMoviesOfYear(String year){
		if(!hasYear(year))
			return new ArrayList<>(Arrays.asList(new String[] {"404 NOT FOUND"}));
		
		try(Session session = driver.session()){
			Transaction transaction = session.beginTransaction();
			String query =  "MATCH (m: movie)-[:RELEASED_IN]->(y: year {year : '" + year + "'}) RETURN DISTINCT m.id AS id;";
			StatementResult result = transaction.run(query);
			
			List<String> listOfMovies = new ArrayList<>();
			
			while(result.hasNext()) {
				listOfMovies.add(result.next().get("id").asString());
			}
			
			transaction.success();
			transaction.close();
			session.close();
			 
			return listOfMovies;
		 }
		catch(Exception e) {
			e.printStackTrace();
			return new ArrayList<>(Arrays.asList(new String[] {"500 INTERNAL SERVER ERROR"}));
		}
	}
	
}
