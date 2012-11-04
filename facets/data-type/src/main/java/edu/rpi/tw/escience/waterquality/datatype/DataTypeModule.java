package edu.rpi.tw.escience.waterquality.datatype;

import java.util.List;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;

import edu.rpi.tw.escience.waterquality.Domain;
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
		Resource res = config.getResource("data-type.js");
		if(res != null) {
			ui.addScript(res);
		}
		String responseStr = "<div id=\"DataTypeFacet\" class=\"facet no-rest\">";
		List<Domain> domains = config.listDomains();
		for(Domain i : domains) {
			List<String> types = i.getDataTypes();
			for(String j : types) {
				String label = i.getDataTypeName(j);
				Resource icon = i.getDataTypeIcon(j);
				responseStr += "<input name=\"type\" type=\"checkbox\" checked=\"checked\"" +
						"value=\""+j+"\" />";
				responseStr += "<img height=\"12\" src=\""+icon.getPath()+"\" /> ";
				responseStr += label;
				responseStr += "<br />";
			}
		}
		responseStr += "</div>";
		res = config.generateStringResource(responseStr);
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
