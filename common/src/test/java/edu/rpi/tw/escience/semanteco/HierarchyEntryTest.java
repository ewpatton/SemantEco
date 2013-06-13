package edu.rpi.tw.escience.semanteco;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import junit.framework.TestCase;

public class HierarchyEntryTest extends TestCase {

	@Test
	public void testConstructors() {
		final URI uri = URI.create("http://example.com/x");
		final URI parent = URI.create("http://example.com/y");
		HierarchyEntry entry;
		entry = new HierarchyEntry();
		assertNull(entry.getUri());
		assertNull(entry.getLabel());
		assertNull(entry.getParent());
		entry = new HierarchyEntry(uri, "x");
		assertEquals(uri, entry.getUri());
		assertEquals("x", entry.getLabel());
		assertNull(entry.getParent());
		entry = new HierarchyEntry(uri, parent, "x");
		assertEquals(uri, entry.getUri());
		assertEquals(parent, entry.getParent());
		assertEquals("x", entry.getLabel());
	}

	@Test
	public void testAltLabel() {
		HierarchyEntry entry = new HierarchyEntry();
		entry.setAltLabel("test");
		assertEquals("test", entry.getAltLabel());
	}

	@Test
	public void testUri() {
		final URI uri = URI.create("http://example.com/x");
		final String uri2 = "http://example.com/y";
		final String invalidUri = "C:\\failure.txt";
		HierarchyEntry entry = new HierarchyEntry();
		entry.setUri(uri);
		assertEquals(uri, entry.getUri());
		entry.setUri(uri2);
		assertEquals(URI.create(uri2), entry.getUri());
		entry.setUri(uri);
		try {
			entry.setUri(invalidUri);
			fail();
		} catch(IllegalArgumentException e) {}
		try {
			entry.setUri((String)null);
			fail();
		} catch(NullPointerException e) {}
		try {
			entry.setUri((URI)null);
			fail();
		} catch(NullPointerException e) {}
		assertEquals(uri, entry.getUri());
	}

	@Test
	public void testParent() {
		final URI parent = URI.create("http://example.com/x");
		HierarchyEntry entry = new HierarchyEntry();
		assertNull(entry.getParent());
		entry.setParent(parent);
		assertEquals(parent, entry.getParent());
	}

	@Test
	public void testLabel() {
		final String label = "test";
		HierarchyEntry entry = new HierarchyEntry();
		assertNull(entry.getLabel());
		entry.setLabel(label);
		assertEquals(label, entry.getLabel());
	}

	@Test
	public void testField() {
		HierarchyEntry entry = new HierarchyEntry();
		try {
			entry.setField("uri", "http://example.com/x");
			fail();
		} catch(IllegalArgumentException e) {}
		try {
			entry.setField("parent", "http://example.com/x");
			fail();
		} catch(IllegalArgumentException e) {}
		entry.setField("label", "test");
		assertEquals("test", entry.getLabel());
	}

	@Test
	public void testAxioms() {
		HierarchyEntry entry = new HierarchyEntry();
		assertNotNull(entry.getAxioms());
		assertEquals(0, entry.getAxioms().size());
		Map<String, Set<String>> axioms = new HashMap<String, Set<String>>();
		Set<String> subclasses = new HashSet<String>();
		subclasses.add("http://example.com/Test");
		axioms.put("subclass", subclasses);
		entry.setAxioms(axioms);
		axioms = entry.getAxioms();
		assertNotNull(axioms);
		assertEquals(1, axioms.size());
		assertTrue(axioms.containsKey("subclass"));
		subclasses = axioms.get("subclass");
		assertEquals(1, subclasses.size());
		assertTrue(subclasses.contains("http://example.com/Test"));
	}

	@Test
	public void testToString() {
		
	}

	@Test
	public void testToJSONObject() {
		
	}
}
