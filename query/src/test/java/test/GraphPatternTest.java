package test;

import org.junit.Test;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;

import edu.rpi.tw.escience.semanteco.query.impl.GraphPatternImpl;
import edu.rpi.tw.escience.semanteco.query.impl.VariableImpl;
import edu.rpi.tw.escience.semanteco.query.Query;
import edu.rpi.tw.escience.semanteco.query.Variable;

import junit.framework.TestCase;

public class GraphPatternTest extends TestCase {
	@Test
	public void testConstructors() {
		Variable s,p,o;
		s = new VariableImpl(Query.VAR_NS+"s");
		p = new VariableImpl(Query.VAR_NS+"p");
		o = new VariableImpl(Query.VAR_NS+"o");
		new GraphPatternImpl(s, p, o);
		new GraphPatternImpl(s, p, "test", null);
		new GraphPatternImpl(s, p, "test", XSDDatatype.XSDstring);
	}
	
	@Test
	public void testSubjectSetter() {
		Variable s,p,o,y;
		s = new VariableImpl(Query.VAR_NS+"s");
		y = new VariableImpl(Query.VAR_NS+"y");
		p = new VariableImpl(Query.VAR_NS+"p");
		o = new VariableImpl(Query.VAR_NS+"o");
		GraphPatternImpl x = new GraphPatternImpl(s, p, o);
		x.setSubject(y);
		assertEquals(y, x.getSubject());
	}
	
	@Test
	public void testPredicateSetter() {
		Variable s,p,o,y;
		s = new VariableImpl(Query.VAR_NS+"s");
		y = new VariableImpl(Query.VAR_NS+"y");
		p = new VariableImpl(Query.VAR_NS+"p");
		o = new VariableImpl(Query.VAR_NS+"o");
		GraphPatternImpl x = new GraphPatternImpl(s, p, o);
		x.setPredicate(y);
		assertEquals(y, x.getPredicate());
	}
	
	@Test
	public void testObjectSetter() {
		Variable s,p,o,y;
		s = new VariableImpl(Query.VAR_NS+"s");
		y = new VariableImpl(Query.VAR_NS+"y");
		p = new VariableImpl(Query.VAR_NS+"p");
		o = new VariableImpl(Query.VAR_NS+"o");
		GraphPatternImpl x = new GraphPatternImpl(s, p, o);
		x.setObject(y);
		assertEquals(y, x.getObject());
	}
	
	@Test
	public void testValueSetter() {
		Variable s,p;
		s = new VariableImpl(Query.VAR_NS+"s");
		p = new VariableImpl(Query.VAR_NS+"p");
		GraphPatternImpl x = new GraphPatternImpl(s, p, "test", null);
		x.setObject("123", XSDDatatype.XSDint);
		assertEquals("123", x.getValue());
		assertEquals(XSDDatatype.XSDint, x.getValueType());
	}
	
	@Test
	public void testToString() {
		Variable s,p,o;
		s = new VariableImpl(Query.VAR_NS+"s");
		p = new VariableImpl(Query.VAR_NS+"p");
		o = new VariableImpl(Query.VAR_NS+"o");
		GraphPatternImpl x = new GraphPatternImpl(s, p, o);
		assertEquals("?s ?p ?o . ", x.toString());
		x.setTransitive(true);
		assertEquals("?s ?p ?o option(transitive, t_distinct, t_no_cycles) . ", x.toString());
		x.setTransitive(false);
		x.setObject("123", null);
		assertEquals("?s ?p \"123\" . ", x.toString());
		x.setObject("123", XSDDatatype.XSDint);
		assertEquals("?s ?p \"123\"^^xsd:int . ", x.toString());
		x.setObject("long \"string\" with quotes", XSDDatatype.XSDstring);
		assertEquals("?s ?p \"\"\"long \"string\" with quotes\"\"\"^^xsd:string . ",
				x.toString());
	}
	
	@Test
	public void testEquals() {
		Variable s1,p1,o1,s2,p2,o2;
		s1 = new VariableImpl(Query.VAR_NS+"s1");
		p1 = new VariableImpl(Query.VAR_NS+"p1");
		o1 = new VariableImpl(Query.VAR_NS+"o1");
		s2 = new VariableImpl(Query.VAR_NS+"s2");
		p2 = new VariableImpl(Query.VAR_NS+"p2");
		o2 = new VariableImpl(Query.VAR_NS+"o2");
		GraphPatternImpl s1p1o1 = new GraphPatternImpl(s1, p1, o1);
		GraphPatternImpl s2p1o1 = new GraphPatternImpl(s2, p1, o1);
		GraphPatternImpl s1p2o1 = new GraphPatternImpl(s1, p2, o1);
		GraphPatternImpl s1p1o2 = new GraphPatternImpl(s1, p1, o2);
		GraphPatternImpl s1p1v1a = new GraphPatternImpl(s1, p1, "123", null);
		GraphPatternImpl s1p1v1b = new GraphPatternImpl(s1, p1, "123", null);
		GraphPatternImpl s1p1v2a = new GraphPatternImpl(s1, p1, "456", null);
		GraphPatternImpl s1p1v2b = new GraphPatternImpl(s1, p1, "123", XSDDatatype.XSDstring);
		assertTrue(s1p1o1.equals(s1p1o1));
		assertFalse(s1p1o1.equals(null));
		assertFalse(s1p1o1.equals(new Object()));
		assertFalse(s1p1o1.equals(s2p1o1));
		assertFalse(s1p1o1.equals(s1p2o1));
		assertFalse(s1p1o1.equals(s1p1o2));
		assertTrue(s1p1v1a.equals(s1p1v1b));
		assertFalse(s1p1v1a.equals(s1p1v2a));
		assertFalse(s1p1v1a.equals(s1p1v2b));
	}
	
	@Test
	public void testHashCode() {
		Variable s1,p1,o1;
		s1 = new VariableImpl(Query.VAR_NS+"s1");
		p1 = new VariableImpl(Query.VAR_NS+"p1");
		o1 = new VariableImpl(Query.VAR_NS+"o1");
		GraphPatternImpl s1p1o1 = new GraphPatternImpl(s1, p1, o1);
		GraphPatternImpl s0p0v1 = new GraphPatternImpl(null, null, "123", XSDDatatype.XSDint);
		s1p1o1.hashCode();
		s0p0v1.hashCode();
	}

	@Test
	public void testTransitive() {
		Variable s1,p1,o1;
		s1 = new VariableImpl(Query.VAR_NS+"s1");
		p1 = new VariableImpl(Query.VAR_NS+"p1");
		o1 = new VariableImpl(Query.VAR_NS+"o1");
		GraphPatternImpl x = new GraphPatternImpl(s1, p1, o1, true);
		assertTrue(x.isTransitive());
		x.setTransitive(false);
		assertFalse(x.isTransitive());
		x.setTransitive(true);
		assertTrue(x.isTransitive());
	}
}
