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
	String getName();
	void visit(SemantAquaUI ui);
}
