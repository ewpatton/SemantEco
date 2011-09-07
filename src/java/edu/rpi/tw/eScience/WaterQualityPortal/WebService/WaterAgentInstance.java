package edu.rpi.tw.eScience.WaterQualityPortal.WebService;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.HashMap;
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

import edu.rpi.tw.eScience.WaterQualityPortal.zip.ZipCodeLookup;

import org.mindswap.pellet.jena.PelletReasonerFactory;


public class WaterAgentInstance implements HttpHandler {
	
	OntModel owlModel=ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);;
	Model pmlModel;
	Model theModel;
	
	public WaterAgentInstance() {
		owlModel.read("file:///C:/Users/zhengj3/Documents/java_projects/WaterHealth/epa.owl");
		listStatements(owlModel);
	}
	
	
	public WaterAgentInstance(ZipCodeLookup zipCode, File basePath) {
		
		long start = System.currentTimeMillis();
		owlModel = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
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

	}

	public Map<String,String> parseRequest(HttpExchange arg0) throws IOException
	{
		HashMap<String,String> result = new HashMap<String, String>();
		String query = arg0.getRequestURI().getQuery();
		//parse request
		String [] request=query.split("&");
		
		
		for(int i=0;i<request.length;i++) {
			System.out.println(request[i]);
			String[] pieces = request[i].split("=");
			if(pieces.length==2) {
				result.put(pieces[0], java.net.URLDecoder.decode(pieces[1],"UTF-8"));
			}
			else System.err.println(pieces);
		}
		return result;
	}
	


	public void handle(HttpExchange arg0) throws IOException {
		long start = System.currentTimeMillis();
		long start2 = System.currentTimeMillis();
		arg0.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
		try {
			//get query string
			Map<String,String> params = parseRequest(arg0);
			String countyCode = params.get("countyCode");
			String state = params.get("state");
			String queryString=params.get("query");
			String regulation=params.get("regulation");
			String start_index=params.get("start");
			String limit=params.get("limit");
			String data=params.get("data");
			String type=params.get("type");
			String source=params.get("source");
			owlModel = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
			owlModel.read("http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl");
			owlModel.read("http://tw2.tw.rpi.edu/zhengj3/owl/health.owl");
			//OntModel towlModel = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
			
			pmlModel = ModelFactory.createDefaultModel();
			
			//load ontology model
			//OntModel owlModel = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);

			
			try{
			
			if(data.compareTo("water")==0){
			owlModel.read("http://tw2.tw.rpi.edu/zhengj3/owl/"+regulation+".owl");
			System.err.println("http://was.tw.rpi.edu/swqp/waterData.php?state="+state+"&county="+countyCode+"&start="+start_index+"&limit="+limit+"&source="+source);
			pmlModel.read("http://tw2.tw.rpi.edu/zhengj3/owl/"+regulation+".pml");
			owlModel.read("http://was.tw.rpi.edu/swqp/waterData.php?state="+state+"&county="+countyCode+"&start="+start_index+"&limit="+limit+"&source="+source);
			}
			else if(data.compareTo("facility")==0){
				//owlModel.read("http://tw2.tw.rpi.edu/zhengj3/owl/"+regulation+".owl");
				System.err.println("http://was.tw.rpi.edu/swqp/facilityData.php?state="+state+"&county="+countyCode+"&start="+start_index+"&limit="+limit+"&type="+type);
				owlModel.read("http://was.tw.rpi.edu/swqp/facilityData.php?state="+state+"&county="+countyCode+"&start="+start_index+"&limit="+limit+"&type="+type+"&source="+source);			
				
			}
	
			}
			catch(Exception e){System.err.println("Unable to load data");}
			
			//owlModel.read("http://tw2.tw.rpi.edu/zhengj3/water_store/ARC2store/sparql.php?query=+%0D%0A%0D%0APREFIX+epa%3A<http%3A%2F%2Ftw2.tw.rpi.edu%2Fzhengj3%2Fowl%2Fepa.owl%23>%0D%0APREFIX+time%3A<http%3A%2F%2Fwww.w3.org%2F2006%2Ftime%23>%0D%0Aprefix+wgs%3A+<http%3A%2F%2Fwww.w3.org%2F2003%2F01%2Fgeo%2Fwgs84_pos%23>+%0D%0A%0D%0ACONSTRUCT+{%0D%0A++++%3Fs+rdf%3Atype+epa%3AMeasurementSite+.+%0D%0A++++%3Fs+epa%3AhasMeasurement+%3Fmeasurement.%0D%0A++++%3Fs+epa%3AhasCountyCode+\"3\".%0D%0A++++%3Fs+wgs%3Alat+%3Flat.%0D%0A++++%3Fs+wgs%3Along+%3Flong.%0D%0A++++%3Fmeasurement+epa%3AhasElement+%3Felement.%0D%0A++++%3Fmeasurement+epa%3AhasValue+%3Fvalue.%0D%0A++++%3Fmeasurement+epa%3AhasUnit+%3Funit.%0D%0A++++%3Fmeasurement+time%3AinXSDDateTime+%3Ftime.%0D%0A}%0D%0AWHERE+{%0D%0A++GRAPH+<http%3A%2F%2Ftw2.tw.rpi.edu%2Fwater%2FRI>+{+%0D%0A++++%3Fs+rdf%3Atype+epa%3AMeasurementSite+.+%0D%0A++++%3Fs+epa%3AhasUSGSSiteId+%3Fid.%0D%0A++++%3Fs+epa%3AhasCountyCode+\"3\".%0D%0A++++%3Fs+wgs%3Alat+%3Flat.%0D%0A++++%3Fs+wgs%3Along+%3Flong.%0D%0A++++%3Fmeasurement+epa%3AhasUSGSSiteId+%3Fid.%0D%0A++++%3Fmeasurement+epa%3AhasElement+%3Felement.%0D%0A++++%3Fmeasurement+epa%3AhasValue+%3Fvalue.%0D%0A++++%3Fmeasurement+epa%3AhasUnit+%3Funit.%0D%0A++++%3Fmeasurement+time%3AinXSDDateTime+%3Ftime.%0D%0A++}%0D%0A}%0D%0A%0D%0A&output=&jsonp=&key=");
			//owlModel.read("http://tw2.tw.rpi.edu/zhengj3/owl/test2.rdf");
			//System.err.println("http://tw2.tw.rpi.edu/zhengj3/demo/waterData.php?state="+state+"&county="+countyCode);
			
		    
			/*
			try {
				//owlModel.read("http://was.tw.rpi.edu/water/rdf/"+state+"-regulations-owl.rdf");
				//pmlModel.read("http://was.tw.rpi.edu/water/rdf/"+state+"-regulations-pml.rdf");
			}
			catch(Exception e) {
				System.err.println("Unable to find regulations for state "+state);
			}
			*/
			System.err.println("Created initial model in "+(System.currentTimeMillis()-start2)+" ms");

			
			Model model = ModelFactory.createUnion(owlModel, pmlModel);
			
			//System.out.println("============");
			//listStatements(towlModel);
			//get query result in xml format
			start2 = System.currentTimeMillis();
			String response = getQueryResult(model,queryString);
			System.err.println("Processed query in "+(System.currentTimeMillis()-start2)+" ms");
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
