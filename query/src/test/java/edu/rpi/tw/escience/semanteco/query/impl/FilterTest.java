package edu.rpi.tw.escience.semanteco.query.impl;

import org.junit.Test;

import edu.rpi.tw.escience.semanteco.query.impl.FilterComponentImpl;
import junit.framework.TestCase;

public class FilterTest extends TestCase {
	@Test
	public void testConstructor() {
		new FilterComponentImpl("?x != ?y");
	}
	
	@Test
	public void testGetCondition() {
		FilterComponentImpl x = new FilterComponentImpl("?x != ?y");
		assertEquals("?x != ?y", x.getCondition());
	}
	
	@Test
	public void testToString() {
		FilterComponentImpl x = new FilterComponentImpl("?x != ?y");
		assertEquals("FILTER(?x != ?y)", x.toString());
	}
	
	@Test
	public void testConstructorFailure() {
		try {
			new FilterComponentImpl(null);
			fail();
		}
		catch(Exception e) {
			
		}
	}
	
	@Test
	public void testEquals() {
		FilterComponentImpl x = new FilterComponentImpl("?x != ?y");
		FilterComponentImpl y = new FilterComponentImpl("?x != ?z");
		FilterComponentImpl z = new FilterComponentImpl("?x != ?y");
		assertFalse(x.equals(null));
		assertTrue(x.equals(x));
		assertFalse(x.equals(new Object()));
		assertTrue(x.equals(z));
		assertFalse(x.equals(y));
	}
	
	@Test
	public void testHashCode() {
		FilterComponentImpl x = new FilterComponentImpl("?x != ?y");
		assertEquals(31+"?x != ?y".hashCode(), x.hashCode());
	}
}
