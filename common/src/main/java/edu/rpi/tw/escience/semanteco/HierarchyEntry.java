package edu.rpi.tw.escience.semanteco;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;

import org.json.JSONObject;

/**
 * HierarchyEntry encapsulates information about an entry
 * in the hierarchy so that it can be serialized between
 * the client and server.
 * 
 * @author ewpatton
 *
 */
public class HierarchyEntry {
	private Map<String, Object> contents;
	private Hashtable<String,HashSet<String>> axioms;
	
	
	public HierarchyEntry() {
		this.contents = new HashMap<String, Object>();
		this.axioms = new Hashtable<String,HashSet<String>>();
		
	}
	
	public HierarchyEntry(final URI uri, final String label) {
		this.contents = new HashMap<String, Object>();
		this.contents.put("uri", uri.toASCIIString());
		this.contents.put("label", label);
	}
	
	public HierarchyEntry(final URI uri, final URI parent, final String label) {
		this.contents = new HashMap<String, Object>();
		this.contents.put("uri", uri.toASCIIString());
		this.contents.put("parent", parent.toASCIIString());
		this.contents.put("label", label);
	}
	
	public void setAxioms(final Hashtable<String,HashSet<String>> axioms) {
		this.axioms = axioms;
		this.contents.put("axioms", new JSONObject(axioms));
	}
	
	public void setAltLabel(final String altLabel) {
		this.contents.put("altLabel", altLabel);
	}
	
	public void setUri(final URI uri) {
		this.contents.put("uri", uri.toASCIIString());
	}
	
	public void setParent(final URI parent) {
		this.contents.put("parent", parent.toASCIIString());
	}
	
	public void setLabel(final String label) {
		this.contents.put("label", label);
	}
	
	public void setField(final String field, final String value) {
		this.contents.put(field, value);
	}
	
	public String getAltLabel() {
		return (String)this.contents.get("altLabel");
	}
	public Hashtable<String,HashSet<String>> getAxioms(){
		return this.axioms;
	}
	
	public URI getUri() {
		final String uri = (String)this.contents.get("uri");
		if(uri == null) {
			return null;
		}
		else {
			return URI.create(uri);
		}
	}
	
	public String getLabel() {
		return (String)this.contents.get("label");
	}
	
	public URI getParent() {
		final String uri = (String)this.contents.get("parent");
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

	public JSONObject toJSONObject() {
		return new JSONObject(this.contents);
	}

	public void setUri(final String uri) {
		this.contents.put("uri", uri);
	}
}
