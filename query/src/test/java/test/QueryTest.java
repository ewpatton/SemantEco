package test;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;

import edu.rpi.tw.escience.waterquality.query.NamedGraphComponent;
import edu.rpi.tw.escience.waterquality.query.Query;
import edu.rpi.tw.escience.waterquality.query.QueryResource;
import edu.rpi.tw.escience.waterquality.query.Variable;
import edu.rpi.tw.escience.waterquality.query.Query.Type;
import edu.rpi.tw.escience.waterquality.query.impl.FilterComponentImpl;
import edu.rpi.tw.escience.waterquality.query.impl.GraphPatternImpl;
import edu.rpi.tw.escience.waterquality.query.impl.QueryImpl;
import edu.rpi.tw.escience.waterquality.query.impl.VariableImpl;

import junit.framework.TestCase;

public class QueryTest extends TestCase {
	
	@Test
	public void testConstructor() {
		new QueryImpl();
		new QueryImpl(Type.CONSTRUCT);
		new QueryImpl(Type.ASK);
		new QueryImpl(Type.DESCRIBE);
		try {
			new QueryImpl(null);
			fail();
		}
		catch(Exception e) {
			
		}
	}
	
	@Test
	public void testAddPattern() {
		QueryImpl x = new QueryImpl();
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
	public void testAddFilter() {
		QueryImpl x = new QueryImpl();
		FilterComponentImpl filter = new FilterComponentImpl("?x != ?y");
		x.addFilter("?x != ?y");
		assertEquals(1, x.getComponents().size());
		assertEquals(filter, x.getComponents().get(0));
	}
	
	@Test
	public void testAddGraphComponent() {
		QueryImpl x = new QueryImpl();
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
	public void testGetConstructClause() {
		QueryImpl x = new QueryImpl();
		assertNull(x.getConstructComponent());
		x.setType(Type.CONSTRUCT);
		assertNotNull(x.getConstructComponent());
	}
	
	@Test
	public void testNamedGraphs() {
		QueryImpl x = new QueryImpl();
		NamedGraphComponent graph = x.getNamedGraph("http://example.com/testgraph");
		NamedGraphComponent graph2 = x.getNamedGraph("http://example.com/testgraph");
		assertTrue(graph == graph2);
	}
	
	@Test
	public void testUnionGraph() {
		Query x = new QueryImpl();
		x.createUnion();
	}
	
	@Test
	public void testOptionalGraph() {
		Query x = new QueryImpl();
		x.createOptional();
	}
	
	@Test
	public void testVariables() {
		Query x = new QueryImpl();
		x.createVariable(Query.VAR_NS+"s");
		x.getVariable(Query.VAR_NS+"s");
		x.getVariable(Query.VAR_NS+"p");
		try {
			x.createVariable(Query.VAR_NS+"s");
			fail();
		}
		catch(Exception e) {
			
		}
	}
	
	@Test
	public void testBlankNodes() {
		Query x = new QueryImpl();
		x.createBlankNode();
	}
	
	@Test
	public void testSetType() {
		QueryImpl x = new QueryImpl();
		x.setType(Type.ASK);
		assertEquals(Type.ASK, x.getType());
		x.setType(Type.CONSTRUCT);
		x.setType(Type.CONSTRUCT);
		try {
			x.setType(null);
			fail();
		}
		catch(Exception e) {
			
		}
	}
	
	@Test
	public void testAddFrom() {
		Query x = new QueryImpl();
		x.addFrom("http://example.com/testgraph");
		x.addFromNamed("http://example.com/testgraph2");
		Set<String> graphs = x.getFrom();
		assertEquals(1, graphs.size());
		assertTrue(graphs.contains("http://example.com/testgraph"));
		graphs = x.getFromNamed();
		assertEquals(1, graphs.size());
		assertTrue(graphs.contains("http://example.com/testgraph2"));
	}
	
	@Test
	public void testSetVariables() {
		Variable s, o;
		s = new VariableImpl(Query.VAR_NS+"s");
		o = new VariableImpl(Query.VAR_NS+"o");
		Query x = new QueryImpl();
		x.setVariables(null);
		assertNull(x.getVariables());
		Set<Variable> vars = new HashSet<Variable>();
		vars.add(s);
		vars.add(o);
		x.setVariables(vars);
		assertEquals(vars, x.getVariables());
	}
	
	@Test
	public void testResources() {
		Query x = new QueryImpl();
		QueryResource a,b,c;
		a = x.getResource("http://example.com/a");
		b = x.getResource("http://example.com/b");
		c = x.getResource("http://example.com/a");
		assertEquals(a, a);
		assertEquals(a, c);
		assertFalse(a.equals(b));
	}
	
	@Test
	public void testToStringSelect() {
		Query x = new QueryImpl();
		x.setVariables(null);
		Variable s,p,o;
		s = x.getVariable(Query.VAR_NS+"s");
		p = x.getVariable(Query.VAR_NS+"p");
		o = x.getVariable(Query.VAR_NS+"o");
		x.addPattern(s, p, o);
		x.toString();
		Set<Variable> temp = new HashSet<Variable>();
		x.setVariables(temp);
		x.toString();
		temp.add(s);
		x.setVariables(temp);
		x.toString();
		x.addFrom("http://example.com/default");
		x.addFromNamed("http://example.com/nameduri");
		System.out.println(x.toString());
	}
	
	@Test
	public void testToStringConstruct() {
		Query x = new QueryImpl();
		x.setType(Type.CONSTRUCT);
		x.toString();
	}
	
	@Test
	public void testToStringAsk() {
		Query x = new QueryImpl();
		x.setType(Type.ASK);
		x.toString();
	}
	
	@Test
	public void testToStringDescribe() {
		Query x = new QueryImpl();
		x.setType(Type.DESCRIBE);
		x.toString();
	}
	
	@Test
	public void testEquals() {
		Query x,y;
		x = new QueryImpl();
		y = new QueryImpl();
		
		// test this == other
		assertTrue(x.equals(x));
		
		// test other == null
		assertFalse(x.equals(null));
		
		// test different classes
		assertFalse(x.equals(new Object()));
		
		// test different objects yet equal
		assertTrue(x.equals(y));
		y.setType(Type.ASK);
		
		// test different types
		assertFalse(x.equals(y));
		
		// test construct clauses different
		y.setType(Type.CONSTRUCT);
		assertFalse(x.equals(y));
		assertFalse(y.equals(x));
		
		// test resources different
		y = new QueryImpl();
		Set<Variable> temp = new HashSet<Variable>();
		temp.add(y.createVariable(Query.VAR_NS+"s"));
		assertFalse(x.equals(y));
		
		// test variables different
		y = new QueryImpl();
		y.setVariables(temp);
		assertFalse(x.equals(y));
		
		// test fromList different
		y = new QueryImpl();
		y.addFrom("http://example.com/testgraph");
		assertFalse(x.equals(y));
		
		// test fromNamedList different
		y = new QueryImpl();
		y.addFromNamed("http://example.com/testgraph");
		assertFalse(x.equals(y));
		
		// test named graphs different
		y = new QueryImpl();
		y.getNamedGraph("http://example.com/testgraph");
		assertFalse(x.equals(y));
		
		// test where clauses different
		y = new QueryImpl();
		y.addFilter("?x != ?y");
		assertFalse(x.equals(y));
		
	}
	
	@Test
	public void testHashCode() {
		Query x;
		x = new QueryImpl();
		x.hashCode();
		x.setType(Type.CONSTRUCT);
		Set<Variable> vars = new HashSet<Variable>();
		VariableImpl y = new VariableImpl(Query.VAR_NS+"y");
		vars.add(y);
		x.setVariables(vars);
		x.hashCode();
	}
}
