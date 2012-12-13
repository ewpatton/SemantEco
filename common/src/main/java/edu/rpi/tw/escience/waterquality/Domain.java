package edu.rpi.tw.escience.waterquality;

import java.net.URI;
import java.util.List;

/**
 * The Domain interface allows a module to describe
 * the various properties of a scientific domain
 * that it provides data for.
 * 
 * Domains provide data sources, data types, and
 * regulations. These are aggregated together by
 * the core SemantEco system and are exposed to
 * the user via the data-source, data-type, and 
 * regulation modules, resp.
 * 
 * @author ewpatton
 *
 */
public interface Domain {
	/**
	 * Gets the URI of the domain
	 * @return
	 */
	URI getUri();
	
	/**
	 * Adds a data source to the domain.
	 * @param sourceUri URI representing the source of the data
	 * @param label A human-readable label used in UI components
	 */
	void addSource(URI sourceUri, String label);
	
	/**
	 * Adds a data type to the domain.
	 * @param id A unique identifier used in an HTML id attribute
	 * @param name Label used in UI components
	 * @param icon A resource representing an icon for the
	 * data type so it can be rendered in the map display.
	 */
	void addDataType(String id, String name, Resource icon);
	
	/**
	 * Adds a regulation to the domain.
	 * @param regulationUri OWL file containing the regulatory data.
	 * @param label A label for UI components
	 */
	void addRegulation(URI regulationUri, String label);
	
	/**
	 * Gets the list of sources in this domain
	 * @return
	 */
	List<URI> getSources();
	
	/**
	 * Gets the list of regulations in this domain
	 * @return
	 */
	List<URI> getRegulations();
	
	/**
	 * Gets the list of data types in this domain
	 * @return
	 */
	List<String> getDataTypes();
	
	/**
	 * Gets the label for a datatype given its id
	 * @param id An id for the datatype. Supplied in {@link #addDataType(String, String, Resource)}
	 * and retrievable by {@link #getDataTypes()}
	 * @return Label for the data type, or null if id is not defined
	 */
	String getDataTypeName(String id);
	
	/**
	 * Gets the icon for a datatype given its id
	 * @param id An id for the datatype. Supplied in {@link #addDataType(String, String, Resource)}
	 * and retrievable by {@link #getDataTypes()}
	 * @return Icon resource for the data type, or null if id is not defined
	 */
	Resource getDataTypeIcon(String id);
	
	/**
	 * Gets the label for a data source in this domain
	 * @param uri URI of a data source in the domain
	 * @return Label for the URI, or null if uri is not defined
	 */
	String getLabelForSource(URI uri);
	
	/**
	 * Gets the label for this domain
	 * @return
	 */
	String getLabel();
	
	/**
	 * Sets a label for this domain to be used when
	 * displaying it in the user interface.
	 * @param label Human-readable label for this domain
	 */
	void setLabel(String label);
	
	/**
	 * Gets the label for a regulation in this domain.
	 * @param uri Regulation URI
	 * @return Label for the URI, or null if uri is not defined
	 */
	String getLabelForRegulation(URI uri);
}
