package edu.rpi.tw.escience.semanteco.geo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;

import edu.rpi.tw.escience.semanteco.Module;
import edu.rpi.tw.escience.semanteco.ModuleConfiguration;
import edu.rpi.tw.escience.semanteco.QueryMethod;
import edu.rpi.tw.escience.semanteco.Request;
import edu.rpi.tw.escience.semanteco.Resource;
import edu.rpi.tw.escience.semanteco.SemantEcoUI;
import edu.rpi.tw.escience.semanteco.query.Query;
import edu.rpi.tw.escience.semanteco.Domain;
import edu.rpi.tw.escience.semanteco.ProvidesDomain;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.log4j.Logger;


public class SemantEcoGeoModule implements Module {

	private ModuleConfiguration config = null;
	
	@Override
	public void visit(final Model model, final Request request) {
		// TODO populate data model
	}

	@Override
	public void visit(final OntModel model, final Request request) {
		// TODO populate ontology model
	}

	@Override
	public void visit(final Query query, final Request request) {
		// TODO modify queries
	}
	
	@Override
	public void visit(final SemantEcoUI ui, final Request request) {
		// TODO add resources to display
	}

	@Override
	public String getName() {
		return "SemantEcoGeo";
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
	public void setModuleConfiguration(final ModuleConfiguration config) {
		this.config = config;
	}
	
	@QueryMethod
	public String testMethod(final Request request) {
		request.getLogger().debug("Hey you! Test 1.");
		return null;
	}
	
	public List<Domain> getDomains(final Request request) {
		List<Domain> domains = new ArrayList<Domain>();
	    Domain semantGeoDomain = config.getDomain(URI.create("http://purl.org/twc/SemantGeo/"), true);
	    // add data sources, regulations, and data types here
	    domains.add(semantGeoDomain);
	    semantGeoDomain.setLabel("SemantGeo");
		addDataSources(semantGeoDomain, request);
		addRegulations(semantGeoDomain);
		addDataTypes(semantGeoDomain);
	    return domains;
	 }
	
	protected void addDataSources(final Domain domain, final Request request) {
		// TODO query for data sources and add them here (cf {@link WaterDataProviderModule#addDataSources(Domain, Request)})
		domain.addSource(URI.create("http://sparql.tw.rpi.edu/source/darrin-fresh-water"), "Darrin Fresh Water");
	}
	
	protected void addRegulations(final Domain domain) {
		// TODO query for regulations and add them here
		//domain.addRegulation(URI.create("http://was.tw.rpi.edu/semanteco/regulations/EPA-air-regulation.owl"), "Darrin Fresh Water");
	}
	
	protected void addDataTypes(final Domain domain) {
		// change to icon names here should also occur in air-data-provider.js
		Resource res = config.getResource("clean-air.png");
		domain.addDataType("clean-air", "DFW - Temp - 1", res);
		res = config.getResource("polluted-air.png");
		domain.addDataType("polluted-air", "DFW - Temp - 2", res);
	}
}
