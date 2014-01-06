package edu.rpi.tw.escience.semanteco.util;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.mozilla.javascript.Context;

import edu.rpi.tw.escience.semanteco.HierarchicalMethod;
import edu.rpi.tw.escience.semanteco.HierarchyEntry;
import edu.rpi.tw.escience.semanteco.HierarchyVerb;
import edu.rpi.tw.escience.semanteco.QueryMethod;
import edu.rpi.tw.escience.semanteco.QueryMethod.HTTP;
import edu.rpi.tw.escience.semanteco.Request;
import edu.rpi.tw.escience.semanteco.test.MockModule;
import edu.rpi.tw.escience.semanteco.test.TestSemantEcoConfiguration;
import junit.framework.TestCase;

public class JavaScriptGeneratorTest extends TestCase {

	static class TestModule1 extends MockModule {
		@QueryMethod
		public String queryTester(final Request request) {
			return null;
		}
	}

	static class TestModule2 extends MockModule {
		@QueryMethod(method = HTTP.POST)
		public String queryTester(final Request request) {
			return null;
		}
	}

	static class TestModule3 extends MockModule {
		@HierarchicalMethod(parameter = "test")
		public Collection<HierarchyEntry> queryHierarchy(final Request request,
				final HierarchyVerb verb) {
			return null;
		}
	}

	static class TestModule4 extends MockModule {
		@QueryMethod
		public String queryTest1(final Request request) {
			return null;
		}

		@QueryMethod
		public String queryTest2(final Request request) {
			return null;
		}
	}

	protected void runScript(String js) {
		Context cx = Context.enter();
		try {
			cx.compileString(js, "<cmd>", 1, null);
		} catch(Exception e) {
			throw new IllegalArgumentException("JavaScript could not be evaluated successfully.", e);
		} finally {
			Context.exit();
		}
	}

	@Before
	public void setUp() {
		new TestSemantEcoConfiguration();
	}

	@Test
	public void testNoAnnotations() {
		MockModule m = new MockModule();
		String js = JavaScriptGenerator.ajaxForModule(m);
		assertEquals("", js);
	}

	@Test
	public void testQueryGET() {
		TestModule1 m = new TestModule1();
		String js = JavaScriptGenerator.ajaxForModule(m);

		assertTrue(js.contains("TestModule1"));
		// these strings should not show up for a GET QueryMethod
		assertFalse(js.contains("POST"));
		assertFalse(js.contains("mode"));

		// verify the script can be interpreted
		runScript(js);
	}

	@Test
	public void testQueryPOST() {
		TestModule2 m = new TestModule2();
		String js = JavaScriptGenerator.ajaxForModule(m);

		assertTrue(js.contains("TestModule2"));
		assertTrue(js.contains("POST"));
		assertFalse(js.contains("mode"));

		// verify the script can be interpreted
		runScript(js);
	}

	@Test
	public void testHierarchy() {
		TestModule3 m = new TestModule3();
		String js = JavaScriptGenerator.ajaxForModule(m);

		assertTrue(js.contains("TestModule3"));
		assertFalse(js.contains("POST"));
		assertTrue(js.contains("mode"));

		// verify the script can be interpreted
		runScript(js);
	}

	@Test
	public void testMultiMethods() {
		TestModule4 m = new TestModule4();
		String js = JavaScriptGenerator.ajaxForModule(m);

		assertTrue(js.contains("TestModule4"));
		assertFalse(js.contains("POST"));
		assertFalse(js.contains("mode"));
		assertTrue(js.contains("queryTest1"));
		assertTrue(js.contains("queryTest2"));

		runScript(js);
	}

	@Test
	public void testDebug() {
		TestSemantEcoConfiguration config = new TestSemantEcoConfiguration();
		config.setDebug(true);
		testMultiMethods();
	}
}
