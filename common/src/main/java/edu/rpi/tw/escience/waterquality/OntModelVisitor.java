package edu.rpi.tw.escience.waterquality;

import java.util.Map;

import com.hp.hpl.jena.ontology.OntModel;

public interface OntModelVisitor {
	String getName();
	void visit(OntModel model, Map<String, String> params);
}
