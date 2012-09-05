package edu.rpi.tw.escience.waterquality.datasource;
import java.util.Map;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;

import edu.rpi.tw.escience.waterquality.Module;
import edu.rpi.tw.escience.waterquality.ModuleConfiguration;
import edu.rpi.tw.escience.waterquality.Query;
import edu.rpi.tw.escience.waterquality.QueryMethod;
import edu.rpi.tw.escience.waterquality.Resource;
import edu.rpi.tw.escience.waterquality.SemantAquaUI;


public class DataSourceModule implements Module {

	private ModuleConfiguration config = null;
	
	@Override
	public void visit(Model model, Map<String, String> params) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(OntModel model, Map<String, String> params) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(Query query, Map<String, String> params) {
		
	}

	@Override
	public void visit(SemantAquaUI ui) {
		Resource res = null;
		res = config.getResource("web/data-source.js");
		ui.addScript(res);
		res = config.getResource("web/data-source.jsp");
		ui.addFacet(res);
	}

	@Override
	public String getName() {
		return "Data Source";
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

	@QueryMethod
	public String queryForDataSources(Map<String, String> params) {
		Query query = config.newQuery();
		config.getQueryExecutor().execute(query);
		return null;
	}

}
