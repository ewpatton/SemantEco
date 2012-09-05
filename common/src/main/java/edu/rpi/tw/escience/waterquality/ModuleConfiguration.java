package edu.rpi.tw.escience.waterquality;

import java.util.Properties;

import org.apache.log4j.Logger;

public abstract class ModuleConfiguration extends Properties {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2756682567606291417L;

	public abstract void getSparqlEndpoint();
	public abstract Query newQuery();
	public abstract QueryExecutor getQueryExecutor();
	public abstract Resource getResource(String path);
	public abstract Resource generateStringResource(String content);
	public abstract Logger getLogger();

}
