package edu.rpi.tw.escience.waterquality.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.rpi.tw.escience.waterquality.Domain;
import edu.rpi.tw.escience.waterquality.Resource;

public class DomainImpl implements Domain {

	private final URI uri;
	private Set<URI> sources = new LinkedHashSet<URI>();
	private Set<URI> regulations = new LinkedHashSet<URI>();
	private final class DataType {
		private String name;
		private Resource icon;
	}
	
	private Map<String, DataType> types = new LinkedHashMap<String, DataType>();
	
	public DomainImpl(final URI uri) {
		this.uri = uri;
	}
	
	@Override
	public URI getUri() {
		return uri;
	}

	@Override
	public void addSource(final URI sourceUri) {
		sources.add(sourceUri);
	}

	@Override
	public void addDataType(final String id, final String name, final Resource icon) {
		final DataType dt = new DataType();
		dt.name = name;
		dt.icon = icon;
		types.put(id, dt);
	}

	@Override
	public void addRegulation(final URI regulationUri) {
		regulations.add(regulationUri);
	}

	@Override
	public List<URI> getSources() {
		return new ArrayList<URI>(sources);
	}

	@Override
	public List<URI> getRegulations() {
		return new ArrayList<URI>(regulations);
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

}
