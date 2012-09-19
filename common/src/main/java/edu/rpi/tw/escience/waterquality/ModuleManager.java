package edu.rpi.tw.escience.waterquality;

import java.util.List;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;

import edu.rpi.tw.escience.waterquality.query.Query;

/**
 * The ModuleManager provides a mechanism for collecting, configuring, and
 * processing modules in the SemantAqua framework.
 * 
 * @author ewpatton
 *
 */
public interface ModuleManager {
	/**
	 * Gets a module given its processed name. The processed name
	 * is the name of the module with whitespace replaced with dashes,
	 * e.g. "Data-Source" instead of "Data Source"
	 * @param name
	 * @return
	 */
	Module getModuleByName(String name);
	
	/**
	 * Requests that the ModuleManager help construct the UI by having
	 * each of its modules visit the provided SemantAquaUI object.
	 * @param ui
	 * @param params
	 */
	void buildUserInterface(SemantAquaUI ui, Request request);
	
	/**
	 * Requests that the ModuleManager help construct the ontology model
	 * by having each of its modules visit the provided OntModel object.
	 * @param model
	 * @param params
	 */
	void buildOntologyModel(OntModel model, Request request);
	
	/**
	 * Requests that the ModuleManager help construct the data model
	 * by having each of its modules visit the provided Model object.
	 * @param model
	 * @param params
	 */
	void buildDataModel(Model model, Request request);
	
	/**
	 * Requests that the provided module give an updated code fragment
	 * for its facet due to parameter changes on the client.
	 * @param module
	 * @param params
	 * @return
	 */
	String updateFragmentForFacet(Module module, Request request);
	
	/**
	 * Allows any modules in this ModuleManager interface to change the provided
	 * query prior to execution. Optional parameters passed from the client are
	 * provided.
	 * @param query
	 * @param params
	 */
	void augmentQuery(Query query, Request request);
	
	/**
	 * Allows any modules in this ModuleManager interface to change the provided
	 * query prior to execution. Optional parameters passed from the client are
	 * provided. If this query was originated from a module, that is passed as
	 * well so that the ModuleManager can ignore it while executing.
	 * @param query
	 * @param params
	 * @param originator
	 */
	void augmentQuery(Query query, Request request, Module originator);
	
	/**
	 * Lists all of the modules managed by this ModuleManager.
	 * @return
	 */
	List<Module> listModules();
	
	/**
	 * Returns the last modified time in milliseconds of the manager. This is
	 * primarily useful for when modules are redeployed in SemantAqua so that
	 * other subsystems that rely on being fresh relative to the modules can
	 * perform additional updates (e.g. SemantAquaUI)
	 * @return
	 */
	long getLastModified();
}
