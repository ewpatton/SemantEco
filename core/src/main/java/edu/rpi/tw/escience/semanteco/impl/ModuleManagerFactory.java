package edu.rpi.tw.escience.semanteco.impl;

import edu.rpi.tw.escience.semanteco.ModuleManager;

/**
 * ModuleManagerFactory provides an interface for the core of SemantEco to obtain
 * a reference to a ModuleManager in order to manipulate or use the various
 * modules available.
 * 
 * @author ewpatton
 *
 */
public class ModuleManagerFactory {

	private static volatile ModuleManagerFactory instance = new ModuleManagerFactory();
	private ModuleManager manager = null;
	
	protected ModuleManagerFactory() {
		manager = new ModuleManagerImpl();
	}
	
	/**
	 * Gets the current ModuleManager
	 * @return
	 */
	public ModuleManager getManager() {
		return manager;
	}
	
	/**
	 * Sets the path where the manager should look for modules
	 * @param path
	 */
	public void setModulePath(String path) {
		manager = new ModuleManagerImpl(path);
		((ModuleManagerImpl) manager).initialize();
	}
	
	protected static void setManagerFactory(ModuleManagerFactory factory) {
		instance = factory;
	}

	/**
	 * Gets a reference to the ModuleManagerFactory
	 * @return
	 */
	public static ModuleManagerFactory getInstance() {
		return instance;
	}

	/**
	 * Destroys the factory instance for memory management purposes.
	 */
	public static void destroy() {
		if(instance == null) {
			return;
		}
		ModuleManagerImpl impl = (ModuleManagerImpl)instance.manager;
		if(impl != null) {
			impl.stopListening();
		}
		instance.manager = null;
		instance = null;
	}

}
