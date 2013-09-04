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


	public static void main (String[] args) throws JSONException{
		//queryTopic("birds");
	}
	//
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
		//query = "http://alchemist.nceas.ucsb.edu/tmosearch/get_results_topic_expansion.php?q=insect%20herbivore";
		//WebServiceRequestHandler.getRequest(query, "text");		
		//query = "http://data1.tw.rpi.edu/tomcat/VocabularyServer/ServeSparql?term=passeriformes&Submit=Submit&domain=organism&getTree=true&upward=true&level=1";			
		//WebServiceRequestHandler.getRequest(query, "text");

		JSONObject objVocab = null;
		JSONObject objTopic = null;
		JSONArray domains = null;
		String searchTerm = request.getParam("term").toString();
		System.out.println("searched term is : " + searchTerm);
		System.out.println("request object is : " + request.toString());
		
		
		if(request.getParam("domain") != null){
		domains = (JSONArray) request.getParam("domain");
		for(int i = 0; i< domains.length(); i++){
			System.out.println("a domain: " + domains.get(i));
		}	
		}

		JSONArray searchType = (JSONArray) request.getParam("searchType");
		for(int i = 0; i< searchType.length(); i++){
			System.out.println("a searchType: " + searchType.get(i));
			if(searchType.get(i).toString().equals("vocabulary")){
				System.out.println("(doing vocab search first) "); 

				String searchTermVocabExpanded = expandConcept(searchTerm, domains);
				objVocab = searchSolr(searchTermVocabExpanded);
			}
			if(searchType.get(i).toString().equals("topic")){
				System.out.println("(doing topic search now) "); 
				String searchTermTopicExpanded = expandTopic(searchTerm);
				objTopic =  searchSolr(searchTermTopicExpanded);
			}	
		}
		//query = "https://cn-orc-1.dataone.org/cn/v1/query/solr/?q=abstract:ecology+OR+hydrology&wt=json&rows=100";
		//query = "https://cn-orc-1.dataone.org/cn/v1/query/solr/?q=abstract:ecology+AND+hydrology&wt=json&rows=10000000";

		//JSONObject j = getRequest(query, "json");
		//coll = new DataoneDataObjectCollection(j, query, false);
		//keywords = coll.getKeywords();

		//JSONObject results = j.getJSONObject("response");
		//JSONArray tempDataoneArray = (JSONArray) results.get("docs");
		//System.out.println("Document1 count is: " + tempDataoneArray.length());

		//query = "https://cn-orc-1.dataone.org/cn/v1/query/solr/?q=abstract:hydrology&wt=json&rows=100";
		
		//System.out.println("objVocab: " + objVocab.toString()); 
		//System.out.println("objTopic: " + objTopic.toString()); 

		JSONArray inVocabNotTopic = getDiff(objVocab, objTopic);
		JSONArray inTopicNotVocab = getDiff(objTopic, objVocab);
		
		//diff id output
		for(int i = 0 ; i < inVocabNotTopic.length(); i++){
			System.out.println("inVocabNotTopic: " + inVocabNotTopic.get(i).toString());
		}
		System.out.println("inVocabNotTopic diff id count:" + inVocabNotTopic.length());

		
		//String searchTermTopicExpanded = expandConcept(searchTerm);
		return objVocab.toString();


	}

	public static JSONObject searchSolr(String searchTerm) throws JSONException{

		String query = "https://cn-orc-1.dataone.org/cn/v1/query/solr/?q=abstract:" + searchTerm + "&wt=json&rows=20";
		System.out.println("query is : " + query);
		JSONObject j2 = (JSONObject) getRequest(query, "json");		
		//System.out.println("Document count is: " +  DataoneDataObjectCollection.getDataOneJsonFromResponse(j).length());
		//DataoneDataObject d;	
		System.out.println("(query has been made)");	
		System.out.println("j2 in searchSolr: " + j2.toString());

		JSONObject  results = j2.getJSONObject("response");
		//System.out.println("response: " + results);
		//System.out.println("j2: " + j2.toString());

		JSONArray  tempDataoneArray = (JSONArray) results.get("docs");	
		System.out.println("Document2 count is: " + tempDataoneArray.length());		
		System.out.println("searched term is : " + searchTerm);
		System.out.println("query is : " + query);
		return j2;

	}

	public static String expandConcept(String searchTerm, JSONArray domains) throws JSONException{
		//http://data1.tw.rpi.edu/tomcat/VocabularyServer/ServeSparql?term=passeriformes&Submit=Submit&domain=organism&getTree=true&upward=true&level=1	
		String query = "http://data1.tw.rpi.edu/tomcat/VocabularyServer/ServeSparql?term=" + searchTerm + "&Submit=Submit" ;
		if(domains != null){
			query += "&domain=organism";
		}
		query += "&getTree=true&upward=true&level=1";
		
		System.out.println("query is : " + query);
		JSONObject j2 = (JSONObject) getRequest(query, "json");	
		System.out.println("j2 is : " + j2.toString());
		String preferredLabel = j2.get("preferredLabel").toString();
		searchTerm = searchTerm + "+OR+" + "'" + preferredLabel + "'";
		return searchTerm;
	}

	/**
	 * from the search term, return the EES corpora trained topic that term appears in
	 * calls Ben Adam's service at nceas and parses it into a solr string using disjunction
	 * @param searchTerm
	 * @return
	 * @throws JSONException
	 */
	public static String expandTopic(String searchTerm) throws JSONException{
		// http://alchemist.nceas.ucsb.edu/tmosearch/get_results_topic_expansion_ees.php?q=
		String query = "http://alchemist.nceas.ucsb.edu/tmosearch/get_results_topic_expansion_ees.php?q=" + searchTerm;
		System.out.println("query is : " + query);
		String j2 = (String) getRequest(query, "text");	
		System.out.println("j2 is : " + j2);
		String[] temp = new String[100];
		String[] temp3 = new String[100];

		temp = j2.split("\t");
		//String[] temp2 = new String[100];
		String temp2 = temp[2].toString();
		temp3 = temp2.split(" ");

		System.out.println("index 0: " + temp[0].toString());		
		System.out.println("1: " + temp[1].toString());
		System.out.println("2: " + temp[2].toString());
		System.out.println("temp3: " + temp3.toString());


		//iterate on the one forward for OR string
		String search = "";
		String search2 = "";
		for(int i = 2; i< temp3.length; i++){
			//System.out.println("j2: " + temp[2].toString());	
			if(!search.equals("") ){
				search += "+OR+";
			}
			search2 = temp3[i].replace("<br/>","");
			search +=  search2;
			//System.out.println("search: " + search);
			//System.out.println("search2: " + search2);

		}
		//JSONObject  results = j2.getJSONObject("response");	

		System.out.println("final search: " + search);
		return search;
	}

	public static JSONArray getDiff(JSONObject objVocab,JSONObject  objTopic) throws JSONException{
		System.out.println("got to getDiff**********");
		JSONObject  vocabResults = objVocab.getJSONObject("response");
		JSONArray vocabDocs = (JSONArray) vocabResults.get("docs");
		
		JSONObject  topicResults = objTopic.getJSONObject("response");
		JSONArray topicDocs = (JSONArray) topicResults.get("docs");
		
		System.out.println("vocabDocs: " + vocabDocs.toString()); 
		System.out.println("topicDocs: " + topicDocs.toString()); 
		
		JSONArray diffIds = new JSONArray();
		for(int i = 0 ; i < vocabDocs.length(); i++){

			//System.out.println("doc is : "  +  docs.get(i));
			JSONObject vocabDoc = (JSONObject) vocabDocs.get(i);
			System.out.println("got to loop getDiff**********" + " id: " + vocabDoc.get("id"));

			//System.out.println("title is : "  + doc.get("title"));
			//System.out.println("id is : "  + doc.get("id"));
			boolean match = false;
			for(int j = 0 ; j < topicDocs.length(); j++){
				JSONObject topicDoc = (JSONObject) topicDocs.get(j);
				System.out.println("comparing with" + " id: " + topicDoc.get("id"));
				if(vocabDoc.get("id").equals(topicDoc.get("id"))){
					match = true;
					break;
				}
			}
			System.out.println("first statement out of inner for loop");
			System.out.println("value of match is: " + match);

			if(match == false){
				System.out.println("no match..."); 
				diffIds.put(vocabDoc.get("id"));
			}
		}
		

		return diffIds;
	}

	@SuppressWarnings("deprecation")
	public static Object getRequest(String query, String resultType){
		System.out.println("**********getRequest");
		try{		
			System.out.println("**********getRequest2");

			//URLEncoder.encode(term, "UTF-8") 
			//String query = "https://www.googleapis.com/freebase/v1/search?query=" + URLEncoder.encode(term, "UTF-8") + "&indent=true&limit=1" + "&key=" + "AIzaSyBuu7a45hYNBYcwTC9DkeXCIHI3_o0fBd0";
			//String query = "https://cn-orc-1.dataone.org/cn/v1/query/solr/?q=keywords:*ecology*&wt=json&indent=true&rows=10000000";
			System.out.println("dataOne GET query is : " + query);
			//UrlValidator urlValidator = new UrlValidator();
			//System.out.println("valid?: " + urlValidator.isValid(query));

			//JSONObject freebaseConcept = new JSONObject();
			BufferedReader br = null;
			try{
				System.out.println("**********getRequest3");

				URL requestURL = new URL(query);
				System.out.println("requestURL : " + requestURL.toURI().toString());
				//URLConnection conn = requestURL.openConnection();
				HttpURLConnection conn = (HttpURLConnection) requestURL.openConnection();
				conn.setReadTimeout(50000);
				//String contentType = conn.getContentType();
				//System.out.println("connType : " + contentType);
				try{
					System.out.println("**********getRequest4");

					InputStream o = (InputStream)conn.getContent();
					InputStreamReader isr = new InputStreamReader(o);
					br = new BufferedReader(isr);
				}
				catch(Exception e){ e.printStackTrace();
				System.out.println("**********getRequest5");

				JSONObject error = new JSONObject();
				error.put("error", "test");
				return error;
				}
			}
			catch (UnsupportedEncodingException e) {
				System.out.println("**********getRequest6");

				e.printStackTrace();
			} 
			catch (MalformedURLException e) {
				System.out.println("**********getRequest7");

				e.printStackTrace();
			}
			String result="";
			String line;
			while((line=br.readLine())!=null) result += line;		
			System.out.println("**********getRequest8");

			System.out.println("result: " + result);
			System.out.println("**********getRequest9");

			//br.close();
			//System.out.println("full string is: " + result.toString());
			//System.out.println("(WebServiceRequestHandler) freebase result is : " + result);

			if(resultType.equals("json")){
				System.out.println("**********getRequest10");

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
				return result.toString();		

			}

		}
		catch(Exception e){
		}


		return null;
	}

}
