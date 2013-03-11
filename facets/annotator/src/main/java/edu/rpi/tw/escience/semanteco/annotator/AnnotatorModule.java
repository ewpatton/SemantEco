package edu.rpi.tw.escience.semanteco.annotator;
import static edu.rpi.tw.escience.semanteco.query.Query.VAR_NS;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mindswap.pellet.jena.PelletReasonerFactory;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.OWL;

import edu.rpi.tw.escience.semanteco.Module;
import edu.rpi.tw.escience.semanteco.ModuleConfiguration;
import edu.rpi.tw.escience.semanteco.QueryMethod;
import edu.rpi.tw.escience.semanteco.Request;
import edu.rpi.tw.escience.semanteco.SemantEcoUI;
import edu.rpi.tw.escience.semanteco.query.GraphComponentCollection;
import edu.rpi.tw.escience.semanteco.query.Query;
import edu.rpi.tw.escience.semanteco.query.QueryResource;
import edu.rpi.tw.escience.semanteco.query.Variable;
import edu.rpi.tw.escience.semanteco.query.Query.Type;

public class AnnotatorModule implements Module {

	private ModuleConfiguration config = null;
	private static final String RDFS_NS = "http://www.w3.org/2000/01/rdf-schema#";
	private static final String BINDINGS = "bindings";
	private static final String FAILURE = "{\"success\":false}";
	private static OntModel model = null;

	public void setModel(OntModel model){
		this.model = model;
	}
	public OntModel getModel(){
		return this.model;
	}

	//a set of key/value pairs (id/label pairs)
	//			Hashtable<String, String> table = new Hashtable<String, String>();
	
	public String getShortName(String inName)
	{
		int pAt = inName.indexOf("#");
		return (inName.substring(pAt+1));
	}

	public String jsonWrapper(Hashtable<String, String> table, String parent) throws JSONException{
		JSONArray data = new JSONArray();
		JSONObject response = new JSONObject();
		response.put("success", true);
		response.put("data", data);
		String str;
		 Set<String> set = table.keySet();
		    Iterator<String> itr = set.iterator();
		    while (itr.hasNext()) {
		      str = itr.next();
		      System.out.println(str + ": " + table.get(str));
		      
				JSONObject mapping = new JSONObject();
				mapping.put("id", str);
				//should use "short name" if there is no label.
				
				if(table.get(str) == ""){
					table.put(str, getShortName(str));
				}
				
				mapping.put("label", table.get(str));
				mapping.put("parent", parent);
				data.put(mapping);
		    }
		return response.toString();
	}
	
	//would it be better to have one model and reasoner for this module, instead of per query method? yes.
	//do that through a constructor?
	@QueryMethod
	public String queryForAnnotatorRootClasses(final Request request) throws JSONException{
		
		
		//construct an owlontology and pose sparql queries against it.
		OntModel model = null;
		model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);

		
		//load certain ontologies
		//model.read("http://was.tw.rpi.edu/semanteco/air/air.owl", "TTL");
		FileManager.get().readModel(model, "/Users/apseyed/Documents/rpi/semanteco-products/obo-e-ontologies/oboe-sbclter.owl") ;
		FileManager.get().readModel(model, "/Users/apseyed/Documents/rpi/semanteco-products/obo-e-ontologies/oboe-temporal.owl") ;
		FileManager.get().readModel(model, "/Users/apseyed/Documents/rpi/semanteco-products/obo-e-ontologies/oboe-spatial.owl") ;
		FileManager.get().readModel(model, "/Users/apseyed/Documents/rpi/semanteco-products/obo-e-ontologies/oboe-biology.owl") ;
		FileManager.get().readModel(model, "/Users/apseyed/Documents/rpi/semanteco-products/obo-e-ontologies/oboe-chemistry.owl") ;
		FileManager.get().readModel(model, "/Users/apseyed/Documents/rpi/semanteco-products/obo-e-ontologies/oboe-anatomy.owl") ;
		FileManager.get().readModel(model, "/Users/apseyed/Documents/rpi/semanteco-products/obo-e-ontologies/oboe-characteristics.owl") ;
		FileManager.get().readModel(model, "/Users/apseyed/Documents/rpi/semanteco-products/obo-e-ontologies/oboe-taxa.owl") ;
		FileManager.get().readModel(model, "/Users/apseyed/Documents/rpi/semanteco-products/obo-e-ontologies/oboe-standards.owl") ;
		FileManager.get().readModel(model, "/Users/apseyed/Documents/rpi/semanteco-products/obo-e-ontologies/oboe-core.owl") ;
		setModel(model);
		//model.
		//InputStream is = new BufferedInputStream(new FileInputStream("blah.turtle"));
		
		
		//apply sparql queries against it
		//final Query query = config.getQueryFactory().newQuery(Type.CONSTRUCT);
		//final GraphComponentCollection construct = query.getConstructComponent();
		final Query query = config.getQueryFactory().newQuery(Type.SELECT);
		final Variable id = query.getVariable(VAR_NS+ "child");
		final Variable label = query.getVariable(VAR_NS+ "label");
		final Variable parent = query.getVariable(VAR_NS+ "parent");		

