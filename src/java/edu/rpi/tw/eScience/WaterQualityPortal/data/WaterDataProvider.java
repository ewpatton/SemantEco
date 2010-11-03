package edu.rpi.tw.eScience.WaterQualityPortal.data;

import java.net.URL;
import java.util.Date;

import com.hp.hpl.jena.rdf.model.Model;

public interface WaterDataProvider {
	public void setUserSource(String county, String state, String zip);
	public Model getData();
	public Model getData(Date start, Date end);
	public String getName();
	public URL getURL();
}
