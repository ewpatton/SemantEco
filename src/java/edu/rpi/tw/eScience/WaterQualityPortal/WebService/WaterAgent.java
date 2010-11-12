package WebService;

import java.io.IOException;

import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

public class WaterAgent {
	public static void main(String[] args) {

		System.out.println("Starting web service...");
		HttpServer server=null;
		try {
			server = HttpServer.create(new InetSocketAddress(14490), 10);
		}
		catch(IOException e) {
			e.printStackTrace();
			return;
		}
		server.createContext("/", new WaterAgentInstance());
		server.setExecutor(null);
		server.start();
		System.out.println("Web service stated...");
		
	}
}
