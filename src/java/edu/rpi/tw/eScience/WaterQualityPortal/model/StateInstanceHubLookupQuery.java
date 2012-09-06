package edu.rpi.tw.eScience.WaterQualityPortal.model;

import java.io.IOException;

public class StateInstanceHubLookupQuery extends Query {

	public StateInstanceHubLookupQuery(String id) {
		super(null);
		queryString =
			"prefix us: <http://logd.tw.rpi.edu/source/twc-rpi-edu/dataset/instance-hub-us-states-and-territories/vocab/> "+
			"prefix dc: <http://purl.org/dc/terms/> "+
			"SELECT distinct ?s WHERE { " +
			"GRAPH <http://logd.tw.rpi.edu/source/twc-rpi-edu/dataset/instance-hub-us-states-and-territories/version/2011-Oct-09> { " +
			"?s a us:State ; dc:identifier \""+id+"\" }}";
	}

	@Override
	public Object execute(String endpoint) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
