package edu.rpi.tw.escience.semanteco;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * HierarchyEntry encapsulates information about an entry
 * in the hierarchy so that it can be serialized between
 * the client and server.
 * 
 * @author ewpatton
 *
 */
public class HierarchyEntry {
	private Map<String, String> contents;
	
	public HierarchyEntry() {
		this.contents = new HashMap<String, String>();
	}
	
	public HierarchyEntry(final URI uri, final String label) {
		this.contents = new HashMap<String, String>();
		this.contents.put("uri", uri.toASCIIString());
		this.contents.put("label", label);
	}
	
	public HierarchyEntry(final URI uri, final URI parent, final String label) {
		this.contents = new HashMap<String, String>();
		this.contents.put("uri", uri.toASCIIString());
		this.contents.put("parent", parent.toASCIIString());
		this.contents.put("label", label);
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
		return this.contents.get("altLabel");
	}
	
	public URI getUri() {
		final String uri = this.contents.get("uri");
		if(uri == null) {
			return null;
		}
		else {
			return URI.create(uri);
		}
	}
	
	public String getLabel() {
		return this.contents.get("label");
	}
	
	public URI getParent() {
		final String uri = this.contents.get("parent");
		if(uri == null) {
			return null;
		}
		else {
			return URI.create(uri);
		}
	}
}
