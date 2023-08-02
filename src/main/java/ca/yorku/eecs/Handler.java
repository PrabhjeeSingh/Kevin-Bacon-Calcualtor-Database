package ca.yorku.eecs;
import java.io.IOException;

import java.io.*;
import java.net.*;
import java.util.*;
import com.sun.net.httpserver.*;
import org.json.*;

public class Handler implements HttpHandler {
	private Neo4Jdatabase neo4JObject;
	
	public Handler(){
		this.neo4JObject = new Neo4Jdatabase();
	}

	@Override
	public void handle(HttpExchange request) throws IOException {
		// TODO Auto-generated method stub
		try {
            if (request.getRequestMethod().equals("GET")) {
            	handleGet(request);
            }
            else if(request.getRequestMethod().equals("PUT")) {
            	handlePut(request);
            }
            
            //if the request is for neither GET nor PUT features
            else {
            	sendString(request, "501 UNIMPLEMENTED METHOD", 501);
            }
            }
		catch (Exception e) {
        	e.printStackTrace();
        	
        	//if there is server error
        	sendString(request, "500 INTERNAL SERVER ERROR", 500);
        }
	}

	private void handlePut(HttpExchange request) throws IOException {
		// TODO Auto-generated method stub
		URI uri = request.getRequestURI();
		String path = uri.getPath();
		
		if(path.equals("/api/v1/addActor")) {
			//Complete
		}
		else if(path.equals("/api/v1/addMovie")) {
			//complete
		}
		else if(path.equals("/api/v1/addRelationship")) {
			//complete
		}
		else {
			//if the request is for some PUT feature other than specified in the handout
			sendString(request, "501 UNIMPLEMENTED METHOD", 501);
		}

		
	}



	private void handleGet(HttpExchange request) throws IOException, JSONException {
		// TODO Auto-generated method stub
		URI uri = request.getRequestURI();
		String path = uri.getPath();
		
		if(path.equals("/api/v1/getActor")) {
			//complete
			getActorHandler(request);
		}
		else if(path.equals("/api/v1/getMovie")) {
			//complete
			getMovieHandler(request);
		}
		else if(path.equals("/api/v1/hasRelationship")) {
			//complete
		}
		else if(path.equals("/api/v1/computeBaconNumber")) {
			//complete
		}
		else if(path.equals("/api/v1/computeBaconPath")) {
			//complete
		}
		else {
			//if the request is for some GET feature other than specified in the handout
			sendString(request, "501 UNIMPLEMENTED METHOD", 501);
		}
	}
	
	private void getActorHandler(HttpExchange request) throws IOException, JSONException{
		//Converting request to String
		String stringBody = Utils.getBody(request);
		
		//Converting String request to query parameters in the form of LinkedHashMap
		Map<String, String> mapBody = Utils.splitQuery(stringBody);
		//JSONObject jsonBody = new JSONObject(stringBody);
		
		JSONObject jsonFinalResult = new JSONObject();
		
		if(mapBody.containsKey("actorId")) {
			String name, actorId;
			List<String> listOfMovies;
			
			//get the actorId from the request body
			actorId = mapBody.get("actorId").toString();
			
			//call the method from Neo4Jdatabase class to get actor name
			name = neo4JObject.getActorName(actorId);
			
			//if the actor doesn't exist
			if(name.equals(""))
				sendString(request, "404 NOT FOUND", 404);
			
			//if there is server error (Java Exception)
			else if(name.equals("500 INTERNAL SERVER ERROR"))
				sendString(request, "500 INTERNAL SERVER ERROR", 500);
			
			else {
				//call the method from Neo4Jdatabase class to get list of movies
				listOfMovies = neo4JObject.getMoviesOfActor(actorId);
				
				
				//add all the data as specified in the handout
				jsonFinalResult.put("actorId", actorId);
				jsonFinalResult.put("name", name);
				jsonFinalResult.put("movies", listOfMovies);
				
				//send the response of 200 OK along with the result
				sendString(request, jsonFinalResult.toString(), 200);
			}
		}
		
		//if the request is not properly formatted or missing some information
		else {
			sendString(request, "404 BAD REQUEST", 404);
		}
	}
	
	private void getMovieHandler(HttpExchange request) throws IOException, JSONException{
		//Converting request to String
		String stringBody = Utils.getBody(request);
		
		//Converting String request to query parameters in the form of LinkedHashMap
		Map<String, String> mapBody = Utils.splitQuery(stringBody);
		//JSONObject jsonBody = new JSONObject(stringBody);
		
		JSONObject jsonFinalResult = new JSONObject();
		
		if(mapBody.containsKey("movieId")) {
			String name, movieId;
			List<String> listOfActors;
			
			//get the movieId from the request body
			movieId = mapBody.get("movieId").toString();
			
			//call the method from Neo4Jdatabase class to get movie name
			name = neo4JObject.getActorName(movieId);
			
			//if the movie doesn't exist
			if(name.equals(""))
				sendString(request, "404 NOT FOUND", 404);
			
			//if there is server error (Java Exception)
			else if(name.equals("500 INTERNAL SERVER ERROR"))
				sendString(request, "500 INTERNAL SERVER ERROR", 500);
			
			else {
				//call the method from Neo4Jdatabase class to get list of actors
				listOfActors = neo4JObject.getActorsOfMovie(movieId);
				
				
				//add all the data as specified in the handout
				jsonFinalResult.put("movieId", movieId);
				jsonFinalResult.put("name", name);
				jsonFinalResult.put("actors", listOfActors);
				
				//send the response of 200 OK along with the result
				sendString(request, jsonFinalResult.toString(), 200);
			}
		}
		
		//if the request is not properly formatted or missing some information
		else {
			sendString(request, "404 BAD REQUEST", 404);
		}
	}
	private void sendString(HttpExchange request, String data, int restCode) throws IOException {
		// TODO Auto-generated method stub
		request.sendResponseHeaders(restCode, data.length());
        OutputStream os = request.getResponseBody();
        os.write(data.getBytes());
        os.close();
		
	}

}