		final QueryResource PollutedThing = query.getResource("http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#PollutedThing");
		final QueryResource Measurement = query.getResource("http://ecoinformatics.org/oboe/oboe.1.0/oboe-core.owl#Measurement");
		final QueryResource Thing = query.getResource("http://www.w3.org/2002/07/owl#Thing");
		//http://ecoinformatics.org/oboe/oboe.1.0/oboe-core.owl#Entity
		final QueryResource Entity = query.getResource("http://ecoinformatics.org/oboe/oboe.1.0/oboe-core.owl#Entity");

		final QueryResource subClassOf = query.getResource(RDFS_NS+"subClassOf");
		final Variable site = query.getVariable(VAR_NS+"site");
		final QueryResource hasLabel = query.getResource(RDFS_NS + "label");

		Set<Variable> vars = new LinkedHashSet<Variable>();
		vars.add(id);
		vars.add(parent);
		vars.add(label);

		query.setVariables(vars);
		//query.addPattern(site, subClassOf, PollutedThing);
		//query.addPattern(site, subClassOf, Measurement);
		query.addPattern(id, subClassOf, parent);
		query.addPattern(id, subClassOf, Entity);
		query.addPattern(id, hasLabel, label);

		//construct.addPattern(site, subClassOf, PollutedThing);

		//return executeLocalQuery(query, model);
		String responseStr = FAILURE;
		//String resultStr = config.getQueryExecutor(request).accept("application/json").executeLocalQuery(query, model);
		
		Set master = new HashSet();		//model.
		Set<OntClass> classes = new HashSet<OntClass>();		//model.
		Set<String> labels = new HashSet<String>();		//model.

		OntClass thing = model.getOntClass( OWL.Thing.getURI() );
		//OntClass entity = model.getOntClass( "http://ecoinformatics.org/oboe/oboe.1.0/oboe-core.owl#Entity" );

		Hashtable<String, String> table = new Hashtable<String, String>();
		
		for (Iterator<OntClass> i = thing.listSubClasses(true); i.hasNext(); ) { //true here is for direct
    	OntClass hierarchyRoot = i.next();
    	
    	    //classes.add( hierarchyRoot);
		    //labels.add( hierarchyRoot.getLabel(null));
    	System.out.println("root: " + hierarchyRoot.toString());
    	System.out.println("label: " + hierarchyRoot.getLabel(null));
    	
    	if(hierarchyRoot.getLabel(null) == "" || hierarchyRoot.getLabel(null) == null){
    		table.put(hierarchyRoot.toString(), getShortName(hierarchyRoot.toString()));
		}
    	else{
		 table.put(hierarchyRoot.toString(), hierarchyRoot.getLabel(null));
    	}
        
		}	

		

		/*
		 * 
		for (Iterator<OntClass> i = model.listHierarchyRootClasses(); i.hasNext(); ) {
		    OntClass hierarchyRoot = i.next();
		    classes.add( hierarchyRoot);
		    labels.add( hierarchyRoot.getLabel(null));
		}
		 */
		master.add(classes);
		master.add(labels);
		
		return jsonWrapper(table, OWL.Thing.getURI().toString());
		
		//return master.toString();
		//return config.getQueryExecutor(request).accept("application/json").executeLocalQuery(query, model);

	/*	if(resultStr == null) {
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
			//log.error("Unable to parse JSON results", e);
		}
		return responseStr;		
		*/
	}
	
