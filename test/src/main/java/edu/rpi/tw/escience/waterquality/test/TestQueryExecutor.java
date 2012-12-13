package edu.rpi.tw.escience.waterquality.test;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import junit.framework.Assert;

import com.hp.hpl.jena.rdf.model.Model;

import edu.rpi.tw.escience.waterquality.QueryExecutor;
import edu.rpi.tw.escience.waterquality.query.Query;

/**
 * TestQueryExecutor provides a mechanism for testing how modules
 * interact with the QueryExecutor and how they handle various
 * response strings.
 * @author ewpatton
 *
 */
public class TestQueryExecutor extends MockQueryExecutor {

	final Logger log = Logger.getLogger(TestQueryExecutor.class);
	
	private Collection<String> contentTypes = null;
	private Model targetModel = null;
	private String endpoint = null;
	private String query = null;
	private Map<String, String> defaults = new TreeMap<String, String>();
	
	/**
	 * Interface for an execution entry
	 * @author ewpatton
	 *
	 */
	interface ExecEntry {
		/**
		 * Executes the entry against the current state
		 * of the TestQueryExecutor and the expected/default
		 * values for different properties.
		 * @return true if the test was successful, false otherwise.
		 */
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
	public String executeLocalQuery(Query query) {
		return "";
	}
	
	/**
	 * Tells the QueryExecutor to expect a particular value
	 * from the target module for a specific parameter
	 * @param param A parameter specified as part of a query execution:
	 * <ul>
	 * <li>Content-Type: Checks that the specified content type is in the set provided by the module</li>
	 * <li>model: Checks that the module provided a model (true) vs a string (false)</li>
	 * <li>endpoint: Checks URI of the endpoint being queried</li>
	 * <li>query: Checks the serialization of a Query object against the contents of a file</li>
	 * </ul>
	 * @param value The expected value for the parameter
	 * @return The TestQueryExecutor for chaining calls
	 */
	public TestQueryExecutor expect(String param, String value) {
		Expect expect = new Expect();
		expect.param = param;
		expect.value = value;
		entries.add(expect);
		return this;
	}
	
	/**
	 * Returns the contents of the specified file as the response
	 * to a query execution by the target module.
	 * @param file File containing a valid SPARQL response for the tested query
	 * @return The TestQueryExecutor for chaining calls
	 */
	public TestQueryExecutor andReturn(String file) {
		entries.add(new Return(file));
		return this;
	}
	
	protected final String readFile(InputStream is) {
		final int bufsize = 1024;
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final byte[] buffer = new byte[bufsize];
		int read = 0;
		try {
			while((read = is.read(buffer)) > 0) {
				baos.write(buffer, 0, read);
			}
			return baos.toString("UTF-8");
		}
		catch(IOException e) {
			throw new IllegalStateException("Could not read input data", e);
		}
		finally {
			try {
				is.close();
			}
			catch(IOException e) {
				log.warn("Unable to close stream due to exception", e);
			}
		}
	}
	
	class Expect implements ExecEntry {

		private String param = null;
		private String value = null;
		
		protected final void checkContentType() {
			if(contentTypes == null || !contentTypes.contains(value)) {
				Assert.fail("Expected Content-Type "+value);
			}
		}
		
		protected final void checkModel() {
			if((targetModel == null && value.equals("true")) ||
					(value == null && targetModel != null)) {
				Assert.fail("Expected model");
			}
		}
		
		protected final void checkEndpoint() {
			if(endpoint == null || !endpoint.equals(value)) {
				Assert.fail("Expected endpoint "+endpoint);
			}
		}
		
		protected final void checkQuery() throws FileNotFoundException {
			String matchQuery = readFile(new FileInputStream("src/test/resources/"+value));
			String temp = query;
			
			// correct for platform-dependent line endings
			if(temp != null) {
			    temp = temp.replaceAll("\r\n", "\n");
			    temp = temp.replaceAll("\r", "\n");
			}
			matchQuery = matchQuery.replaceAll("\r\n", "\n");
			matchQuery = matchQuery.replaceAll("\r", "\n");
			
			if(temp == null || !temp.equals(matchQuery)) {
				Assert.fail();
			}
		}
		
		@Override
		public boolean execute() {
			try {
				if(param.equals("Content-Type")) {
					checkContentType();
				}
				else if(param.equals("model")) {
					checkModel();
				}
				else if(param.equals("endpoint")) {
					checkEndpoint();
				}
				else if(param.equals("query")) {
					checkQuery();
				}
				else {
					Assert.fail("unknown condition in Expect#execute");
				}
			}
			catch(IOException e) {
				Assert.fail(e.toString());
			}
			catch(RuntimeException e) {
				Assert.fail(e.toString());
			}
			return true;
		}
		
	}
	
	class Return implements ExecEntry {
		private String path = null;
		
		/**
		 * Constructs a return object that will load the contents
		 * of the specified path.
		 * @param path
		 */
		public Return(String path) {
			this.path = path;
		}
		
		@Override
		public boolean execute() {
			return true;
		}
		
		/**
		 * Opens the path encapsulated by this Return object
		 * and returns an input stream
		 * @return An InputStream for {@link #path}
		 */
		public InputStream open() {
			try {
				return new FileInputStream("src/test/resources/"+path);
			}
			catch(Exception e) {
				return null;
			}
		}
		
		/**
		 * Opens the path encapsulated by this Return object
		 * and reads its contents as a java.lang.String
		 * @return Contents of the file at {@link #path}
		 */
		public String getContents() {
			try {
				return readFile(open());
			}
			catch(Exception e) {
				return null;
			}
		}
		
	}

	/**
	 * Sets a default value for the specified key.
	 * @param key see {@link TestQueryExecutor#expect(String, String)} 
	 * @param value An appropriate value for the particular key.
	 */
	public void setDefault(String key, String value) {
		defaults.put(key, value);
	}

}
