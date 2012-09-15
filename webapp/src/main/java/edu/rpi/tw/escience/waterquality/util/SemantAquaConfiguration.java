package edu.rpi.tw.escience.waterquality.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;

public class SemantAquaConfiguration extends Properties {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2415095741351692534L;
	private static final String PROPERTIES = "/WEB-INF/classes/semantaqua.properties";
	private static SemantAquaConfiguration config = null;
	private static Logger log = Logger.getLogger(SemantAquaConfiguration.class);
	
	private static boolean debugging = false;
	
	public static void configure(ServletContext context) {
		config = new SemantAquaConfiguration();
		try {
			InputStream is = context.getResourceAsStream(PROPERTIES);
			if(is == null) {
				return;
			}
			config.load(is);
			if(config.getProperty("debug", "false").equals("true")) {
				debugging = true;
			}
		}
		catch(IOException e) {
			log.warn("Unable to load "+PROPERTIES, e);
		}
	}
	
	public static SemantAquaConfiguration get() {
		return config;
	}
	
	protected static void setConfig(SemantAquaConfiguration config) {
		SemantAquaConfiguration.config = config;
	}
	
	public boolean isDebug() {
		return debugging;
	}
	
	public String getTripleStore() {
		return config.getProperty("triple-store", "http://sparql.tw.rpi.edu/virtuoso/sparql");
	}
}
