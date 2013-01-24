package edu.rpi.tw.escience.semanteco;

/**
 * An object implementing UIVisitor can be used to extend the
 * SemantEco user interface via JavaServer Faces or by adding
 * additional CSS and JavaScript resources to the page.
 * 
 * @author ewpatton
 *
 */
public interface UIVisitor {
	
	/**
	 * Gets the name of the UIVisitor for
	 * provenance purposes
	 * @return
	 */
	String getName();
	
	/**
	 * Allows the UIVisitor to manipulate
	 * the SemantEco UI at runtime
	 * @param ui
	 * @param params
	 */
	void visit(SemantEcoUI ui, Request request);
}
