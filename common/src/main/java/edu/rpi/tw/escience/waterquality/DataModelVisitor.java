package edu.rpi.tw.escience.waterquality;

import java.util.Map;

import com.hp.hpl.jena.rdf.model.Model;

public interface DataModelVisitor {
	String getName();
	void visit(Model model, Map<String, String> params);
}
