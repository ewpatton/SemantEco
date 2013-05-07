package edu.rpi.tw.escience.semanteco;

import org.junit.Test;

import junit.framework.TestCase;

public class HierarchyVerbTest extends TestCase {
	@Test
	public void testEnum() {
		assertNotSame(HierarchyVerb.ROOTS, HierarchyVerb.CHILDREN);
		assertNotSame(HierarchyVerb.ROOTS, HierarchyVerb.SEARCH);
		assertNotSame(HierarchyVerb.ROOTS, HierarchyVerb.COUNT_DESCENDANTS);
		assertNotSame(HierarchyVerb.ROOTS, HierarchyVerb.PATH_TO_NODE);
		assertNotSame(HierarchyVerb.CHILDREN, HierarchyVerb.SEARCH);
		assertNotSame(HierarchyVerb.CHILDREN, HierarchyVerb.COUNT_DESCENDANTS);
		assertNotSame(HierarchyVerb.CHILDREN, HierarchyVerb.PATH_TO_NODE);
		assertNotSame(HierarchyVerb.SEARCH, HierarchyVerb.COUNT_DESCENDANTS);
		assertNotSame(HierarchyVerb.SEARCH, HierarchyVerb.PATH_TO_NODE);
		assertNotSame(HierarchyVerb.COUNT_DESCENDANTS, HierarchyVerb.PATH_TO_NODE);
	}
}
