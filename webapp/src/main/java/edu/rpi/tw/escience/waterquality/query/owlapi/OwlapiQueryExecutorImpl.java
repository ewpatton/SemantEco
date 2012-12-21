package edu.rpi.tw.escience.waterquality.query.owlapi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.InferenceType;

import com.clarkparsia.pellet.owlapiv3.PelletReasoner;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;
import com.hp.hpl.jena.rdf.model.Model;

import edu.rpi.tw.escience.waterquality.Module;
import edu.rpi.tw.escience.waterquality.ModuleManager;
import edu.rpi.tw.escience.waterquality.impl.ModuleManagerFactory;
import edu.rpi.tw.escience.waterquality.query.Query;
import edu.rpi.tw.escience.waterquality.query.QueryExecutorImpl;

/**
 * OwlapiQueryExecutorImpl provides an alternative QueryExecutor that
 * uses the OWL API to query Pellet in order to obtain explanations
 * using the PelletReasoner interface.
 * @author ewpatton
 *
 */
public class OwlapiQueryExecutorImpl extends QueryExecutorImpl {

	/**
	 * Constructs a new OwlapiQueryExecutorImpl owned by the specified
	 * Module and pointing by default at tripleStore
	 * @param owner Module that this QueryExecutor belongs to
	 * @param tripleStore Default triple store; can be overridden using
	 * calls to execute(String, \.\.\.) methods."
	 */
	public OwlapiQueryExecutorImpl(Module owner, String tripleStore) {
		super(owner, tripleStore);
	}
	
	/**
	 * This protected method is used to support the {@link Object#clone()}
	 * method.
	 * @param other The QueryExecutorImpl to clone.
	 */
	protected OwlapiQueryExecutorImpl(final OwlapiQueryExecutorImpl other) {
		super(other);
		this.log = other.log;
		this.request = other.request;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		OwlapiQueryExecutorImpl copy = new OwlapiQueryExecutorImpl(this);
		return copy;
	}
	
	@Override
	public String executeLocalQuery(Query query) {
		log.trace("executeLocalQuery");
		Model model = request.getCombinedModel();
		log.debug("Module '"+owner.get().getName()+"' executing local query");
		ModuleManager mgr = ModuleManagerFactory.getInstance().getManager();
		mgr.augmentQuery(query, request, owner.get());
		log.debug("Query: "+query.toString());
		ByteArrayOutputStream bufferedModel = new ByteArrayOutputStream();
		model.write(bufferedModel);
		ByteArrayInputStream byteStream = new ByteArrayInputStream(bufferedModel.toByteArray());
		bufferedModel = null;
		model = null;
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		try {
			OWLOntology ontology = manager.loadOntologyFromOntologyDocument(byteStream);
			return executeOwlapiQuery(query, ontology, manager);
		} catch (OWLOntologyCreationException e) {
			log.warn("OWL API unable to parse combined model exported by Jena.");
		}
		return "{\"error\":\"Unable to process query using OWL API\"}";
	}
	
	protected String executeOwlapiQuery(final Query query, final OWLOntology ontology, OWLOntologyManager manager) {
		PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner(ontology);
		// TODO process query object here
		OWLDataFactory dataFactory = manager.getOWLDataFactory();
		OWLClass PollutedSites = dataFactory.getOWLClass(IRI.create("http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#PollutedSite"));	
		reasoner.precomputeInferences(InferenceType.CLASS_ASSERTIONS);
		reasoner.getInstances(PollutedSites, false);
		return null;
		
		
		
	}

}
