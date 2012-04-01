package edu.rpi.tw.eScience.WaterQualityPortal.WebService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.google.common.io.CharStreams;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import edu.rpi.tw.eScience.WaterQualityPortal.model.CountInstanceQuery;
import edu.rpi.tw.eScience.WaterQualityPortal.model.EPAHack;
import edu.rpi.tw.eScience.WaterQualityPortal.model.ListCharacteristicsQuery;
import edu.rpi.tw.eScience.WaterQualityPortal.model.LoadDataQuery;
import edu.rpi.tw.eScience.WaterQualityPortal.model.Query;
import edu.rpi.tw.eScience.WaterQualityPortal.model.Query.FacilityDataQuery;
import edu.rpi.tw.eScience.WaterQualityPortal.model.Query.WaterDataQuery;
import edu.rpi.tw.eScience.WaterQualityPortal.species.DistributionWebService;
import edu.rpi.tw.eScience.WaterQualityPortal.species.WaterEntityAgent;
import edu.rpi.tw.eScience.WaterQualityPortal.zip.GeonameIdLookup;
import edu.rpi.tw.eScience.WaterQualityPortal.zip.ZipCodeLookup;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.PelletOptions.MonitorType;
import org.mindswap.pellet.jena.PelletReasonerFactory;

public class WaterAgentInstance implements HttpHandler {
	
	List<String> loadedFiles = new ArrayList<String>();
	CachedDataModel owlModel;
	Model pmlModel;
	Model theModel;
	static Cache cache;
	static HashMap<String, String> states = new HashMap<String, String>();
	static Logger log = Logger.getRootLogger();
	
	public static String getStateURI(String state) {
		return states.get(state);
	}
	
	static {
		log.setLevel(Level.INFO);
		//PelletOptions.USE_CLASSIFICATION_MONITOR = MonitorType.NONE;
		PelletOptions.USE_CLASSIFICATION_MONITOR = MonitorType.CONSOLE;
		states.put("AZ", "http://logd.tw.rpi.edu/id/us/state/Arizona");
		states.put("CA", "http://logd.tw.rpi.edu/id/us/state/California");
		states.put("MA", "http://logd.tw.rpi.edu/id/us/state/Massachusetts");
		states.put("NY", "http://logd.tw.rpi.edu/id/us/state/New_York");
		states.put("PA", "http://logd.tw.rpi.edu/id/us/state/Pennsylvania");
		states.put("RI", "http://logd.tw.rpi.edu/id/us/state/Rhode_Island");
		states.put("WA", "http://logd.tw.rpi.edu/id/us/state/Washington");
		/*
		try {
			cache = new MysqlCache(Configuration.CACHEDB_URL,Configuration.CACHEDB_USER,
					Configuration.CACHEDB_PASS,Configuration.CACHEDB_DB,Configuration.CACHEDB_PREFIX);
		}
		catch(Exception e) {
			System.err.println("Error occurred creating cache: ");
			e.printStackTrace();
		}
		*/
	}
	
	public WaterAgentInstance() {
	}
	
	public WaterAgentInstance(ZipCodeLookup zipCode, File basePath) {
		/*
		long start = System.currentTimeMillis();
		pmlModel = ModelFactory.createDefaultModel();
		theModel = ModelFactory.createUnion(owlModel, pmlModel);
		owlModel.read("http://was.tw.rpi.edu/water/rdf/cleanwater.owl");
		String state = zipCode.getStateAbbreviation();
		try {
			owlModel.read("http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl");
			pmlModel.read("http://was.tw.rpi.edu/water/rdf/"+state+"-regulations-pml.rdf");
		}
		catch(Exception e) {
			System.err.println("Unable to find regulations for state "+state);
		}
		System.err.println("Initialized agent instance in "+(System.currentTimeMillis()-start)+" ms");
		 */
	}

	public Map<String,String> parseRequest(HttpExchange arg0) throws IOException
	{
		HashMap<String,String> result = new HashMap<String, String>();
		String query = arg0.getRequestURI().getQuery();
		//parse request
		String [] request=query.split("&");
		
		
		for(int i=0;i<request.length;i++) {
			log.trace(request[i]);
			String[] pieces = request[i].split("=");
			if(pieces.length==2) {
				result.put(pieces[0], java.net.URLDecoder.decode(pieces[1],"UTF-8"));
			}
			//else System.err.println(pieces);
		}
		return result;
	}
	
