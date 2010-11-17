package edu.rpi.tw.eScience.WaterQualityPortal.WebService;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import edu.rpi.tw.eScience.WaterQualityPortal.data.WaterDataProvider;
import edu.rpi.tw.eScience.WaterQualityPortal.usgs.DataService;

import org.mindswap.pellet.jena.PelletReasonerFactory;


public class WaterAgentInstance implements HttpHandler {
	
	
	public WaterAgentInstance() {
	}
	
	public Map<String,String> parseRequest(HttpExchange arg0) throws IOException
	{
		HashMap<String,String> result = new HashMap<String, String>();
		String query = arg0.getRequestURI().getQuery();
		//parse request
		String [] request=query.split("&");
		
		
		for(int i=0;i<request.length;i++) {
			String[] pieces = request[i].split("=");
			result.put(pieces[0], java.net.URLDecoder.decode(pieces[1],"UTF-8"));
		}
		return result;
	}
	
	public List<WaterDataProvider> getProviders() {
		List<WaterDataProvider> providers = new ArrayList<WaterDataProvider>();
		try {
			providers.add(new DataService());
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return providers;
	}

	public void handle(HttpExchange arg0) throws IOException {
		arg0.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
		try {
			//get query string
			Map<String,String> params = parseRequest(arg0);
			String countyCode = params.get("countyCode");
			String stateCode = params.get("stateCode");
			String state = params.get("state");
			String zip = params.get("zip");
			String queryString=params.get("query");
			
			//load ontology model
			OntModel owlModel = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
			Model pmlModel = ModelFactory.createDefaultModel();
			owlModel.read("http://was.tw.rpi.edu/water/rdf/cleanwater.owl");
			try {
				owlModel.read("http://was.tw.rpi.edu/water/rdf/"+state+"-regulations-owl.rdf");
				pmlModel.read("http://was.tw.rpi.edu/water/rdf/"+state+"-regulations-pml.rdf");
			}
			catch(Exception e) {
				System.err.println("Unable to find regulations for state "+state);
			}
			List<WaterDataProvider> providers = getProviders();
			for(WaterDataProvider wdp : providers) {
				try {
					wdp.setUserSource(countyCode, stateCode, zip);
					wdp.getData(owlModel, pmlModel);
				}
				catch(Exception e) {
					System.err.println("Exception thrown by "+wdp.getName());
					e.printStackTrace();
				}
			}
			FileOutputStream fos = new FileOutputStream("/usr/local/water/example.rdf");
			owlModel.write(fos);
			fos.close();
			Model model = ModelFactory.createUnion(owlModel, pmlModel);
			
			//get query result in xml format
			String response = getQueryResult(model,queryString);
			//String response = arg0.getRequestURI().getQuery();
			//send response back
			arg0.getResponseHeaders().set("Content-type", "text/xml");
			arg0.sendResponseHeaders(200, response.length());
			OutputStream os = arg0.getResponseBody();
			os.write(response.getBytes());
			os.flush();
			os.close();
		} catch(Exception e) {
			e.printStackTrace();
			String response = "Server side error. Please see log for details.";
			arg0.sendResponseHeaders(500, response.length());
			arg0.getResponseBody().write(response.getBytes("UTF-8"));
			arg0.getResponseBody().close();
		}
	}

	public String getQueryResult(Model model, String queryString)
	{
		QueryExecution qe = QueryExecutionFactory.create(queryString, model);
		ResultSet queryResults = qe.execSelect();
		
		if(queryResults!=null) {
		
			String result = ResultSetFormatter.asXMLString(queryResults);
			qe.close();
			return result;
		}
		else {
			Model m2 = qe.execDescribe();
			StringWriter sw = new StringWriter();
			m2.write(sw);
			return sw.toString();
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
	}
