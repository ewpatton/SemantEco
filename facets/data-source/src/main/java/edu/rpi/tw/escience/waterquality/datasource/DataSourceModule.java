package edu.rpi.tw.escience.waterquality.datasource;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;

import edu.rpi.tw.escience.waterquality.Domain;
import edu.rpi.tw.escience.waterquality.Module;
import edu.rpi.tw.escience.waterquality.ModuleConfiguration;
import edu.rpi.tw.escience.waterquality.Request;
import edu.rpi.tw.escience.waterquality.Resource;
import edu.rpi.tw.escience.waterquality.SemantAquaUI;
import edu.rpi.tw.escience.waterquality.query.Query;
import edu.rpi.tw.escience.waterquality.util.NameUtils;

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
	public void visit(Model model, Request request) {
		// data source is a UI module, it does not modify the data model at all
	}

	@Override
	public void visit(OntModel model, Request request) {
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
	public void visit(SemantAquaUI ui, Request request) {
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
			String responseText = "<div id=\"DataSourceFacet\" class=\"facet\">";
			if(labelMap.size()==0) {
				responseText += "<i>No data sources available</i>";
			}
			else {
				for(Entry<String, String> i : labelMap.entrySet()) {
					final String label = i.getKey();
					final String uri = i.getValue();
					responseText += "<input name=\"source\" type=\"checkbox\" checked=\"checked\" value=\""+uri+"\" id=\""+NameUtils.cleanName(label)+"\" />";
					responseText += "<label for=\""+NameUtils.cleanName(label)+"\">"+label+"</label>";
					responseText += "<br />";
				}
			}
			responseText += "</div>";
			Resource res = config.generateStringResource(responseText);
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
