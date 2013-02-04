package edu.rpi.tw.escience.semanteco.impl;

import java.lang.ref.WeakReference;
import java.net.URI;
import java.util.List;

import org.apache.log4j.Logger;

import edu.rpi.tw.escience.semanteco.query.QueryExecutorImpl;
import edu.rpi.tw.escience.semanteco.query.QueryFactoryImpl;
import edu.rpi.tw.escience.semanteco.res.GenericResource;
import edu.rpi.tw.escience.semanteco.res.JspResource;
import edu.rpi.tw.escience.semanteco.res.ScriptResource;
import edu.rpi.tw.escience.semanteco.res.StringResource;
import edu.rpi.tw.escience.semanteco.res.StyleResource;
import edu.rpi.tw.escience.semanteco.Domain;
import edu.rpi.tw.escience.semanteco.Module;
import edu.rpi.tw.escience.semanteco.ModuleConfiguration;
import edu.rpi.tw.escience.semanteco.ModuleManager;
import edu.rpi.tw.escience.semanteco.QueryExecutor;
import edu.rpi.tw.escience.semanteco.QueryFactory;
import edu.rpi.tw.escience.semanteco.Request;
import edu.rpi.tw.escience.semanteco.Resource;

/**
 * The ModuleConfigurationImpl provides the default implementation of
 * ModuleConfiguration by providing the appropriate default implementations
 * of the many other interfaces that make up the SemantEco API.
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
	private QueryExecutorImpl executor = null;
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
		else {
			throw new IllegalArgumentException("module should not be null.");
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
	public QueryExecutor getQueryExecutor(Request request) {
		log.trace("getQueryExecutor");
		try {
			QueryExecutorImpl newExecutor = (QueryExecutorImpl) executor.clone();
			newExecutor.setRequest(request);
			return newExecutor;
		} catch (CloneNotSupportedException e) {
			log.warn("Unable to clone query executor", e);
			return null;
		}
	}

	@Override
	public Resource getResource(String path) {
		assert(owner!=null);
		final Module mod = owner.get();
		assert(mod!=null);
		final String modName = mod.getName();
		assert(modName != null);
		log.trace("getResource");
		log.debug("Generating resource for '"+path+"' for module '"+modName+"'");
		if(path.endsWith(".css")) {
			return new StyleResource(mod, resourceDir+path);
		}
		else if(path.endsWith(".js")) {
			return new ScriptResource(mod, resourceDir+path);
		}
		else if(path.endsWith(".jsp")) {
			return new JspResource(mod, resourceDir+path);
		}
		else {
			return new GenericResource(mod, resourceDir+path);
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
		assert(owner!=null);
		final Module mod = owner.get();
		assert(mod!=null);
		Class<?> cls = mod.getClass();
		if(cls != null) {
			return Logger.getLogger(cls);
		}
		return null;
	}

	@Override
	public Domain getDomain(URI uri, boolean create) {
		ModuleManager manager = ModuleManagerFactory.getInstance().getManager();
		Domain domain = manager.getDomain(uri);
		if(domain == null && create) {
			domain = new DomainImpl(uri);
			manager.registerDomain(domain);
		}
		return domain;
	}

	@Override
	public List<Domain> listDomains() {
		return ModuleManagerFactory.getInstance().getManager().listDomains();
	}

}
