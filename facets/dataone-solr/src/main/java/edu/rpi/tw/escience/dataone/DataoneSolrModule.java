package edu.rpi.tw.escience.dataone;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;

import edu.rpi.tw.escience.semanteco.Domain;
import edu.rpi.tw.escience.semanteco.Module;
import edu.rpi.tw.escience.semanteco.ModuleConfiguration;
import edu.rpi.tw.escience.semanteco.QueryMethod;
import edu.rpi.tw.escience.semanteco.Request;
import edu.rpi.tw.escience.semanteco.Resource;
import edu.rpi.tw.escience.semanteco.SemantEcoUI;
import edu.rpi.tw.escience.semanteco.query.Query;

public class DataoneSolrModule implements Module {

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
		ui.addScript(config.getResource("dataone.js"));
		ui.addScript(config.getResource("vocab.js"));

	}

	@Override
	public String getName() {
		return "DataoneSolr";
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
	
	@QueryMethod
	  public String testMethodAwesome(final Request request) {
		  System.out.println("test awesome");
		return null;
	  }
	
	@QueryMethod
	public String accessService(final Request request) throws JSONException{
		
		
		String query = "";

		//query = "http://alchemist.nceas.ucsb.edu/tmosearch/get_results_topic_expansion.php?q=insect%20herbivore";
		//WebServiceRequestHandler.getRequest(query, "text");
		
		//query = "http://data1.tw.rpi.edu/tomcat/VocabularyServer/ServeSparql?term=passeriformes&Submit=Submit&domain=organism&getTree=true&upward=true&level=1";
				
		//WebServiceRequestHandler.getRequest(query, "text");
		
		String searchTerm = request.getParam("term").toString();
		System.out.println("searched term is : " + searchTerm);
		
		//query = "https://cn-orc-1.dataone.org/cn/v1/query/solr/?q=abstract:ecology+OR+hydrology&wt=json&rows=100";
		//query = "https://cn-orc-1.dataone.org/cn/v1/query/solr/?q=abstract:ecology+AND+hydrology&wt=json&rows=10000000";

		
		JSONObject j = getRequest(query, "json");
		//coll = new DataoneDataObjectCollection(j, query, false);
		//keywords = coll.getKeywords();
		
		JSONObject results = j.getJSONObject("response");
		JSONArray tempDataoneArray = (JSONArray) results.get("docs");
		System.out.println("Document1 count is: " + tempDataoneArray.length());


		//query = "https://cn-orc-1.dataone.org/cn/v1/query/solr/?q=abstract:hydrology&wt=json&rows=100";
		query = "https://cn-orc-1.dataone.org/cn/v1/query/solr/?q=abstract:" + searchTerm + "&wt=json&rows=10000000";

		JSONObject j2 = getRequest(query, "json");
		
		//System.out.println("Document count is: " +  DataoneDataObjectCollection.getDataOneJsonFromResponse(j).length());

		//DataoneDataObject d;

		
		System.out.println("calls made");
		
		results = j2.getJSONObject("response");
		tempDataoneArray = (JSONArray) results.get("docs");
		
		System.out.println("Document2 count is: " + tempDataoneArray.length());		
		System.out.println("searched term is : " + searchTerm);
		System.out.println("query is : " + query);


		return j2.toString();	
	}
	
	@SuppressWarnings("deprecation")
	public static JSONObject getRequest(String query, String resultType){
		try{		
			//URLEncoder.encode(term, "UTF-8") 
			//String query = "https://www.googleapis.com/freebase/v1/search?query=" + URLEncoder.encode(term, "UTF-8") + "&indent=true&limit=1" + "&key=" + "AIzaSyBuu7a45hYNBYcwTC9DkeXCIHI3_o0fBd0";
			//String query = "https://cn-orc-1.dataone.org/cn/v1/query/solr/?q=keywords:*ecology*&wt=json&indent=true&rows=10000000";
			System.out.println("dataOne GET query is : " + query);
			//UrlValidator urlValidator = new UrlValidator();
			//System.out.println("valid?: " + urlValidator.isValid(query));

			JSONObject freebaseConcept = new JSONObject();
			BufferedReader br = null;
			try{
				URL requestURL = new URL(query);
				System.out.println("requestURL : " + requestURL.toURI().toString());
				//URLConnection conn = requestURL.openConnection();
				HttpURLConnection conn = (HttpURLConnection) requestURL.openConnection();


				conn.setReadTimeout(5000);
				//String contentType = conn.getContentType();
				//System.out.println("connType : " + contentType);
				try{
					InputStream o = (InputStream)conn.getContent();
					InputStreamReader isr = new InputStreamReader(o);
					br = new BufferedReader(isr);
				}
				catch(Exception e){ e.printStackTrace();
				JSONObject error = new JSONObject();
				error.put("error", "test");
				return error;
				}
			}
			catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} 
			catch (MalformedURLException e) {
				e.printStackTrace();
			}
			
			
			
			String result="";
			String line;
			while((line=br.readLine())!=null) result += line;			
			//br.close();
			//System.out.println("full string is: " + result.toString());
			//System.out.println("(WebServiceRequestHandler) freebase result is : " + result);

if(resultType.equals("json")){
			JSONObject content = new JSONObject(result);
			System.out.println("content: " + content.toString());
			
			if(content.has("error")){
				return content;
			}
			else{
				//JSONArray j = (JSONArray) content.get("result");
				//JSONObject jp = (JSONObject) j.get(0);
				return content;		
			}
		}

if(resultType.equals("text")){
	//JSONObject content = new JSONObject(result);
	System.out.println("content: " + result.toString());
}
			
		}
		catch(Exception e){
		}
		
		
		return null;
	}

}
