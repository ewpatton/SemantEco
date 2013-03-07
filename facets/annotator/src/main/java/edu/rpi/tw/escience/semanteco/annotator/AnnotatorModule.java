package edu.rpi.tw.escience.semanteco.annotator;
import static edu.rpi.tw.escience.semanteco.query.Query.VAR_NS;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.LinkedHashSet;
import java.util.Set;

import org.mindswap.pellet.jena.PelletReasonerFactory;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import edu.rpi.tw.escience.semanteco.Module;
import edu.rpi.tw.escience.semanteco.ModuleConfiguration;
import edu.rpi.tw.escience.semanteco.QueryMethod;
import edu.rpi.tw.escience.semanteco.Request;
import edu.rpi.tw.escience.semanteco.SemantEcoUI;
import edu.rpi.tw.escience.semanteco.query.GraphComponentCollection;
import edu.rpi.tw.escience.semanteco.query.Query;
import edu.rpi.tw.escience.semanteco.query.QueryResource;
import edu.rpi.tw.escience.semanteco.query.Variable;
import edu.rpi.tw.escience.semanteco.query.Query.Type;

public class AnnotatorModule implements Module {

	private ModuleConfiguration config = null;
	private static final String RDFS_NS = "http://www.w3.org/2000/01/rdf-schema#";

	
	@QueryMethod
	public String queryForAnnotator(final Request request){
		
		//construct an owlontology and pose sparql queries against it.
		OntModel model = null;
		model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);

		
		//load certain ontologies
		model.read("http://was.tw.rpi.edu/semanteco/air/air.owl", "TTL");
		
		//apply sparql queries against it
		//final Query query = config.getQueryFactory().newQuery(Type.CONSTRUCT);
		//final GraphComponentCollection construct = query.getConstructComponent();
		final Query query = config.getQueryFactory().newQuery(Type.SELECT);

		final QueryResource PollutedThing = query.getResource("http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#PollutedThing");
		final QueryResource subClassOf = query.getResource(RDFS_NS+"subClassOf");
		final Variable site = query.getVariable(VAR_NS+"site");
		Set<Variable> vars = new LinkedHashSet<Variable>();
		vars.add(site);
		query.setVariables(vars);
		query.addPattern(site, subClassOf, PollutedThing);
		//construct.addPattern(site, subClassOf, PollutedThing);

		//return executeLocalQuery(query, model);
		return config.getQueryExecutor(request).accept("application/json").executeLocalQuery(query, model);

		
	}
	
	public String executeLocalQuery(Query query, Model model) {
		
		if(System.getProperty("edu.rpi.tw.escience.writemodel", "false").equals("true")) {
			try {
				FileOutputStream fos = new FileOutputStream(System.getProperty("java.io.tmpdir")+"/model.rdf");
				model.write(fos);
				fos.close();
			}
			catch(Exception e) {
				// do nothing
			}
		}
		
		Model resultModel = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		long start = System.currentTimeMillis();
		QueryExecution qe = QueryExecutionFactory.create(query.toString(), model);
		try {
			switch(query.getType()) {
			case SELECT:
				ResultSet results = qe.execSelect();
				ResultSetFormatter.outputAsJSON(baos, results);
				//log.debug("Local query took "+(System.currentTimeMillis()-start)+" ms");
				return baos.toString("UTF-8");
			case DESCRIBE:
				resultModel = qe.execDescribe();
				resultModel.write(baos);
				//log.debug("Local query took "+(System.currentTimeMillis()-start)+" ms");
				return baos.toString("UTF-8");
			case CONSTRUCT:
				resultModel = qe.execConstruct();
				resultModel.write(baos);
				//log.debug("Local query took "+(System.currentTimeMillis()-start)+" ms");
				return baos.toString("UTF-8");
			case ASK:
				if(qe.execAsk()) {
					//log.debug("Local query took "+(System.currentTimeMillis()-start)+" ms");
					return "{\"result\":true}";
				}
				else {
				//	log.debug("Local query took "+(System.currentTimeMillis()-start)+" ms");
					return "{\"result\":false}";
				}
			}
		}
		catch(Exception e) {
		//	log.warn("Unable to execute query due to exception", e);
		}
		return null;
	}
	
	
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
		return "Annotator";
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

}
