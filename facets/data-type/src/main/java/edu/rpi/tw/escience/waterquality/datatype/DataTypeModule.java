package edu.rpi.tw.escience.waterquality.datatype;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;

import edu.rpi.tw.escience.waterquality.Module;
import edu.rpi.tw.escience.waterquality.ModuleConfiguration;
import edu.rpi.tw.escience.waterquality.Request;
import edu.rpi.tw.escience.waterquality.Resource;
import edu.rpi.tw.escience.waterquality.SemantAquaUI;
import edu.rpi.tw.escience.waterquality.query.Query;

public class DataTypeModule implements Module {

	private ModuleConfiguration config = null;
	
	@Override
	public void visit(Model model, Request request) {
	}

	@Override
	public void visit(OntModel model, Request request) {
	}

	@Override
	public void visit(Query query, Request request) {
	}

	@Override
	public void visit(SemantAquaUI ui, Request request) {
		Resource res = config.getResource("data-type.css");
		if(res != null) {
			ui.addStylesheet(res);
		}
		res = config.getResource("data-type.js");
		if(res != null) {
			ui.addScript(res);
		}
		res = config.getResource("data-type.jsp");
		if(res != null) {
			ui.addFacet(res);
		}
	}

	@Override
	public String getName() {
		return "Icon Type";
	}

	@Override
	public int getMajorVersion() {
		return 1;
	}

	@Override
	public int getMinorVersion() {
		return 0;
	}

	@Override
	public String getExtraVersion() {
		return null;
	}

	@Override
	public void setModuleConfiguration(ModuleConfiguration config) {
		this.config = config;
	}

}