@QueryMethod
public String queryForAnnotatorSubClasses(final Request request) throws JSONException{
		
	String classRequiresSubclassesString = (String) request.getParam("SubClass");	
	if(classRequiresSubclassesString == null){
		return null;
	}
	
	OntModel model = null;
	model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);

	
	//load certain ontologies
	//model.read("http://was.tw.rpi.edu/semanteco/air/air.owl", "TTL");
	FileManager.get().readModel(model, "/Users/apseyed/Documents/rpi/semanteco-products/obo-e-ontologies/oboe-sbclter.owl") ;
	FileManager.get().readModel(model, "/Users/apseyed/Documents/rpi/semanteco-products/obo-e-ontologies/oboe-temporal.owl") ;
	FileManager.get().readModel(model, "/Users/apseyed/Documents/rpi/semanteco-products/obo-e-ontologies/oboe-spatial.owl") ;
	FileManager.get().readModel(model, "/Users/apseyed/Documents/rpi/semanteco-products/obo-e-ontologies/oboe-biology.owl") ;
	FileManager.get().readModel(model, "/Users/apseyed/Documents/rpi/semanteco-products/obo-e-ontologies/oboe-chemistry.owl") ;
	FileManager.get().readModel(model, "/Users/apseyed/Documents/rpi/semanteco-products/obo-e-ontologies/oboe-anatomy.owl") ;
	FileManager.get().readModel(model, "/Users/apseyed/Documents/rpi/semanteco-products/obo-e-ontologies/oboe-characteristics.owl") ;
	FileManager.get().readModel(model, "/Users/apseyed/Documents/rpi/semanteco-products/obo-e-ontologies/oboe-taxa.owl") ;
	FileManager.get().readModel(model, "/Users/apseyed/Documents/rpi/semanteco-products/obo-e-ontologies/oboe-standards.owl") ;
	FileManager.get().readModel(model, "/Users/apseyed/Documents/rpi/semanteco-products/obo-e-ontologies/oboe-core.owl") ;
		//construct an owlontology and pose sparql queries against it.
	///	OntModel model = getModel();

		
		//apply sparql queries against it
		//final Query query = config.getQueryFactory().newQuery(Type.CONSTRUCT);
		//final GraphComponentCollection construct = query.getConstructComponent();
		final Query query = config.getQueryFactory().newQuery(Type.SELECT);
		final Variable id = query.getVariable(VAR_NS+ "child");
		final Variable label = query.getVariable(VAR_NS+ "label");
		final Variable parent = query.getVariable(VAR_NS+ "parent");		

		final QueryResource PollutedThing = query.getResource("http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#PollutedThing");
		final QueryResource Measurement = query.getResource("http://ecoinformatics.org/oboe/oboe.1.0/oboe-core.owl#Measurement");
		final QueryResource Thing = query.getResource("http://www.w3.org/2002/07/owl#Thing");
		//http://ecoinformatics.org/oboe/oboe.1.0/oboe-core.owl#Entity
		final QueryResource Entity = query.getResource("http://ecoinformatics.org/oboe/oboe.1.0/oboe-core.owl#Entity");

		final QueryResource subClassOf = query.getResource(RDFS_NS+"subClassOf");
		final Variable site = query.getVariable(VAR_NS+"site");
		final QueryResource hasLabel = query.getResource(RDFS_NS + "label");

		Set<Variable> vars = new LinkedHashSet<Variable>();
		vars.add(id);
		vars.add(parent);
		vars.add(label);

		query.setVariables(vars);
		//query.addPattern(site, subClassOf, PollutedThing);
		//query.addPattern(site, subClassOf, Measurement);
		query.addPattern(id, subClassOf, parent);
		query.addPattern(id, subClassOf, Entity);
		query.addPattern(id, hasLabel, label);

		//construct.addPattern(site, subClassOf, PollutedThing);

		//return executeLocalQuery(query, model);
		String responseStr = FAILURE;
		//String resultStr = config.getQueryExecutor(request).accept("application/json").executeLocalQuery(query, model);
		
		Set master = new HashSet();		//model.
		Set<OntClass> classes = new HashSet<OntClass>();		//model.
		Set<String> labels = new HashSet<String>();		//model.

		//OntClass thing = model.getOntClass( OWL.Thing.getURI() );
		//OntClass subclass = model.getOntClass( classRequiresSubclassesString );
		
		OntClass subclass = model.getOntClass( classRequiresSubclassesString );

		Hashtable<String, String> table = new Hashtable<String, String>();

		for (Iterator<OntClass> i = subclass.listSubClasses(true); i.hasNext(); ) { //true here is for direct
    	OntClass hierarchyRoot = i.next();
    	
    	
    	if(hierarchyRoot.getLabel(null) == "" || hierarchyRoot.getLabel(null) == null){
    		table.put(hierarchyRoot.toString(), getShortName(hierarchyRoot.toString()));
		}
    	else{
		 table.put(hierarchyRoot.toString(), hierarchyRoot.getLabel(null));
    	}
    	    //classes.add( hierarchyRoot);
		    //labels.add( hierarchyRoot.getLabel(null));
        
		}	

		

		/*
		 * 
		for (Iterator<OntClass> i = model.listHierarchyRootClasses(); i.hasNext(); ) {
		    OntClass hierarchyRoot = i.next();
		    classes.add( hierarchyRoot);
		    labels.add( hierarchyRoot.getLabel(null));
		}
		 */
		master.add(classes);
		master.add(labels);
		
		return jsonWrapper(table, classRequiresSubclassesString);

		//return master.toString();
		//return config.getQueryExecutor(request).accept("application/json").executeLocalQuery(query, model);

	/*	if(resultStr == null) {
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
			//log.error("Unable to parse JSON results", e);
		}
		return responseStr;		
		*/
	}
	
	
	@QueryMethod
	public String queryForAnnotatorRootObjectProperties(Request request) throws JSONException{
		//construct an owlontology and pose sparql queries against it.
				OntModel model = null;
				model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
				

				
				//load certain ontologies
				//model.read("http://was.tw.rpi.edu/semanteco/air/air.owl", "TTL");
				FileManager.get().readModel(model, "/Users/apseyed/Documents/rpi/semanteco-products/obo-e-ontologies/oboe-sbclter.owl") ;
				FileManager.get().readModel(model, "/Users/apseyed/Documents/rpi/semanteco-products/obo-e-ontologies/oboe-temporal.owl") ;
				FileManager.get().readModel(model, "/Users/apseyed/Documents/rpi/semanteco-products/obo-e-ontologies/oboe-spatial.owl") ;
				FileManager.get().readModel(model, "/Users/apseyed/Documents/rpi/semanteco-products/obo-e-ontologies/oboe-biology.owl") ;
				FileManager.get().readModel(model, "/Users/apseyed/Documents/rpi/semanteco-products/obo-e-ontologies/oboe-chemistry.owl") ;
				FileManager.get().readModel(model, "/Users/apseyed/Documents/rpi/semanteco-products/obo-e-ontologies/oboe-anatomy.owl") ;
				FileManager.get().readModel(model, "/Users/apseyed/Documents/rpi/semanteco-products/obo-e-ontologies/oboe-characteristics.owl") ;
				FileManager.get().readModel(model, "/Users/apseyed/Documents/rpi/semanteco-products/obo-e-ontologies/oboe-taxa.owl") ;
				FileManager.get().readModel(model, "/Users/apseyed/Documents/rpi/semanteco-products/obo-e-ontologies/oboe-standards.owl") ;
				FileManager.get().readModel(model, "/Users/apseyed/Documents/rpi/semanteco-products/obo-e-ontologies/oboe-core.owl") ;
				//InputStream is = new BufferedInputStream(new FileInputStream("blah.turtle"));
				
				
				//apply sparql queries against it
				//final Query query = config.getQueryFactory().newQuery(Type.CONSTRUCT);
				//final GraphComponentCollection construct = query.getConstructComponent();
				final Query query = config.getQueryFactory().newQuery(Type.SELECT);

				
				final QueryResource topObjectProperty = query.getResource("http://www.w3.org/2002/07/owl#topObjectProperty");
				final QueryResource subPropertyOf = query.getResource(RDFS_NS+"subPropertyOf");
				final Variable site = query.getVariable(VAR_NS+"site");
				Set<Variable> vars = new LinkedHashSet<Variable>();
				vars.add(site);
				query.setVariables(vars);
				//query.addPattern(site, subClassOf, PollutedThing);
				//query.addPattern(site, subClassOf, Measurement);
				query.addPattern(site, subPropertyOf, topObjectProperty);

				//construct.addPattern(site, subClassOf, PollutedThing);

				//return executeLocalQuery(query, model);
				
				Set master = new HashSet();		//model.
				Set<OntProperty> props = new HashSet<OntProperty>();		//model.
				Set<String> labels = new HashSet<String>();		//model.

				//OntClass thing = model.getOntClass( OWL.Thing.getURI() );
				//OntClass entity = model.getOntClass( "http://ecoinformatics.org/oboe/oboe.1.0/oboe-core.owl#Entity" );
				OntProperty topObjProp = model.getOntProperty("http://www.w3.org/2002/07/owl#topObjectProperty");

				Hashtable<String, String> table = new Hashtable<String, String>();

				for (Iterator<? extends OntProperty> i = topObjProp.listSubProperties(true); i.hasNext(); ) { //true here is for direct
		    	OntProperty hierarchyRoot = i.next();
		    	
		    	   // props.add( hierarchyRoot);
				    //labels.add( hierarchyRoot.getLabel(null));
		    	if(hierarchyRoot.getLabel(null) == "" || hierarchyRoot.getLabel(null) == null){
		    		table.put(hierarchyRoot.toString(), getShortName(hierarchyRoot.toString()));
				}
		    	else{
				 table.put(hierarchyRoot.toString(), hierarchyRoot.getLabel(null));
		    	}
		        
				}	

				

				/*
				 * 
				for (Iterator<OntClass> i = model.listHierarchyRootClasses(); i.hasNext(); ) {
				    OntClass hierarchyRoot = i.next();
				    classes.add( hierarchyRoot);
				    labels.add( hierarchyRoot.getLabel(null));
				}
				 */
				master.add(props);
				master.add(labels);
				return jsonWrapper(table, topObjProp.toString());
				//return master.toString();
				
				
				
				//return config.getQueryExecutor(request).accept("application/json").executeLocalQuery(query, model);
	}
	
	
	@QueryMethod
	public String queryForAnnotatorSubObjectProperties(Request request) throws JSONException{
		
		String classRequiresSubpropertyString = (String) request.getParam("SubProperty");	
		if(classRequiresSubpropertyString == null){
			return null;
		}
		//construct an owlontology and pose sparql queries against it.
				OntModel model = null;
				model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);

				
				//load certain ontologies
				//model.read("http://was.tw.rpi.edu/semanteco/air/air.owl", "TTL");
				FileManager.get().readModel(model, "/Users/apseyed/Documents/rpi/semanteco-products/obo-e-ontologies/oboe-sbclter.owl") ;
				FileManager.get().readModel(model, "/Users/apseyed/Documents/rpi/semanteco-products/obo-e-ontologies/oboe-temporal.owl") ;
				FileManager.get().readModel(model, "/Users/apseyed/Documents/rpi/semanteco-products/obo-e-ontologies/oboe-spatial.owl") ;
				FileManager.get().readModel(model, "/Users/apseyed/Documents/rpi/semanteco-products/obo-e-ontologies/oboe-biology.owl") ;
				FileManager.get().readModel(model, "/Users/apseyed/Documents/rpi/semanteco-products/obo-e-ontologies/oboe-chemistry.owl") ;
				FileManager.get().readModel(model, "/Users/apseyed/Documents/rpi/semanteco-products/obo-e-ontologies/oboe-anatomy.owl") ;
				FileManager.get().readModel(model, "/Users/apseyed/Documents/rpi/semanteco-products/obo-e-ontologies/oboe-characteristics.owl") ;
				FileManager.get().readModel(model, "/Users/apseyed/Documents/rpi/semanteco-products/obo-e-ontologies/oboe-taxa.owl") ;
				FileManager.get().readModel(model, "/Users/apseyed/Documents/rpi/semanteco-products/obo-e-ontologies/oboe-standards.owl") ;
				FileManager.get().readModel(model, "/Users/apseyed/Documents/rpi/semanteco-products/obo-e-ontologies/oboe-core.owl") ;
				//InputStream is = new BufferedInputStream(new FileInputStream("blah.turtle"));
				
				
				//apply sparql queries against it
				//final Query query = config.getQueryFactory().newQuery(Type.CONSTRUCT);
				//final GraphComponentCollection construct = query.getConstructComponent();
				final Query query = config.getQueryFactory().newQuery(Type.SELECT);

				
				final QueryResource topObjectProperty = query.getResource("http://www.w3.org/2002/07/owl#topObjectProperty");
				final QueryResource subPropertyOf = query.getResource(RDFS_NS+"subPropertyOf");
				final Variable site = query.getVariable(VAR_NS+"site");
				Set<Variable> vars = new LinkedHashSet<Variable>();
				vars.add(site);
				query.setVariables(vars);
				//query.addPattern(site, subClassOf, PollutedThing);
				//query.addPattern(site, subClassOf, Measurement);
				query.addPattern(site, subPropertyOf, topObjectProperty);

				//construct.addPattern(site, subClassOf, PollutedThing);

				//return executeLocalQuery(query, model);
				
				Set master = new HashSet();		//model.
				Set<OntProperty> props = new HashSet<OntProperty>();		//model.
				Set<String> labels = new HashSet<String>();		//model.

				//OntClass thing = model.getOntClass( OWL.Thing.getURI() );
				//OntClass entity = model.getOntClass( "http://ecoinformatics.org/oboe/oboe.1.0/oboe-core.owl#Entity" );
				OntProperty topObjProp = model.getOntProperty("http://www.w3.org/2002/07/owl#topObjectProperty");
				OntProperty subProp = model.getOntProperty( classRequiresSubpropertyString );

				Hashtable<String, String> table = new Hashtable<String, String>();

				for (Iterator<? extends OntProperty> i = subProp.listSubProperties(true); i.hasNext(); ) { //true here is for direct
		    	OntProperty hierarchyRoot = i.next();
		    	
		    	   // props.add( hierarchyRoot);
				  //  labels.add( hierarchyRoot.getLabel(null));
		    	if(hierarchyRoot.getLabel(null) == "" || hierarchyRoot.getLabel(null) == null){
		    		table.put(hierarchyRoot.toString(), getShortName(hierarchyRoot.toString()));
				}
		    	else{
				 table.put(hierarchyRoot.toString(), hierarchyRoot.getLabel(null));
		    	}
		        
				}	

				

				/*
				 * 
				for (Iterator<OntClass> i = model.listHierarchyRootClasses(); i.hasNext(); ) {
				    OntClass hierarchyRoot = i.next();
				    classes.add( hierarchyRoot);
				    labels.add( hierarchyRoot.getLabel(null));
				}
				 */
				master.add(props);
				master.add(labels);
				return jsonWrapper(table, classRequiresSubpropertyString);

				//return master.toString();
				
				
				
				//return config.getQueryExecutor(request).accept("application/json").executeLocalQuery(query, model);
	}
	
	
	
	public String executeLocalQuery(Query query, Model model) {
		
		if(System.getProperty("edu.rpi.tw.escience.writemodel", "false").equals("true")) {
			try {
				FileOutputStream fos = new FileOutputStream(System.getProperty("java.io.tmpdir")+"/model.rdf");
				model.write(fos);
				fos.close();
			}
			catch(Exception e) {
				// do nothing
			}
		}
		
		Model resultModel = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		long start = System.currentTimeMillis();
		QueryExecution qe = QueryExecutionFactory.create(query.toString(), model);
		try {
			switch(query.getType()) {
			case SELECT:
				ResultSet results = qe.execSelect();
				ResultSetFormatter.outputAsJSON(baos, results);
				//log.debug("Local query took "+(System.currentTimeMillis()-start)+" ms");
				return baos.toString("UTF-8");
			case DESCRIBE:
				resultModel = qe.execDescribe();
				resultModel.write(baos);
				//log.debug("Local query took "+(System.currentTimeMillis()-start)+" ms");
				return baos.toString("UTF-8");
			case CONSTRUCT:
				resultModel = qe.execConstruct();
				resultModel.write(baos);
				//log.debug("Local query took "+(System.currentTimeMillis()-start)+" ms");
				return baos.toString("UTF-8");
			case ASK:
				if(qe.execAsk()) {
					//log.debug("Local query took "+(System.currentTimeMillis()-start)+" ms");
					return "{\"result\":true}";
				}
				else {
				//	log.debug("Local query took "+(System.currentTimeMillis()-start)+" ms");
					return "{\"result\":false}";
				}
			}
		}
		catch(Exception e) {
		//	log.warn("Unable to execute query due to exception", e);
		}
		return null;
	}
	
	
	@Override
	public void visit(final Model model, final Request request) {
		// TODO populate data model
	}

	@Override
	public void visit(final OntModel model, final Request request) {
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
		return "Annotator";
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
