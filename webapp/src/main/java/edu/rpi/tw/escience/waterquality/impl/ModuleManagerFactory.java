package edu.rpi.tw.escience.waterquality.impl;

import edu.rpi.tw.escience.waterquality.ModuleManager;

public class ModuleManagerFactory {

	private static ModuleManagerFactory instance = new ModuleManagerFactory();
	private ModuleManager manager = null;
	
	protected ModuleManagerFactory() {
		manager = new ModuleManagerImpl();
	}
	
	public ModuleManager getManager() {
		return manager;
	}
	
	public void setModulePath(String path) {
		manager = new ModuleManagerImpl(path);
	}
	
	protected void setManagerFactory(ModuleManagerFactory factory) {
		instance = factory;
	}

	public static ModuleManagerFactory getInstance() {
		return instance;
	}

}
