package edu.rpi.tw.escience.WaterQualityPortal.WebService;

import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;

public interface QueryEngine {
	public void setModel(Model model);
	public Model getModel();
	public ResultSet runSelect(String query);
	public String runQuery(String query);
}
