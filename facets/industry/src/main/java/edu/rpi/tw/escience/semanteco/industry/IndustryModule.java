package edu.rpi.tw.escience.semanteco.industry;

import java.util.List;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;

import edu.rpi.tw.escience.waterquality.Module;
import edu.rpi.tw.escience.waterquality.ModuleConfiguration;
import edu.rpi.tw.escience.waterquality.Request;
import edu.rpi.tw.escience.waterquality.SemantAquaUI;
import edu.rpi.tw.escience.waterquality.query.GraphComponentCollection;
import edu.rpi.tw.escience.waterquality.query.OptionalComponent;
import edu.rpi.tw.escience.waterquality.query.Query;
import edu.rpi.tw.escience.waterquality.query.Query.Type;
import edu.rpi.tw.escience.waterquality.query.QueryResource;
import edu.rpi.tw.escience.waterquality.query.Variable;

import static edu.rpi.tw.escience.waterquality.query.Query.RDF_NS;
import static edu.rpi.tw.escience.waterquality.query.Query.VAR_NS;

/**
 * The Industry module provides a mechanism for users to select subsets of
 * facilities based on an industry using the North American Industry
 * Classification System (NAICS). It modifies SPARQL CONSTRUCT queries
 * in its {@link #visit(Query, Request)} method by adding a triple assertion
 * in the form of ?site pol:hasNAICS ?naics FILTER(regex("^{x}", ?naics))
 * where {x} is the industry component of the NAICS code.
 * 
 * @author ewpatton
 *
 */
public class IndustryModule implements Module {

	private static final String NAICS_VAR = "naics";
	private static final String POL_NS = "http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#";
	private ModuleConfiguration config = null;
	
	@Override
	public void visit(Model model, Request request) {

	}

	@Override
	public void visit(OntModel model, Request request) {
		
	}

	/**
	 * Converts the NAICS component sent by the client into a regular expression
	 * statement that can be used as part of a SPARQL regular expression.
	 * @param code A code expression in the form of 11,31-33 (- is a range and , separates ranges)
	 * @return A regulation expression representing the values specified in the code
	 */
	protected static String code2RegExp(final String code) {
		// 11,31-33 becomes "^(11|31|32|33)"
		final StringBuilder sb = new StringBuilder();
		sb.append("\"^(");
		String[] parts = code.split(",");
		for(int i=0;i<parts.length;i++) {
			String[] parts2 = parts[i].split("-");
			for(int j=0;j<parts2.length;i++) {
				if(parts2[i].length() != 2) {
					continue;
				}
				sb.append(parts2[i]);
				if(i > 0 || j > 0) {
					sb.append("|");
				}
			}
		}
		sb.append(")\"");
		return sb.toString();
	}
	
	@Override
	public void visit(Query query, Request request) {
		if(query.getType() != Type.CONSTRUCT) {
			return;
		}
		String code = (String)request.getParam("industry");
		if(code == null || code.isEmpty()) {
			return;
		}
		String regex = code2RegExp(code);
		
		final Variable site = query.getVariable(VAR_NS+"s");
		final Variable naics = query.getVariable(VAR_NS+NAICS_VAR);
		final QueryResource rdfType = query.getResource(RDF_NS+"type");
		final QueryResource polHasNAICS = query.getResource(POL_NS+"hasNAICS");
		
		List<GraphComponentCollection> graphs = query.findGraphComponentsWithPattern(site, rdfType, null);
		if(graphs != null && graphs.size()>0) {
			GraphComponentCollection graph = graphs.get(0);
			final OptionalComponent optional = query.createOptional();
			graph.addGraphComponent(optional);
			optional.addPattern(site, polHasNAICS, naics);
			graph.addFilter("!BOUND(?"+NAICS_VAR+") || regex(?"+NAICS_VAR+", "+regex+")");
		}
	}

	@Override
	public void visit(SemantAquaUI ui, Request request) {
		ui.addFacet(config.getResource("industry.jsp"));
	}

	@Override
	public String getName() {
		return "Industry";
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
