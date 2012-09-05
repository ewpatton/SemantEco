package edu.rpi.tw.escience.waterquality;

/**
 * The Module interface defines the API between the core SemantAqua system
 * and modules that extend the service by adding new facets, data, or ontologies.
 * 
 * @author ewpatton
 *
 */
public interface Module extends DataModelVisitor, OntModelVisitor, QueryVisitor, UIVisitor {
	String getName();
	int getMajorVersion();
	int getMinorVersion();
	String getExtraVersion();
	void setModuleConfiguration(ModuleConfiguration config);
}
