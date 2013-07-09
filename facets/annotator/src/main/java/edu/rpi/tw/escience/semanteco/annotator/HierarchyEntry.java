package edu.rpi.tw.escience.semanteco.annotator;

import java.io.Serializable;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

/**
 * HierarchyEntry encapsulates information about an entry
 * in the hierarchy so that it can be serialized between
 * the client and server.
 * 
 * @author ewpatton
 *
 */
public class HierarchyEntry implements Serializable {
	private static final long serialVersionUID = -28764795460121436L;
	private static final String URI_FIELD = "uri";
	private static final String LABEL_FIELD = "label";
	private static final String COMMENT_FIELD = "comment";
	private static final String PARENT_FIELD = "parent";
	private static final String AXIOMS_FIELD = "axioms";
	private static final String ALTLABEL_FIELD = "altLabel";

	private Map<String, Object> contents;
	private Map<String, Set<String>> axioms;

	/**
	 * Creates a new, empty hierarchy entry
	 */
	public HierarchyEntry() {
		this.contents = new HashMap<String, Object>();
		this.axioms = new HashMap<String, Set<String>>();
		
	}

	/**
	 * Creates a new hierarchy entry identified by the given uri and label
	 * @param uri
	 * @param label
	 */
	public HierarchyEntry(final URI uri, final String label) {
		this.contents = new HashMap<String, Object>();
		this.contents.put(URI_FIELD, uri.toASCIIString());
		this.contents.put(LABEL_FIELD, label);
	}

	/**
	 * Creates a new hierarchy entry identified by the given uri and label under
	 * the given parent
	 * @param uri
	 * @param parent
	 * @param label
	 */
	public HierarchyEntry(final URI uri, final URI parent, final String label) {
		this.contents = new HashMap<String, Object>();
		this.contents.put(URI_FIELD, uri.toASCIIString());
		this.contents.put(PARENT_FIELD, parent.toASCIIString());
		this.contents.put(LABEL_FIELD, label);
	}

	/**
	 * Sets the collection of OWL axioms describing this entry
	 * @param axioms
	 */
	public void setAxioms(final Map<String, Set<String>> axioms) {
		this.axioms = axioms;
		this.contents.put(AXIOMS_FIELD, new JSONObject(axioms));
	}
	
	/**
	 * Sets the collection of OWL axioms describing this entry
	 * @param axioms
	 */
	public void setComment(String comment) {
		this.contents.put(COMMENT_FIELD, comment);
	}

	/**
	 * Sets an alternative label for the entry
	 * @param altLabel
	 */
	public void setAltLabel(final String altLabel) {
		this.contents.put(ALTLABEL_FIELD, altLabel);
	}

	/**
	 * Sets the URI for the entry
	 * @param uri
	 */
	public void setUri(final URI uri) {
		this.contents.put(URI_FIELD, uri.toASCIIString());
	}

	/**
	 * Sets the parent URI for the entry
	 * @param parent
	 */
	public void setParent(final URI parent) {
		this.contents.put(PARENT_FIELD, parent.toASCIIString());
	}

	/**
	 * Sets the default label for the entry
	 * @param label
	 */
	public void setLabel(final String label) {
		this.contents.put(LABEL_FIELD, label);
	}

	/**
	 * Sets an arbitrary field for the entry
	 * @param field
	 * @param value
	 * @throws IllegalArgumentException if the particular field value is
	 * reserved by the Hierarchical Method system.
	 */
	public void setField(final String field, final String value) {
		if(field.equals(URI_FIELD) || field.equals(PARENT_FIELD)) {
			throw new IllegalArgumentException("Cannot set field '"+field+"'"+
					"via the setField method.");
		}
		this.contents.put(field, value);
	}

	/**
	 * Gets the alternative label (if any) for this entry
	 * @return
	 */
	public String getAltLabel() {
		return (String)this.contents.get(ALTLABEL_FIELD);
	}
	
	/**
	 * Gets the comment for this entry
	 * @return
	 */
	public String getComment() {
		return (String)this.contents.get(COMMENT_FIELD);
	}

	/**
	 * Gets the collection of axioms for this entry (if any)
	 * @return
	 */
	public Map<String, Set<String>> getAxioms(){
		return this.axioms;
	}

	/**
	 * Gets the URI of this entry (if any)
	 * @return
	 */
	public URI getUri() {
		final String uri = (String)this.contents.get(URI_FIELD);
		if(uri == null) {
			return null;
		}
		else {
			return URI.create(uri);
		}
	}

	/**
	 * Gets the label of this entry (if any)
	 * @return
	 */
	public String getLabel() {
		return (String)this.contents.get(LABEL_FIELD);
	}

	/**
	 * Gets the parent of this entry (if any)
	 * @return
	 */
	public URI getParent() {
		final String uri = (String)this.contents.get(PARENT_FIELD);
		if(uri == null) {
			return null;
		}
		else {
			return URI.create(uri);
		}
	}

	@Override
	public String toString() {
		return new JSONObject(this.contents).toString();
	}

	/**
	 * Converts the contents of the hierarchy entry into a JSONObject for
	 * use in client-server communication.
	 * @return
	 */
	public JSONObject toJSONObject() {
		return new JSONObject(this.contents);
	}

	/**
	 * Sets the URI of the entry
	 * @param uri
	 * @throws NullPointerException if uri is null
	 * @throws IllegalArgumentException if uri does not conform to RFC 2396
	 */
	public void setUri(final String uri) {
		setUri(URI.create(uri));
	}
}
