package edu.rpi.tw.escience.semanteco.util;

import java.util.List;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import edu.rpi.tw.escience.semanteco.query.Query;

/**
 * DomainQueryUtils provide utilities for executing a query over multiple models
 * generated from different domains. In effect, it performs similar functionality
 * to the SPARQL SERVICE keyword while operating over local models. It does not
 * support ORDER BY statements, so results will be ordered within a particular
 * domain but not across domains. The split nature of the models also means that
 * queries referencing sites in two different domains will not work.
 * @author ewpatton
 *
 */
public final class DomainQueryUtils {

	private static final Logger log = Logger.getLogger(DomainQueryUtils.class);

	private DomainQueryUtils() {
	}

	/**
	 * Executes a select query against the given list of models, potentially in
	 * parallel.
	 * @param query A SemantEco Query object
	 * @param models A list of Jena Models constructed for active domains
	 * @param parallel true if the query should be performed in parallel over
	 * the models, false otherwise. Currently this must be false.
	 * @return A ResultSet over all of the models or null if the
	 * query is not valid
	 */
	public static ResultSet executeSelect(Query query, List<Model> models,
			boolean parallel) {
		return executeSelect(query.toString(), models, parallel);
	}

	/**
	 * Executes a select query against the given list of models, potentially in
	 * parallel.
	 * @param query A string containing a valid SPARQL query
	 * @param models A list of Jena Models constructed for active domains
	 * @param parallel true if the query should be performed in parallel over
	 * the models, false otherwise. Currently this must be false.
	 * @return A ResultSet over all of the models or null if the
	 * query is not valid
	 */
	public static ResultSet executeSelect(String query, List<Model> models,
			boolean parallel) {
		MultiResultSet results = new MultiResultSet();
		com.hp.hpl.jena.query.Query queryObj;
		try {
			queryObj = QueryFactory.create(query);
		} catch(Exception e) {
			log.warn("Invalid SPARQL query", e);
			return null;
		}
		for(Model m : models) {
			log.debug("Processing next model");
			long start = System.currentTimeMillis();
			QueryExecution qe = QueryExecutionFactory.create(queryObj, m);
			results.addResultSet(qe.execSelect());
			log.debug("Executed subquery in "+(System.currentTimeMillis()-start)+" ms");
		}
		return results;
	}

	/**
	 * Executes a describe query against the given list of models, potentially
	 * in parallel.
	 * @param query A SemantEco query object
	 * @param models A list of Jena Models constructed for active domains
	 * @param target A target Model that all results will be read into
	 * @param parallel true if the query should be performed in parallel over
	 * the models, false otherwise. Currently this must be false.
	 */
	public static void executeDescribe(Query query, List<Model> models,
			Model target, boolean parallel) {
		executeDescribe(query.toString(), models, target, parallel);
	}

	private static void doBuildModel(String query, List<Model> models, 
			Model target) {
		com.hp.hpl.jena.query.Query queryObj = null;
		try {
			queryObj = QueryFactory.create(query);
		} catch(Exception e) {
			log.warn("Invalid SPARQL query", e);
			return;
		}
		for(Model m : models) {
			long start = System.currentTimeMillis();
			try {
				QueryExecution qe = QueryExecutionFactory.create(queryObj, m);
				if(queryObj.isConstructType()) {
					qe.execConstruct(target);
				} else if(queryObj.isDescribeType()) {
					qe.execDescribe(target);
				}
			} catch(Exception e) {
				log.warn("Unexpected exception when performing query", e);
			}
			log.debug("Executed subquery in "+(System.currentTimeMillis()-start)+" ms");
		}
	}

	/**
	 * Executes a describe query against the given list of models, potentially
	 * in parallel.
	 * @param query A string containing a valid SPARQL query
	 * @param models A list of Jena Models constructed for active domains
	 * @param target A target Model that all results will be read into
	 * @param parallel true if the query should be performed in parallel over
	 * the models, false otherwise. Currently this must be false.
	 */
	public static void executeDescribe(String query, List<Model> models,
			Model target, boolean parallel) {
		doBuildModel(query, models, target);
	}

	/**
	 * Executes a construct query against the given list of models, potentially
	 * in parallel.
	 * @param query A string containing a valid SPARQL query
	 * @param models A list of Jena Models constructed for active domains
	 * @param target A target Model that all results will be read into
	 * @param parallel true if the query should be performed in parallel over
	 * the models, false otherwise. Currently this must be false.
	 */
	public static void executeConstruct(String query, List<Model> models,
			Model target, boolean parallel) {
		doBuildModel(query, models, target);
	}