	protected void loadUri(String uri) {
		if(!loadedFiles.contains(uri)) {
			owlModel.read(uri);
			loadedFiles.add(uri);
		}
	}
	
	public static Calendar processTimeParam(String time) {
		if(time == null || time.equals("")) return null;
		Calendar c = Calendar.getInstance();
		char type = time.charAt(time.length()-1);
		time = time.substring(1, time.length()-1);
		int move = Integer.parseInt(time);
		switch(type) {
		case 'Y':
			c.add(Calendar.YEAR, move);
			break;
		case 'M':
			c.add(Calendar.MONTH, move);
			break;
		}
		return c;
	}
	
	public static ArrayList<String> processCharacteristicParam(String characteristic) {
		if(characteristic == null || characteristic.equals("")) return null;
		String[] parts = characteristic.split(";");
		ArrayList<String> result = new ArrayList<String>();
		for(int i=0;i<parts.length;i++) {
			result.add("http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#"+parts[i]);
		}
		return result;
	}
	
	public static ArrayList<String> processHealthEffectParam(String health) {
		if(health == null || health.equals("")) return null;
		String[] parts = health.split(";");
		ArrayList<String> result = new ArrayList<String>();
		for(int i=0;i<parts.length;i++) {
			result.add("http://escience.rpi.edu/ontology/semanteco/2/0/health.owl#"+parts[i]);
		}
		return result;
	}
	
