package edu.rpi.tw.escience.query;

import org.junit.Test;

import edu.rpi.tw.escience.semanteco.query.QueryFactoryImpl;
import junit.framework.TestCase;

public class QueryFactoryImplTest extends TestCase {
	@Test
	public void testGet() {
		assertNotNull(QueryFactoryImpl.getInstance());
		assertNotNull(QueryFactoryImpl.getInstance());
	}

	@Test
	public void testNewQuery() {
		assertNotNull(QueryFactoryImpl.getInstance().newQuery());
	}
}
