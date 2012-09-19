package edu.rpi.tw.escience.waterquality.impl;

import java.lang.ref.WeakReference;

import org.apache.log4j.Logger;

import edu.rpi.tw.escience.waterquality.Module;
import edu.rpi.tw.escience.waterquality.ModuleConfiguration;
import edu.rpi.tw.escience.waterquality.QueryExecutor;
import edu.rpi.tw.escience.waterquality.QueryFactory;
import edu.rpi.tw.escience.waterquality.Resource;
import edu.rpi.tw.escience.waterquality.query.QueryExecutorImpl;
import edu.rpi.tw.escience.waterquality.query.QueryFactoryImpl;
import edu.rpi.tw.escience.waterquality.res.GenericResource;
import edu.rpi.tw.escience.waterquality.res.JspResource;
import edu.rpi.tw.escience.waterquality.res.ScriptResource;
import edu.rpi.tw.escience.waterquality.res.StringResource;
import edu.rpi.tw.escience.waterquality.res.StyleResource;

/**
 * The ModuleConfigurationImpl provides the default implementation of
 * ModuleConfiguration by providing the appropriate default implementations
 * of the many other interfaces that make up the SemantAqua API.
 * 
 * @author ewpatton
 *
 */
public class ModuleConfigurationImpl extends ModuleConfiguration {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3086677327878305603L;
	private WeakReference<Module> owner = null;
	private QueryExecutor executor = null;
	private Logger log = Logger.getLogger(ModuleConfigurationImpl.class);
	private String resourceDir = null;
	
	/**
	 * Constructs a ModuleConfigurationImpl for the specified module.
	 * @param module
	 * @param resourceDir Directory to where resources were extracted by the ModuleClassLoader associated with the module
	 */
	public ModuleConfigurationImpl(Module module, String resourceDir) {
		log.trace("ModuleConfigurationImpl");
		if(module != null) {
			owner = new WeakReference<Module>(module);
		}
		this.resourceDir = resourceDir;
		this.executor = QueryExecutorImpl.getExecutorForModule(module);
	}

	@Override
	public String getSparqlEndpoint() {
		log.trace("getSparqlEndpoint");
		return executor.getDefaultSparqlEndpoint();
	}

	@Override
	public QueryFactory getQueryFactory() {
		log.trace("getQueryFactory");
		return QueryFactoryImpl.getInstance();
	}

	@Override
	public QueryExecutor getQueryExecutor() {
		log.trace("getQueryExecutor");
		return executor;
	}

	@Override
	public Resource getResource(String path) {
		log.trace("getResource");
		log.debug("Generating resource for '"+path+"' for module '"+owner.get().getName()+"'");
		if(path.endsWith(".css")) {
			return new StyleResource(owner.get(), resourceDir+path);
		}
		else if(path.endsWith(".js")) {
			return new ScriptResource(owner.get(), resourceDir+path);
		}
		else if(path.endsWith(".jsp")) {
			return new JspResource(owner.get(), resourceDir+path);
		}
		else {
			return new GenericResource(owner.get(), resourceDir+path);
		}
	}

	@Override
	public Resource generateStringResource(String content) {
		log.trace("generateStringResource");
		if(owner.get() != null) {
			return new StringResource(owner.get(), content);
		}
		return null;
	}

	@Override
	public Logger getLogger() {
		if(owner.get() != null) {
			return Logger.getLogger(owner.get().getClass());
		}
		return null;
	}

}
