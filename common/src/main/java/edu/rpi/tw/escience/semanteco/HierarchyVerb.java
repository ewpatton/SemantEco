package edu.rpi.tw.escience.semanteco;

/**
 * HierarchyVerb enumerates the different actions that
 * can occur in a hierarchical facet that require
 * client-server communication.
 * 
 * @author ewpatton
 * @see HierarchicalMethod
 *
 */
public enum HierarchyVerb {
	ROOTS,
	CHILDREN,
	SEARCH,
	COUNT_DESCENDANTS
}
