package edu.rpi.tw.escience.waterquality.datasource;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;

import edu.rpi.tw.escience.waterquality.Module;
import edu.rpi.tw.escience.waterquality.ModuleConfiguration;
import edu.rpi.tw.escience.waterquality.QueryMethod;
import edu.rpi.tw.escience.waterquality.Request;
import edu.rpi.tw.escience.waterquality.Resource;
import edu.rpi.tw.escience.waterquality.SemantAquaUI;
import edu.rpi.tw.escience.waterquality.query.BlankNode;
import edu.rpi.tw.escience.waterquality.query.NamedGraphComponent;
import edu.rpi.tw.escience.waterquality.query.OptionalComponent;
import edu.rpi.tw.escience.waterquality.query.Query;
import edu.rpi.tw.escience.waterquality.query.QueryResource;
import edu.rpi.tw.escience.waterquality.query.Variable;
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
	private static final String LABEL_VAR = "label";
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

	@Override
	public void visit(SemantAquaUI ui, Request request) {
		log.trace("visit(ui)");
		Resource res = null;
		res = config.getResource("test.js");
		ui.addScript(res);
		try {
			String responseText = "<div id=\"DataSourceFacet\" class=\"facet\">";
			String response = queryForDataSources(request);
			log.debug("Response: "+response);
			JSONObject data = new JSONObject(response);
			if(data.getBoolean("success")) {
				JSONArray sources = (JSONArray)data.get("data");
				for(int i=0;i<sources.length();i++) {
					JSONObject mapping = sources.getJSONObject(i);
					responseText += "<input name=\"source\" type=\"checkbox\" checked=\"checked\" value=\""+mapping.getString("uri")+"\" id=\""+NameUtils.cleanName(mapping.getString(LABEL_VAR))+"\" />";
					responseText += "<label for=\""+NameUtils.cleanName(mapping.getString(LABEL_VAR))+"\">"+mapping.getString(LABEL_VAR)+"</label>";
					responseText += "<br />";
				}
			}
			else {
				responseText += "<i>No data sources available</i>";
			}
			responseText += "</div>";
			res = config.generateStringResource(responseText);
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
