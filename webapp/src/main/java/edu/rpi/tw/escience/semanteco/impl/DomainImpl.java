package edu.rpi.tw.escience.semanteco.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.rpi.tw.escience.semanteco.Domain;
import edu.rpi.tw.escience.semanteco.Resource;

/**
 * DomainImpl provides the default implementation of Domain
 * used by ModuleConfigurationImpl.
 * @author ewpatton
 *
 */
public class DomainImpl implements Domain {

	private String label = null;
	private final URI uri;
	private Map<URI, String> sources = new LinkedHashMap<URI, String>();
	private Map<URI, String> regulations = new LinkedHashMap<URI, String>();
	private static final class DataType {
		private String name;
		private Resource icon;
	}
	
	private Map<String, DataType> types = new LinkedHashMap<String, DataType>();
	
	/**
	 * Create a DomainImpl identified by the specified URI.
	 * @param uri URI used to name the domain.
	 */
	public DomainImpl(final URI uri) {
		this.uri = uri;
	}
	
	@Override
	public URI getUri() {
		return uri;
	}

	@Override
	public void addSource(final URI sourceUri, final String label) {
		if(uri == null) {
			throw new IllegalArgumentException("Data source uri cannot be null");
		}
		if(label == null) {
			throw new IllegalArgumentException("Data source label cannot be null");
		}
		sources.put(sourceUri, label);
	}

	@Override
	public void addDataType(final String id, final String name, final Resource icon) {
		final DataType dt = new DataType();
		dt.name = name;
		dt.icon = icon;
		types.put(id, dt);
	}

	@Override
	public void addRegulation(final URI regulationUri, final String label) {
		regulations.put(regulationUri, label);
	}

	@Override
	public List<URI> getSources() {
		return new ArrayList<URI>(sources.keySet());
	}

	@Override
	public List<URI> getRegulations() {
		return new ArrayList<URI>(regulations.keySet());
	}

	@Override
	public List<String> getDataTypes() {
		return new ArrayList<String>(types.keySet());
	}

	@Override
	public String getDataTypeName(final String id) {
		final DataType dt = types.get(id);
		if(dt == null) {
			return null;
		}
		return dt.name;
	}

	@Override
	public Resource getDataTypeIcon(final String id) {
		final DataType dt = types.get(id);
		if(dt == null) {
			return null;
		}
		return dt.icon;
	}

	@Override
	public String getLabelForSource(final URI uri) {
		return sources.get(uri);
	}
	
	@Override
	public String getLabelForRegulation(final URI uri) {
		return regulations.get(uri);
	}

	@Override
	public String getLabel() {
		return label;
	}
	
	@Override
	public void setLabel(String label) {
		this.label = label;
	}

}
