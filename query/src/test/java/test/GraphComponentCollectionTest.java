package test;

import org.junit.Test;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;

import edu.rpi.tw.escience.waterquality.query.GraphComponentCollection;
import edu.rpi.tw.escience.waterquality.query.Query;
import edu.rpi.tw.escience.waterquality.query.Variable;
import edu.rpi.tw.escience.waterquality.query.impl.FilterComponentImpl;
import edu.rpi.tw.escience.waterquality.query.impl.GraphComponentCollectionImpl;
import edu.rpi.tw.escience.waterquality.query.impl.GraphPatternImpl;
import edu.rpi.tw.escience.waterquality.query.impl.VariableImpl;
import junit.framework.TestCase;

public class GraphComponentCollectionTest extends TestCase {
	@Test
	public void testConstructor() {
		new GraphComponentCollectionImpl();
	}
	
	@Test
	public void testAddPattern() {
		GraphComponentCollectionImpl x = new GraphComponentCollectionImpl();
		GraphPatternImpl a,b;
		Variable s,p,o;
		s = new VariableImpl(Query.VAR_NS+"s");
		p = new VariableImpl(Query.VAR_NS+"p");
		o = new VariableImpl(Query.VAR_NS+"o");
		a = new GraphPatternImpl(s, p, o);
		b = new GraphPatternImpl(s, p, "test", XSDDatatype.XSDstring);
		x.addPattern(s, p, o);
		assertEquals(1, x.getComponents().size());
		assertEquals(a, x.getComponents().get(0));
		x.addPattern(s, p, "test", XSDDatatype.XSDstring);
		assertEquals(2, x.getComponents().size());
		assertEquals(b, x.getComponents().get(1));
	}
	
	@Test
	public void testAddComponent() {
		GraphComponentCollectionImpl x = new GraphComponentCollectionImpl();
		GraphPatternImpl a;
		Variable s,p,o;
		s = new VariableImpl(Query.VAR_NS+"s");
		p = new VariableImpl(Query.VAR_NS+"p");
		o = new VariableImpl(Query.VAR_NS+"o");
		a = new GraphPatternImpl(s, p, o);
		x.addGraphComponent(a);
		assertEquals(1, x.getComponents().size());
		assertEquals(a, x.getComponents().get(0));
	}
	
	@Test
	public void testAddFilter() {
		GraphComponentCollectionImpl x = new GraphComponentCollectionImpl();
		FilterComponentImpl filter = new FilterComponentImpl("?x != ?y");
		x.addFilter("?x != ?y");
		assertEquals(1, x.getComponents().size());
		assertEquals(filter, x.getComponents().get(0));
	}
	
	@Test
	public void testEquals() {
		GraphComponentCollection x,y;
		x = new GraphComponentCollectionImpl();
		y = new GraphComponentCollectionImpl();
		assertTrue(x.equals(x));
		assertFalse(x.equals(null));
		assertFalse(x.equals(new Object()));
		assertTrue(x.equals(y));
		x.addFilter("?x != ?y");
		assertFalse(x.equals(y));
		assertFalse(y.equals(x));
	}
	
	@Test
	public void testHashCode() {
		GraphComponentCollection x;
		x = new GraphComponentCollectionImpl();
		x.hashCode();
	}
}
