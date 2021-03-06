package edu.rpi.tw.escience.semanteco.datasource;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;

import edu.rpi.tw.escience.semanteco.Domain;
import edu.rpi.tw.escience.semanteco.Module;
import edu.rpi.tw.escience.semanteco.ModuleConfiguration;
import edu.rpi.tw.escience.semanteco.Request;
import edu.rpi.tw.escience.semanteco.Resource;
import edu.rpi.tw.escience.semanteco.SemantEcoUI;
import edu.rpi.tw.escience.semanteco.query.Query;
import edu.rpi.tw.escience.semanteco.util.NameUtils;

/**
 * The Data Source Module provides the mechanisms for constructing the primary
 * graph of data used for populating the data model before reasoning occurs.
 * 
 * @author ewpatton
 *
 */
public class DataSourceModule implements Module {

	private ModuleConfiguration config = null;
	private Logger log = Logger.getLogger(DataSourceModule.class);
	
	/**
	 * Queries for data from the SPARQL endpoint and uses that data to
	 * populate the data model.
	 */
	@Override
	public void visit(Model model, Request request, Domain domain) {
		// data source is a UI module, it does not modify the data model at all
	}

	@Override
	public void visit(OntModel model, Request request, Domain domain) {
		// do nothing as we are simply a data provider, not an ontology provider
	}

	@Override
	public void visit(Query query, Request request) {
		// the data source module handles all of its customization when
		// constructing the data model see visit(Model, Map)
	}
	
	protected Map<String, String> order(final Map<String, String> map) {
		Map<String, String> temp = new TreeMap<String, String>();
		for(Entry<String, String> i : map.entrySet()) {
			temp.put(i.getValue(), i.getKey());
		}
		return temp;
	}

	@Override
	public void visit(SemantEcoUI ui, Request request) {
		log.trace("visit(ui)");
		// get all of the sources via the available domains
		List<Domain> domains = config.listDomains();
		Map<String, String> labelMap = new HashMap<String, String>();
		for(Domain i : domains) {
			List<URI> sources = i.getSources();
			for(URI j : sources) {
				String label = i.getLabelForSource(j);
				labelMap.put(j.toString(), label);
			}
		}
		// order the entries by label
		labelMap = order(labelMap);
		try {
			// generate facet
			final StringBuilder responseText = new StringBuilder("<div id=\"DataSourceFacet\" class=\"facet\">");
			if(labelMap.size()==0) {
				responseText.append("<i>No data sources available</i>");
			}
			else {
				for(Entry<String, String> i : labelMap.entrySet()) {
					final String label = i.getKey();
					final String uri = i.getValue();
					responseText.append("<input name=\"source\" type=\"checkbox\" checked=\"checked\" value=\"");
					responseText.append(uri);
					responseText.append("\" id=\"");
					responseText.append(NameUtils.cleanName(label));
					responseText.append("\" />");
					responseText.append("<label for=\"");
					responseText.append(NameUtils.cleanName(label));
					responseText.append("\">");
					responseText.append(label);
					responseText.append("</label>");
					responseText.append("<br />");
				}
			}
			responseText.append("</div>");
			Resource res = config.generateStringResource(responseText.toString());
			ui.addFacet(res);
		}
		catch(Exception e) {
			log.error("Unable to generate UI component", e);
		}
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
		log.trace("setModuleConfiguration");
		this.config = config;
	}

}
