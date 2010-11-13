package edu.rpi.tw.eScience.WaterQualityPortal.WebService;

import java.io.IOException;
import java.io.OutputStream;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.mindswap.pellet.jena.PelletReasonerFactory;


public class WaterAgentInstance implements HttpHandler {
	
	public void handle(HttpExchange arg0) throws IOException {
		try {
		//get query string
		String queryString=parseRequest(arg0);
		
		//load ontology model
		Model model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
		model.read("http://tw2.tw.rpi.edu/zhengj3/demo/cleanwater.owl");
		
		//get query result in xml format
		String response = getQueryResult(model,queryString);
		//String response = arg0.getRequestURI().getQuery();
		//send response back
		arg0.sendResponseHeaders(200, response.length());
		OutputStream os = arg0.getResponseBody();
		os.write(response.getBytes());
		os.flush();
		os.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public String parseRequest(HttpExchange arg0) throws IOException
	{
		//read request
		/*
		InputStream is = arg0.getRequestBody();		
		String query = "";
		
		int c = is.read();
		while(c!=-1) {
			query = query + Character.toString((char)c);
			c = is.read();
		}
		*
		*/
		String query = arg0.getRequestURI().getQuery();
		//parse request
		String [] request=query.split("=");
		
		String queryString="";
		
		if(request.length>=2)
		{
			if(request[0].compareTo("query")==0)
			{
				queryString=request[1];
			}
		}
		
		return java.net.URLDecoder.decode( queryString, "UTF-8");
	}
	public String getQueryResult(Model model, String queryString)
	{
		QueryExecution qe = QueryExecutionFactory.create(queryString, model);
		ResultSet queryResults = qe.execSelect();
		
		String result = ResultSetFormatter.asXMLString(queryResults);

		qe.close();
		
		return result;
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
