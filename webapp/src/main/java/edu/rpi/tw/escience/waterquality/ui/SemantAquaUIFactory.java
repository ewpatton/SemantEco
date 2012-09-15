package edu.rpi.tw.escience.waterquality.ui;

import java.util.HashMap;

import org.apache.log4j.Logger;

import edu.rpi.tw.escience.waterquality.ModuleManager;
import edu.rpi.tw.escience.waterquality.SemantAquaUI;
import edu.rpi.tw.escience.waterquality.impl.ModuleManagerFactory;

public class SemantAquaUIFactory {
	
	private static SemantAquaUIFactory instance = null;
	private long lastModified = 0;
	private SemantAquaUI ui = null;
	private static Logger log = Logger.getLogger(SemantAquaUIFactory.class);
	
	public static SemantAquaUIFactory getInstance() {
		log.trace("getInstance");
		if(instance == null) {
			instance = new SemantAquaUIFactory();
		}
		return instance;
	}
	
	public SemantAquaUI getUI() {
		log.trace("getUI");
		ModuleManager mgr = ModuleManagerFactory.getInstance().getManager();
		if(ui == null || lastModified < mgr.getLastModified()) {
			ui = new SemantAquaUIImpl();
			log.debug("building user interface");
			mgr.buildUserInterface(ui, new HashMap<String, String>());
			lastModified = mgr.getLastModified();
		}
		return ui;
	}
}
