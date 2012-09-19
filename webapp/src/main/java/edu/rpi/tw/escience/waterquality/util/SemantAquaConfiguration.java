package edu.rpi.tw.escience.waterquality.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;

/**
 * SemantAquaConfiguration encapsulates the various properties
 * used through SemantAqua and is generated from the semantaqua.properties
 * file packaged in WEB-INF/classes.
 * 
 * @author ewpatton
 *
 */
public class SemantAquaConfiguration extends Properties {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2415095741351692534L;
	private static final String PROPERTIES = "/WEB-INF/classes/semantaqua.properties";
	private static SemantAquaConfiguration config = null;
	private static Logger log = Logger.getLogger(SemantAquaConfiguration.class);
	
	private static boolean debugging = false;
	
	/**
	 * Configures the object from the provided servlet context
	 * @param context
	 */
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
	
	/**
	 * Returns the current configuration
	 * @return
	 */
	public static SemantAquaConfiguration get() {
		return config;
	}
	
	protected static void setConfig(SemantAquaConfiguration config) {
		SemantAquaConfiguration.config = config;
	}
	
	/**
	 * Checks whether we are in a debug environment
	 * @return
	 */
	public boolean isDebug() {
		return debugging;
	}
	
	/**
	 * Gets the default triple store specified in the properties file.
	 * @return
	 */
	public String getTripleStore() {
		return config.getProperty("triple-store", "http://sparql.tw.rpi.edu/virtuoso/sparql");
	}
}
