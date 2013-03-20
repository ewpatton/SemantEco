package edu.rpi.tw.escience.species;

import static edu.rpi.tw.escience.semanteco.query.Query.VAR_NS;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
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
import edu.rpi.tw.escience.semanteco.ProvidesDomain;
import edu.rpi.tw.escience.semanteco.QueryMethod;
import edu.rpi.tw.escience.semanteco.Request;
import edu.rpi.tw.escience.semanteco.Resource;
import edu.rpi.tw.escience.semanteco.SemantEcoUI;
import edu.rpi.tw.escience.semanteco.query.GraphComponentCollection;
import edu.rpi.tw.escience.semanteco.query.NamedGraphComponent;
import edu.rpi.tw.escience.semanteco.query.OptionalComponent;
import edu.rpi.tw.escience.semanteco.query.Query;
import edu.rpi.tw.escience.semanteco.query.Query.SortType;
import edu.rpi.tw.escience.semanteco.query.Query.Type;
import edu.rpi.tw.escience.semanteco.query.QueryResource;
import edu.rpi.tw.escience.semanteco.query.UnionComponent;
import edu.rpi.tw.escience.semanteco.query.Variable;

public class SpeciesDataProviderModule implements Module, ProvidesDomain {
	
	private static final String POL_NS = "http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#";
	private static final String RDFS_NS = "http://www.w3.org/2000/01/rdf-schema#";
	private static final String RDF_NS = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	public static final String  TXN_NS = "http://lod.taxonconcept.org/ontology/txn.owl#";
	public static final String  EBIRD_NS = "http://ebird#";

	public static final String  BIRD_NS = "http://escience.rpi.edu/ontology/semanteco/2/0/bird.owl#";
	public static final String  FISH_NS = "http://escience.rpi.edu/ontology/semanteco/2/0/fish.owl#";

	public static final String  EBIRD_DATA_NS = "http://was.tw.rpi.edu/source/bird-data/dataset/ebird-data/vocab/enhancement/1/";
	private static final String WGS_NS = "http://www.w3.org/2003/01/geo/wgs84_pos#";
	private static final String e1_NS = "http://was.tw.rpi.edu/source/bird-data/dataset/ebird-data/version/2012-Nov-4/params/enhancement/1/";
	public static final String e2_NS = "http://was.tw.rpi.edu/source/bird-data/dataset/ebird-data/vocab/enhancement/1/";
	public static final String QUERY_NS = "http://aquarius.tw.rpi.edu/projects/semantaqua/data-source/query-variable/";
	public static final String SKOS_NS = "http://www.w3.org/2004/02/skos/core#";
	private static final String FAILURE = "{\"success\":false}";
	//private ModuleConfiguration config = null;
	private static final String BINDINGS = "bindings";
	private Logger log = Logger.getLogger(SpeciesDataProviderModule.class);
	private static final String SITE_VAR = "site";
	private static final String LAT = "lat";
	private static final String LONG = "long";
	private static final String ISBIRD_VAR = "isBird";
	private static final String ISFISH_VAR = "isFish";
	private ModuleConfiguration config = null;
	
