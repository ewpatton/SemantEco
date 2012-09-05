package edu.rpi.tw.escience.waterquality;

/**
 * An object implementing UIVisitor can be used to extend the
 * SemantAqua user interface via JavaServer Faces or by adding
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
	 * the SemantAqua UI at runtime
	 * @param ui
	 */
	void visit(SemantAquaUI ui);
}