	/**
	 * Executes a construct query against the given list of models, potentially
	 * in parallel.
	 * @param query A SemantEco Query object
	 * @param models A list of Jena Models constructed for active domains
	 * @param target A target Model that all results will be read into
	 * @param parallel true if the query should be performed in parallel over
	 * the models, false otherwise. Currently this must be false.
	 */
	public static void executeConstruct(Query query, List<Model> models,
			Model target, boolean parallel) {
		executeConstruct(query.toString(), models, target, parallel);
	}

	/**
	 * Executes a describe query against the given list of models, potentially
	 * in parallel, and returns a new Model with the results
	 * @param query A SemantEco query object
	 * @param models A list of Jena Models constructed for active domains
	 * @param parallel true if the query should be performed in parallel over
	 * the models, false otherwise. Currently this must be false.
	 * @return A new Jena Model containing the results of the construct query.
	 */
	public static Model executeDescribe(Query query, List<Model> models,
			boolean parallel) {
		return executeDescribe(query.toString(), models, parallel);
	}

	/**
	 * Executes a describe query against the given list of models, potentially
	 * in parallel, and returns a new Model with the results
	 * @param query A string containing a valid SPARQL query
	 * @param models A list of Jena Models constructed for active domains
	 * @param parallel true if the query should be performed in parallel over
	 * the models, false otherwise. Currently this must be false.
	 * @return A new Jena Model containing the results of the construct query.
	 */
	public static Model executeDescribe(String query, List<Model> models,
			boolean parallel) {
		Model target = ModelFactory.createDefaultModel();
		executeDescribe(query, models, target, parallel);
		return target;
	}

	/**
	 * Executes a construct query against the given list of models, potentially
	 * in parallel, and returns the results in a new Jena Model.
	 * @param query A SemantEco Query object
	 * @param models A list of Jena Models constructed for active domains
	 * @param target A target Model that all results will be read into
	 * @param parallel true if the query should be performed in parallel over
	 * the models, false otherwise. Currently this must be false.
	 */
	public static Model executeConstruct(Query query, List<Model> models,
			boolean parallel) {
		return executeConstruct(query.toString(), models, parallel);
	}

	/**
	 * Executes a construct query against the given list of models, potentially
	 * in parallel, and returns the results in a new Jena Model.
	 * @param query A string containing a valid SPARQL query
	 * @param models A list of Jena Models constructed for active domains
	 * @param target A target Model that all results will be read into
	 * @param parallel true if the query should be performed in parallel over
	 * the models, false otherwise. Currently this must be false.
	 */
	public static Model executeConstruct(String query, List<Model> models,
			boolean parallel) {
		Model target = ModelFactory.createDefaultModel();
		executeConstruct(query, models, target, parallel);
		return target;
	}

	/**
	 * Executes an ask query against the given list of models. This will
	 * return true so long as at least one model satisfies the query.
	 * @param query A SemantEco Query object
	 * @param models A list of Jena Models constructed for active domains
	 * @param parallel true if the query should be performed in parallel over
	 * the models, false otherwise. Currently this must be false.
	 * @return
	 */
	public static boolean executeAsk(Query query, List<Model> models,
			boolean parallel) {
		return executeAsk(query.toString(), models, parallel);
	}

	/**
	 * Executes an ask query against the given list of models. This will
	 * return true so long as at least one model satisfies the query.
	 * @param query A SemantEco Query object
	 * @param models A list of Jena Models constructed for active domains
	 * @param parallel true if the query should be performed in parallel over
	 * the models, false otherwise. Currently this must be false.
	 * @return
	 */
	public static boolean executeAsk(String query, List<Model> models,
			boolean parallel) {
		com.hp.hpl.jena.query.Query queryObj;
		try {
			queryObj = QueryFactory.create(query);
		} catch(Exception e) {
			log.warn("Invalid SPARQL query", e);
			return false;
		}
		for(Model m : models) {
			QueryExecution qe = QueryExecutionFactory.create(queryObj, m);
			try {
				if(qe.execAsk()) {
					return true;
				}
			} catch(Exception e) {
				log.warn("Unable to execute query against model", e);
			}
		}
		return false;
	}
}
