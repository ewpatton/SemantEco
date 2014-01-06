package edu.rpi.tw.escience.semanteco.ui;


import org.apache.log4j.Logger;


import edu.rpi.tw.escience.semanteco.impl.ModuleManagerFactory;
import edu.rpi.tw.escience.semanteco.request.DummyRequest;
import edu.rpi.tw.escience.semanteco.ModuleManager;
import edu.rpi.tw.escience.semanteco.SemantEcoUI;

/**
 * SemantEcoUIFactory gets a UI object that can be manipulated by
 * different modules that is then ultimately used to generate the
 * user-facing website.
 * 
 * @author ewpatton
 *
 */
public class SemantEcoUIFactory {
	
	private static volatile SemantEcoUIFactory instance = null;
	private long lastModified = 0;
	private SemantEcoUI ui = null;
	private static Logger log = Logger.getLogger(SemantEcoUIFactory.class);
	
	protected SemantEcoUIFactory() {
		
	}
	
	/**
	 * Gets the singleton instance of the UIFactory
	 * @return
	 */
	public static SemantEcoUIFactory getInstance() {
		log.trace("getInstance");
		if(instance == null) {
			instance = new SemantEcoUIFactory();
		}
		return instance;
	}
	
	/**
	 * Gets a UI object for the factory. This object is cached until
	 * a module is changed at which point a new UI object will be
	 * generated.
	 * @return
	 */
	public SemantEcoUI getUI() {
		log.trace("getUI");
		ModuleManager mgr = ModuleManagerFactory.getInstance().getManager();
		if(ui == null || lastModified < mgr.getLastModified()) {
			ui = new SemantEcoWebUIImpl();
			log.debug("building user interface");
			mgr.buildUserInterface(ui, new DummyRequest(mgr));
			lastModified = mgr.getLastModified();
		}
		return ui;
	}
}
