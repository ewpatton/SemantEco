package edu.rpi.tw.escience.darrin;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;

import edu.rpi.tw.escience.semanteco.Domain;
import edu.rpi.tw.escience.semanteco.HierarchicalMethod;
import edu.rpi.tw.escience.semanteco.HierarchyEntry;
import edu.rpi.tw.escience.semanteco.HierarchyVerb;
import edu.rpi.tw.escience.semanteco.Module;
import edu.rpi.tw.escience.semanteco.ModuleConfiguration;
import edu.rpi.tw.escience.semanteco.Request;
import edu.rpi.tw.escience.semanteco.Resource;
import edu.rpi.tw.escience.semanteco.SemantEcoUI;
import edu.rpi.tw.escience.semanteco.query.NamedGraphComponent;
import edu.rpi.tw.escience.semanteco.query.Query;
import edu.rpi.tw.escience.semanteco.query.QueryResource;
import edu.rpi.tw.escience.semanteco.query.Variable;
import static edu.rpi.tw.escience.semanteco.query.Query.VAR_NS;
import edu.rpi.tw.escience.semanteco.ProvidesDomain;
import edu.rpi.tw.escience.semanteco.query.Query.Type;
import edu.rpi.tw.escience.semanteco.query.Query.SortType;
import org.apache.log4j.Logger;


public class DarrinModule implements Module {
	  private static final String RDFS_NS = "http://www.w3.org/2000/01/rdf-schema#";
	  private static final String BINDINGS = "bindings";
	  private static final Logger log = Logger.getLogger(DarrinModule.class);


	private ModuleConfiguration config = null;

	@Override
	public void visit(final Model model, final Request request, final Domain domain) {
		// TODO populate data model
	}

	@Override
	public void visit(final OntModel model, final Request request, final Domain domain) {
		// TODO populate ontology model
	}

	@Override
	public void visit(final Query query, final Request request) {
		// TODO modify queries
	}

	@Override
	public void visit(final SemantEcoUI ui, final Request request) {
		// TODO add resources to display
		Resource res = null;
		Resource res2 = null;

		res = config.getResource("darrinSpeciesHierarchy.js"); 
		res2 =config.getResource("darrinSpeciesHierarchy.jsp");
		ui.addScript(res); 
		ui.addFacet(res2);	     
	}

	/**
	 * Provides the hierarchical behavior for the darrin species module's facet.
	 * 
	 * @param request
	 * @param action
	 * @return
	 */

	@HierarchicalMethod(parameter = "darrinspecies") public
	Collection<HierarchyEntry> querydarrinTaxonomyHM( final Request request,
			final HierarchyVerb action) { List<HierarchyEntry> items = new
			ArrayList<HierarchyEntry>(); if (action == HierarchyVerb.ROOTS) { 
				HierarchyEntry entry = new HierarchyEntry(); //
				
				return queryedarrinTaxonomyHMRoots(request); } 
			else if(action == HierarchyVerb.CHILDREN) { 
					return querydarrinTaxonomyHMChildren(request,(String) request.getParam("darrinspecies")); } 
					//else if (action ==HierarchyVerb.SEARCH) {
					//	return searchDarrinSpecies(request, (String) null);
					//}
								//	request.getParam("string")); } 
				else if (action == HierarchyVerb.PATH_TO_NODE) { 
					return darrinSpeciesPathToNode(request,(String) request.getParam("uri")); } return items; 
				}

	
	  
