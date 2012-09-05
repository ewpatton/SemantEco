package edu.rpi.tw.escience.waterquality;

/**
 * The NamedGraphComponent is used to represent a GRAPH &lt;...&gt; { }
 * statement within a SPARQL query.
 * 
 * @author ewpatton
 *
 */
public interface NamedGraphComponent extends GraphComponentCollection {
	/**
	 * Gets the URI for this NamedGraphComponent
	 * @return
	 */
	String getURI();
}
