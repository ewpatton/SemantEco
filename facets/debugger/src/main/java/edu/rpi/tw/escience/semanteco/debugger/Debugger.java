package edu.rpi.tw.escience.semanteco.debugger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;

import edu.rpi.tw.escience.semanteco.Domain;
import edu.rpi.tw.escience.semanteco.Module;
import edu.rpi.tw.escience.semanteco.ModuleConfiguration;
import edu.rpi.tw.escience.semanteco.QueryMethod;
import edu.rpi.tw.escience.semanteco.Request;
import edu.rpi.tw.escience.semanteco.SemantEcoUI;
import edu.rpi.tw.escience.semanteco.query.Query;

import static edu.rpi.tw.escience.semanteco.util.DomainQueryUtils.executeAsk;
import static edu.rpi.tw.escience.semanteco.util.DomainQueryUtils.executeConstruct;
import static edu.rpi.tw.escience.semanteco.util.DomainQueryUtils.executeDescribe;
import static edu.rpi.tw.escience.semanteco.util.DomainQueryUtils.executeSelect;

/**
 * The Debugger class provides a client-side interface to
 * execute arbitrary queries on the server-side model, optionally
 * with OWL inferencing enabled, to enable developers to debug
 * queries on the combined model after all other modules have
 * executed.
 * @author ewpatton
 *
 */
public class Debugger implements Module {

	private ModuleConfiguration config=null;
	
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
		ui.addScript(config.getResource("debugger.js"));
	}

	@Override
	public String getName() {
		return "Debugger";
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
	 * Executes a SPARQL query on a model constructed from the
	 * given application state in the request.
	 * @param request A Request object encapsulating the client's state
	 * @return Results of the SPARQL query in JSON formatted based on
	 * the type of the user's input query.
	 */
	@QueryMethod
	public String sparql(final Request request) {
		final Logger log = request.getLogger();
		boolean reason = false;
		if(request.getParam("reason") != null && request.getParam("reason")==Boolean.TRUE) {
			reason = true;
		}

		if(request.getParam("query") == null || !(request.getParam("query") instanceof String)) {
			log.error("No query parameter supplied for call to sparql");
			return "{\"success\":false,\"error\"No query parameter supplied for call to sparql\"}";
		}
		final String queryStr = (String)request.getParam("query");

		log.debug("Building models for query execution");
		List<Domain> domains = request.listActiveDomains();
		List<Model> models = new ArrayList<Model>();
		for(Domain d : domains) {
			models.add(reason ? request.getCombinedModel(d) : 
				mergeModels(request.getDataModel(d), request.getModel(d)));
		}
		
		final com.hp.hpl.jena.query.Query query = QueryFactory.create(queryStr);
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		log.debug("Executing debugger query");
		long start = System.currentTimeMillis();
		if(query.isSelectType()) {
			ResultSet rs = executeSelect(queryStr, models, false);
			ResultSetFormatter.outputAsJSON(buffer, rs);
		}
		else if(query.isConstructType()) {
			executeConstruct(queryStr, models, false).write(buffer, "TTL");
		}
		else if(query.isDescribeType()) {
			executeDescribe(queryStr, models, false).write(buffer, "TTL");
		}
		else if(query.isAskType()) {
			boolean bool = executeAsk(queryStr, models, false);
			ResultSetFormatter.outputAsJSON(buffer, bool);
		}
		log.debug("Query execution took "+(System.currentTimeMillis()-start)+" ms");
		
		try {
			return buffer.toString("UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.error("Unable to encode results as UTF 8", e);
		}
		return "{\"success\":false,\"error\"Unable to encode results as UTF 8\"}";
	}
	
	protected Model mergeModels(Model m1, OntModel m2) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		m2.write(baos);
		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		m1.read(bais, "");
		return m1;
	}

}
