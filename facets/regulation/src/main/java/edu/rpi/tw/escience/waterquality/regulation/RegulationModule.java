package edu.rpi.tw.escience.waterquality.regulation;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;

import edu.rpi.tw.escience.waterquality.Module;
import edu.rpi.tw.escience.waterquality.ModuleConfiguration;
import edu.rpi.tw.escience.waterquality.QueryMethod;
import edu.rpi.tw.escience.waterquality.Request;
import edu.rpi.tw.escience.waterquality.SemantAquaUI;
import edu.rpi.tw.escience.waterquality.query.Query;
import edu.rpi.tw.escience.waterquality.query.Query.Type;

public class RegulationModule implements Module {

	private ModuleConfiguration config = null;
	
	@Override
	public void visit(Model model, Request request) {
		// we don't modify the data model at all
	}

	@Override
	public void visit(OntModel model, Request request) {
		// load the appropriate regulation ontology here
		String[] regulations = request.getParam("regulation");
		if(regulations.length > 0) {
			String regulation = regulations[0];
			model.read(regulation);
		}
	}

	@Override
	public void visit(Query query, Request request) {
		
	}

	@Override
	public void visit(SemantAquaUI ui, Request request) {
		// TODO autogen this in the future
		ui.addFacet(config.getResource("regulations.jsp"));
	}

	@Override
	public String getName() {
		return "Regulation";
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
	
	/**
	 * 
	 */
	@QueryMethod
	public String queryForPollutedSites(final Request request) {
		Query query = config.getQueryFactory().newQuery(Type.SELECT);
		
		return config.getQueryExecutor(request).accept("application/json").executeLocalQuery(request, query);
	}
	
	@QueryMethod
	public String queryForSitePollution(Request request) {
		return null;
	}
	
}
