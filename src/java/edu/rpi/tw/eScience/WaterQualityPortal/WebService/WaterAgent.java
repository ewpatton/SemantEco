package edu.rpi.tw.eScience.WaterQualityPortal.WebService;

import java.io.IOException;

import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

public class WaterAgent {
	public static void main(String[] args) {

		System.out.println("Starting web service...");
		HttpServer server=null;
		try {
			server = HttpServer.create(new InetSocketAddress(14490), 30);
		}
		catch(IOException e) {
			e.printStackTrace();
			return;
		}
		server.createContext("/zip", new ZipCodeDecoder());
		server.createContext("/agent", new WaterAgentInstance());
		server.setExecutor(null);
		server.start();
		System.out.println("Web service started...");
		
	}
}
