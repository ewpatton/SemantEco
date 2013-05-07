package test;

import org.junit.Test;

import edu.rpi.tw.escience.semanteco.query.Variable;
import edu.rpi.tw.escience.semanteco.query.impl.VariableExprImpl;

import junit.framework.TestCase;

public class VariableExprTest extends TestCase {

	@Test
	public void testGetUri() {
		Variable x = new VariableExprImpl("SUM(?count) AS ?x");
		assertNull(x.getUri());
	}

	@Test
	public void testToString() {
		final String str = "SUM(?count) AS ?x";
		Variable x = new VariableExprImpl(str);
		assertEquals("("+str+")", x.toString());
	}

	@Test
	public void testHashCode() {
		final String xStr = "SUM(?count) AS ?x";
		Variable x = new VariableExprImpl(xStr);
		Variable y = new VariableExprImpl("SUM(?count) AS ?y");
		Variable z = new VariableExprImpl(xStr);
		assertEquals(x.hashCode(), x.hashCode());
		assertEquals(x.hashCode(), z.hashCode());
		assertFalse(x.hashCode() == y.hashCode());
	}

	@Test
	public void testEquals() {
		final String xStr = "SUM(?count) AS ?x";
		Variable x = new VariableExprImpl(xStr);
		Variable y = new VariableExprImpl("SUM(?count) AS ?y");
		Variable z = new VariableExprImpl(xStr);
		Variable w = new VariableExprImpl(null);
		Variable v = new VariableExprImpl(null);
		assertTrue(x.equals(x));
		assertFalse(x.equals(null));
		assertFalse(x.equals(""));
		assertTrue(x.equals(z));
		assertFalse(x.equals(y));
		assertFalse(x.equals(w));
		assertFalse(w.equals(x));
		assertTrue(w.equals(v));
	}

	@Test
	public void testGetName() {
		Variable x = new VariableExprImpl("SUM(?count) AS ?x");
		Variable y = new VariableExprImpl("SUM(?count)");
		assertEquals("x", x.getName());
		assertNull(y.getName());
	}
}
