package edu.rpi.tw.escience.waterquality.ui;

import org.apache.log4j.Logger;

import edu.rpi.tw.escience.waterquality.ModuleManager;
import edu.rpi.tw.escience.waterquality.Request;
import edu.rpi.tw.escience.waterquality.SemantAquaUI;
import edu.rpi.tw.escience.waterquality.impl.ModuleManagerFactory;

/**
 * SemantAquaUIFactory gets a UI object that can be manipulated by
 * different modules that is then ultimately used to generate the
 * user-facing website.
 * 
 * @author ewpatton
 *
 */
public class SemantAquaUIFactory {
	
	private static SemantAquaUIFactory instance = null;
	private long lastModified = 0;
	private SemantAquaUI ui = null;
	private static Logger log = Logger.getLogger(SemantAquaUIFactory.class);
	
	protected SemantAquaUIFactory() {
		
	}
	
	/**
	 * Gets the singleton instance of the UIFactory
	 * @return
	 */
	public static SemantAquaUIFactory getInstance() {
		log.trace("getInstance");
		if(instance == null) {
			instance = new SemantAquaUIFactory();
		}
		return instance;
	}
	
	/**
	 * Gets a UI object for the factory. This object is cached until
	 * a module is changed at which point a new UI object will be
	 * generated.
	 * @return
	 */
	public SemantAquaUI getUI() {
		log.trace("getUI");
		ModuleManager mgr = ModuleManagerFactory.getInstance().getManager();
		if(ui == null || lastModified < mgr.getLastModified()) {
			ui = new SemantAquaUIImpl();
			log.debug("building user interface");
			mgr.buildUserInterface(ui, new NOPRequest());
			lastModified = mgr.getLastModified();
		}
		return ui;
	}
	
	private static class NOPRequest implements Request {

		@Override
		public String[] getParam(String key) {
			return null;
		}

		@Override
		public Logger getLogger() {
			return Logger.getLogger(SemantAquaUI.class);
		}
		
	}
}
