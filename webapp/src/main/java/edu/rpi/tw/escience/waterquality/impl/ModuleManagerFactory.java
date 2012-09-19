package edu.rpi.tw.escience.waterquality.impl;

import edu.rpi.tw.escience.waterquality.ModuleManager;

/**
 * ModuleManagerFactory provides an interface for the core of SemantAqua to obtain
 * a reference to a ModuleManager in order to manipulate or use the various
 * modules available.
 * 
 * @author ewpatton
 *
 */
public class ModuleManagerFactory {

	private static ModuleManagerFactory instance = new ModuleManagerFactory();
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
	}
	
	protected void setManagerFactory(ModuleManagerFactory factory) {
		instance = factory;
	}

	/**
	 * Gets a reference to the ModuleManagerFactory
	 * @return
	 */
	public static ModuleManagerFactory getInstance() {
		return instance;
	}

}
