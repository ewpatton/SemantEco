package edu.rpi.tw.escience.waterquality.query.owlapi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import com.clarkparsia.pellet.owlapiv3.PelletReasoner;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;
import com.hp.hpl.jena.rdf.model.Model;

import edu.rpi.tw.escience.waterquality.Module;
import edu.rpi.tw.escience.waterquality.ModuleManager;
import edu.rpi.tw.escience.waterquality.Request;
import edu.rpi.tw.escience.waterquality.impl.ModuleManagerFactory;
import edu.rpi.tw.escience.waterquality.query.Query;
import edu.rpi.tw.escience.waterquality.query.QueryExecutorImpl;

public class OwlapiQueryExecutorImpl extends QueryExecutorImpl {

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
			return executeOwlapiQuery(query, ontology);
		} catch (OWLOntologyCreationException e) {
			log.warn("OWL API unable to parse combined model exported by Jena.");
		}
		return "{\"error\":\"Unable to process query using OWL API\"}";
	}
	
	protected String executeOwlapiQuery(final Query query, final OWLOntology ontology) {
		PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner(ontology);
		// TODO process query object here
		return null;
	}

}
