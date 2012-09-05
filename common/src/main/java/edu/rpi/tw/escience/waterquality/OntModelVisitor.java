package edu.rpi.tw.escience.waterquality;

import java.util.Map;

import com.hp.hpl.jena.ontology.OntModel;

/**
 * The SemantAqua core will pass an OntModel object to all
 * OntModelVisitor objects in order to construct a final ontology
 * used for reasoning over the data model.
 * 
 * @author ewpatton
 *
 */
public interface OntModelVisitor {
	
	/**
	 * Gets the name of this OntModelVisitor for
	 * provenance purposes.
	 * @return
	 */
	String getName();
	
	/**
	 * Allows this visitor to make modifications to the OntModel
	 * before reasoning is executed.
	 * @param model OntModel used for reasoning
	 * @param params Parameters passed from the client in the RESTful call
	 */
	void visit(OntModel model, Map<String, String> params);
}
