package edu.rpi.tw.escience.waterquality;

import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;

import edu.rpi.tw.escience.waterquality.query.Query;

public interface ModuleManager {
	Module getModuleByName(String name);
	void buildUserInterface(SemantAquaUI ui, Map<String, String> params);
	void buildOntologyModel(OntModel model, Map<String, String> params);
	void buildDataModel(Model model, Map<String, String> params);
	String updateFragmentForFacet(Module module, Map<String, String> params);
	void augmentQuery(Query query, Map<String, String> params);
	void augmentQuery(Query query, Map<String, String> params, Module originator);
	List<Module> listModules();
	long getLastModified();
}
