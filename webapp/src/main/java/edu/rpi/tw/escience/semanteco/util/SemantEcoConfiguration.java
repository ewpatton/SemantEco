package edu.rpi.tw.escience.semanteco.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;

/**
 * SemantEcoConfiguration encapsulates the various properties
 * used through SemantEco and is generated from the semanteco.properties
 * file packaged in WEB-INF/classes.
 * 
 * @author ewpatton
 *
 */
public class SemantEcoConfiguration extends Properties {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2415095741351692534L;
	private static final String PROPERTIES = "/WEB-INF/classes/semanteco.properties";
	private static SemantEcoConfiguration config = null;
	private static Logger log = Logger.getLogger(SemantEcoConfiguration.class);
	
	private static boolean debugging = false;
	private static String basePath = null;

	/**
	 * Constructs a new configuration object given the servlet context.
	 * @param context
	 */
	protected SemantEcoConfiguration(ServletContext context) {
		if( context == null ) {
			basePath = "";
		} else {
			basePath = context.getRealPath("/");
		}
	}

	/**
	 * Configures the object from the provided servlet context
	 * @param context
	 */
	public static void configure(ServletContext context) {
		config = new SemantEcoConfiguration(context);
		try {
			InputStream is = context.getResourceAsStream(PROPERTIES);
			if(is == null) {
				return;
			}
			config.load(is);
		}
		catch(IOException e) {
			log.warn("Unable to load "+PROPERTIES, e);
		}
		install(config);
	}
	
	/**
	 * Returns the current configuration
	 * @return
	 */
	public static SemantEcoConfiguration get() {
		return config;
	}
	
	protected static void setConfig(SemantEcoConfiguration config) {
		SemantEcoConfiguration.config = config;
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
		return config.getProperty("triple-store",
				"http://sparql.tw.rpi.edu/virtuoso/sparql");
	}
	
	protected final static void install(SemantEcoConfiguration config) {
		SemantEcoConfiguration.config = config;
		if(config.getProperty("debug", "false").equals("true")) {
			debugging = true;
		}
	}

	/**
	 * Gets the base path to where SemantEco lives on disk.
	 * @return
	 */
	public String getBasePath() {
		return basePath;
	}
}