	public static JSONObject executeJSONQuery(String endpoint, String query) {
		JSONObject result = null;
		try {
			URL url = new URL(endpoint+"?query="+URLEncoder.encode(query, "UTF-8")+"&format="+URLEncoder.encode("application/sparql-results+json","UTF-8"));
			InputStream is = url.openStream();
			String content = CharStreams.toString(new InputStreamReader(is));
			result = new JSONObject(content);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	protected void getLimitData(HttpExchange req, Map<String,String> params) {
		JSONObject response = new JSONObject();
		try {
			JSONArray sources = new JSONArray(params.get("sources"));
			String state = states.get(params.get("state"));
			for(int i=0;i<sources.length();i++) {
				String source = sources.getString(i);
				String query = "PREFIX sioc: <http://rdfs.org/sioc/ns#> PREFIX dc: <http://purl.org/dc/terms/> "+
				"SELECT ?graph WHERE { GRAPH <http://sparql.tw.rpi.edu/semanteco/data-source> { ?graph sioc:topic <"+state+"> "+
				"; dc:source <"+source+"> } }";
				System.out.print(query);
				JSONObject graphs = executeJSONQuery(Configuration.TRIPLE_STORE, query);
				JSONArray arr = graphs.getJSONObject("results").getJSONArray("bindings");
				ArrayList<String> list = new ArrayList<String>();
				for(int j=0;j<arr.length();j++) {
					list.add(arr.getJSONObject(j).getJSONObject("graph").getString("value"));
				}
				System.out.print(list);
				String sites,measures;
				if(list.size()==0)
					continue;
				if(list.get(0).contains("measurements")) {
					measures = list.get(0);
					sites = list.get(1);
				}
				else {
					measures = list.get(1);
					sites = list.get(0);
				}
				CountInstanceQuery q = new CountInstanceQuery(sources.getString(i), sites, measures, params);
				Integer count = (Integer) q.execute(Configuration.TRIPLE_STORE);
				if(source.equals("http://sparql.tw.rpi.edu/source/usgs-gov")) {
					response.put("siteCount", count);
				}
				else if(source.equals("http://sparql.tw.rpi.edu/source/epa-gov")) {
					response.put("facilityCount", count);
				}
			}
		}
		catch(Exception e) {
			try {
				response = new JSONObject();
				response.put("error", true);
				response.put("errorString", "Exception 500 occurred on server, contact the system administrator for details.");
			}
			catch(Exception e1) { }
			e.printStackTrace();
		}
		String result = null;
		try {
			result = response.toString();
		}
		catch(Exception e) { }
		OutputStream os = null;
		try {
			req.getResponseHeaders().add("Content-Type", "application/sparql-results+json");
			req.sendResponseHeaders(200, result.length());
			os = req.getResponseBody();
			os.write(result.getBytes());
			os.flush();
		}
		catch(Exception e) { }
		finally {
			try {
				os.close();
			}
			catch(Exception e) {}
		}
	}
	
	protected Model loadData(Map<String,String> params) throws JSONException, IOException {
		long loadStart = System.currentTimeMillis();
		String regulation = params.get("regulation");
		JSONArray sources = new JSONArray(params.get("sources"));
		boolean reason = Boolean.parseBoolean(params.get("reason")!=null?params.get("reason"):"true");

		// Load ontologies
		Model owlModel,rdfModel;
		if(reason)
			owlModel = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
		else
			owlModel = ModelFactory.createDefaultModel();
		if(reason)
			rdfModel = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
		else
			rdfModel = ModelFactory.createDefaultModel();
		Model model = ModelFactory.createUnion(owlModel, rdfModel);
		//model.setCache(cache);
		owlModel.read("http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl");
		owlModel.read("http://escience.rpi.edu/ontology/semanteco/2/0/water.owl");
		owlModel.read("http://escience.rpi.edu/ontology/semanteco/2/0/health.owl");
		owlModel.read(regulation);
		rdfModel.read("http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl");
		rdfModel.read("http://escience.rpi.edu/ontology/semanteco/2/0/water.owl");
		rdfModel.read("http://escience.rpi.edu/ontology/semanteco/2/0/health.owl");
	
		// Load data
		for(int i=0;i<sources.length();i++) {
			long start = System.currentTimeMillis();
			log.debug("Loading data for source <"+sources.getString(i)+">");
			if(sources.getString(i).equals("http://sparql.tw.rpi.edu/source/epa-gov")) {
				//LoadDataQuery q = new LoadDataQuery(sources.getString(i), params);
				EPAHack q = new EPAHack(params);
				try {
					q.execute(Configuration.TRIPLE_STORE, rdfModel);
				}
				catch(Exception e) {
					LoadDataQuery q1 = new LoadDataQuery(sources.getString(i), params);
					q1.execute(Configuration.TRIPLE_STORE, rdfModel);
				}
			}
			else {
				LoadDataQuery q = new LoadDataQuery(sources.getString(i), params);
				q.execute(Configuration.TRIPLE_STORE, owlModel);
			}
			log.debug("Load finished in "+(System.currentTimeMillis()-start)+" ms");
		}
		log.info("Data load finished in "+(System.currentTimeMillis()-loadStart)+" ms");
		owlModel = null;
		rdfModel = null;
		return model;
	}
	
	protected JSONObject queryModel(String type, boolean polluted, Model model, Map<String,String> params) throws UnsupportedEncodingException, JSONException {
		String queryString;
		QueryExecution qe;
		ResultSet queryResults;
		Calendar t = processTimeParam(params.get("time"));
		String charClause = "", healthClause = "";
		ArrayList<String> uris = processCharacteristicParam(params.get("contaminants"));
		if(uris != null && uris.size()>0) {
			charClause = "<"+uris.get(0)+">";
			for(int i=1;i<uris.size();i++) {
				charClause += ",<"+uris.get(i)+">";
			}
			charClause = " FILTER(?elem IN ("+charClause+")) ";
		}
		uris = processHealthEffectParam(params.get("effects"));
		if(uris != null && uris.size()>0) {
			healthClause = "<"+uris.get(0)+">";
			for(int i=1;i<uris.size();i++) {
				healthClause += ",<"+uris.get(i)+">";
			}
			healthClause = " FILTER(?effect IN ("+healthClause+")) ";
		}
		
		queryString = "prefix pol: <http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#> " +
				"prefix health: <http://escience.rpi.edu/ontology/semanteco/2/0/health.owl#> " +
				"prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
				"prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
				"prefix geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> " +
				"prefix time: <http://www.w3.org/2006/time#> " +
				"prefix xsd: <http://www.w3.org/2001/XMLSchema#> "+
				"prefix repr: <http://sweet.jpl.nasa.gov/2.1/repr.owl#> " +
				"select ?s ?lat ?long ?label ";
		queryString +=
				"where {"+
				"?s a " + type + " ; geo:lat ?lat ; geo:long ?long ";
		if(polluted) {
		if(t!=null || !charClause.equals("") || !healthClause.equals(""))
			queryString += "; pol:hasMeasurement ?m . ?m a pol:RegulationViolation . " ;
		//if(t!=null)
		//	queryString += "?m time:inXSDDateTime ?t . FILTER(?t > xsd:dateTime(\""+sdf.format(t.getTime())+"\"))";
		if(!charClause.equals("") || !healthClause.equals(""))
			queryString += "?m pol:hasCharacteristic ?elem . ";
		if(!charClause.equals(""))
			queryString += charClause;
		if(!healthClause.equals(""))
			queryString += " ?elem health:hasHealthEffect ?effect "+healthClause+" ";
		}
		queryString += 
				" OPTIONAL { ?s rdfs:label ?label } "+
				"FILTER(?lat != 0 && ?long != 0) " +
		    //"} group by ?s ";
		    "}";
		qe = QueryExecutionFactory.create(QueryFactory.create(queryString, Syntax.syntaxSPARQL_11), model);
		queryResults = qe.execSelect();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ResultSetFormatter.outputAsJSON(baos, queryResults);
		JSONObject result = new JSONObject(baos.toString("UTF-8"));
		qe.close();
		return result;
	}
	
	protected void getData(HttpExchange req, Map<String,String> params) {
		String result = null;
		boolean err = false;
		OutputStream os = req.getResponseBody();
		Model model;
		try {
			model = loadData(params);
			String queryString;
			queryString = "prefix pol: <http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#> " +
					"prefix health: <http://escience.rpi.edu/ontology/semanteco/2/0/health.owl#> " +
					"prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
					"prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
					"prefix geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> " +
					"prefix time: <http://www.w3.org/2006/time#> " +
					"prefix xsd: <http://www.w3.org/2001/XMLSchema#> "+
					"prefix repr: <http://sweet.jpl.nasa.gov/2.1/repr.owl#> " +
					"select ?s ?lat ?lng where { ?s a pol:MeasurementSite ; geo:lat ?lat ; geo:long ?lng }";
			QueryExecution qe;
			ResultSet queryResults;
			qe = QueryExecutionFactory.create(QueryFactory.create(queryString, Syntax.syntaxSPARQL_11), model);
			queryResults = qe.execSelect();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ResultSetFormatter.outputAsJSON(baos, queryResults);
			log.debug(baos.toString("UTF-8"));
			qe.close();
			
			// Query
			long start = System.currentTimeMillis();
			JSONObject sites=null,facilities=null,pollutedSites=null;
			for(int i=0;i<3;i++) {
				log.debug("Querying data...");
				switch(i) {
				case 0:
					sites = queryModel("pol:MeasurementSite", false, model, params);
					break;
				case 1:
					facilities = queryModel("pol:Facility", false, model, params);
					break;
				case 2:
					pollutedSites = queryModel("pol:PollutedSite", true, model, params);
					break;
				}
				log.info("Query finished in "+(System.currentTimeMillis()-start)+" ms");
			}
			TreeMap<String, JSONObject> allBindings = new TreeMap<String, JSONObject>();
			JSONArray bindings = sites.getJSONObject("results").getJSONArray("bindings");
			for(int i=0;i<bindings.length();i++) {
				try {
				JSONObject binding  = bindings.getJSONObject(i);
				String uri = binding.getJSONObject("s").getString("value");
				JSONObject entry = new JSONObject();
				entry.put("type", "literal");
				entry.put("datatype", "http://www.w3.org/2001/XMLSchema#boolean");
				entry.put("value", "false");
				binding.put("facility", entry);
				entry = new JSONObject();
				entry.put("type", "literal");
				entry.put("datatype", "http://www.w3.org/2001/XMLSchema#boolean");
				entry.put("value", "false");
				binding.put("polluted", entry);
				allBindings.put(uri, binding);
				}
				catch(Exception e) { }
			}
			bindings = facilities.getJSONObject("results").getJSONArray("bindings");
			for(int i=0;i<bindings.length();i++) {
				try {
				JSONObject binding = bindings.getJSONObject(i);
				String uri = binding.getJSONObject("s").getString("value");
				if(allBindings.containsKey(uri)) {
					binding = allBindings.get(uri);
					binding.getJSONObject("facility").put("value", "true");
				}
				}
				catch(Exception e) { }
			}
			bindings = pollutedSites.getJSONObject("results").getJSONArray("bindings");
			for(int i=0;i<bindings.length();i++) {
				try {
				JSONObject binding = bindings.getJSONObject(i);
				String uri = binding.getJSONObject("s").getString("value");
				if(allBindings.containsKey(uri)) {
					binding = allBindings.get(uri);
					binding.getJSONObject("polluted").put("value", "true");
				}
				}
				catch(Exception e) { }
			}
			result = sites.toString();
			req.getResponseHeaders().add("Content-Type", "application/sparql-results+json");
		}
		catch(Exception e) {
			err = true;
			e.printStackTrace();
			result = "An error occurred on the server. Please contact the system administrator.";
		}
		finally {
			model = null;
		}
		try {
			req.sendResponseHeaders(err ? 500 : 200, result.length());
			os.write(result.getBytes());
		}
		catch(IOException e1) {
			e1.printStackTrace();
		}
		finally {
			try {
				os.flush();
				os.close();
			}
			catch(Exception e) {}
		}
	}
	
	protected void queryForWaterPollution(HttpExchange req, Map<String,String> params) {
		String result = null;
		boolean err = false;
		OutputStream os = req.getResponseBody();
		try {
			Model model = loadData(params);
			
			// Query
			long start = System.currentTimeMillis();
			log.debug("Querying data...");
			String queryString;
			QueryExecution qe;
			ResultSet queryResults;
			String site = params.get("site");
			String charClause = "", healthClause = "";
			ArrayList<String> uris = processCharacteristicParam(params.get("contaminants"));
			if(uris != null && uris.size()>0) {
				charClause = "<"+uris.get(0)+">";
				for(int i=1;i<uris.size();i++) {
					charClause += ",<"+uris.get(i)+">";
				}
				charClause = " FILTER(?element IN ("+charClause+")) ";
			}
			uris = processHealthEffectParam(params.get("effects"));
			if(uris != null && uris.size()>0) {
				healthClause = "<"+uris.get(0)+">";
				for(int i=1;i<uris.size();i++) {
					healthClause += ",<"+uris.get(i)+">";
				}
				healthClause = " FILTER(?effect IN ("+healthClause+")) ";
			}

			queryString = "prefix pol: <http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#> " +
					"prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
					"prefix geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> " +
					"prefix time: <http://www.w3.org/2006/time#> " +
					"prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
					"prefix owl: <http://www.w3.org/2002/07/owl#> " +
					"prefix xsd: <http://www.w3.org/2001/XMLSchema#> "+
					"prefix unit: <http://sweet.jpl.nasa.gov/2.1/reprSciUnits.owl#> " +
					"prefix health: <http://escience.rpi.edu/ontology/semanteco/2/0/health.owl#> "+
					"select ?element ?permit ?type ?value ?unit ?limit ?op ?time ?effect " +
					"where {" +
					"<"+site+"> pol:hasMeasurement ?m OPTIONAL { ?S pol:hasPermit ?permit } " +
					"?m a pol:RegulationViolation ; pol:hasCharacteristic ?element ; pol:hasValue ?value ; "+
					"unit:hasUnit ?unit ; time:inXSDDateTime ?time .OPTIONAL { ?m a ?type . ?type rdfs:subClassOf pol:RegulationViolation } ";
				//if(t!=null)
				//	queryString += "FILTER(?time > xsd:dateTime(\""+sdf.format(t.getTime())+"\"))";
				if(!charClause.equals(""))
					queryString += charClause;
				if(!healthClause.equals(""))
					queryString += " ?element health:hasHealthEffect ?effect "+healthClause+" ";
				queryString +=
					"OPTIONAL { ?element health:hasHealthEffect ?effect } "+
					"OPTIONAL { ?m a ?cls . " +
					"?cls owl:intersectionOf ?list . ?list rdf:rest*/rdf:first ?supers . " +
					"?supers owl:onProperty pol:hasValue ; owl:someValuesFrom ?dt . ?dt owl:withRestrictions ?res ." +
					"?res rdf:rest*/rdf:first [ ?p ?limit ] ." +
					"FILTER( datatype(?limit) = xsd:double ) "+
					"OPTIONAL { FILTER( ?p = xsd:minInclusive ) BIND (\"<=\" AS ?op) } " +
					"OPTIONAL { FILTER( ?p = xsd:maxInclusive ) BIND (\">=\" AS ?op) } " +
					"OPTIONAL { FILTER( ?p = xsd:minExclusive ) BIND (\"<\" AS ?op) } "+
					"OPTIONAL { FILTER( ?p = xsd:maxExclusive ) BIND (\">\" AS ?op) } "+
					" } OPTIONAL { ?m pol:hasLimitOperator ?op ; pol:hasLimitValue ?limit } " +
					"} order by asc(?time)";
			qe = QueryExecutionFactory.create(QueryFactory.create(queryString, Syntax.syntaxSPARQL_11), model);
			queryResults = qe.execSelect();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ResultSetFormatter.outputAsJSON(baos, queryResults);
			result = baos.toString("UTF-8");
			qe.close();
			log.info("Query finished in "+(System.currentTimeMillis()-start)+" ms");
			req.getResponseHeaders().add("Content-Type", "application/sparql-results+json");
		}
		catch(Exception e) {
			err = true;
			e.printStackTrace();
			result = "An error occurred on the server. Please contact the system administrator.";
		}
		try {
			req.sendResponseHeaders(err ? 500 : 200, result.length());
			os.write(result.getBytes());
		}
		catch(IOException e1) {
			e1.printStackTrace();
		}
		finally {
			try {
				os.flush();
				os.close();
			}
			catch(Exception e) {}
		}
	}

	

	
	protected void listCharacteristics(HttpExchange req, Map<String,String> params) {
		String result = null;
		boolean err = false;
		OutputStream os = req.getResponseBody();
		try {
			Model model = ModelFactory.createDefaultModel();
			
			JSONArray sources = new JSONArray(params.get("sources"));
			for(int i=0;i<sources.length();i++) {
				String source = sources.getString(i);
				ListCharacteristicsQuery q = new ListCharacteristicsQuery(source, params);
				ResultSet rs = (ResultSet)q.execute(Configuration.TRIPLE_STORE);
				while(rs.hasNext()) {
					QuerySolution qs = rs.next();
					Resource c = qs.getResource("c");
					Resource type = model.createResource("http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#Characteristic");
					if(c!=null)
						model.add(c, RDF.type, type);
				}
			}
			
			// Query
			long start = System.currentTimeMillis();
			log.debug("Querying data...");
			String queryString;
			QueryExecution qe;
			ResultSet queryResults;

			queryString = "prefix pol: <http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#> " +
					"prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
					"prefix geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> " +
					"prefix time: <http://www.w3.org/2006/time#> " +
					"prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
					"prefix owl: <http://www.w3.org/2002/07/owl#> " +
					"prefix xsd: <http://www.w3.org/2001/XMLSchema#> "+
					"prefix unit: <http://sweet.jpl.nasa.gov/2.1/reprSciUnits.owl#> " +
					"prefix health: <http://escience.rpi.edu/ontology/semanteco/2/0/health.owl#> "+
					"select distinct ?item where { ?item a pol:Characteristic } order by asc(?item)"
					;
			qe = QueryExecutionFactory.create(QueryFactory.create(queryString, Syntax.syntaxSPARQL_11), model);
			queryResults = qe.execSelect();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ResultSetFormatter.outputAsJSON(baos, queryResults);
			result = baos.toString("UTF-8");
			qe.close();
			log.info("Query finished in "+(System.currentTimeMillis()-start)+" ms");
			req.getResponseHeaders().add("Content-Type", "application/sparql-results+json");
		}
		catch(Exception e) {
			err = true;
			e.printStackTrace();
			result = "An error occurred on the server. Please contact the system administrator.";
		}
		try {
			req.sendResponseHeaders(err ? 500 : 200, result.length());
			os.write(result.getBytes());
		}
		catch(IOException e1) {
			e1.printStackTrace();
		}
		finally {
			try {
				os.flush();
				os.close();
			}
			catch(Exception e) {}
		}
	}
	
	protected void listHealthEffects(HttpExchange req, Map<String,String> params) {
		String result = null;
		boolean err = false;
		OutputStream os = req.getResponseBody();
		try {
			Model model = ModelFactory.createDefaultModel();
			model.read("http://escience.rpi.edu/ontology/semanteco/2/0/health.owl");
			
			JSONArray sources = new JSONArray(params.get("sources"));
			for(int i=0;i<sources.length();i++) {
				String source = sources.getString(i);
				ListCharacteristicsQuery q = new ListCharacteristicsQuery(source, params);
				ResultSet rs = (ResultSet)q.execute(Configuration.TRIPLE_STORE);
				while(rs.hasNext()) {
					QuerySolution qs = rs.next();
					Resource c = qs.getResource("c");
					Resource type = model.createResource("http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#Characteristic");
					if(c!=null)
						model.add(c, RDF.type, type);
				}
			}
			
			// Query
			long start = System.currentTimeMillis();
			log.debug("Querying data...");
			String queryString;
			QueryExecution qe;
			ResultSet queryResults;

			queryString = "prefix pol: <http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#> " +
					"prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
					"prefix geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> " +
					"prefix time: <http://www.w3.org/2006/time#> " +
					"prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
					"prefix owl: <http://www.w3.org/2002/07/owl#> " +
					"prefix xsd: <http://www.w3.org/2001/XMLSchema#> "+
					"prefix unit: <http://sweet.jpl.nasa.gov/2.1/reprSciUnits.owl#> " +
					"prefix health: <http://escience.rpi.edu/ontology/semanteco/2/0/health.owl#> "+
					"select distinct ?item where { ?c a pol:Characteristic ; health:hasHealthEffect ?item } order by asc(?item)"
					;
			qe = QueryExecutionFactory.create(QueryFactory.create(queryString, Syntax.syntaxSPARQL_11), model);
			queryResults = qe.execSelect();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ResultSetFormatter.outputAsJSON(baos, queryResults);
			result = baos.toString("UTF-8");
			qe.close();
			log.info("Query finished in "+(System.currentTimeMillis()-start)+" ms");
			req.getResponseHeaders().add("Content-Type", "application/sparql-results+json");
		}
		catch(Exception e) {
			err = true;
			e.printStackTrace();
			result = "An error occurred on the server. Please contact the system administrator.";
		}
		try {
			req.sendResponseHeaders(err ? 500 : 200, result.length());
			os.write(result.getBytes());
		}
		catch(IOException e1) {
			e1.printStackTrace();
		}
		finally {
			try {
				os.flush();
				os.close();
			}
			catch(Exception e) {}
		}
	}

	public void handle(HttpExchange arg0) throws IOException {
		long start = System.currentTimeMillis();
		long start2 = System.currentTimeMillis();
		arg0.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
		try {
			//get query string
			Map<String,String> params = parseRequest(arg0);
			
			String method = params.get("method");
			if(method!=null) {
				if(method.equalsIgnoreCase("getLimitData")) {
					getLimitData(arg0, params);
				}
				else if(method.equalsIgnoreCase("getData")) {
					getData(arg0, params);
				}
				else if(method.equalsIgnoreCase("queryForWaterPollution")) {
					queryForWaterPollution(arg0, params);
				}
				else if(method.equalsIgnoreCase("listCharacteristics")) {
					listCharacteristics(arg0, params);
				}
				else if(method.equalsIgnoreCase("listHealthEffects")) {
					listHealthEffects(arg0, params);
				}
				else if(method.equalsIgnoreCase("getHUC8Codes")) {
					//System.err.println("getHUC8Codes");
					WaterEntityAgent.getHUC8Codes(arg0, params, log);
				}
				else if(method.equalsIgnoreCase("getHUC8CodesOneState")) {
					//System.err.println("getHUC8CodesOneState");
					WaterEntityAgent.getHUC8CodesOneState(arg0, params, log);
				}
				else if(method.equalsIgnoreCase("getSpeciesNames")) {
					System.err.println("getSpeciesNames");
					DistributionWebService.getSpeciesNames(arg0, params, log);
				}
				else if(method.equalsIgnoreCase("getSpeciesDistributionByCounty")) {
					//System.err.println("getSpeciesDistributionByCounty");
					DistributionWebService.getSpeciesDistributionByCounty(arg0, params, log);
				}
				return;
			}
			
			// extract parameters
			String countyCode = params.get("countyCode");
			String state = params.get("state");
			String queryString=params.get("query");
			String regulation=params.get("regulation");
			String start_index=params.get("start");
			String limit=params.get("limit");
			String data=params.get("data");
			String type=params.get("type");
			String zip=params.get("zip");

			// create models
			owlModel = new CachedDataModel(ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC));
			owlModel.read("http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl");
			owlModel.read("http://escience.rpi.edu/ontology/semanteco/2/0/health.owl");
			
			pmlModel = ModelFactory.createDefaultModel();
			
			try{
			
				if(data.compareTo("water")==0){
					owlModel.read("http://escience.rpi.edu/semanteco/2/0/"+regulation+".owl");
					WaterDataQuery query = Query.getWaterData(state, countyCode, start_index, limit);
					query.execute("http://sparql.tw.rpi.edu/virtuoso/sparql", owlModel);
				}
				else if(data.compareTo("facility")==0){
					FacilityDataQuery query = Query.getFacilityData(state, countyCode, zip, start_index, limit, type);
					query.execute("http://sparql.tw.rpi.edu/virtuoso/sparql", owlModel);
				}
	
			}
			catch(Exception e){System.err.println("Unable to load data");}
			
			System.err.println("Created initial model in "+(System.currentTimeMillis()-start2)+" ms");

			Model model = ModelFactory.createUnion(owlModel, pmlModel);
			
			start2 = System.currentTimeMillis();
			String response = getQueryResult(model,queryString);
			System.err.println("Processed query in "+(System.currentTimeMillis()-start2)+" ms");

			// write http response
			arg0.getResponseHeaders().set("Content-type", "text/xml");
			arg0.sendResponseHeaders(200, response.length());
			OutputStream os = arg0.getResponseBody();
			os.write(response.getBytes());
			os.flush();
			os.close();
		} catch(Exception e) {
			
			// error handling
			e.printStackTrace();
			String response = "Server side error. Please see log for details.";
			arg0.sendResponseHeaders(500, response.length());
			arg0.getResponseBody().write(response.getBytes("UTF-8"));
			arg0.getResponseBody().close();
		}
		System.err.println("Processed request in "+(System.currentTimeMillis()-start)+" ms");
	}

	public String getQueryResult(Model model, String queryString)
	{
		QueryExecution qe = QueryExecutionFactory.create(queryString, model);
		
		try {
			ResultSet queryResults = qe.execSelect();

			String result = ResultSetFormatter.asXMLString(queryResults);
			qe.close();
			return result;
		}
		catch(Exception e) {
			if(queryString.indexOf("DESCRIBE")>-1) {
				Model m2 = qe.execDescribe();
				StringWriter sw = new StringWriter();
				m2.write(sw);
				return sw.toString();
			}
			else {
				e.printStackTrace();
			}
			return "";
		}
	}

	public void listStatements(Model model)
	{
		StmtIterator iter = model.listStatements();
		
		if (iter.hasNext()) {
		    while (iter.hasNext()) {
		        System.out.println("  " + iter.nextStatement().toString());
		    }
		} else {
		    System.out.println("No vcards were found in the database");
		}
	}

	public String performQuery(String query) {
		String response = getQueryResult(theModel,query);
		return response;
	}
}
