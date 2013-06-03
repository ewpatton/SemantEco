package edu.rpi.tw.escience.semanteco.query.impl;

import org.junit.Test;

import edu.rpi.tw.escience.semanteco.query.impl.NamedGraphComponentImpl;

import junit.framework.TestCase;

public class NamedGraphTest extends TestCase {
	@Test
	public void testConstructor() {
		new NamedGraphComponentImpl("http://example.com/testgraph");
	}
	
	@Test
	public void testGetUri() {
		NamedGraphComponentImpl x = new NamedGraphComponentImpl("http://example.com/testgraph");
		assertEquals("http://example.com/testgraph", x.getUri());
	}
}
