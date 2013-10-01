package edu.rpi.tw.escience.facetedmodule;

import static edu.rpi.tw.escience.semanteco.query.Query.VAR_NS;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
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
import edu.rpi.tw.escience.semanteco.QueryMethod;
import edu.rpi.tw.escience.semanteco.Request;
import edu.rpi.tw.escience.semanteco.Resource;
import edu.rpi.tw.escience.semanteco.SemantEcoUI;
import edu.rpi.tw.escience.semanteco.query.GraphComponentCollection;
import edu.rpi.tw.escience.semanteco.query.NamedGraphComponent;
import edu.rpi.tw.escience.semanteco.query.Query;
import edu.rpi.tw.escience.semanteco.query.QueryResource;
import edu.rpi.tw.escience.semanteco.query.Variable;
import edu.rpi.tw.escience.semanteco.query.Query.Type;

public class FacetedModuleModule implements Module {

	private static final String RDFS_NS = "http://www.w3.org/2000/01/rdf-schema#";
	private static final String RDF_NS = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	private Logger log = Logger.getLogger(FacetedModuleModule.class);


	//graph<http://dataone.tw.rpi.edu/freebaseStatements>{
	//http://dataone.org/dois works


	/*
	 * select * where {

graph <http://dataone.org/dois>{

?a ?b ?c

}} LIMIT 200

select * where {

graph <http://dataone.tw.rpi.edu/inferred2>{

?a ?b ?c

}} LIMIT 200

http://dataone.tw.rpi.edu/freebaseStatements on local...


//test awesome query method
//test query for facet and find out the bar.
 //also look at older version of s2s:
	 * 
	 * prefix skos: <http://www.w3.org/2004/02/skos/core#>

select * where {

graph <http://dataone.tw.rpi.edu/inf>{

?ecosystemsSubclass skos:broaderTransitive ?c .

}} LIMIT 200

	 */

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
	}

	@Override
	public String getName() {
		return "FacetedModule";
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

	/**
	 * will be called on node selection, which will ultimately call the query Executor
	 * @param request
	 * @return
	 * @throws JSONException
	 */
	@QueryMethod
	protected String searchAnnotations(final Request request) throws JSONException{
		final Collection<HierarchyEntry> entries ;
		String str = (String) request.getParam("search");
		//	entries = this.annotatorTester.searchAnnotations(str);
		//	return entries.toString();

		//must use a query executor to "run" all the visitors
		return str;
	}

	@HierarchicalMethod(parameter = "classes")
	public Collection<HierarchyEntry> querySNOMED_HM(final Request request, final HierarchyVerb action) throws JSONException, UnsupportedEncodingException {
		List<HierarchyEntry> items = new ArrayList<HierarchyEntry>();

		if(action == HierarchyVerb.ROOTS) {

			return   querySNOMED_HMRoots(request);
		} else if ( action == HierarchyVerb.CHILDREN ) {
			return  querySNOMED_HMChildren(request, (String) request.getParam("classes"));
		} 

		//	else if ( action == HierarchyVerb.SEARCH ) {
		//		return searchAnnotatorClass( request, (String) request.getParam("string") );
		//	} 

		//	else if ( action == HierarchyVerb.PATH_TO_NODE ) {
		//		return annotatorClassToNode( request, (String) request.getParam("uri") );
		//	}	
		return items;
	}

	private Collection<HierarchyEntry> querySNOMED_HMRoots(final Request request) throws UnsupportedEncodingException {		
		System.out.println("queryClassHMRoots");
		return null;// this.annotatorTester.getChildClasses("root");	
	}

	private Collection<HierarchyEntry> querySNOMED_HMChildren(final Request request, String clazz) throws JSONException {
		//AnnotatorTester ann = new AnnotatorTester();
		System.out.println("queryClassHMChildren");
		return null;// this.annotatorTester.getChildClasses(clazz);		
	}


	@HierarchicalMethod(parameter = "chemicals")
	public Collection<HierarchyEntry> queryChemicalHM(final Request request, final HierarchyVerb action) throws JSONException, UnsupportedEncodingException {
		List<HierarchyEntry> items = new ArrayList<HierarchyEntry>();

		if(action == HierarchyVerb.ROOTS) {

			return   queryChemicalHMRoots(request);
		} else if ( action == HierarchyVerb.CHILDREN ) {
			return  queryChemical_HMChildren(request, (String) request.getParam("classes"));
		} 

		//	else if ( action == HierarchyVerb.SEARCH ) {
		//		return searchAnnotatorClass( request, (String) request.getParam("string") );
		//	} 

		//	else if ( action == HierarchyVerb.PATH_TO_NODE ) {
		//		return annotatorClassToNode( request, (String) request.getParam("uri") );
		//	}	
		return items;
	}

	@HierarchicalMethod(parameter = "features")
	public Collection<HierarchyEntry> queryGeospatialFeaturesHM(final Request request, final HierarchyVerb action) throws JSONException, UnsupportedEncodingException {
		List<HierarchyEntry> items = new ArrayList<HierarchyEntry>();

		if(action == HierarchyVerb.ROOTS) {

			return   queryGeospatialFeaturesHMRoots(request);
		} else if ( action == HierarchyVerb.CHILDREN ) {
			//		return  queryGeospatialFeaturesHMChildren(request, (String) request.getParam("classes"));
		} 

		//	else if ( action == HierarchyVerb.SEARCH ) {
		//		return searchAnnotatorClass( request, (String) request.getParam("string") );
		//	} 

		//	else if ( action == HierarchyVerb.PATH_TO_NODE ) {
		//		return annotatorClassToNode( request, (String) request.getParam("uri") );
		//	}	
		return items;
	}


	@HierarchicalMethod(parameter = "features")
	public Collection<HierarchyEntry> queryOrganismsHM(final Request request, final HierarchyVerb action) throws JSONException, UnsupportedEncodingException {
		List<HierarchyEntry> items = new ArrayList<HierarchyEntry>();

		if(action == HierarchyVerb.ROOTS) {

			return  queryOrganismsHMRoots(request);
		} else if ( action == HierarchyVerb.CHILDREN ) {
			//	return  queryOrganismsHMChildren(request, (String) request.getParam("classes"));
		} 

		//	else if ( action == HierarchyVerb.SEARCH ) {
		//		return searchAnnotatorClass( request, (String) request.getParam("string") );
		//	} 

		//	else if ( action == HierarchyVerb.PATH_TO_NODE ) {
		//		return annotatorClassToNode( request, (String) request.getParam("uri") );
		//	}	
		return items;
	}

	/*
	@QueryMethod
	public Collection<HierarchyEntry> chemicalUpdate(final Request request) throws JSONException{
		return null;

	}

	@QueryMethod
	public Collection<HierarchyEntry> organismUpdate(final Request request) throws JSONException{
		return null;

	}

	@QueryMethod
	public Collection<HierarchyEntry> featureUpdate(final Request request) throws JSONException{
		return null;

	}

	@QueryMethod
	public Collection<HierarchyEntry> topicUpdate(final Request request) throws JSONException{
		return null;

	}
	 */

	public Query processFacetRequests(Request request, Query query) throws JSONException{

		if(request.getParam("chemicals") != null){
			JSONArray chemicals = (JSONArray) request.getParam("chemicals");
			query = addConstraintsForFacet(query, chemicals);
		}
		if(request.getParam("features") != null){
			JSONArray features = (JSONArray) request.getParam("features");
			query = addConstraintsForFacet(query, features);
		}
		if(request.getParam("organisms") != null){
			JSONArray organisms = (JSONArray) request.getParam("organisms");
			query = addConstraintsForFacet(query, organisms);
		}
		if(request.getParam("topics") != null){
			JSONArray topics = (JSONArray) request.getParam("topics");
			query = addConstraintsForFacet(query, topics);
		}



		return query;
	}

	public Query addConstraintsForFacet(Query query, JSONArray entityArray) throws JSONException{

		// Variables
		final Variable goal = query.getVariable(VAR_NS + "goal");
		final Variable label = query.getVariable(VAR_NS + "label");
		final Variable anAbstract = query.getVariable(VAR_NS + "abstract");
		final Variable v1 = query.getVariable(VAR_NS + "v1");
		final QueryResource hasKeyword = query.getResource( "http://dataone.tw.rpi.edu/hasKeyword");
		final QueryResource subClassOf = query.getResource( "http://www.w3.org/2000/01/rdf-schema#subClassOf");

		//loop over the array
		for (int i = 0; i < entityArray.length(); i++) {
			String entity = (String) entityArray.get(0);
			QueryResource entityResource = query.getResource(entity);
			
			//you want to union a graph component and a clause with two graph components
			//query.get
			

			GraphComponentCollection graph1 = query.getNamedGraph("http://dataone.tw.rpi.edu/inferred2");
			graph1.addPattern(v1, subClassOf, entityResource);
			GraphComponentCollection graph2 = query.getNamedGraph("http://dataone.tw.rpi.edu/dois2");
			graph2.addPattern(goal, hasKeyword, v1);

			return query;				
		}


		/*
		JSONArray chemicals = (JSONArray) request.getParam("chemicals");
		String chemical = (String) chemicals.get(0);
		QueryResource chemicalResource = query.getResource(chemical);

		// Variables
		final Variable goal = query.getVariable(VAR_NS + "goal");
		final Variable label = query.getVariable(VAR_NS + "label");
		final Variable anAbstract = query.getVariable(VAR_NS + "abstract");
		final Variable v1 = query.getVariable(VAR_NS + "v1");
		final QueryResource hasKeyword = query.getResource( "http://dataone.tw.rpi.edu/hasKeyword");
		final QueryResource subClassOf = query.getResource( "http://www.w3.org/2000/01/rdf-schema#subClassOf");

		Set<Variable> vars = new LinkedHashSet<Variable>();
		vars.add(goal);
		//vars.add(label);
		//vars.add(anAbstract);
		vars.add(v1);
		query.setVariables(vars);
		// add patterns to graph1
		String BINDINGS = "bindings";

		GraphComponentCollection graph1 = query.getNamedGraph("http://dataone.tw.rpi.edu/inferred2");
		graph1.addPattern(v1, subClassOf, chemicalResource);
		GraphComponentCollection graph2 = query.getNamedGraph("http://dataone.tw.rpi.edu/dois2");
		graph2.addPattern(goal, hasKeyword, v1);


		// add patterns to graph2
		System.out.println("results: " +  config.getQueryExecutor(request).accept("application/json").execute("http://data1.tw.rpi.edu/sparql",query));
		return config.getQueryExecutor(request).accept("application/json").execute("http://data1.tw.rpi.edu/sparql",query);	
		 */

		return null;
	}


	@HierarchicalMethod(parameter = "topics")
	public Collection<HierarchyEntry> queryTopicsHM(final Request request, final HierarchyVerb action) throws JSONException, UnsupportedEncodingException {
		List<HierarchyEntry> items = new ArrayList<HierarchyEntry>();

		if(action == HierarchyVerb.ROOTS) {

			return  queryTopicsHMRoots(request);
		} else if ( action == HierarchyVerb.CHILDREN ) {
			//	return  queryOrganismsHMChildren(request, (String) request.getParam("classes"));
		} 

		//	else if ( action == HierarchyVerb.SEARCH ) {
		//		return searchAnnotatorClass( request, (String) request.getParam("string") );
		//	} 

		//	else if ( action == HierarchyVerb.PATH_TO_NODE ) {
		//		return annotatorClassToNode( request, (String) request.getParam("uri") );
		//	}	
		return items;
	}




	@QueryMethod
	public String updateResults(final Request request) throws JSONException{

		/*
		JSONObject serviceResults = null;
		JSONArray expansion = (JSONArray) request.getParam("expansionTerm");
		System.out.println("expansion: " + expansion.toString());
		serviceResults =  searchSolr(Orify(expansion));
		return serviceResults.toString();
		 */
		System.out.println("server updateResults");

		//implement a query that takes chemical parameters


		/*
		 * SELECT DISTINCT ?goal ?label ?Abstract WHERE { { { 


GRAPH <http://dataone.tw.rpi.edu/inferred2> { 
?v11 <http://www.w3.org/2000/01/rdf-schema#subClassOf> <http://freebase.com#Chemical_Compound> . } 

GRAPH <http://dataone.tw.rpi.edu/dois2> { ?goal <http://dataone.tw.rpi.edu/hasKeyword> ?v11 . }  } 

UNION { GRAPH <http://dataone.tw.rpi.edu/dois2> { ?goal <http://dataone.tw.rpi.edu/hasKeyword> <http://freebase.com#Chemical_Compound> . } }  } 
?goal <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.org/ontology/bibo/Document> . 

OPTIONAL { GRAPH <http://dataone.tw.rpi.edu/dois2> { ?goal <http://dataone.tw.rpi.edu/hasTitle> ?label . } }  
OPTIONAL { GRAPH <http://dataone.tw.rpi.edu/dois2> { ?goal <http://dataone.tw.rpi.edu/hasAbstract> ?Abstract . } }  

} LIMIT 10 OFFSET 0

SELECT (count(DISTINCT ?goal) as ?count) WHERE {

{ 

{GRAPH <http://dataone.tw.rpi.edu/inferred2> { ?v11 <http://www.w3.org/2000/01/rdf-schema#subClassOf> <http://freebase.com#Chemical_Compound> . } 
GRAPH <http://dataone.tw.rpi.edu/dois2> { ?goal <http://dataone.tw.rpi.edu/hasKeyword> ?v11 . } } 

UNION { GRAPH <http://dataone.tw.rpi.edu/dois2> { ?goal <http://dataone.tw.rpi.edu/hasKeyword> <http://freebase.com#Chemical_Compound> . } }  }{ { 


GRAPH <http://dataone.tw.rpi.edu/inferred2> { ?v21 <http://www.w3.org/2000/01/rdf-schema#subClassOf> <http://freebase.com#Country> . } 
GRAPH <http://dataone.tw.rpi.edu/dois2> { ?goal <http://dataone.tw.rpi.edu/hasKeyword> ?v21 . }  } UNION { 
GRAPH <http://dataone.tw.rpi.edu/dois2> { ?goal <http://dataone.tw.rpi.edu/hasKeyword> <http://freebase.com#Country> . } }  } 

?goal <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.org/ontology/bibo/Document> . OPTIONAL { 
GRAPH <http://dataone.tw.rpi.edu/dois2> { ?goal <http://dataone.tw.rpi.edu/hasTitle> ?label . } }  OPTIONAL { 
GRAPH <http://dataone.tw.rpi.edu/dois2> { ?goal <http://dataone.tw.rpi.edu/hasAbstract> ?Abstract . } 


}  }
		 */
		final Query query = config.getQueryFactory().newQuery(Type.SELECT);

		JSONArray chemicals = (JSONArray) request.getParam("chemicals");
		String chemical = (String) chemicals.get(0);
		QueryResource chemicalResource = query.getResource(chemical);

		// Variables
		final Variable goal = query.getVariable(VAR_NS + "goal");
		final Variable label = query.getVariable(VAR_NS + "label");
		final Variable anAbstract = query.getVariable(VAR_NS + "abstract");
		final Variable v1 = query.getVariable(VAR_NS + "v1");
		final QueryResource hasKeyword = query.getResource( "http://dataone.tw.rpi.edu/hasKeyword");
		final QueryResource subClassOf = query.getResource( "http://www.w3.org/2000/01/rdf-schema#subClassOf");

		Set<Variable> vars = new LinkedHashSet<Variable>();
		vars.add(goal);
		//vars.add(label);
		//vars.add(anAbstract);
		vars.add(v1);
		query.setVariables(vars);
		// add patterns to graph1
		String BINDINGS = "bindings";

		GraphComponentCollection graph1 = query.getNamedGraph("http://dataone.tw.rpi.edu/inferred2");
		graph1.addPattern(v1, subClassOf, chemicalResource);
		GraphComponentCollection graph2 = query.getNamedGraph("http://dataone.tw.rpi.edu/dois2");
		graph2.addPattern(goal, hasKeyword, v1);


		// add patterns to graph2
		System.out.println("results: " +  config.getQueryExecutor(request).accept("application/json").execute("http://data1.tw.rpi.edu/sparql",query));
		return config.getQueryExecutor(request).accept("application/json").execute("http://data1.tw.rpi.edu/sparql",query);

		//this should be handled like a result set, not like a hierarchy result.
		//look at species facet




		/*
		if (resultStr == null) {
			return null;
		}		try {
			JSONObject results = new JSONObject(resultStr);
			results = results.getJSONObject("results");
			JSONArray bindings = results.getJSONArray(BINDINGS);
			for (int i = 0; i < bindings.length(); i++) {
				JSONObject binding = bindings.getJSONObject(i);
				String subclassId = binding.getJSONObject("child").getString(
						"value");
				String subclassLabel = binding.getJSONObject("label")
						.getString("value");
				HierarchyEntry entry = new HierarchyEntry();
				entry.setUri(subclassId);
				entry.setLabel(subclassLabel);
				entries.add(entry);
			}
		} catch (JSONException e) {
			log.error("Unable to parse JSON results", e);
		}
		return entries;
		 */
	}

	private Collection<HierarchyEntry> queryChemicalHMRoots(final Request request) throws UnsupportedEncodingException, JSONException {		
		System.out.println("queryChemicalHMRoots");

		Query query = config.getQueryFactory().newQuery(Type.SELECT);

		query = processFacetRequests(request, query);


		// Variables
		final Variable id = query.getVariable(VAR_NS + "child");
		final Variable label = query.getVariable(VAR_NS + "label");
		final Variable parent = query.getVariable(VAR_NS + "parent");				
		final Variable goal = query.getVariable(VAR_NS + "goal");


		// URIs
		final QueryResource category = query.getResource( "http://dataone.org#Category");
		final QueryResource hasLabel = query.getResource(RDFS_NS + "label");
		final QueryResource chemicalEntity = query.getResource("http://freebase.com#Chemical_Entity");
		final QueryResource hasKeyword = query.getResource( "http://dataone.tw.rpi.edu/hasKeyword");


		//final NamedGraphComponent graph = query.getNamedGraph("http://dataone.org/dois");
		final NamedGraphComponent graph = query.getNamedGraph("http://dataone.tw.rpi.edu/dois2");

		Set<Variable> vars = new LinkedHashSet<Variable>();
		vars.add(id);
		vars.add(label);
		vars.add(parent);
		query.setVariables(vars);
		graph.addPattern(id, category, parent);
		graph.addPattern(id, category, chemicalEntity);
		graph.addPattern(id, hasLabel, label);

		graph.addPattern(goal, hasKeyword, id);

		//should also update the inferred graph patterns
		// v2 subClassOf  id


		String BINDINGS = "bindings";

		Collection<HierarchyEntry> entries = new ArrayList<HierarchyEntry>();
		String resultStr = config.getQueryExecutor(request).accept("application/json").execute("http://data1.tw.rpi.edu/sparql",query);
		if (resultStr == null) {
			return entries;
		}		try {
			JSONObject results = new JSONObject(resultStr);
			results = results.getJSONObject("results");
			JSONArray bindings = results.getJSONArray(BINDINGS);
			for (int i = 0; i < bindings.length(); i++) {
				JSONObject binding = bindings.getJSONObject(i);
				String subclassId = binding.getJSONObject("child").getString(
						"value");
				String subclassLabel = binding.getJSONObject("label")
						.getString("value");
				HierarchyEntry entry = new HierarchyEntry();
				entry.setUri(subclassId);
				entry.setLabel(subclassLabel);
				entries.add(entry);
			}
		} catch (JSONException e) {
			log.error("Unable to parse JSON results", e);
		}
		return entries;
		/*
		 * 
		 * select * where {
graph<http://dataone.org/dois> {
?id <http://dataone.org#Category> <http://freebase.com#Chemical_Entity>  .
}
}
		 * 
		 * 
		 */	
	}

	private Collection<HierarchyEntry> queryGeospatialFeaturesHMRoots(final Request request) throws UnsupportedEncodingException {		
		System.out.println("queryGeospatialFeaturesHMRoots");

		final Query query = config.getQueryFactory().newQuery(Type.SELECT);
		// Variables
		final Variable id = query.getVariable(VAR_NS + "child");
		final Variable label = query.getVariable(VAR_NS + "label");
		final Variable parent = query.getVariable(VAR_NS + "parent");
		// URIs
		final QueryResource category = query.getResource( "http://dataone.org#Category");
		final QueryResource hasLabel = query.getResource(RDFS_NS + "label");
		final QueryResource chemicalEntity = query.getResource("http://freebase.com#Geospatial_Feature_Entity");

		//final NamedGraphComponent graph = query.getNamedGraph("http://dataone.org/dois");
		final NamedGraphComponent graph = query.getNamedGraph("http://dataone.tw.rpi.edu/dois2");


		Set<Variable> vars = new LinkedHashSet<Variable>();
		vars.add(id);
		vars.add(label);
		vars.add(parent);
		query.setVariables(vars);
		graph.addPattern(id, category, parent);
		graph.addPattern(id, category, chemicalEntity);
		graph.addPattern(id, hasLabel, label);
		String BINDINGS = "bindings";

		Collection<HierarchyEntry> entries = new ArrayList<HierarchyEntry>();
		String resultStr = config.getQueryExecutor(request).accept("application/json").execute("http://data1.tw.rpi.edu/sparql",query);
		if (resultStr == null) {
			return entries;
		}		try {
			JSONObject results = new JSONObject(resultStr);
			results = results.getJSONObject("results");
			JSONArray bindings = results.getJSONArray(BINDINGS);
			for (int i = 0; i < bindings.length(); i++) {
				JSONObject binding = bindings.getJSONObject(i);
				String subclassId = binding.getJSONObject("child").getString(
						"value");
				String subclassLabel = binding.getJSONObject("label")
						.getString("value");
				HierarchyEntry entry = new HierarchyEntry();
				entry.setUri(subclassId);
				entry.setLabel(subclassLabel);
				entries.add(entry);
			}
		} catch (JSONException e) {
			log.error("Unable to parse JSON results", e);
		}
		return entries;

		/*
		 * 
		 * select * where {
graph<http://dataone.org/dois> {
?id <http://dataone.org#Category> <http://freebase.com#Chemical_Entity>  .
}
}
		 * 
		 * 
		 */	
	}

	private Collection<HierarchyEntry> queryTopicsHMRoots(final Request request) throws UnsupportedEncodingException {		
		System.out.println("queryGeospatialFeaturesHMRoots");

		final Query query = config.getQueryFactory().newQuery(Type.SELECT);
		// Variables
		final Variable id = query.getVariable(VAR_NS + "child");
		final Variable label = query.getVariable(VAR_NS + "label");
		final Variable parent = query.getVariable(VAR_NS + "parent");
		// URIs
		final QueryResource category = query.getResource( "http://dataone.org#Category");
		final QueryResource hasLabel = query.getResource(RDFS_NS + "label");
		final QueryResource chemicalEntity = query.getResource("http://freebase.com#Topic_Of_Study");

		//final NamedGraphComponent graph = query.getNamedGraph("http://dataone.org/dois");
		final NamedGraphComponent graph = query.getNamedGraph("http://dataone.tw.rpi.edu/dois2");


		Set<Variable> vars = new LinkedHashSet<Variable>();
		vars.add(id);
		vars.add(label);
		vars.add(parent);
		query.setVariables(vars);
		graph.addPattern(id, category, parent);
		graph.addPattern(id, category, chemicalEntity);
		graph.addPattern(id, hasLabel, label);
		String BINDINGS = "bindings";

		Collection<HierarchyEntry> entries = new ArrayList<HierarchyEntry>();
		String resultStr = config.getQueryExecutor(request).accept("application/json").execute("http://data1.tw.rpi.edu/sparql",query);
		if (resultStr == null) {
			return entries;
		}		try {
			JSONObject results = new JSONObject(resultStr);
			results = results.getJSONObject("results");
			JSONArray bindings = results.getJSONArray(BINDINGS);
			for (int i = 0; i < bindings.length(); i++) {
				JSONObject binding = bindings.getJSONObject(i);
				String subclassId = binding.getJSONObject("child").getString(
						"value");
				String subclassLabel = binding.getJSONObject("label")
						.getString("value");
				HierarchyEntry entry = new HierarchyEntry();
				entry.setUri(subclassId);
				entry.setLabel(subclassLabel);
				entries.add(entry);
			}
		} catch (JSONException e) {
			log.error("Unable to parse JSON results", e);
		}
		return entries;

		/*
		 * 
		 * select * where {
graph<http://dataone.org/dois> {
?id <http://dataone.org#Category> <http://freebase.com#Chemical_Entity>  .
}
}
		 * 
		 * 
		 */	
	}


	private Collection<HierarchyEntry> queryOrganismsHMRoots(final Request request) throws UnsupportedEncodingException {		
		System.out.println("queryOrganismsHMRoots");

		final Query query = config.getQueryFactory().newQuery(Type.SELECT);
		// Variables
		final Variable id = query.getVariable(VAR_NS + "child");
		final Variable label = query.getVariable(VAR_NS + "label");
		final Variable parent = query.getVariable(VAR_NS + "parent");
		// URIs
		final QueryResource category = query.getResource( "http://dataone.org#Category");
		final QueryResource hasLabel = query.getResource(RDFS_NS + "label");
		final QueryResource chemicalEntity = query.getResource("http://freebase.com#Organismal_Entity");

		//final NamedGraphComponent graph = query.getNamedGraph("http://dataone.org/dois");
		final NamedGraphComponent graph = query.getNamedGraph("http://dataone.tw.rpi.edu/dois2");

		Set<Variable> vars = new LinkedHashSet<Variable>();
		vars.add(id);
		vars.add(label);
		vars.add(parent);
		query.setVariables(vars);
		graph.addPattern(id, category, parent);
		graph.addPattern(id, category, chemicalEntity);
		graph.addPattern(id, hasLabel, label);
		String BINDINGS = "bindings";

		Collection<HierarchyEntry> entries = new ArrayList<HierarchyEntry>();
		String resultStr = config.getQueryExecutor(request).accept("application/json").execute("http://data1.tw.rpi.edu/sparql",query);
		if (resultStr == null) {
			return entries;
		}		try {
			JSONObject results = new JSONObject(resultStr);
			results = results.getJSONObject("results");
			JSONArray bindings = results.getJSONArray(BINDINGS);
			for (int i = 0; i < bindings.length(); i++) {
				JSONObject binding = bindings.getJSONObject(i);
				String subclassId = binding.getJSONObject("child").getString(
						"value");
				String subclassLabel = binding.getJSONObject("label")
						.getString("value");
				HierarchyEntry entry = new HierarchyEntry();
				entry.setUri(subclassId);
				entry.setLabel(subclassLabel);
				entries.add(entry);
			}
		} catch (JSONException e) {
			log.error("Unable to parse JSON results", e);
		}
		return entries;



		/*
		 * 
		 * select * where {
graph<http://dataone.org/dois> {
?id <http://dataone.org#Category> <http://freebase.com#Chemical_Entity>  .
}
}
		 * 
		 * 
		 */



	}

	private Collection<HierarchyEntry> queryChemical_HMChildren(final Request request, String clazz) throws JSONException {
		//AnnotatorTester ann = new AnnotatorTester();
		System.out.println("queryClassHMChildren");
		return null;// this.annotatorTester.getChildClasses(clazz);		
	}



}
