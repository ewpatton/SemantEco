package edu.rpi.tw.escience.semanteco.util;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.sparql.engine.binding.Binding;

/**
 * The MultiResultSet wraps a number of ResultSet objects
 * and allows iterating over them as if they were a single
 * contiguous ResultSet.
 * @author ewpatton
 *
 */
class MultiResultSet implements ResultSet {

	private final List<ResultSet> results;
	private int activeSet = -1;
	private int activeRow = -1;

	/**
	 * Creates a new MultiResultSet
	 */
	public MultiResultSet() {
		results = new ArrayList<ResultSet>();
	}

	/**
	 * Adds an existing ResultSet to this MultiResultSet
	 * @param rs
	 */
	public void addResultSet(ResultSet rs) {
		synchronized(results) {
			results.add(rs);
			if(activeSet == -1) {
				activeSet = 0;
			}
		}
	}

	@Override
	public void remove() {
	}

	@Override
	public boolean hasNext() {
		if(results.size() == 0) {
			return false;
		}
		if(results.get(activeSet).hasNext()) {
			return true;
		} else {
			activeSet++;
			while(activeSet < results.size()) {
				if(results.get(activeSet).hasNext()) {
					return true;
				}
				activeSet++;
			}
		}
		return false;
	}

	@Override
	public QuerySolution next() {
		if(results.size() == 0 || results.size() <= activeSet) {
			return null;
		}
		if(results.get(activeSet).hasNext()) {
			activeRow++;
			return results.get(activeSet).next();
		} else {
			activeSet++;
			while(activeSet < results.size()) {
				if(results.get(activeSet).hasNext()) {
					activeRow++;
					return results.get(activeSet).next();
				}
				activeSet++;
			}
		}
		return null;
	}

	@Override
	public QuerySolution nextSolution() {
		return next();
	}

	@Override
	public Binding nextBinding() {
		if(results.size() == 0 || results.size() >= activeSet) {
			return null;
		}
		if(results.get(activeSet).hasNext()) {
			activeRow++;
			return results.get(activeSet).nextBinding();
		} else {
			activeSet++;
			while(activeSet < results.size()) {
				if(results.get(activeSet).hasNext()) {
					activeRow++;
					return results.get(activeSet).nextBinding();
				}
				activeSet++;
			}
		}
		return null;
	}

	@Override
	public int getRowNumber() {
		return activeRow;
	}

	@Override
	public List<String> getResultVars() {
		if(results.size() == 0) {
			return new ArrayList<String>();
		} else {
			return results.get(0).getResultVars();
		}
	}

	@Override
	public Model getResourceModel() {
		return null;
	}
}
