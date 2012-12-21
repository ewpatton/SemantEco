package edu.rpi.tw.escience.semanteco.test;

import edu.rpi.tw.escience.semanteco.query.Query;
import edu.rpi.tw.escience.semanteco.query.impl.QueryImpl;

/**
 * TestQuery can be used by unit tests to
 * capture interaction of modules and an
 * implementation of the Query interface.
 * @author ewpatton
 *
 */
public class TestQuery extends QueryImpl implements Query {
	
	/**
	 * Constructs a new TestQuery using
	 * the given SPARQL verb.
	 * @param type One of SELECT, CONSTRUCT, DESCRIBE, ASK
	 */
	public TestQuery(Type type) {
		super(type);
	}

}
