package ca.yorku.eecs;
import java.io.IOException;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
public class Handler implements HttpHandler {

	@Override
	public void handle(HttpExchange request) throws IOException {
		// TODO Auto-generated method stub
		try {
            if (request.getRequestMethod().equals("GET")) {
            	handleGet(request);
                System.out.println("get");
            }
            else if(request.getRequestMethod().equals("PUT")) {
            	handlePut(request);
                System.out.println("put");
            	}
            else {
            	sendString(request, "Unimplemented method\n", 501);
            }
            }
		catch (Exception e) {
        	e.printStackTrace();
        	sendString(request, "Server error\n", 500);
        }
	}

	private void handlePut(HttpExchange request) {
		// TODO Auto-generated method stub
		URI uri = request.getRequestURI();
		String path=uri.getPath();
		if(path=="/api/v1/addActor") {
			//Complete
		}
		else if(path=="/api/v1/addMovie") {
			//complete
		}
		else if(path=="/api/v1/addRelationship") {
			//complete
		}
		String query = uri.getQuery();
        System.out.println(query);

        //Map<String, String> queryParam = Utils.splitQuery(query);
        //System.out.println(queryParam);
		
	}



	private void handleGet(HttpExchange request)throws IOException {
		// TODO Auto-generated method stub
		URI uri = request.getRequestURI();
		String path=uri.getPath();
		if(path=="/api/v1/getActor") {
			String id_num=Utils.getBody(request);
			
		}
		else if(path=="/api/v1/getMovie") {
			//complete
		}
		else if(path=="/api/v1/hasRelationship") {
			//complete
		}
		else if(path=="/api/v1/computeBaconNumber") {
			//complete
		}
		else if(path=="/api/v1/computeBaconPath") {
			//complete
		}
		String query = uri.getQuery();
        System.out.println(query);
        Map<String, String> queryParam = Utils.splitQuery(query);
        System.out.println(queryParam);
        long first = Long.parseLong(queryParam.get("firstNumber"));
        long second = Long.parseLong(queryParam.get("secondNumber"));
        
        
	}
	private void sendString(HttpExchange request, String data, int restCode)throws IOException {
		// TODO Auto-generated method stub
		request.sendResponseHeaders(restCode, data.length());
        OutputStream os = request.getResponseBody();
        os.write(data.getBytes());
        os.close();
		
	}

}
