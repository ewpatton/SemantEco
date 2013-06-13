package edu.rpi.tw.escience.semanteco;

import org.junit.Test;

import edu.rpi.tw.escience.semanteco.QueryMethod.HTTP;
import junit.framework.TestCase;

public class QueryMethodTest extends TestCase {
	@Test
	public void testEnum() {
		assertNotSame(HTTP.GET, HTTP.POST);
	}
}
