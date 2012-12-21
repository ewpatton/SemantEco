package edu.rpi.tw.escience.semanteco;

import java.net.URI;
import java.util.List;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;

import edu.rpi.tw.escience.semanteco.Domain;
import edu.rpi.tw.escience.semanteco.Module;
import edu.rpi.tw.escience.semanteco.ModuleConfiguration;
import edu.rpi.tw.escience.semanteco.ProvidesDomain;
import edu.rpi.tw.escience.semanteco.Request;
import edu.rpi.tw.escience.semanteco.Resource;
import edu.rpi.tw.escience.semanteco.SemantEcoUI;
import edu.rpi.tw.escience.semanteco.query.Query;

/**
 * The Domain module is responsible for generating the Domain facet. Users can
 * indirectly enable and disable modules by changing which domains are active.
 * 
 * To add domains, a module should implement the {@link ProvidesDomain} interface
 * and return a list of domains created using the {@link ModuleConfiguration#getDomain(URI, boolean)}
 * method from its {@link ProvidesDomain#getDomains(Request)} method, e.g.:
 * 
 * <code>
 * public List&lt;Domain&gt; getDomains(final Request request) {
 *     List&lt;Domain&gt; domains = new ArrayList&lt;Domain&gt;();
 *     Domain myDomain = config.getDomain(URI.create("http://mydomain#"), true);
 *     // add data sources, regulations, and data types here
 *     domains.add(myDomain);
 *     return domains;
 * }
 * </code>
 * 
 * @author ewpatton
 *
 */
public class DomainModule implements Module {

	private ModuleConfiguration config = null;
	
	@Override
	public void visit(Model model, Request request) {
		// does nothing
	}

	@Override
	public void visit(OntModel model, Request request) {
		// does nothing
	}

	@Override
	public void visit(Query query, Request request) {
		// does nothing
	}

	@Override
	public void visit(SemantEcoUI ui, Request request) {
		String responseStr = "<div id=\"DomainFacet\" class=\"facet\">";
		@SuppressWarnings("unchecked")
		List<Domain> domains = (List<Domain>)request.getParam("available-domains");
		if(domains != null) {
			for(Domain i : domains) {
				URI uri = i.getUri();
				responseStr += "<input name=\"domain\" type=\"checkbox\" checked=\"checked\" value=\""+uri.toString()+"\" />";
				responseStr += i.getLabel();
				responseStr += "<br />";
			}
		}
		responseStr += "</div>";
		Resource res = config.generateStringResource(responseStr);
		ui.addFacet(res);
	}

	@Override
	public String getName() {
		return "Domain";
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
