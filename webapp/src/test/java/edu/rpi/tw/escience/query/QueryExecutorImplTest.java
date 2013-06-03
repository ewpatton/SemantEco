package edu.rpi.tw.escience.query;

import org.junit.Before;
import org.junit.Test;

import edu.rpi.tw.escience.semanteco.query.QueryExecutorImpl;
import edu.rpi.tw.escience.semanteco.test.TestModule;
import edu.rpi.tw.escience.semanteco.test.TestRequest;
import edu.rpi.tw.escience.semanteco.util.TestSemantEcoConfiguration;

import junit.framework.TestCase;

public class QueryExecutorImplTest extends TestCase {

	@Before
	public void setUp() {
		new TestSemantEcoConfiguration() {
			private static final long serialVersionUID = 1L;

			@Override
			public String getTripleStore() {
				return "http://was.tw.rpi.edu/virtuoso/sparql";
			}
		};
	}

	@Test
	public void testConstructors() throws CloneNotSupportedException {
		QueryExecutorImpl qe = null;
		try {
			qe = new QueryExecutorImpl(null, null);
			fail();
		} catch(IllegalArgumentException e) {
			// expected behavior
		}
		qe = new QueryExecutorImpl(new TestModule(), null);
		qe.clone();
	}

	@Test
	public void testAccept() {
		QueryExecutorImpl qe = new QueryExecutorImpl(new TestModule(), null);
		QueryExecutorImpl qe2 = (QueryExecutorImpl)qe.accept("application/json");
		assertNotSame(qe, qe2);
	}

	@Test
	public void testSetRequest() {
		QueryExecutorImpl qe = new QueryExecutorImpl(new TestModule(), null);
		qe.setRequest(new TestRequest());
	}

	@Test
	public void testSaveModel() {
		System.setProperty("edu.rpi.tw.escience.writemodel", "true");
		System.setProperty("edu.rpi.tw.escience.writemodel", "true");
	}
}