	   protected Collection<HierarchyEntry> queryedarrinTaxonomyHMRoots(final Request request) { 
		   Collection<HierarchyEntry> entries = new ArrayList<HierarchyEntry>(); // 
		   final Query query =   config.getQueryFactory().newQuery(Type.SELECT);
		    Set<Variable> vars = new LinkedHashSet<Variable>();
		    
	

	   
	   final Variable id = query.getVariable(VAR_NS + "child");
	   final Variable label = query.getVariable(VAR_NS + "label"); final Variable parent =
	    query.getVariable(VAR_NS + "parent");
	    
	    final QueryResource subClassOf = query.getResource(RDFS_NS + "subClassOf");
	    final QueryResource hasLabel = query.getResource(RDFS_NS + "label");
	    
	    final QueryResource darrinTaxonomy = query
	    .getResource("http://darrin-taxonomy");
	    request.getLogger().info("reached darrin-taxonomy \n");
	    
	    vars.add(id); vars.add(label); vars.add(parent); 
	    query.setVariables(vars);
	    query.addOrderBy(label, SortType.ASC); 
	    final NamedGraphComponent graph =
	    query .getNamedGraph("http://darrin-taxonomy"); 
	    graph.addPattern(id,subClassOf, parent); graph.addPattern(id, subClassOf, darrinTaxonomy);
	   graph.addPattern(id, hasLabel, label);
	    
	    String resultStr = config.getQueryExecutor(request).accept("application/json").execute(query); 
	    if (resultStr == null) { return entries; }
	    
	    try { JSONObject results = new JSONObject(resultStr); results =
	    results.getJSONObject("results"); JSONArray bindings =
	    results.getJSONArray(BINDINGS); for (int i = 0; i < bindings.length(); i++)
	    { JSONObject binding = bindings.getJSONObject(i); String subclassId =
	    binding.getJSONObject("child").getString( "value"); String subclassLabel =
	    binding.getJSONObject("label") .getString("value"); HierarchyEntry entry =
	    new HierarchyEntry(); entry.setUri(subclassId);
	    entry.setLabel(subclassLabel); entries.add(entry); } } catch (JSONException
	    e) { log.error("Unable to parse JSON results", e); }
	    
	    
	    
	    return entries;
	    
	    }
	  
	 
	    protected Collection<HierarchyEntry> querydarrinTaxonomyHMChildren( final
	    Request request, final String species) { Collection<HierarchyEntry> entries
	    = new ArrayList<HierarchyEntry>();
	    
	    final Query query = config.getQueryFactory().newQuery(Type.SELECT); 
	    //Variables 
	    final Variable id = query.getVariable(VAR_NS + "child"); 
	    final Variable label = query.getVariable(VAR_NS + "label"); final Variable parent
	    = query.getVariable(VAR_NS + "parent"); // URIs 
	    final QueryResource subClassOf = query.getResource(RDFS_NS + "subClassOf"); final QueryResource
	    classPredicate = query .getResource(
	    "http://purl.org/twc/semantgeo/source/aeap_nys/dataset/dfw_lake_samples/vocab/enhancement/1/class"
	    );
	    
	    final QueryResource classRequiresSubclasses = query .getResource(species);
	    final QueryResource rdfsLabel = query .getResource(RDFS_NS + "label");
	    
	    // build query 
	    Set<Variable> vars = new LinkedHashSet<Variable>();
	    vars.add(id); vars.add(label); vars.add(parent); query.setVariables(vars);
	    query.addOrderBy(label, SortType.ASC); final NamedGraphComponent graph =
	    query .getNamedGraph("http://darrin-taxonomy"); graph.addPattern(id,
	    classPredicate, parent); graph.addPattern(id, classPredicate,
	    classRequiresSubclasses);
	    
	    graph.addPattern(id, rdfsLabel, label); String resultStr =
	    config.getQueryExecutor(request)
	    .accept("application/json").execute(query); if (resultStr == null) { return
	    entries; } try { JSONObject results = new JSONObject(resultStr); results =
	    results.getJSONObject("results"); JSONArray bindings =
	    results.getJSONArray(BINDINGS); for (int i = 0; i < bindings.length(); i++)
	    { JSONObject binding = bindings.getJSONObject(i); HierarchyEntry entry =
	    new HierarchyEntry();
	    entry.setUri(binding.getJSONObject("child").getString("value"));
	    entry.setLabel(binding.getJSONObject("label") .getString("value"));
	    
	    try { entry.setParent(URI.create(binding.getJSONObject("parent")
	    .getString("value"))); } catch (Exception e) { }
	    
	    entries.add(entry); } } catch (JSONException e) {
	    log.error("Unable to parse JSON results", e); }
	    
	    return entries; } protected Collection<HierarchyEntry>
	    searchDarrinSpecies(final Request request, final String str) { final
	    Collection<HierarchyEntry> entries = new ArrayList<HierarchyEntry>();
	    return entries; } protected Collection<HierarchyEntry>
	    darrinSpeciesPathToNode(final Request request, final String node) { final
	    Collection<HierarchyEntry> entries = new ArrayList<HierarchyEntry>();
	    return entries;
	    
	    }
	   
	

	@Override
	public String getName() {
		return "Darrin";
	}

	@Override
	public int getMajorVersion() {
		return 1;
	}

	@Override
	public int getMinorVersion() {
		return 0;
	}

	@Override
	public String getExtraVersion() {
		return null;
	}

	@Override
	public void setModuleConfiguration(final ModuleConfiguration config) {
		this.config = config;
	}

}
