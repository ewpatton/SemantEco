package edu.rpi.tw.escience.waterquality.test;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import junit.framework.Assert;

import com.hp.hpl.jena.rdf.model.Model;

import edu.rpi.tw.escience.waterquality.QueryExecutor;
import edu.rpi.tw.escience.waterquality.Request;
import edu.rpi.tw.escience.waterquality.query.Query;

public class TestQueryExecutor extends MockQueryExecutor {

	private Collection<String> contentTypes = null;
	private Model targetModel = null;
	private String endpoint = null;
	private String query = null;
	private Map<String, String> defaults = new TreeMap<String, String>();
	
	interface ExecEntry {
		boolean execute();
	}
	
	private List<ExecEntry> entries = new ArrayList<ExecEntry>();
	private int counter = 0;
	
	final Return runTests() {
		Return response = null;
		while(true) {
			ExecEntry entry = entries.get(counter);
			counter++;
			if(entry instanceof Return) {
				response = (Return)entry;
				break;
			}
			else if(!entry.execute()) {
				break;
			}
		}
		return response;
	}
	
	@Override
	public String execute(Query query) {
		this.query = query.toString();
		Return response = runTests();
		contentTypes = null;
		return response.getContents();
	}
	
	@Override
	public QueryExecutor execute(Query query, Model model) {
		this.query = query.toString();
		targetModel = model;
		Return response = runTests();
		contentTypes = null;
		InputStream is = response.open();
		model.read(is, "http://aquarius.tw.rpi.edu/projects/semantaqua/", "TTL");
		return this;
	}
	
	@Override
	public String execute(String endpoint, Query query) {
		this.query = query.toString();
		this.endpoint = endpoint;
		Return response = runTests();
		contentTypes = null;
		return response.getContents();
	}
	
	@Override
	public QueryExecutor execute(String endpoint, Query query, Model model) {
		this.query = query.toString();
		targetModel = model;
		this.endpoint = endpoint;
		Return response = runTests();
		contentTypes = null;
		InputStream is = response.open();
		model.read(is, "http://aquarius.tw.rpi.edu/projects/semantaqua/");
		return this;
	}
	
	@Override
	public String getDefaultSparqlEndpoint() {
		return "http://sparql.tw.rpi.edu/virtuoso/sparql";
	}
	
	@Override
	public QueryExecutor accept(String mimeType) {
		if(contentTypes == null) {
			contentTypes = new ArrayList<String>();
		}
		contentTypes.add(mimeType);
		return this;
	}
	
	@Override
	public String executeLocalQuery(Request request, Query query) {
		return "";
	}
	
	public TestQueryExecutor expect(String param, String value) {
		Expect expect = new Expect();
		expect.param = param;
		expect.value = value;
		entries.add(expect);
		return this;
	}
	
	public TestQueryExecutor andReturn(String file) {
		entries.add(new Return(file));
		return this;
	}
	
	public String readFile(InputStream is) {
		final int BUFSIZE = 1024;
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final byte[] buffer = new byte[BUFSIZE];
		int read = 0;
		try {
			while((read = is.read(buffer)) > 0) {
				baos.write(buffer, 0, read);
			}
			return baos.toString("UTF-8");
		}
		catch(Exception e) {
			throw new IllegalStateException("Could not read input data", e);
		}
		finally {
			try {
				is.close();
			}
			catch(IOException e) { }
		}
	}
	
	class Expect implements ExecEntry {

		private String param = null;
		private String value = null;
		
		@Override
		public boolean execute() {
			try {
				if(param.equals("Content-Type")) {
					if(contentTypes == null || !contentTypes.contains(value)) {
						Assert.fail("Expected Content-Type "+value);
					}
				}
				else if(param.equals("model")) {
					if((targetModel == null && value.equals("true")) ||
							(value == null && targetModel != null)) {
						Assert.fail("Expected model");
					}
				}
				else if(param.equals("endpoint")) {
					if(endpoint == null || !endpoint.equals(value)) {
						Assert.fail("Expected endpoint "+endpoint);
					}
				}
				else if(param.equals("query")) {
					String matchQuery = readFile(new FileInputStream("src/test/resources/"+value));
					if(query == null || !query.equals(matchQuery)) {
						System.err.println(query);
						Assert.fail();
					}
				}
				else {
					Assert.fail("unknown condition in Expect#execute");
				}
			}
			catch(Exception e) {
				Assert.fail(e.toString());
			}
			return true;
		}
		
	}
	
	class Return implements ExecEntry {
		private String path = null;
		
		public Return(String path) {
			this.path = path;
		}
		
		@Override
		public boolean execute() {
			return true;
		}
		
		public InputStream open() {
			try {
				return new FileInputStream("src/test/resources/"+path);
			}
			catch(Exception e) {
				return null;
			}
		}
		
		public String getContents() {
			try {
				return readFile(open());
			}
			catch(Exception e) {
				return null;
			}
		}
		
	}

	public void setDefault(String key, String value) {
		defaults.put(key, value);
	}

}
