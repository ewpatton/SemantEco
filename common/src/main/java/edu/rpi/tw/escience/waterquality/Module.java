package edu.rpi.tw.escience.waterquality;

/**
 * The Module interface defines the API between the core SemantAqua system
 * and modules that extend the service by adding new facets, data, or ontologies.
 * 
 * @author ewpatton
 *
 */
public interface Module extends DataModelVisitor, OntModelVisitor, QueryVisitor, UIVisitor {
	
	/**
	 * Gets the name for this module
	 * @return
	 */
	String getName();
	
	/**
	 * Gets the major version number for this module
	 * @return
	 */
	int getMajorVersion();
	
	/**
	 * Gets the minor version number for this module
	 * @return
	 */
	int getMinorVersion();
	
	/**
	 * Gets any extra version information for this module
	 * @return
	 */
	String getExtraVersion();
	
	/**
	 * Sets the configuration for this module
	 * @param config
	 */
	void setModuleConfiguration(ModuleConfiguration config);
}
