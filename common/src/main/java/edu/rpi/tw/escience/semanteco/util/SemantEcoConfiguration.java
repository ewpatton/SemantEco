package edu.rpi.tw.escience.semanteco.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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
	private static SemantEcoConfiguration config = null;
	private static Logger log = Logger.getLogger(SemantEcoConfiguration.class);
	
	private boolean debugging = false;
	private String basePath = null;
	private boolean parallel = false;
	private String encoding = "UTF-8";

	/**
	 * Constructs a new configuration object given the servlet context.
	 * @param context
	 */
	protected SemantEcoConfiguration(String basePath) {
		this.basePath = basePath;
	}

	/**
	 * Configures the object from the provided servlet context
	 * @param context
	 */
	public static void configure(String basePath, InputStream properties) {
		if(basePath == null) {
			throw new IllegalArgumentException("basePath cannot be null");
		}
		config = new SemantEcoConfiguration(basePath);
		if(properties == null) {
			return;
		}
		try {
			config.load(properties);
		} catch(IOException e) {
			log.warn("Unable to load properties file.", e);
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
	
	protected static final void install(SemantEcoConfiguration config) {
		SemantEcoConfiguration.config = config;
		if(config.getProperty("debug", "false").equals("true")) {
			config.debugging = true;
		}
		if(config.getProperty("parallel", "false").equals("true")) {
			config.parallel = true;
		}
		config.encoding = config.getProperty("encoding", "UTF-8");
	}

	/**
	 * Gets the base path to where SemantEco lives on disk.
	 * @return
	 */
	public String getBasePath() {
		return basePath;
	}

	public boolean isParallel() {
		return parallel;
	}

	protected void setDebug(boolean debug) {
		debugging = debug;
	}

	/**
	 * Gets the text encoding for SemantEco, defaults to UTF-8
	 * @return
	 */
	public String getEncoding() {
		return encoding;
	}
}
