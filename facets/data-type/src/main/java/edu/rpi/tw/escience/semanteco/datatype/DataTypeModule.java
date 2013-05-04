package edu.rpi.tw.escience.semanteco.datatype;

import java.util.List;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;

import edu.rpi.tw.escience.semanteco.Domain;
import edu.rpi.tw.escience.semanteco.Module;
import edu.rpi.tw.escience.semanteco.ModuleConfiguration;
import edu.rpi.tw.escience.semanteco.Request;
import edu.rpi.tw.escience.semanteco.Resource;
import edu.rpi.tw.escience.semanteco.SemantEcoUI;
import edu.rpi.tw.escience.semanteco.query.Query;

/**
 * The Data Type Module provides the necessary client-side logic to handle
 * presentation of data markers on the map. It obtains information about
 * data types via the {@link ModuleConfiguration#listDomains()} method.
 * 
 * The majority of the logic for this class is defined in META-INF/res/data-type.js.
 * 
 * @author ewpatton
 *
 */
public class DataTypeModule implements Module {

	private ModuleConfiguration config = null;
	
	@Override
	public void visit(Model model, Request request, Domain domain) {
	}

	@Override
	public void visit(OntModel model, Request request, Domain domain) {
	}

	@Override
	public void visit(Query query, Request request) {
	}

	@Override
	public void visit(SemantEcoUI ui, Request request) {
		Resource res = config.getResource("data-type.js");
		if(res != null) {
			ui.addScript(res);
		}
		final StringBuilder responseStr = new StringBuilder("<div id=\"DataTypeFacet\" class=\"facet no-rest\">");
		List<Domain> domains = config.listDomains();
		for(Domain i : domains) {
			List<String> types = i.getDataTypes();
			for(String j : types) {
				String label = i.getDataTypeName(j);
				Resource icon = i.getDataTypeIcon(j);
				responseStr.append("<input name=\"type\" type=\"checkbox\" checked=\"checked\"");
				responseStr.append("value=\"");
				responseStr.append(j);
				responseStr.append("\" />");
				responseStr.append("<img height=\"12\" src=\"");
				responseStr.append(icon.getPath());
				responseStr.append("\" /> ");
				responseStr.append(label);
				responseStr.append("<br />");
			}
		}
		responseStr.append("</div>");
		res = config.generateStringResource(responseStr.toString());
		if(res != null) {
			ui.addFacet(res);
		}
	}

	@Override
	public String getName() {
		return "Entity Type";
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
