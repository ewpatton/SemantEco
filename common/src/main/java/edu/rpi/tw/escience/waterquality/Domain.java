package edu.rpi.tw.escience.waterquality;

import java.net.URI;
import java.util.List;

public interface Domain {
	URI getUri();
	void addSource(URI sourceUri);
	void addDataType(String id, String name, Resource icon);
	void addRegulation(URI regulationUri);
	List<URI> getSources();
	List<URI> getRegulations();
	List<String> getDataTypes();
	String getDataTypeName(String id);
	Resource getDataTypeIcon(String id);
}
