package test;

import org.junit.Test;

import edu.rpi.tw.escience.semanteco.query.Query;
import edu.rpi.tw.escience.semanteco.query.Variable;
import edu.rpi.tw.escience.semanteco.query.impl.OptionalComponentImpl;
import edu.rpi.tw.escience.semanteco.query.impl.VariableImpl;

import junit.framework.TestCase;

public class OptionalComponentTest extends TestCase {
	@Test
	public void testToString() {
		Variable s = new VariableImpl(Query.VAR_NS + "s");
		Variable p = new VariableImpl(Query.VAR_NS + "p");
		Variable o = new VariableImpl(Query.VAR_NS + "o");
		OptionalComponentImpl comp = new OptionalComponentImpl();
		comp.addPattern(s, p, o);
		String text = comp.toString().trim();
		text = text.replaceAll("\r", "");
		text = text.replaceAll("\n", "");
		assertEquals(text, "OPTIONAL {?s ?p ?o . }");
	}
}