	/**
	 * 
	 * 	 this executes the query on the remote endpoint and provides the results to the model passed in
	 */
	@Override
	public void visit(final Model model, final Request request) {
		boolean bird = false;
		boolean fish = false;
		
		//until we encode the dependency of entity type on domain, let's just check for domain in the query.
		//also in the future let the entity uris refer to owl class uris for the higest level category relevant to that entity from our ontologies.
		
		//"http://escience.rpi.edu/ontology/semanteco/2/0/bird.owl#", "http://escience.rpi.edu/ontology/semanteco/2/0/water.owl#"
		
		/*
		JSONArray sourceArray = (JSONArray) request.getParam("source");
		for(int i = 0; i < sourceArray.length(); i++){
			try {
				String objectInArray = sourceArray.getString(i);
				
				if (objectInArray.equals("http://ebird#")){
					bird = true;
				}
				
				if (objectInArray.equals("http://sbcFish#")){
					fish = true;
				}				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		*/
		JSONArray domainArray = (JSONArray) request.getParam("domain");
		for(int i = 0; i < domainArray.length(); i++){
			try {
				String objectInArray = domainArray.getString(i);
				
				if (objectInArray.equals(BIRD_NS)){
					bird = true;
				}
				
				if (objectInArray.equals(FISH_NS)){
					fish = true;
				}				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		
		
		// TODO populate data model
		// would have to load the bird site data for the particular county/state
		//get the state and county from params
		
		if(fish){
		String countyCode = (String) request.getParam("county");
		String stateAbbr = (String) request.getParam("state");
		//String site = (String) request.getParam("uri");
		assert(countyCode != null);
		assert(stateAbbr != null);	
		final Query query = config.getQueryFactory().newQuery(Type.CONSTRUCT);
		final Variable s = query.getVariable(QUERY_NS+"s");
		final GraphComponentCollection construct = query.getConstructComponent();
		final QueryResource wgsLat = query.getResource(WGS_NS+LAT);
		final QueryResource wgsLong = query.getResource(WGS_NS+LONG);
		final QueryResource fishSite = query.getResource("http://escience.rpi.edu/ontology/semanteco/2/0/fish.owl#FishSite");
		final Variable lat = query.getVariable(QUERY_NS+LAT);
		final Variable lng = query.getVariable(QUERY_NS+LONG);
		final Variable label = query.getVariable(QUERY_NS+"label");
		final Variable measurement = query.getVariable(QUERY_NS+"measurement");
		final QueryResource rdfsLabel = query.getResource(RDFS_NS+"label");
		final QueryResource rdfType = query.getResource(RDF_NS+"type");
		//not correct http://was.tw.rpi.edu/source/bird-data/dataset/ebird-data/vocab/enhancement/1/locality/locality
		// what is in the rdf: 
		//@prefix e1: <http://was.tw.rpi.edu/source/bird-data/dataset/ebird-data/vocab/enhancement/1/> .
		//final QueryResource siteUri = query.getResource(Site); //update locality property namespace	
		//species site and measurement (count)
		//just the uri, lat and long.
		final QueryResource countyCoded = query.getResource("http://was.tw.rpi.edu/source/fish-data/dataset/sbc-data/vocab/enhancement/1/" + "countycoded");
		final QueryResource stateAbbrev = query.getResource("http://was.tw.rpi.edu/source/fish-data/dataset/sbc-data/vocab/enhancement/1/" + "statecoded");	
		final QueryResource site = query.getResource("http://was.tw.rpi.edu/source/fish-data/dataset/sbc-data/vocab/enhancement/1/" + "site");	

		construct.addPattern(s, rdfType, fishSite );
		construct.addPattern(s, rdfsLabel, label);
		construct.addPattern(s, wgsLat, lat);
		construct.addPattern(s, wgsLong, lng);	
		final GraphComponentCollection graph = query.getNamedGraph("http://was.tw.rpi.edu/fish-data");
		final GraphComponentCollection graph2 = query.getNamedGraph("http://was.tw.rpi.edu/fish-sites");

		//sites are per measurement
		graph.addPattern(s, rdfType, fishSite );
		graph.addPattern(s, rdfsLabel, label);
		//graph.addPattern(measurement, wgsLat, lat);
		//graph.addPattern(measurement, wgsLong, lng);
		graph.addPattern(measurement, countyCoded, countyCode,null);
		graph.addPattern(measurement, stateAbbrev, stateAbbr,null);
		graph.addPattern(measurement, site, s);	
		graph2.addPattern(s, wgsLat, lat);
		graph2.addPattern(s, wgsLong, lng);

		//this executes the query on the remote endpoint and provides the results to the model passed in
		config.getQueryExecutor(request).accept("text/turtle").execute(query, model);
		//config.getQueryExecutor(request).accept("application/rdf+xml").execute(query, model);
		
		}
		
		
		if(bird){
		String countyCode = (String) request.getParam("county");
		String stateAbbr = (String) request.getParam("state");
		//String site = (String) request.getParam("uri");
		assert(countyCode != null);
		assert(stateAbbr != null);	
		final Query query = config.getQueryFactory().newQuery(Type.CONSTRUCT);
		final Variable s = query.getVariable(QUERY_NS+"s");
		final GraphComponentCollection construct = query.getConstructComponent();
		final QueryResource wgsLat = query.getResource(WGS_NS+LAT);
		final QueryResource wgsLong = query.getResource(WGS_NS+LONG);
		final QueryResource birdSite = query.getResource("http://escience.rpi.edu/ontology/semanteco/2/0/bird.owl#BirdSite");
		final Variable lat = query.getVariable(QUERY_NS+LAT);
		final Variable lng = query.getVariable(QUERY_NS+LONG);
		final Variable label = query.getVariable(QUERY_NS+"label");
		final Variable measurement = query.getVariable(QUERY_NS+"measurement");
		final QueryResource rdfsLabel = query.getResource(RDFS_NS+"label");
		final QueryResource rdfType = query.getResource(RDF_NS+"type");
		//not correct http://was.tw.rpi.edu/source/bird-data/dataset/ebird-data/vocab/enhancement/1/locality/locality
		// what is in the rdf: 
		//@prefix e1: <http://was.tw.rpi.edu/source/bird-data/dataset/ebird-data/vocab/enhancement/1/> .
		
		final QueryResource locality = query.getResource("http://was.tw.rpi.edu/source/bird-data/dataset/ebird-data/vocab/enhancement/1/locality"); //update locality property namespace********
		//final QueryResource siteUri = query.getResource(Site); //update locality property namespace	
		//species site and measurement (count)
		//just the uri, lat and long.
		//the namespace for countyCode and StateCoded in the fish data
		final QueryResource countyCoded = query.getResource(e1_NS + "countyCoded");
		final QueryResource stateAbbrev = query.getResource(e1_NS + "stateCoded");	
		construct.addPattern(s, rdfType, birdSite );
		construct.addPattern(s, rdfsLabel, label);
		construct.addPattern(s, wgsLat, lat);
		construct.addPattern(s, wgsLong, lng);	
		//final GraphComponentCollection graph = query.getNamedGraph("http://was.tw.rpi.edu/ebird-data2");
		final GraphComponentCollection graph = query.getNamedGraph("http://was.tw.rpi.edu/ebird-data-ca");
		final QueryResource hasScientificName = query.getResource(e2_NS+ "scientific_name");
		final Variable scientificName = query.getVariable(VAR_NS + "scientific_name");	
		final Variable count = query.getVariable(QUERY_NS+"count");
		final Variable date = query.getVariable(QUERY_NS+"date");
		final Variable commonName = query.getVariable(VAR_NS+"commonName");
		final Variable species = query.getVariable(VAR_NS + "species");
		final QueryResource birdCount = query.getResource(e2_NS+"observation_count");
		final QueryResource obsDate = query.getResource(e2_NS+"observation_date");

		//sites are per measurement
		graph.addPattern(s, rdfType, birdSite );
		graph.addPattern(s, rdfsLabel, label);
		graph.addPattern(measurement, wgsLat, lat);
		graph.addPattern(measurement, wgsLong, lng);
		graph.addPattern(measurement, countyCoded, countyCode,null);
		graph.addPattern(measurement, stateAbbrev, stateAbbr,null);
		graph.addPattern(measurement, locality, s);	
		graph.addPattern(measurement, hasScientificName, scientificName);
		
		//query based on the species name, you are doing this across two graphs
		final QueryResource hasCommonName = query.getResource(TXN_NS+"CommonNameID");
		
		final QueryResource hasLabel = query.getResource(RDFS_NS+ "label");
		//here we are binding the search to specific species
				if(request.getParam("species") != null && ((JSONArray) request.getParam("species")).length() > 1  ){// && request.getParam("species").length() > 0) {
				    request.getLogger().error("species length: " + ((JSONArray) request.getParam("species")).length());
				    request.getLogger().error("(got to else if where species > 1)");

					//note that this is going to be a json array of strings
					//you're "joining" on the scientific name, for now
					JSONArray speciesParams = (JSONArray)request.getParam("species");	
					final UnionComponent union = query.createUnion();
					final NamedGraphComponent graph2 = query.getNamedGraph("http://was.tw.rpi.edu/ebird-taxonomy");
					graph2.addGraphComponent(union);
					//GraphComponentCollection coll = union.getUnionComponent(0);
					GraphComponentCollection coll;
					//for each element in the bbq state array for "species" key
					for(int i = 0; i < speciesParams.length(); i++)
					{
						//JSONObject objectInArray = speciesParams.getJSONObject(i);
						String speciesInArray = null;
						try {
							speciesInArray = speciesParams.getString(i);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}				
						//request.getLogger().error("JSON Object: " + speciesInArray.toString());
						//here is where you do the union			
						//put the species returned into a resource
						
						//check if the arguments in speciesParams has subclasses
						//query for some scientific name, such that:
						// superClassOfSpecies is a (kingdom, phylum, or x (non-species)) and
						//there is some superclass that is a species name
						//scientificName subClassOf superClassOfSpecies
						 //addedSpecies, hasLabel, scientificName
						//if that is the case, then add 'addedSpecies, hasLabel, scientificName'  to the union
						
						//two graphs for this:
						//http://was.tw.rpi.edu/ebird-taxonomy
						//http://was.tw.rpi.edu/ebird-data
						//namespace of scientificName: http://was.tw.rpi.edu/source/bird-data/dataset/ebird-data/vocab/enhancement/1/
						
						//here is where I can check results of subClassOf hierarchy query, and then union it
						String resultStr = null;
						try {
							resultStr = queryIfTaxonomicCategory(request, speciesInArray);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						//if there are any bindings union up				
						if(resultStr != "FAILURE"){
					    request.getLogger().error("subclassOf results: " + resultStr);
					    JSONObject results = null;
						try {
							results = new JSONObject(resultStr);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					    JSONArray data = null;
						try {
							data = (JSONArray) results.get("data");
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					    //data.length is  > 0 then there were positive results, so now we can ask for subclasses of selection
					    request.getLogger().error("data.length : " + data.length());

					    	if(data.length() > 0){			    		
					    		//just use the pattern species subClassOf speciesSelection
				    			final QueryResource addedSpecies = query.getResource(speciesInArray);
				    			final QueryResource subClassOf = query.getResource(RDFS_NS+"subClassOf");
								coll = union.getUnionComponent(i);
								//final NamedGraphComponent graph3 = query.getNamedGraph("http://was.tw.rpi.edu/ebird-taxonomy");
								//coll.addGraphComponent(graph3);					
								//ebird taxonomy graph
								
						        coll.addPattern(species, subClassOf, addedSpecies, true);	
						        coll.addPattern(species, hasLabel, scientificName);	
						        //ebird data graph (already handled in the first part of the query)
						        //graph3.addPattern(addedSpecies, hasLabel, scientificName);			    			
					    		/*
					    		for(int n = 0; n < data.length(); n++)
					    		{
					    			JSONObject objectInArray = data.getJSONObject(n);
					    			String speciesForQuery = objectInArray.get("species").toString();
					    			request.getLogger().error("species for query: " + speciesForQuery);    	    			
					    			final QueryResource addedSpecies = query.getResource(speciesForQuery);
									coll = union.getUnionComponent(n);
							        coll.addPattern(addedSpecies, hasLabel, scientificName);			    			
					    		}
					    		*/
					    	}
					    	else{
							    request.getLogger().error("(no results for queryIfTaxonomicCategory)");
							  final QueryResource addedSpecies = query.getResource(speciesInArray);
								coll = union.getUnionComponent(i);
						        coll.addPattern(addedSpecies, hasLabel, scientificName);		
					    	}
						}
						else{
						    request.getLogger().error("(failure to queryIfTaxonomicCategory)");
						}			      			    					
					}			
				}	
				else if (((JSONArray) request.getParam("species")).length() == 1){
				    request.getLogger().error("(got to else if where species == 1)");

					JSONArray speciesParams = (JSONArray)request.getParam("species");	
					String speciesInArray = null;
					try {
						speciesInArray = speciesParams.getString(0);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}		
					String resultStr = null;
					try {
						resultStr = queryIfTaxonomicCategory(request, speciesInArray);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(resultStr != "FAILURE"){
					    request.getLogger().error("subclassOf results: " + resultStr);
					    JSONObject results = null;
						try {
							results = new JSONObject(resultStr);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					    JSONArray data = null;
						try {
							data = (JSONArray) results.get("data");
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					    	//data.length is  > 0 then there were positive results, so now we can ask for subclasses of selection
					    request.getLogger().error("data.length : " + data.length());
					    final NamedGraphComponent graph2 = query.getNamedGraph("http://was.tw.rpi.edu/ebird-taxonomy");
						final QueryResource addedSpecies = query.getResource(speciesInArray);
					    	if(data.length() > 0){			    		
					    		//just use the pattern species subClassOf speciesSelection
				    			final QueryResource subClassOf = query.getResource(RDFS_NS+"subClassOf");

							    request.getLogger().error("addedSpecies: " + addedSpecies.toString());
							    request.getLogger().error("species: " + species.toString());
							    request.getLogger().error("subclassof: " + subClassOf.toString());
						        graph2.addPattern(species, subClassOf, addedSpecies, true);	
						        graph2.addPattern(species, hasLabel, scientificName);				       
					    	}
					    	else{
							    request.getLogger().error("(no results for queryIfTaxonomicCategory)");
								graph2.addPattern(addedSpecies, hasLabel, scientificName);		
					    	}
						}		
				}
				else{
					final NamedGraphComponent graph2 = query.getNamedGraph("http://was.tw.rpi.edu/ebird-taxonomy");
					graph2.addPattern(species, hasLabel, scientificName);		
				}//for this case the species is left as a variable and is not constrained, "all bird counts" for the county
		
		
		//this executes the query on the remote endpoint and provides the results to the model passed in
		//config.getQueryExecutor(request).accept("application/json").execute(query, model);
		config.getQueryExecutor(request).accept("application/rdf+xml").execute(query, model);
		}	
	}

	/**
	 * Loads the bird and fish ontologies.
	 */
	@Override
	public void visit(final OntModel model, final Request request) {
		model.read(BIRD_NS);		
		model.read(FISH_NS);
		//create a bird.owl that imports pollution.owl and asserts a subclass of Measurement site
		//that is a BirdSite used as class, the enhancement in the rdf eBird data.
		//in the enhancement, need to promote location to a uri, and make it a subclass, BirdSite.
		//check if the label shows up. try on a subset.
	}

	
	@Override
	public void visit(final Query query, final Request request) {
		// TODO modify queries
		request.getLogger().debug("SpeciesDataProviderModule updating query");
		if(query.getType() != Type.SELECT) {
			return;
		}
		final Variable site = query.getVariable(VAR_NS+SITE_VAR);
		final QueryResource rdfType = query.getResource(RDF_NS+"type");
		final QueryResource polMeasurementSite = query.getResource(POL_NS+"MeasurementSite");
		List<GraphComponentCollection> graphs = query.findGraphComponentsWithPattern(site, rdfType, polMeasurementSite);
		if(graphs != null && graphs.size() > 0) {
		//	query.setNamespace("air", AIR_NS);
			Set<Variable> vars = new LinkedHashSet<Variable>(query.getVariables());
			
			boolean bird = false;
			boolean fish = false;
			
			
			//same as at the top of the class
		/*
			JSONArray sourceArray = (JSONArray) request.getParam("source");
			for(int i = 0; i < sourceArray.length(); i++){
				try {
					String objectInArray = sourceArray.getString(i);
					
					if (objectInArray.equals("http://ebird#")){
						bird = true;
					}
					
					if (objectInArray.equals("http://sbcFish#")){
						fish = true;
					}
					
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			*/
			JSONArray domainArray = (JSONArray) request.getParam("domain");
			for(int i = 0; i < domainArray.length(); i++){
				try {
					String objectInArray = domainArray.getString(i);
					
					if (objectInArray.equals(BIRD_NS)){
						bird = true;
					}
					
					if (objectInArray.equals(FISH_NS)){
						fish = true;
					}				
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}			
			}
			
			
					
			if(bird){
			final Variable isBird = query.createVariableExpression("EXISTS { ?"+SITE_VAR+" a <http://escience.rpi.edu/ontology/semanteco/2/0/bird.owl#BirdSite> } as ?"+ISBIRD_VAR);
			vars.add(isBird);
			}
			if(fish){
			final Variable isFish = query.createVariableExpression("EXISTS { ?"+SITE_VAR+" a <http://escience.rpi.edu/ontology/semanteco/2/0/fish.owl#FishSite> } as ?"+ ISFISH_VAR);
			vars.add(isFish);
			}
			
			query.setVariables(vars);
		}
		
		
		
	}
	
	/**
	 * Loads speciesHierarchy.js, speciesHierarchy.jsp, and jstree.js
	 */
	@Override
	public void visit(SemantEcoUI ui, Request request) {
		Resource res = null;
		res = config.getResource("speciesHierarchy.js");
		Resource res2 = config.getResource("speciesHierarchy.jsp");

		ui.addScript(res);
		ui.addFacet(res2);
//		res = config.getResource("jstree/jquery.jstree.js");
//		ui.addScript(res);
//		res = config.getResource("jstree/themes/default/style.css");
//		ui.addStylesheet(res);
	}
	
	/**
	 * A test method that returns a string indicating the type of object the species key contains
	 * @param request
	 * @return
	 * @throws JSONException
	 */
	@QueryMethod
	public String queryParams2(Request request) throws JSONException
	{
		Class<?> cls = request.getParam("species").getClass();  
	    return ("The type of the object is: " + cls.getName());  
		//return request.getParam("species").toString();
	}
	
	@QueryMethod
	public String queryParams(Request request) throws JSONException
	{
		if(request.getParam("species") != null){// && request.getParam("species").length() > 0) {
			//throw new IllegalArgumentException("The source parameter must be supplied");
			//note that this is going to be a json object
			JSONArray speciesParams = (JSONArray)request.getParam("species");			
			for(int i = 0; i < speciesParams.length(); i++)
			{
				//JSONObject objectInArray = speciesParams.getJSONObject(i);
				String objectInArray = speciesParams.getString(i);				
				request.getLogger().error("JSON Object: " + objectInArray.toString());
				//here is where you do the union	
			}
		
			//iterate over the species and create a union clause
			//iterate over jason entity uris
			//graph2.addPattern(species, hasLabel, scientificName);			
		}
		return null;
	}
	
	/*
	 This method will let the calling UI client know if the class is a leaf or a non-leaf node in the class hierarchy.
	 checks the bbq state value for the key: "queryIfTaxonomicCategoryForJsTree",

	so for testing:
	$.bbq.pushState({"queryIfTaxonomicCategoryForJstree":"http://ebird#Struthionidae"})

	returns:
	{"data":[{"species":"http://ebird#Struthio_camelus"},{"species":"http://ebird#Struthio_camelus_molybdophanes"}],"success":true}

	but if I try with a leaf node:
	$.bbq.pushState({"queryIfTaxonomicCategoryForJstree":"http://ebird#Nothocercus_bonapartei_frantzii"})

	you get:
	{"data":[],"success":true}
	 */

	/**
	 * This method lets the calling UI client know if the class is a leaf or a non-leaf node in the class hierarchy.
	 * checks the bbq state value of "queryIfTaxonomicCategoryForJsTree".
	 * @param request
	 * @return
	 * @throws JSONException
	 */
	@QueryMethod
	public String queryIfTaxonomicCategoryForJstree(Request request) throws JSONException {
		
		String candidateTaxonomicCategory = (String) request.getParam("queryIfTaxonomicCategoryForJstree");	
	    request.getLogger().error("json object is: " + candidateTaxonomicCategory);
	  //  String candidateTaxonomicCategoryString  = candidateTaxonomicCategory.toString();
		if(candidateTaxonomicCategory == null){
			return null;
		}
		else{
		final Query query = config.getQueryFactory().newQuery(Type.SELECT);	
		final NamedGraphComponent graph = query.getNamedGraph("http://was.tw.rpi.edu/ebird-taxonomy");
		//final NamedGraphComponent graph = query.getNamedGraph("http://was.tw.rpi.edu/ebird-data");
		final QueryResource subClassOf = query.getResource(RDFS_NS + "subClassOf");
		final Variable speciesVariable = query.getVariable(VAR_NS + "species");	
		final QueryResource addedSpecies = query.getResource(candidateTaxonomicCategory);
		graph.addPattern(speciesVariable, subClassOf , addedSpecies);				
		String responseStr = FAILURE;
		String resultStr = config.getQueryExecutor(request).accept("application/json").execute(query);	
			log.debug("Results: "+resultStr);
			if(resultStr == null) {
				return responseStr;
			}
			try {
				JSONObject results = new JSONObject(resultStr);
				JSONObject response = new JSONObject();
				JSONArray data = new JSONArray();
				response.put("success", true);
				response.put("data", data);
				results = results.getJSONObject("results");
				JSONArray bindings = results.getJSONArray(BINDINGS);
				for(int j=0;j <bindings.length();j++) {
					JSONObject binding = bindings.getJSONObject(j);
					String speciesId = binding.getJSONObject("species").getString("value");
					JSONObject mapping = new JSONObject();
					mapping.put("species", speciesId);
					data.put(mapping);
				}
				responseStr = response.toString();
			} catch (JSONException e) {
				log.error("Unable to parse JSON results", e);
			}
			return responseStr;		
		}
	}
	
	
	
	/**
	 * This method will return if a class is a leaf or non-leaf. It is called by the method queryForNearbySpeciesCounts.
	 * I believe this was for testing and is not used in production.
	 * @param request
	 * @param speciesInArray
	 * @return
	 * @throws JSONException
	 */
	public String queryIfTaxonomicCategory(Request request, String speciesInArray) throws JSONException {
		final Query query = config.getQueryFactory().newQuery(Type.SELECT);	
		final NamedGraphComponent graph = query.getNamedGraph("http://was.tw.rpi.edu/ebird-taxonomy");
		final QueryResource subClassOf = query.getResource(RDFS_NS + "subClassOf");
		final Variable speciesVariable = query.getVariable(VAR_NS + "species");	
		final QueryResource addedSpecies = query.getResource(speciesInArray);
		graph.addPattern(speciesVariable, subClassOf , addedSpecies);				
		String responseStr = FAILURE;
		String resultStr = config.getQueryExecutor(request).accept("application/json").execute(query);	
			log.debug("Results: "+resultStr);
			if(resultStr == null) {
				return responseStr;
			}
			try {
				JSONObject results = new JSONObject(resultStr);
				JSONObject response = new JSONObject();
				JSONArray data = new JSONArray();
				response.put("success", true);
				response.put("data", data);
				results = results.getJSONObject("results");
				JSONArray bindings = results.getJSONArray(BINDINGS);
				for(int j=0;j <bindings.length();j++) {
					JSONObject binding = bindings.getJSONObject(j);
					String speciesId = binding.getJSONObject("species").getString("value");
					JSONObject mapping = new JSONObject();
					mapping.put("species", speciesId);
					data.put(mapping);
				}
				responseStr = response.toString();
			} catch (JSONException e) {
				log.error("Unable to parse JSON results", e);
			}
			return responseStr;		
	}
	
	/**
	 * @deprecated
	 * @param request
	 * @return
	 * @throws JSONException
	 */
	@QueryMethod
	public String querySpeciesByTaxonomicCategories(Request request) throws JSONException {
		final Query query = config.getQueryFactory().newQuery(Type.SELECT);	
		final NamedGraphComponent graph = query.getNamedGraph("http://was.tw.rpi.edu/ebird-taxonomy");
		//final NamedGraphComponent graph = query.getNamedGraph("http://was.tw.rpi.edu/ebird-data");
		final QueryResource subClassOf = query.getResource(RDFS_NS + "subClassOf");
		final Variable speciesVariable = query.getVariable(VAR_NS + "species");
				
		if(request.getParam("species") != null && ((JSONArray) request.getParam("species")).length() > 1){			
			JSONArray speciesParams = (JSONArray)request.getParam("species");	
			final UnionComponent union = query.createUnion();
			//final UnionComponent union = query.createUnion();
			//final NamedGraphComponent graph2 = query.getNamedGraph("http://was.tw.rpi.edu/ebird-taxonomy");
			//graph2.addGraphComponent(union);
			//GraphComponentCollection coll = union.getUnionComponent(0);
			GraphComponentCollection coll;
			//coll.addPattern(species, hasLabel, scientificName);			
			for(int i = 0; i < speciesParams.length(); i++)
			{			
			//check if the arguments in speciesParams has subclasses
			//query for some scientific name, such that:
			// superClassOfSpecies is a (kingdom, phylum, or x (non-species)) and
			//there is some superclass that is a species name
			//scientificName subClassOf superClassOfSpecies
			 //addedSpecies, hasLabel, scientificName
			//if that is the case, then add 'addedSpecies, hasLabel, scientificName'  to the union
			
			String speciesInArray = speciesParams.getString(i);	
			final QueryResource addedSpecies = query.getResource(speciesInArray);
			Set<Variable> vars = new LinkedHashSet<Variable>();
			vars.add(speciesVariable);
			query.setVariables(vars);			
			coll = union.getUnionComponent(i);
	        coll.addPattern(speciesVariable, subClassOf , addedSpecies);						
			}
		}
	else if (request.getParam("species") != null && ((JSONArray) request.getParam("species")).length() == 1){
		JSONArray speciesParams = (JSONArray)request.getParam("species");	
		String speciesInArray = speciesParams.getString(0);		
		final QueryResource addedSpecies = query.getResource(speciesInArray);
		graph.addPattern(speciesVariable, subClassOf , addedSpecies);		
	}
	else{
		return "no species categories chosen";
	}
						
			String responseStr = FAILURE;

			String resultStr = config.getQueryExecutor(request).accept("application/json").execute(query);
			
			log.debug("Results: "+resultStr);
			if(resultStr == null) {
				return responseStr;
			}
			try {
				JSONObject results = new JSONObject(resultStr);
				JSONObject response = new JSONObject();
				JSONArray data = new JSONArray();
				response.put("success", true);
				response.put("data", data);
				results = results.getJSONObject("results");
				JSONArray bindings = results.getJSONArray(BINDINGS);
				for(int j=0;j <bindings.length();j++) {
					JSONObject binding = bindings.getJSONObject(j);
					String speciesId = binding.getJSONObject("species").getString("value");
				//	String subclassLabel = binding.getJSONObject("label").getString("value");

					//if(labelStr == null) {
					//	labelStr = sourceUri.substring(sourceUri.lastIndexOf('/')+1).replace('-', '.');
					//}
					JSONObject mapping = new JSONObject();
					mapping.put("species", speciesId);
				//	mapping.put("label", subclassLabel);
				//	mapping.put("parent", superclassId);
					data.put(mapping);
				}
				responseStr = response.toString();
			} catch (JSONException e) {
				log.error("Unable to parse JSON results", e);
			}
			return responseStr;
						
		//	final Variable scientificName = query.getVariable(VAR_NS + "scientific_name");
		//	final QueryResource hasScientificName = query.getResource(e2_NS+ "scientific_name");
			
		//	graph.addPattern(measurement, hasScientificName, scientificName);
		//	graph2.addPattern(species, hasLabel, scientificName);		

	}
	
	public Query addSpeciesClause(Request request, Query query) throws JSONException{
		//query based on the species name, you are doing this across two graphs
		final QueryResource hasCommonName = query.getResource(TXN_NS+"CommonNameID");
		final QueryResource hasScientificName = query.getResource(e2_NS+ "scientific_name");
		final QueryResource hasLabel = query.getResource(RDFS_NS+ "label");
		final Variable commonName = query.getVariable(VAR_NS+"commonName");
		final Variable scientificName = query.getVariable(VAR_NS + "scientific_name");
		final Variable species = query.getVariable(VAR_NS + "species");
		
		//for helper would need to pass a request and a query object
		//coll is created within.
		//here we are binding the search to specific species
		if(request.getParam("species") != null && ((JSONArray) request.getParam("species")).length() > 1  ){// && request.getParam("species").length() > 0) {
		    request.getLogger().error("species length: " + ((JSONArray) request.getParam("species")).length());

		    request.getLogger().error("(got to else if where species > 1)");

			//note that this is going to be a json array of strings
			//you're "joining" on the scientific name, for now
			JSONArray speciesParams = (JSONArray)request.getParam("species");	
			final UnionComponent union = query.createUnion();
			final NamedGraphComponent graph2 = query.getNamedGraph("http://was.tw.rpi.edu/ebird-taxonomy");
			graph2.addGraphComponent(union);
			//GraphComponentCollection coll = union.getUnionComponent(0);
			GraphComponentCollection coll;
			//for each element in the bbq state array for "species" key
			for(int i = 0; i < speciesParams.length(); i++)
			{
				//JSONObject objectInArray = speciesParams.getJSONObject(i);
				String speciesInArray = speciesParams.getString(i);				
				
				String resultStr = queryIfTaxonomicCategory(request, speciesInArray);
				//if there are any bindings union up				
				if(resultStr != "FAILURE"){
			    request.getLogger().error("subclassOf results: " + resultStr);
			    JSONObject results = new JSONObject(resultStr);
			    JSONArray data = (JSONArray) results.get("data");
			    	//data.length is  > 0 then there were positive results, so now we can ask for subclasses of selection
			    request.getLogger().error("data.length : " + data.length());

			    	if(data.length() > 0){			    		
			    		//just use the pattern species subClassOf speciesSelection
		    			final QueryResource addedSpecies = query.getResource(speciesInArray);
		    			final QueryResource subClassOf = query.getResource(RDFS_NS+"subClassOf");
						coll = union.getUnionComponent(i);
						//final NamedGraphComponent graph3 = query.getNamedGraph("http://was.tw.rpi.edu/ebird-taxonomy");
						//coll.addGraphComponent(graph3);					
						//ebird taxonomy graph
				        coll.addPattern(species, subClassOf, addedSpecies);	
				        coll.addPattern(species, hasLabel, scientificName);	
				        //ebird data graph (already handled in the first part of the query)
				        //graph3.addPattern(addedSpecies, hasLabel, scientificName);			    			
			    		
			    	}
			    	else{
					    request.getLogger().error("(no results for queryIfTaxonomicCategory)");
					  final QueryResource addedSpecies = query.getResource(speciesInArray);
						coll = union.getUnionComponent(i);
				        coll.addPattern(addedSpecies, hasLabel, scientificName);		
			    	}
				}
				else{
				    request.getLogger().error("(failure to queryIfTaxonomicCategory)");
				}			      			    					
			}			
		}	
		else if (((JSONArray) request.getParam("species")).length() == 1){
		    request.getLogger().error("(got to else if where species == 1)");

			JSONArray speciesParams = (JSONArray)request.getParam("species");	
			String speciesInArray = speciesParams.getString(0);		
			String resultStr = queryIfTaxonomicCategory(request, speciesInArray);
			if(resultStr != "FAILURE"){
			    request.getLogger().error("subclassOf results: " + resultStr);
			    JSONObject results = new JSONObject(resultStr);
			    JSONArray data = (JSONArray) results.get("data");
			    	//data.length is  > 0 then there were positive results, so now we can ask for subclasses of selection
			    request.getLogger().error("data.length : " + data.length());
			    final NamedGraphComponent graph2 = query.getNamedGraph("http://was.tw.rpi.edu/ebird-taxonomy");
				final QueryResource addedSpecies = query.getResource(speciesInArray);
			    	if(data.length() > 0){			    		
			    		//just use the pattern species subClassOf speciesSelection
		    			final QueryResource subClassOf = query.getResource(RDFS_NS+"subClassOf");

					    request.getLogger().error("addedSpecies: " + addedSpecies.toString());
					    request.getLogger().error("species: " + species.toString());
					    request.getLogger().error("subclassof: " + subClassOf.toString());

				        graph2.addPattern(species, subClassOf, addedSpecies);	
				        graph2.addPattern(species, hasLabel, scientificName);				       
			    	}
			    	else{
					    request.getLogger().error("(no results for queryIfTaxonomicCategory)");
						graph2.addPattern(addedSpecies, hasLabel, scientificName);		
			    	}
				}		
		}
		else{
			final NamedGraphComponent graph2 = query.getNamedGraph("http://was.tw.rpi.edu/ebird-taxonomy");
			graph2.addPattern(species, hasLabel, scientificName);		
		}
	    request.getLogger().error("query is : " + query.toString());
		
		
		return query;
	}
	
	
	/**
	 * Probably never used. Will double check before commenting out.
	 * @param request
	 * @return
	 * @throws JSONException
	 */
	@QueryMethod
	public String queryForNearbySpeciesCountsByLocality(Request request) throws JSONException{
		
		/*
SELECT  ?scientific_name ?count ?location ?date
WHERE 
 {
    graph <http://was.tw.rpi.edu/ebird-data-big> {
    ?measurement <http://rdfs.org/ns/void#inDataset> <http://was.tw.rpi.edu/source/bird-data/dataset/ebird-data/version/2012-Nov-4> . 
    ?measurement <http://was.tw.rpi.edu/source/bird-data/dataset/ebird-data/version/2012-Nov-4/params/enhancement/1/countyCoded> "019" . 
    ?measurement <http://was.tw.rpi.edu/source/bird-data/dataset/ebird-data/version/2012-Nov-4/params/enhancement/1/stateCoded> "MD" .
?measurement <http://was.tw.rpi.edu/source/bird-data/dataset/ebird-data/vocab/enhancement/1/locality> ?location .
    ?measurement <http://was.tw.rpi.edu/source/bird-data/dataset/ebird-data/vocab/enhancement/1/scientific_name> ?scientific_name . 
    ?measurement <http://was.tw.rpi.edu/source/bird-data/dataset/ebird-data/vocab/enhancement/1/observation_count> ?count . 
    ?measurement <http://was.tw.rpi.edu/source/bird-data/dataset/ebird-data/vocab/enhancement/1/observation_date> ?date . 
    ?measurement <http://www.w3.org/2003/01/geo/wgs84_pos#lat> ?lat . 
    ?measurement <http://www.w3.org/2003/01/geo/wgs84_pos#long> ?long . 
    
    "Castle Haven Rd"
}
} ORDER BY ?location
		 */
		
/*would it be better for matthew if i have a json with the locality and date as the key, and then a json array with key/values for location,
		
		*/
		
		final Query query = config.getQueryFactory().newQuery(Type.SELECT);	
		//Variables
		String countyCode = (String) request.getParam("county");
		String stateAbbr = (String) request.getParam("state");	
		
		assert(countyCode != null);
		assert(stateAbbr != null);
		
		//URIs
		//final QueryResource dataSet = query.getResource("http://sparql.tw.rpi.edu/source/akn/dataset/GBBC_CSV/version/2012-Oct-19");
		final QueryResource dataSet = query.getResource("http://was.tw.rpi.edu/source/bird-data/dataset/ebird-data/version/2012-Nov-4");
		//<http://was.tw.rpi.edu/source/bird-data/dataset/ebird-data/version/2012-Nov-4>
		final QueryResource inDataSet = query.getResource("http://rdfs.org/ns/void#inDataset");
		                                             //http://rdfs.org/ns/void#inDataset	
		final QueryResource countyCoded = query.getResource(e1_NS + "countyCoded");
		final QueryResource stateAbbrev = query.getResource(e1_NS + "stateCoded");
		final Variable measurement = query.getVariable(VAR_NS+"measurement");
		final Variable count = query.getVariable(QUERY_NS+"count");
		final Variable date = query.getVariable(QUERY_NS+"date");
		final Variable commonName = query.getVariable(VAR_NS+"commonName");
		final Variable scientificName = query.getVariable(VAR_NS + "scientific_name");
		final Variable species = query.getVariable(VAR_NS + "species");

		final Variable lat = query.getVariable(QUERY_NS+LAT);
		final Variable lng = query.getVariable(QUERY_NS+LONG);
		
		final QueryResource wgsLat = query.getResource(WGS_NS+LAT);
		final QueryResource wgsLong = query.getResource(WGS_NS+LONG);
		final QueryResource birdCount = query.getResource(e2_NS+"observation_count");
		final QueryResource obsDate = query.getResource(e2_NS+"observation_date");
		
		//query based on the species name, you are doing this across two graphs
		final QueryResource hasCommonName = query.getResource(TXN_NS+"CommonNameID");
		final QueryResource hasScientificName = query.getResource(e2_NS+ "scientific_name");
		final QueryResource hasLabel = query.getResource(RDFS_NS+ "label");
		
		//build query
		Set<Variable> vars = new LinkedHashSet<Variable>();
		vars.add(measurement);
		vars.add(count);
		vars.add(date);
		vars.add(commonName);
		vars.add(scientificName);
		vars.add(lat);
		vars.add(lng);
		//vars.add(species);
		
		//vars.add(measurement);

		query.setVariables(vars);
		//query pattern
		final NamedGraphComponent graph = query.getNamedGraph("http://was.tw.rpi.edu/ebird-data-ca");

		graph.addPattern(measurement, inDataSet, dataSet);
		graph.addPattern(measurement, countyCoded, countyCode,null);
		graph.addPattern(measurement, stateAbbrev, stateAbbr,null);
		graph.addPattern(measurement, birdCount, count);
		graph.addPattern(measurement, obsDate, date);
		graph.addPattern(measurement, hasCommonName, commonName);
		graph.addPattern(measurement, hasScientificName, scientificName);
		graph.addPattern(measurement, wgsLat, lat);
		graph.addPattern(measurement, wgsLong, lng);
	
	//for helper would need to pass a request and a query object
		//coll is created within.
		//here we are binding the search to specific species
		if(request.getParam("species") != null && ((JSONArray) request.getParam("species")).length() > 1  ){// && request.getParam("species").length() > 0) {
		    request.getLogger().error("species length: " + ((JSONArray) request.getParam("species")).length());

		    request.getLogger().error("(got to else if where species > 1)");

			//note that this is going to be a json array of strings
			//you're "joining" on the scientific name, for now
			JSONArray speciesParams = (JSONArray)request.getParam("species");	
			final UnionComponent union = query.createUnion();
			final NamedGraphComponent graph2 = query.getNamedGraph("http://was.tw.rpi.edu/ebird-taxonomy");
			graph2.addGraphComponent(union);
			//GraphComponentCollection coll = union.getUnionComponent(0);
			GraphComponentCollection coll;
			//for each element in the bbq state array for "species" key
			for(int i = 0; i < speciesParams.length(); i++)
			{
				//JSONObject objectInArray = speciesParams.getJSONObject(i);
				String speciesInArray = speciesParams.getString(i);				
				
				String resultStr = queryIfTaxonomicCategory(request, speciesInArray);
				//if there are any bindings union up				
				if(resultStr != "FAILURE"){
			    request.getLogger().error("subclassOf results: " + resultStr);
			    JSONObject results = new JSONObject(resultStr);
			    JSONArray data = (JSONArray) results.get("data");
			    	//data.length is  > 0 then there were positive results, so now we can ask for subclasses of selection
			    request.getLogger().error("data.length : " + data.length());

			    	if(data.length() > 0){			    		
			    		//just use the pattern species subClassOf speciesSelection
		    			final QueryResource addedSpecies = query.getResource(speciesInArray);
		    			final QueryResource subClassOf = query.getResource(RDFS_NS+"subClassOf");
						coll = union.getUnionComponent(i);
						//final NamedGraphComponent graph3 = query.getNamedGraph("http://was.tw.rpi.edu/ebird-taxonomy");
						//coll.addGraphComponent(graph3);					
						//ebird taxonomy graph
				        coll.addPattern(species, subClassOf, addedSpecies);	
				        coll.addPattern(species, hasLabel, scientificName);	
				        //ebird data graph (already handled in the first part of the query)
				        //graph3.addPattern(addedSpecies, hasLabel, scientificName);			    			
			    		
			    	}
			    	else{
					    request.getLogger().error("(no results for queryIfTaxonomicCategory)");
					  final QueryResource addedSpecies = query.getResource(speciesInArray);
						coll = union.getUnionComponent(i);
				        coll.addPattern(addedSpecies, hasLabel, scientificName);		
			    	}
				}
				else{
				    request.getLogger().error("(failure to queryIfTaxonomicCategory)");
				}			      			    					
			}			
		}	
		else if (((JSONArray) request.getParam("species")).length() == 1){
		    request.getLogger().error("(got to else if where species == 1)");

			JSONArray speciesParams = (JSONArray)request.getParam("species");	
			String speciesInArray = speciesParams.getString(0);		
			String resultStr = queryIfTaxonomicCategory(request, speciesInArray);
			if(resultStr != "FAILURE"){
			    request.getLogger().error("subclassOf results: " + resultStr);
			    JSONObject results = new JSONObject(resultStr);
			    JSONArray data = (JSONArray) results.get("data");
			    	//data.length is  > 0 then there were positive results, so now we can ask for subclasses of selection
			    request.getLogger().error("data.length : " + data.length());
			    final NamedGraphComponent graph2 = query.getNamedGraph("http://was.tw.rpi.edu/ebird-taxonomy");
				final QueryResource addedSpecies = query.getResource(speciesInArray);
			    	if(data.length() > 0){			    		
			    		//just use the pattern species subClassOf speciesSelection
		    			final QueryResource subClassOf = query.getResource(RDFS_NS+"subClassOf");

					    request.getLogger().error("addedSpecies: " + addedSpecies.toString());
					    request.getLogger().error("species: " + species.toString());
					    request.getLogger().error("subclassof: " + subClassOf.toString());

				        graph2.addPattern(species, subClassOf, addedSpecies);	
				        graph2.addPattern(species, hasLabel, scientificName);				       
			    	}
			    	else{
					    request.getLogger().error("(no results for queryIfTaxonomicCategory)");
						graph2.addPattern(addedSpecies, hasLabel, scientificName);		
			    	}
				}		
		}
		else{
			final NamedGraphComponent graph2 = query.getNamedGraph("http://was.tw.rpi.edu/ebird-taxonomy");
			graph2.addPattern(species, hasLabel, scientificName);		
		}
	    request.getLogger().error("query is : " + query.toString());

		return config.getQueryExecutor(request).accept("application/json").execute(query);		
	}
	
	/**
	 * This method returns all bird data measurements for a specific site, using uris in bbq state county, state, and uri (for site).
	 * It is done against the endpoint and not the loaded RDF model.
	 * This is called by the UI after a specific bird icon is selected.
	 * @param request
	 * @return
	 * @throws JSONException
	 */
	@QueryMethod
	public String queryForSpeciesForASite(Request request) throws JSONException{
		Query query = config.getQueryFactory().newQuery(Type.SELECT);
		String countyCode = (String) request.getParam("county");
		String stateAbbr = (String) request.getParam("state");
		String site = (String) request.getParam("uri");
		assert(countyCode != null);
		assert(stateAbbr != null);
		assert(site != null);
		//final NamedGraphComponent graph = query.getNamedGraph("http://was.tw.rpi.edu/ebird-data2");
		//currently we have only loaded bird data for California from eBird (conversion to rdf was taking over a week)
		final NamedGraphComponent graph = query.getNamedGraph("http://was.tw.rpi.edu/ebird-data-ca");

		final Variable measurement = query.getVariable(QUERY_NS+"measurement");
		final QueryResource locality = query.getResource("http://was.tw.rpi.edu/source/bird-data/dataset/ebird-data/vocab/enhancement/1/locality"); 
		//update locality property namespace********
		//final QueryResource siteUri = query.getResource(Site); //update locality property namespace	
		final QueryResource countyCoded = query.getResource(e1_NS + "countyCoded");
		final QueryResource stateAbbrev = query.getResource(e1_NS + "stateCoded");	
		final Variable count = query.getVariable(QUERY_NS+"count");
		final Variable date = query.getVariable(QUERY_NS+"date");
		final Variable commonName = query.getVariable(VAR_NS+"commonName");
		final Variable scientificName = query.getVariable(VAR_NS + "scientific_name");
		final QueryResource birdCount = query.getResource(e2_NS+"observation_count");
		final QueryResource obsDate = query.getResource(e2_NS+"observation_date");
		final QueryResource hasCommonName = query.getResource(TXN_NS+"CommonNameID");
		final QueryResource hasScientificName = query.getResource(e2_NS+ "scientific_name");
		final QueryResource siteUri = query.getResource(site);

		
		Set<Variable> vars = new LinkedHashSet<Variable>();
		//vars.add(measurement);
		vars.add(count);
		vars.add(date);
		//vars.add(commonName);
		vars.add(scientificName);
		query.setVariables(vars);	
		graph.addPattern(measurement, countyCoded, countyCode,null);
		graph.addPattern(measurement, stateAbbrev, stateAbbr,null);
		graph.addPattern(measurement, locality, siteUri);	
		//the above three are grounded.
		graph.addPattern(measurement, birdCount, count);
		graph.addPattern(measurement, obsDate, date);
		graph.addPattern(measurement, hasCommonName, commonName);
		graph.addPattern(measurement, hasScientificName, scientificName);	
		
		query = addSpeciesClause(request, query); 
		
	    request.getLogger().error("in SpeciesDataProviderModule.queryForSpeciesForASite, query is : " + query.toString());
		return config.getQueryExecutor(request).accept("application/json").execute(query);		
	}
	
	/**
	 * This method queries and returns bird count data for the current county and state using "county" and "state" from bbq state.
	 * It checks that if the selected species is not a leaf, it adds a pattern for subclass of.
	 * When we add an ebird-data graph for each state, will need to add a graph for finding each, similar
	 * to EPA data where there is a graph for the graph uri and relations to states.
	 * 
	 * This is called only when the water or air data is initially selected, and bird data is sought after nearby the selected zip code. 
	 * In the future we should enable the user to specify distance from selected site.
	 * @param request
	 * @return
	 * @throws JSONException
	 */
	@QueryMethod
	public String queryForNearbySpeciesCounts(Request request) throws JSONException{
	    request.getLogger().info("reached queryForNearbySpeciesCounts\n");

		//this works for eBird only
		Query query = config.getQueryFactory().newQuery(Type.SELECT);	
		//Variables
		String countyCode = (String) request.getParam("county");
		String stateAbbr = (String) request.getParam("state");	
		
		assert(countyCode != null);
		assert(stateAbbr != null);
		
		//URIs
		//final QueryResource dataSet = query.getResource("http://sparql.tw.rpi.edu/source/akn/dataset/GBBC_CSV/version/2012-Oct-19");
		final QueryResource dataSet = query.getResource("http://was.tw.rpi.edu/source/bird-data/dataset/ebird-data/version/2012-Nov-4");
		//<http://was.tw.rpi.edu/source/bird-data/dataset/ebird-data/version/2012-Nov-4>
		final QueryResource inDataSet = query.getResource("http://rdfs.org/ns/void#inDataset");
		                                             //http://rdfs.org/ns/void#inDataset	
		final QueryResource countyCoded = query.getResource(e1_NS + "countyCoded");
		final QueryResource stateAbbrev = query.getResource(e1_NS + "stateCoded");
		final Variable measurement = query.getVariable(VAR_NS+"measurement");
		final Variable count = query.getVariable(QUERY_NS+"count");
		final Variable date = query.getVariable(QUERY_NS+"date");
		final Variable commonName = query.getVariable(VAR_NS+"commonName");
		final Variable scientificName = query.getVariable(VAR_NS + "scientific_name");
		final Variable species = query.getVariable(VAR_NS + "species");

		final Variable lat = query.getVariable(QUERY_NS+LAT);
		final Variable lng = query.getVariable(QUERY_NS+LONG);
		
		final QueryResource wgsLat = query.getResource(WGS_NS+LAT);
		final QueryResource wgsLong = query.getResource(WGS_NS+LONG);
		final QueryResource birdCount = query.getResource(e2_NS+"observation_count");
		final QueryResource obsDate = query.getResource(e2_NS+"observation_date");
		
		//query based on the species name, you are doing this across two graphs
		final QueryResource hasCommonName = query.getResource(TXN_NS+"CommonNameID");
		final QueryResource hasScientificName = query.getResource(e2_NS+ "scientific_name");
		final QueryResource hasLabel = query.getResource(RDFS_NS+ "label");
		
		//build query
		Set<Variable> vars = new LinkedHashSet<Variable>();
		vars.add(measurement);
		vars.add(count);
		vars.add(date);
		vars.add(commonName);
		vars.add(scientificName);
		vars.add(lat);
		vars.add(lng);
		//vars.add(species);
		
		//vars.add(measurement);

		query.setVariables(vars);
		//query pattern
		//final NamedGraphComponent graph = query.getNamedGraph("http://was.tw.rpi.edu/ebird-data2");
		final NamedGraphComponent graph = query.getNamedGraph("http://was.tw.rpi.edu/ebird-data-ca");

		graph.addPattern(measurement, inDataSet, dataSet);
		graph.addPattern(measurement, countyCoded, countyCode,null);
		graph.addPattern(measurement, stateAbbrev, stateAbbr,null);
		graph.addPattern(measurement, birdCount, count);
		graph.addPattern(measurement, obsDate, date);
		graph.addPattern(measurement, hasCommonName, commonName);
		graph.addPattern(measurement, hasScientificName, scientificName);
		graph.addPattern(measurement, wgsLat, lat);
		graph.addPattern(measurement, wgsLong, lng);
	
		/*
		 * 
		 you need to optionally union all subclasses for a measurement. this is how it was done in s2s.
		 
		 pattern:
		 //addedSpecies, hasLabel, scientificName	  
		  */
		 
		 /*
		function organismConstraint($organisms) {
	        file_put_contents("/tmp/debug.log", print_r("\n\ngot to Organism Constraint\n\n", TRUE), FILE_APPEND);
	        $arr = array();
	        for ($i = 0; $i < count($organisms); ++$i)
	                array_push($arr, '{ GRAPH <http://dataone.tw.rpi.edu/inf> { ?organismsSubclass skos:broaderTransitive <'.$organisms[$i].'> } . }' . "\n");
	   //     return implode('UNION ', $arr) . '?organism a ?organismsSubclass . ?dataset sbc:hasKeyword ?organism .' . "\n";
	      return implode('UNION ', $arr) . '?dataset sbc:hasKeyword ?organismsSubclass .' . "\n";

	}
	*/
		
		//before we do this, test that queryForNearbySpeciesCounts works
		/*
		String speciesParams = (String)request.getParam("species");
		if(speciesParams != null && speciesParams.length() > 0) {
			//throw new IllegalArgumentException("The source parameter must be supplied");
			//note that this is going to be a json object
			JSONObject jsonSpecies = new JSONObject(species);
			//iterate over the species and create a union clause
			//iterate over jason entity uris
			graph2.addPattern(species, hasLabel, scientificName);				
			
		}
		*/
		
		 // add item to the found graph
		/*
		final UnionComponent union = query.createUnion();
		 
        GraphComponentCollection coll = union.getUnionComponent(0);
        coll.addPattern(measurement, timeInXSDDateTime, timeVar);
        coll = union.getUnionComponent(1);
        coll.addPattern(measurement, dcDate, timeVar);
        graph.addGraphComponent(union);		
		
		*/	
		/*
		select * where {

			graph <http://was.tw.rpi.edu/ebird-taxonomy>{


			?species rdfs:label ?l .
			?species rdfs:subClassOf ?superClass  option(transitive) .
			}

			graph <http://was.tw.rpi.edu/ebird-data> {

			?m <http://was.tw.rpi.edu/source/bird-data/dataset/ebird-data/vocab/enhancement/1/scientific_name> ?l .

			}
			}
		*/
		
		//we want to get their lat, long, category, commonName, observation date/time observation started, observationCount, 
		//http://lod.taxonconcept.org/ontology/txn.owl#CommonNameID
		//http://was.tw.rpi.edu/source/bird-data/dataset/ebird-data/vocab/enhancement/1/observation_count
		//http://was.tw.rpi.edu/source/bird-data/dataset/ebird-data/vocab/enhancement/1/observation_date
		
		//graph.addPattern(measurement, lat, lat,null);
		//graph.addPattern(measurement, long, long,null);
		//graph.addPattern(measurement, long, long,null);

		//http://sparql.tw.rpi.edu/source/epa-air/id/aqs-site/08-001-3001	
		// ?m void:inDataSet <http://sparql.tw.rpi.edu/source/akn/dataset/GBBC_CSV/version/2012-Oct-19>
		
		query = addSpeciesClause(request, query); 
	    request.getLogger().error("in SpeciesDataProviderModule.queryForNearbySpeciesCounts, query is : " + query.toString());
		return config.getQueryExecutor(request).accept("application/json").execute(query);		
	}
	/**
	 * Serves as a simple test bird taxonomy.
	 * @deprecated
	 * @param request
	 * @return
	 * @throws IOException
	 * @throws JSONException
	 */
	@QueryMethod
	public String queryBirdTaxonomy(Request request) throws IOException, JSONException{	
		
		// * this was for testing, now using real sparql data
		JSONArray data = new JSONArray();
		data.put(new JSONObject().put("Acanthis", "Fringillidae"));
		data.put(new JSONObject().put("Fringillidae", "Passeriformes"));
		data.put(new JSONObject().put("Passeriformes", "Aves"));
		data.put(new JSONObject().put("Aves", "Chordata"));
		data.put(new JSONObject().put("Chordata", "Animalia"));
		
		data.put(new JSONObject().put("Accipiter", "Accipitridae"));
		data.put(new JSONObject().put("Accipitridae", "Falconiformes"));
		data.put(new JSONObject().put("Falconiformes", "Aves"));
		data.put(new JSONObject().put("Aves", "Chordata"));
		data.put(new JSONObject().put("Chordata", "Animalia"));
		data.put(new JSONObject().put("Animalia", ""));
		
		//in the visit which has query and request for characteristic module (use archtype)
		//first check ifg bbbq state is null for chemical
		//new Query().findGraphComponentsWithPattern(?measurement, pol:hasCharacteristic, null)
		//returns a list of graphComponent. should be a singleon list, check on what it returns when empty
	    //if not empty, graph.addpattern();
		
		
		
		return data.toString();
		
		/*
		final Query query = config.getQueryFactory().newQuery(Type.SELECT);	
		//Variables
		final Variable child = query.getVariable(VAR_NS+ "child");
		final Variable parent = query.getVariable(VAR_NS+ "parent");		
		//URIs
		final QueryResource subClassOf = query.getResource(RDFS_NS + "subClassOf");
		final QueryResource inDataSet = query.getResource("http://rdfs.org/ns/void#inDataset");
		                                                 //http://rdfs.org/ns/void#inDataset
		//build query
		Set<Variable> vars = new LinkedHashSet<Variable>();
		vars.add(child);
		vars.add(parent);
		query.setVariables(vars);
		String parentClass = null;
		//query pattern
		//query.addPattern(site, inDataSet, dataSet);
		final NamedGraphComponent graph = query.getNamedGraph("http://was.tw.rpi.edu/bird-taxonomy");
		graph.addPattern(child, subClassOf, parent);

		// ?m void:inDataSet <http://sparql.tw.rpi.edu/source/akn/dataset/GBBC_CSV/version/2012-Oct-19>	
		String responseStr = FAILURE;
		//config.getQueryExecutor(request).accept("application/json").execute(query);
		
		
		String resultStr = config.getQueryExecutor(request).accept("application/json").execute(query);
		////return config.getQueryExecutor(request).accept("application/json").execute(query);

		
		log.debug("Results: "+resultStr);
		if(resultStr == null) {
			return responseStr;
		}
		try {
			JSONObject results = new JSONObject(resultStr);
			JSONObject response = new JSONObject();
			JSONArray data = new JSONArray();
			response.put("success", true);
			response.put("data", data);
			results = results.getJSONObject("results");
			JSONArray bindings = results.getJSONArray(BINDINGS);
			for(int i=0;i<bindings.length();i++) {
				JSONObject binding = bindings.getJSONObject(i);
				String childClass = binding.getJSONObject("child").getString("value");
				try {
					parentClass = binding.getJSONObject("parent").getString("value");
				}
				catch(Exception e) { }
				//if(labelStr == null) {
				//	labelStr = sourceUri.substring(sourceUri.lastIndexOf('/')+1).replace('-', '.');
				//}
				JSONObject mapping = new JSONObject();
				mapping.put("child", childClass);
				mapping.put("parent", parentClass);
				data.put(mapping);
			}
			responseStr = response.toString();
		} catch (JSONException e) {
			log.error("Unable to parse JSON results", e);
		}
		return responseStr;
		
		*/
		//return config.getQueryExecutor(request).accept("application/json").execute(query);		
		

		/*
		String endpoint = "";
		final Query query = config.getQueryFactory().newQuery(Type.SELECT);
		
		String responseStr = FAILURE;
		config.getQueryExecutor(request).accept("application/json").execute(query);
		String resultStr = config.getQueryExecutor(request).execute(endpoint, query);
		log.debug("Results: "+resultStr);
		if(resultStr == null) {
			return responseStr;
		}
		try {
			JSONObject results = new JSONObject(resultStr);
			JSONObject response = new JSONObject();
			JSONArray data = new JSONArray();
			response.put("success", true);
			response.put("data", data);
			results = results.getJSONObject("results");
			JSONArray bindings = results.getJSONArray(BINDINGS);
			for(int i=0;i<bindings.length();i++) {
				JSONObject binding = bindings.getJSONObject(i);
				String sourceUri = binding.getJSONObject(SOURCE_VAR).getString(VALUE);
				String labelStr = null;
				try {
					labelStr = binding.getJSONObject(LABEL_VAR).getString(VALUE);
				}
				catch(Exception e) { }
				if(labelStr == null) {
					labelStr = sourceUri.substring(sourceUri.lastIndexOf('/')+1).replace('-', '.');
				}
				JSONObject mapping = new JSONObject();
				mapping.put("uri", sourceUri);
				mapping.put(LABEL_VAR, labelStr);
				data.put(mapping);
			}
			responseStr = response.toString();
		} catch (JSONException e) {
			log.error("Unable to parse JSON results", e);
		}
		return responseStr;
		*/
		
	


		
	}

	
	
		/*
		 * 
		 * 
		//this works for eBird only
		final Query query = config.getQueryFactory().newQuery(Type.SELECT);	
		//Variables
		final Variable site = query.getVariable(VAR_NS+SITE_VAR);
		String countyCode = (String) request.getParam("county");
		String stateAbbr = (String) request.getParam("state");	
		
		assert(countyCode != null);
		assert(stateAbbr != null);
		
		//URIs
		//final QueryResource dataSet = query.getResource("http://sparql.tw.rpi.edu/source/akn/dataset/GBBC_CSV/version/2012-Oct-19");
		final QueryResource dataSet = query.getResource("http://was.tw.rpi.edu/source/bird-data/dataset/ebird-data/version/2012-Nov-4");
		//<http://was.tw.rpi.edu/source/bird-data/dataset/ebird-data/version/2012-Nov-4>
		final QueryResource inDataSet = query.getResource("http://rdfs.org/ns/void#inDataset");
		                                             //http://rdfs.org/ns/void#inDataset	
		final QueryResource countyCoded = query.getResource(e1_NS + "countyCoded");
		final QueryResource stateAbbrev = query.getResource(e1_NS + "stateCoded");
		final Variable measurement = query.getVariable(VAR_NS+"measurement");
		final Variable count = query.getVariable(QUERY_NS+"count");
		final Variable date = query.getVariable(QUERY_NS+"date");
		final Variable commonName = query.getVariable(VAR_NS+"commonName");
		final Variable scientificName = query.getVariable(VAR_NS + "scientific_name");
		final Variable species = query.getVariable(VAR_NS + "species");

		final Variable lat = query.getVariable(QUERY_NS+LAT);
		final Variable lng = query.getVariable(QUERY_NS+LONG);
		final Variable label = query.getVariable(QUERY_NS+LABEL_VAR);
		
		final QueryResource wgsLat = query.getResource(WGS_NS+LAT);
		final QueryResource wgsLong = query.getResource(WGS_NS+LONG);
		final QueryResource birdCount = query.getResource(e2_NS+"observation_count");
		final QueryResource obsDate = query.getResource(e2_NS+"observation_date");
		
		//query based on the species name, you are doing this across two graphs
		final QueryResource hasCommonName = query.getResource(TXN_NS+"CommonNameID");
		final QueryResource hasScientificName = query.getResource(e2_NS+ "scientific_name");
		final QueryResource hasLabel = query.getResource(RDFS_NS+ "label");
		
		//build query
		Set<Variable> vars = new LinkedHashSet<Variable>();
		vars.add(measurement);
		vars.add(count);
		vars.add(date);
		vars.add(commonName);
		vars.add(scientificName);
		vars.add(lat);
		vars.add(lng);
		vars.add(species);
		
		//vars.add(measurement);

		query.setVariables(vars);
		//query pattern
		final NamedGraphComponent graph = query.getNamedGraph("http://was.tw.rpi.edu/ebird-data");
		graph.addPattern(measurement, inDataSet, dataSet);
		graph.addPattern(measurement, countyCoded, countyCode,null);
		graph.addPattern(measurement, stateAbbrev, stateAbbr,null);
		graph.addPattern(measurement, birdCount, count);
		graph.addPattern(measurement, obsDate, date);
		graph.addPattern(measurement, hasCommonName, commonName);
		graph.addPattern(measurement, hasScientificName, scientificName);
		graph.addPattern(measurement, wgsLat, lat);
		graph.addPattern(measurement, wgsLong, lng); 
		 * 
		 * 
		 */

	/**
	 * This method determines if the jstree selected species (updated in bbq "species") has any sibling species with data.
	 * If so, it returns the list of siblings for user confirmation for plotting. This method is only called when there is one selected species and there is no data returned for it.
	 * @param request
	 * @return JSON Array as a string
	 * @throws JSONException
	 */
	@QueryMethod
	public String queryIfSiblingsExist(Request request) throws JSONException{
		String singletonSpecies ="";
		
		//count(?measurement)
		//?measurement ofEntity ?type
		//?type subClassOf ?class
		//species subClassof ?class
		JSONArray speciesParams = (JSONArray)request.getParam("species");			

		//*****only call this if there is only one! so here we assume there is one parameter
		if(speciesParams != null && speciesParams.length() == 1) {			
			singletonSpecies = speciesParams.getString(0);							
		}
		
		final Query query = config.getQueryFactory().newQuery(Type.SELECT);	
		final QueryResource hasScientificName = query.getResource(e2_NS+ "scientific_name");
		final QueryResource hasLabel = query.getResource(RDFS_NS + "label");
		String countyCode = (String) request.getParam("county");
		String stateAbbr = (String) request.getParam("state");	
		
		final Variable parent = query.getVariable(VAR_NS+ "parent");
		final Variable measurement = query.getVariable(VAR_NS+ "measurement");
		final Variable scientificName = query.getVariable(VAR_NS + "scientific_name");
		final Variable commonName = query.getVariable(VAR_NS+"commonName");
		final QueryResource hasCommonName = query.getResource(TXN_NS+"CommonNameID");
		final Variable siblingScientificName = query.getVariable(VAR_NS + "scientific_name");
		final QueryResource countyCoded = query.getResource(e1_NS + "countyCoded");
		final QueryResource stateAbbrev = query.getResource(e1_NS + "stateCoded");

		final Variable sibling = query.getVariable(VAR_NS+ "sibling");
		query.setDistinct(true);

		//final Variable count = query.createVariableExpression("count(?measurement) as ?"+ measurement);
		final QueryResource subClassOf = query.getResource(RDFS_NS + "subClassOf");
		final QueryResource species = query.getResource(singletonSpecies);
		final NamedGraphComponent graph = query.getNamedGraph("http://was.tw.rpi.edu/ebird-taxonomy");
		
		Set<Variable> vars = new LinkedHashSet<Variable>();
		//vars.add(measurement);
		//vars.add(parent);
		vars.add(sibling);
		vars.add(commonName);

		query.setVariables(vars);

		graph.addPattern(species, subClassOf, parent); 
		graph.addPattern(sibling, subClassOf, parent); 
		graph.addPattern(sibling, hasCommonName, commonName); 
        // graph.addPattern(species, hasLabel, scientificName);	
        graph.addPattern(sibling, hasLabel, siblingScientificName);	
		graph.addFilter("?sibling != <" + singletonSpecies + ">");
		//final NamedGraphComponent graph2 = query.getNamedGraph("http://was.tw.rpi.edu/ebird-data2");
		final NamedGraphComponent graph2 = query.getNamedGraph("http://was.tw.rpi.edu/ebird-data-ca");
		
		//if this works we can just then do "get sbiling data" now

		graph2.addPattern(measurement, hasScientificName, scientificName); //selected species
		graph2.addPattern(measurement, hasScientificName, siblingScientificName); //sibling only if type matches
		graph2.addPattern(measurement, countyCoded, countyCode,null);
		graph2.addPattern(measurement, stateAbbrev, stateAbbr,null);

		String resultStr = config.getQueryExecutor(request).accept("application/json").execute(query);
		String responseStr = FAILURE;
		log.debug("Results: "+resultStr);
		if(resultStr == null) {
			return responseStr;
		}
		try {
			JSONObject results = new JSONObject(resultStr);
			JSONObject response = new JSONObject();
			JSONArray data = new JSONArray();
			response.put("success", true);
			response.put("data", data);
			results = results.getJSONObject("results");
			JSONArray bindings = results.getJSONArray(BINDINGS);
			for(int i=0;i<bindings.length();i++) {
				JSONObject binding = bindings.getJSONObject(i);
				String siblingVar = binding.getJSONObject("sibling").getString("value");
				String commonNameVar = binding.getJSONObject("commonName").getString("value");

				//String subclassLabel = binding.getJSONObject("label").getString("value");

				try {
					//superclassId = binding.getJSONObject("parent").getString("value");
				}
				catch(Exception e) { }
				//if(labelStr == null) {
				//	labelStr = sourceUri.substring(sourceUri.lastIndexOf('/')+1).replace('-', '.');
				//}
				JSONObject mapping = new JSONObject();
				mapping.put("sibling", siblingVar);
				mapping.put("commonName", commonNameVar);
				//mapping.put("label", subclassLabel);
				//mapping.put("parent", superclassId);
				data.put(mapping);
			}
			responseStr = response.toString();
		} catch (JSONException e) {
			log.error("Unable to parse JSON results", e);
		}
		return responseStr;
		
	
		
		/*
		graph.addPattern(measurement, countyCoded, countyCode,null);
		graph.addPattern(measurement, stateAbbrev, stateAbbr,null);
		graph.addPattern(measurement, birdCount, count);
		graph.addPattern(measurement, obsDate, date);
		graph.addPattern(measurement, hasCommonName, commonName);
		*/

//only do this if there is one selected species
		//also, update a field in the response that we have additional data
		//visualizeCharacteristic, getTestForCharacteristic
		

	}

	/*
	String speciesParams = (String)request.getParam("species");
	if(speciesParams != null && speciesParams.length() > 0) {
		//throw new IllegalArgumentException("The source parameter must be supplied");
		//note that this is going to be a json object
		JSONObject jsonSpecies = new JSONObject(species);
		//iterate over the species and create a union clause
		//iterate over jason entity uris
		 * 
		 * 
		 
		 
		 after you select bbq arguments
		 scientificName subClassOf <selection>
		graph2.addPattern(species, hasLabel, scientificName);		
		.createVariableExpression
		cf. the Regulation module
				
	}
	*/
	
	
	
/**
 * This method returns the immediate subclasses of the top-level class, in the current case http://ebird#birdTaxonomy.
 * @param request
 * @return json array with json object where key/value success/true and data key with array of objects
 * with id, label, and parent triples as key/value pairs.
 * 
 * @throws IOException
 * @throws JSONException
 */
	@QueryMethod
	public String queryeBirdTaxonomyRoots(Request request) throws IOException, JSONException{	
		final Query query = config.getQueryFactory().newQuery(Type.SELECT);	
		//Variables
		final Variable id = query.getVariable(VAR_NS+ "child");
		final Variable label = query.getVariable(VAR_NS+ "label");
		final Variable parent = query.getVariable(VAR_NS+ "parent");		
		//URIs
		final QueryResource subClassOf = query.getResource(RDFS_NS + "subClassOf");
		final QueryResource hasLabel = query.getResource(RDFS_NS + "label");
		
		final QueryResource birdTaxonomy = query.getResource("http://ebird#birdTaxonomy");
	    request.getLogger().info("reached queryeBirdTaxonomyRoots \n");

		//build query
		Set<Variable> vars = new LinkedHashSet<Variable>();
		vars.add(id);
		vars.add(label);
		vars.add(parent);
		query.setVariables(vars);
		query.addOrderBy(label, SortType.ASC);
		//query.addPattern(site, inDataSet, dataSet);
				final NamedGraphComponent graph = query.getNamedGraph("http://was.tw.rpi.edu/ebird-taxonomy");
				graph.addPattern(id, subClassOf, parent);
				graph.addPattern(id, subClassOf, birdTaxonomy);

				graph.addPattern(id, hasLabel, label);
				//for testing common names
				//graph.addPattern(id, hasCommonName, label);

				//get only the subclasses of the subclasses of OWL thing
				String responseStr = FAILURE;
			    request.getLogger().info("Query object in queryeBirdTaxonomyRoots is: " + query.toString());

				String resultStr = config.getQueryExecutor(request).accept("application/json").execute(query);
				////return config.getQueryExecutor(request).accept("application/json").execute(query);
				log.debug("Results: "+resultStr);
				if(resultStr == null) {
					return responseStr;
				}
				try {
					JSONObject results = new JSONObject(resultStr);
					JSONObject response = new JSONObject();
					JSONArray data = new JSONArray();
					response.put("success", true);
					response.put("data", data);
					String superclassId = null;
					results = results.getJSONObject("results");
					JSONArray bindings = results.getJSONArray(BINDINGS);
					for(int i=0;i<bindings.length();i++) {
						JSONObject binding = bindings.getJSONObject(i);
						String subclassId = binding.getJSONObject("child").getString("value");
						String subclassLabel = binding.getJSONObject("label").getString("value");

						try {
							superclassId = binding.getJSONObject("parent").getString("value");
						}
						catch(Exception e) { }
						//if(labelStr == null) {
						//	labelStr = sourceUri.substring(sourceUri.lastIndexOf('/')+1).replace('-', '.');
						//}
						JSONObject mapping = new JSONObject();
						mapping.put("id", subclassId);
						mapping.put("label", subclassLabel);
						mapping.put("parent", superclassId);
						data.put(mapping);
					}
					responseStr = response.toString();
				} catch (JSONException e) {
					log.error("Unable to parse JSON results", e);
				}
				return responseStr;			
	}
	
	
	/**
	 * This method returns the subclass data for the uri added to bbq state "queryeBirdTaxonomySubClasses", and is necessary
	 * for the dynamic jstree selection.
	 * @param request
	 * @return JSONArray as string of subclasses (uri), label, parent uri)
	 * @throws IOException
	 * @throws JSONException
	 */
	@QueryMethod
	public String queryeBirdTaxonomySubClasses(Request request) throws IOException, JSONException{	
		
		String classRequiresSubclassesString = (String) request.getParam("queryeBirdTaxonomySubClasses");	
		if(classRequiresSubclassesString == null){
			return null;
		}

		final Query query = config.getQueryFactory().newQuery(Type.SELECT);	
		//Variables
		final Variable id = query.getVariable(VAR_NS+ "child");
		final Variable label = query.getVariable(VAR_NS+ "label");
		final Variable parent = query.getVariable(VAR_NS+ "parent");		
		//URIs
		final QueryResource subClassOf = query.getResource(RDFS_NS + "subClassOf");
		final QueryResource hasCommonName = query.getResource("http://lod.taxonconcept.org/ontology/txn.owl#CommonNameID");

		//final QueryResource birdTaxonomy = query.getResource("http://ebird#birdTaxonomy");
		final QueryResource classRequiresSubclasses = query.getResource(classRequiresSubclassesString);

		//build query
		Set<Variable> vars = new LinkedHashSet<Variable>();
		vars.add(id);
		vars.add(label);
		vars.add(parent);
		query.setVariables(vars);
		query.addOrderBy(label, SortType.ASC);
		//query.addPattern(site, inDataSet, dataSet);
				final NamedGraphComponent graph = query.getNamedGraph("http://was.tw.rpi.edu/ebird-taxonomy");
				graph.addPattern(id, subClassOf, parent);
				graph.addPattern(id, subClassOf, classRequiresSubclasses);

				//graph.addPattern(id, hasLabel, label);
				graph.addPattern(id,hasCommonName, label);
				//get only the subclasses of the subclasses of OWL thing
				String responseStr = FAILURE;
				
				String resultStr = config.getQueryExecutor(request).accept("application/json").execute(query);
				////return config.getQueryExecutor(request).accept("application/json").execute(query);
				log.debug("Results: "+resultStr);
				if(resultStr == null) {
					return responseStr;
				}
				try {
					JSONObject results = new JSONObject(resultStr);
					JSONObject response = new JSONObject();
					JSONArray data = new JSONArray();
					response.put("success", true);
					response.put("data", data);
					String superclassId = null;
					results = results.getJSONObject("results");
					JSONArray bindings = results.getJSONArray(BINDINGS);
					for(int i=0;i<bindings.length();i++) {
						JSONObject binding = bindings.getJSONObject(i);
						String subclassId = binding.getJSONObject("child").getString("value");
						String subclassLabel = binding.getJSONObject("label").getString("value");

						try {
							superclassId = binding.getJSONObject("parent").getString("value");
						}
						catch(Exception e) { }
						//if(labelStr == null) {
						//	labelStr = sourceUri.substring(sourceUri.lastIndexOf('/')+1).replace('-', '.');
						//}
						JSONObject mapping = new JSONObject();
						mapping.put("id", subclassId);
						mapping.put("label", subclassLabel);
						mapping.put("parent", superclassId);
						data.put(mapping);
					}
					responseStr = response.toString();
				} catch (JSONException e) {
					log.error("Unable to parse JSON results", e);
				}
				return responseStr;			
	}
	
	/**
	 * This function is now deprecated once dynamic jstree browsing was enabled.
	 * @deprecated
	 * @param request
	 * @return
	 * @throws IOException
	 * @throws JSONException
	 */
	@QueryMethod
	public String queryeBirdTaxonomy(Request request) throws IOException, JSONException{	
		final Query query = config.getQueryFactory().newQuery(Type.SELECT);	
		//Variables
		final Variable id = query.getVariable(VAR_NS+ "child");
		final Variable label = query.getVariable(VAR_NS+ "label");
		final Variable parent = query.getVariable(VAR_NS+ "parent");		
		//URIs
		final QueryResource subClassOf = query.getResource(RDFS_NS + "subClassOf");
		final QueryResource hasLabel = query.getResource(RDFS_NS + "label");
		//build query
		Set<Variable> vars = new LinkedHashSet<Variable>();
		vars.add(id);
		vars.add(label);
		vars.add(parent);
		query.setVariables(vars);
		//query.addPattern(site, inDataSet, dataSet);
				final NamedGraphComponent graph = query.getNamedGraph("http://was.tw.rpi.edu/ebird-taxonomy");
				graph.addPattern(id, subClassOf, parent);
				graph.addPattern(id, hasLabel, label);
				String responseStr = FAILURE;
				
				String resultStr = config.getQueryExecutor(request).accept("application/json").execute(query);
				////return config.getQueryExecutor(request).accept("application/json").execute(query);
				log.debug("Results: "+resultStr);
				if(resultStr == null) {
					return responseStr;
				}
				try {
					JSONObject results = new JSONObject(resultStr);
					JSONObject response = new JSONObject();
					JSONArray data = new JSONArray();
					response.put("success", true);
					response.put("data", data);
					String superclassId = null;
					results = results.getJSONObject("results");
					JSONArray bindings = results.getJSONArray(BINDINGS);
					for(int i=0;i<bindings.length();i++) {
						JSONObject binding = bindings.getJSONObject(i);
						String subclassId = binding.getJSONObject("child").getString("value");
						String subclassLabel = binding.getJSONObject("label").getString("value");

						try {
							superclassId = binding.getJSONObject("parent").getString("value");
						}
						catch(Exception e) { }
						//if(labelStr == null) {
						//	labelStr = sourceUri.substring(sourceUri.lastIndexOf('/')+1).replace('-', '.');
						//}
						JSONObject mapping = new JSONObject();
						mapping.put("id", subclassId);
						mapping.put("label", subclassLabel);
						mapping.put("parent", superclassId);
						data.put(mapping);
					}
					responseStr = response.toString();
				} catch (JSONException e) {
					log.error("Unable to parse JSON results", e);
				}
				return responseStr;			
	}
	
	@Override
	public String getName() {
		return "Species";
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

	@Override
	public List<Domain> getDomains(final Request request) {
		List<Domain> domains = new ArrayList<Domain>();
		Domain bird = config.getDomain(URI.create("http://escience.rpi.edu/ontology/semanteco/2/0/bird.owl#"), true);
		bird.setLabel("Bird");
		addDataSourcesBird(bird, request);
		addRegulations(bird);
		addDataTypesBirds(bird);
		domains.add(bird);
		
		Domain fish = config.getDomain(URI.create("http://escience.rpi.edu/ontology/semanteco/2/0/fish.owl#"), true);
		fish.setLabel("Fish");
		addDataSourcesFish(fish, request);
		addRegulations(fish);
		addDataTypesFish(fish);
		domains.add(fish);
		
		
		return domains;
	}
	
	protected void addRegulations(final Domain domain) {
		
	}
	/**
	 * This method adds a bird icon as a UI resource, and adds data type "bird" and icon label "Bird Species" for UI.
	 * @param domain
	 */
	protected void addDataTypesBirds(final Domain domain) {
		Resource res = config.getResource("ebird.png");
		domain.addDataType("birds", "Bird Species", res);
		//bird is the identifier of the checkbox, and Bird Species is the label for the icon type
	}
	
	/**
	 * This method adds a fish icon as a UI resource, and adds datatype "fish" and icon label "Fish Species" for UI.
	 * @param domain
	 */
	protected void addDataTypesFish(final Domain domain) {
		Resource res = config.getResource("fish.png");
		domain.addDataType("fish", "Fish Species", res);
		//the first string is used in speciesHierarchy.js where icon variable is set.
	}
	/**
	 * This method adds the uri and label for the Bird data source, here "http://ebird#" and "eBird", resp.
	 * @param domain
	 * @param request
	 */
	protected void addDataSourcesBird(final Domain domain, final Request request) {
		domain.addSource(URI.create("http://ebird#"), "eBird");
		//this is what is put into the bbq state for sources
	}
	
	protected void addDataSourcesFish(final Domain domain, final Request request) {
		domain.addSource(URI.create("http://sbcFish#"), "SBC Fish");
	}

	@HierarchicalMethod(parameter = "species")
	public Collection<HierarchyEntry> queryeBirdTaxonomyHM(final Request request, final HierarchyVerb action) {
		List<HierarchyEntry> items = new ArrayList<HierarchyEntry>();
		if(action == HierarchyVerb.ROOTS) {
//			HierarchyEntry entry = new HierarchyEntry();
//			entry.setUri(URI.create("http://example.com/bird1"));
//			entry.setLabel("bird1");
//			entry.setAltLabel("birdicus uno");
//			items.add(entry);
//			entry = new HierarchyEntry();
//			entry.setUri(URI.create("http://example.com/bird2"));
//			entry.setLabel("bird2");
//			entry.setAltLabel("birdicus dos");
//			items.add(entry);
			return queryeBirdTaxonomyHMRoots(request);
		} else if ( action == HierarchyVerb.CHILDREN ) {
//			if ( request.getParam("species").equals("http://example.com/bird1") ) {
//				HierarchyEntry entry = new HierarchyEntry();
//				entry.setUri(URI.create("http://example.com/bird3"));
//				entry.setLabel("bird3");
//				entry.setAltLabel("birdicus tres");
//				items.add(entry);
//				entry = new HierarchyEntry();
//				entry.setUri(URI.create("http://example.com/bird4"));
//				entry.setLabel("bird4");
//				entry.setAltLabel("birdicus quatro");
//				items.add(entry);
//			}
			return queryeBirdTaxonomyHMChildren(request, (String) request.getParam("species"));
		} else if ( action == HierarchyVerb.SEARCH ) {
			return searcheBird( request, (String) request.getParam("string") );
		} else if ( action == HierarchyVerb.PATH_TO_NODE ) {
			return eBirdPathToNode( request, (String) request.getParam("node") );
		}
		return items;
	}

	protected Collection<HierarchyEntry> queryeBirdTaxonomyHMRoots(final Request request) {
		final Query query = config.getQueryFactory().newQuery(Type.SELECT);
		//Variables
		final Variable id = query.getVariable(VAR_NS+ "child");
		final Variable label = query.getVariable(VAR_NS+ "label");
		final Variable parent = query.getVariable(VAR_NS+ "parent");
		//URIs
		final QueryResource subClassOf = query.getResource(RDFS_NS + "subClassOf");
		final QueryResource hasLabel = query.getResource(RDFS_NS + "label");

		final QueryResource birdTaxonomy = query.getResource("http://ebird#birdTaxonomy");
		request.getLogger().info("reached queryeBirdTaxonomyRoots \n");

		//build query
		Set<Variable> vars = new LinkedHashSet<Variable>();
		vars.add(id);
		vars.add(label);
		vars.add(parent);
		query.setVariables(vars);
		query.addOrderBy(label, SortType.ASC);
		final NamedGraphComponent graph = query.getNamedGraph("http://was.tw.rpi.edu/ebird-taxonomy");
		graph.addPattern(id, subClassOf, parent);
		graph.addPattern(id, subClassOf, birdTaxonomy);

		graph.addPattern(id, hasLabel, label);

		//get only the subclasses of the subclasses of OWL thing
		Collection<HierarchyEntry> entries = new ArrayList<HierarchyEntry>();

		String resultStr = config.getQueryExecutor(request).accept("application/json").execute(query);
		if(resultStr == null) {
			return entries;
		}
		try {
			JSONObject results = new JSONObject(resultStr);
			results = results.getJSONObject("results");
			JSONArray bindings = results.getJSONArray(BINDINGS);
			for(int i=0;i<bindings.length();i++) {
				JSONObject binding = bindings.getJSONObject(i);
				String subclassId = binding.getJSONObject("child").getString("value");
				String subclassLabel = binding.getJSONObject("label").getString("value");
				HierarchyEntry entry = new HierarchyEntry();
				entry.setUri(subclassId);
				entry.setLabel(subclassLabel);
				entries.add(entry);
			}
		} catch (JSONException e) {
			log.error("Unable to parse JSON results", e);
		}
		return entries;
	}

	protected Collection<HierarchyEntry> queryeBirdTaxonomyHMChildren(final Request request, final String species) {
		final Query query = config.getQueryFactory().newQuery(Type.SELECT);
		//Variables
		final Variable id = query.getVariable(VAR_NS+ "child");
		final Variable label = query.getVariable(VAR_NS+ "label");
		final Variable parent = query.getVariable(VAR_NS+ "parent");
		//URIs
		final QueryResource subClassOf = query.getResource(RDFS_NS + "subClassOf");
		final QueryResource hasCommonName = query.getResource("http://lod.taxonconcept.org/ontology/txn.owl#CommonNameID");

		//final QueryResource birdTaxonomy = query.getResource("http://ebird#birdTaxonomy");
		final QueryResource classRequiresSubclasses = query.getResource(species);

		//build query
		Set<Variable> vars = new LinkedHashSet<Variable>();
		vars.add(id);
		vars.add(label);
		vars.add(parent);
		query.setVariables(vars);
		query.addOrderBy(label, SortType.ASC);
		final NamedGraphComponent graph = query.getNamedGraph("http://was.tw.rpi.edu/ebird-taxonomy");
		graph.addPattern(id, subClassOf, parent);
		graph.addPattern(id, subClassOf, classRequiresSubclasses);

		graph.addPattern(id,hasCommonName, label);
		//get only the subclasses of the subclasses of OWL thing
		Collection<HierarchyEntry> entries = new ArrayList<HierarchyEntry>();

		String resultStr = config.getQueryExecutor(request).accept("application/json").execute(query);
		if(resultStr == null) {
			return entries;
		}
		try {
			JSONObject results = new JSONObject(resultStr);
			results = results.getJSONObject("results");
			JSONArray bindings = results.getJSONArray(BINDINGS);
			for(int i=0;i<bindings.length();i++) {
				JSONObject binding = bindings.getJSONObject(i);
				HierarchyEntry entry = new HierarchyEntry();
				entry.setUri(binding.getJSONObject("child").getString("value"));
				entry.setLabel(binding.getJSONObject("label").getString("value"));

				try {
					entry.setParent(URI.create(binding.getJSONObject("parent").getString("value")));
				}
				catch(Exception e) { }

				entries.add(entry);
			}
		} catch (JSONException e) {
			log.error("Unable to parse JSON results", e);
		}
		return entries;
	}

	protected Collection<HierarchyEntry> searcheBird(final Request request, final String str) {
		final Collection<HierarchyEntry> entries = new ArrayList<HierarchyEntry>();
		final Query query = config.getQueryFactory().newQuery(Type.SELECT);
		final Variable uri = query.createVariable(VAR_NS+"uri");
		final Variable label = query.createVariable(VAR_NS+"label");
		final QueryResource hasCommonName = query.getResource(TXN_NS+"CommonNameID");
		final QueryResource hasScientificName = query.getResource(e2_NS+ "scientific_name");
		final NamedGraphComponent graph = query.getNamedGraph("http://was.tw.rpi.edu/ebird-taxonomy");
		final UnionComponent union = query.createUnion();

		graph.addGraphComponent(union);
		union.getUnionComponent(0).addPattern(uri, hasScientificName, label);
		union.getUnionComponent(0).addFilter("bif:contains(?label,\"'"+str+"*'\")");
		union.getUnionComponent(1).addPattern(uri, hasCommonName, label);
		union.getUnionComponent(1).addFilter("bif:contains(?label,\"'"+str+"*'\")");
		query.addOrderBy(label, SortType.ASC);

		try {
			final JSONObject results = new JSONObject(config.getQueryExecutor(request).accept("application/json").execute(query));
			final JSONArray bindings = results.getJSONObject("results").getJSONArray("bindings");
			for(int i=0; i<bindings.length(); i++) {
				final JSONObject binding = bindings.getJSONObject(i);
				final HierarchyEntry entry = new HierarchyEntry();
				entry.setUri(binding.getJSONObject("uri").getString("value"));
				entry.setLabel(binding.getJSONObject("label").getString("value"));
				entries.add(entry);
			}
		} catch (JSONException e) {
			request.getLogger().warn("Unable to parse data from remote server.");
		}
		return entries;
	}

	protected Collection<HierarchyEntry> eBirdPathToNode(final Request request, final String node) {
		final Collection<HierarchyEntry> entries = new ArrayList<HierarchyEntry>();
		final Query query = config.getQueryFactory().newQuery(Type.SELECT);
		final Variable uri = query.createVariable(VAR_NS+"uri");
		final Variable label = query.createVariable(VAR_NS+"label");
		final Variable altLabel = query.createVariable(VAR_NS+"altLabel");
		final Variable parent = query.createVariable(VAR_NS+"parent");
		final QueryResource nodeRes = query.getResource(node);
		final QueryResource rdfsSubClassOf = query.getResource(RDFS_NS+"subClassOf");
		final QueryResource skosPrefLabel = query.getResource(SKOS_NS+"prefLabel");
		final QueryResource rdfsLabel = query.getResource(RDFS_NS+"label");
		final NamedGraphComponent graph = query.getNamedGraph("http://was.tw.rpi.edu/ebird-taxonomy");
		final OptionalComponent optional = query.createOptional();

		graph.addPattern(nodeRes, rdfsSubClassOf, parent, true);
		graph.addPattern(uri, rdfsSubClassOf, parent);
		graph.addPattern(uri, rdfsLabel, label);
		graph.addGraphComponent(optional);
		optional.addPattern(uri, skosPrefLabel, altLabel);
		query.addOrderBy(label, SortType.ASC);

		try {
			final JSONObject results = new JSONObject(config.getQueryExecutor(request).accept("application/json").execute(query));
			final JSONArray bindings = results.getJSONObject("results").getJSONArray("bindings");
			for(int i=0; i<bindings.length(); i++) {
				final JSONObject binding = bindings.getJSONObject(i);
				final HierarchyEntry entry = new HierarchyEntry();
				entry.setUri(binding.getJSONObject("uri").getString("value"));
				entry.setLabel(binding.getJSONObject("label").getString("value"));
				if(binding.has("altLabel")) {
					entry.setAltLabel(binding.getJSONObject("altLabel").getString("value"));
				}
				entry.setParent(URI.create(binding.getJSONObject("parent").getString("value")));
				entries.add(entry);
			}
		} catch(JSONException e) {
			request.getLogger().warn("Unable to parse data from remote server.");
		}
		return entries;
	}

}
