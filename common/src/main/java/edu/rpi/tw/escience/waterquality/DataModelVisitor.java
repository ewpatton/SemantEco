package edu.rpi.tw.escience.waterquality;

import java.util.Map;

import com.hp.hpl.jena.rdf.model.Model;

/**
 * When the SemantAqua core is preparing a model for querying,
 * instances of DataModelVisitor will be called to modify
 * the data model by reading in new datasets.
 * 
 * @author ewpatton
 *
 */
public interface DataModelVisitor {
	
	/**
	 * Gets the name of this data model visitor for
	 * provenance purposes.
	 * @return
	 */
	String getName();

	/**
	 * Allows the visitor to visit the model
	 * @param model Data model to be modified by the visitor
	 * @param params Parameters passed through the RESTful interface by the caller
	 */
	void visit(Model model, Map<String, String> params);
}
