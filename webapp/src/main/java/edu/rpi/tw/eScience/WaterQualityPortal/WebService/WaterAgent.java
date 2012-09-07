package edu.rpi.tw.escience.WaterQualityPortal.WebService;

import java.io.IOException;

import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.sun.net.httpserver.HttpServer;

public class WaterAgent {
	static Logger log = Logger.getRootLogger();
	
	public static void main(String[] args) {
		log.info("Starting web service...");
		HttpServer server=null;
		Executor exec = Executors.newFixedThreadPool(Configuration.WORKERS);
		try {
			server = HttpServer.create(new InetSocketAddress(Configuration.LISTEN_ADDR, Configuration.LISTEN_PORT), 0);
		}
		catch(IOException e) {
			e.printStackTrace();
			return;
		}
		server.setExecutor(exec);
		server.createContext("/zip").setHandler(new ZipCodeDecoder());
		server.createContext("/agent").setHandler(new WaterAgentInstance());		
		server.start();
		log.info("Started web service.");
	}
}
