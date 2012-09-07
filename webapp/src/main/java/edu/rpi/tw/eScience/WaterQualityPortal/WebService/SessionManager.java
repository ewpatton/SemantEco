package edu.rpi.tw.escience.WaterQualityPortal.WebService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import edu.rpi.tw.escience.WaterQualityPortal.zip.ZipCodeLookup.ServerFailedToRespondException;

@Deprecated
public class SessionManager implements HttpHandler, Runnable {

	Thread cleanupThread; 
	HashMap<String,Session> sessions = new HashMap<String,Session>();
	
	public SessionManager() {
		cleanupThread = new Thread(this);
	}

	public Map<String,String> parseRequest(HttpExchange arg0) throws IOException
	{
		HashMap<String,String> result = new HashMap<String, String>();
		String query = arg0.getRequestURI().getQuery();
		//parse request
		String [] request=query.split("&");
		
		
		for(int i=0;i<request.length;i++) {
			String[] pieces = request[i].split("=");
			if(pieces.length==2) {
				result.put(pieces[0], java.net.URLDecoder.decode(pieces[1],"UTF-8"));
			}
			else System.err.println(pieces);
		}
		return result;
	}
	
	@Override
	public void handle(HttpExchange arg0) throws IOException {
		Map<String,String> request = parseRequest(arg0);
		if(request.containsKey("session")) {
			Session x;
			synchronized(sessions) {
				x = sessions.get(request.get("session"));
			}
			String result;
			int code=500;
			if(x==null) {
				result = "{\"error\":\"Your session has expired.\"}";
				arg0.sendResponseHeaders(code, result.length());
				arg0.getResponseBody().write(result.getBytes("UTF-8"));
				arg0.getResponseBody().flush();
				arg0.getResponseBody().close();
			}
			try {
				result = x.performQuery(request.get("query"));
				code = 200;
				arg0.getResponseHeaders().set("Content-type", "text/xml");
			}
			catch(Exception e) {
				e.printStackTrace();
				code = 500;
				result="{\"error\":\"A server-side error occurred.\"}";
				arg0.getResponseHeaders().set("Content-type", "text/plain");
			}
			arg0.sendResponseHeaders(code, result.length());
			arg0.getResponseBody().write(result.getBytes("UTF-8"));
			arg0.getResponseBody().flush();
			arg0.getResponseBody().close();
		}
		else {
			String zip = request.get("code");
			String result;
			int code=500;
			try {
				Session x = new Session(zip);
				sessions.put(x.getId(), x);
				result = "{\"session\":\""+x.getId()+"\",\"result\":{\"lat\":"+x.getZipCode().getLatitude()+",\"lng\":"+x.getZipCode().getLongitude()+"}}";
				code = 200;
			}
			catch(ServerFailedToRespondException e) {
				result = "{\"error\":\"Geonames server not responding. Please try again later.\"}";
			}
			catch(Exception e) {
				result = "{\"error\":\"Unknown zip code\"}";
			}
			arg0.sendResponseHeaders(code, result.length());
			arg0.getResponseBody().write(result.getBytes("UTF-8"));
			arg0.getResponseBody().flush();
			arg0.getResponseBody().close();
		}
	}

	@Override
	public void run() {
		while(true) {
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			synchronized(sessions) {
				for(Entry<String, Session> s : sessions.entrySet()) {
					try {
						if(s.getValue().getTimeout() < System.currentTimeMillis()) {
							s.getValue().clearSession();
							sessions.remove(s.getKey());
						}
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
