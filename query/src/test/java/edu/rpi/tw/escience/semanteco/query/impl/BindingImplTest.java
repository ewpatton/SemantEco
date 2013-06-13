package edu.rpi.tw.escience.semanteco.query.impl;

import org.junit.Test;

import edu.rpi.tw.escience.semanteco.query.impl.BindingImpl;

import junit.framework.TestCase;

public class BindingImplTest extends TestCase {

	@Test
	public void testConstructor() {
		try {
			new BindingImpl(null, null);
			fail();
		} catch(IllegalArgumentException e) {
		} catch(Exception e) {
			fail();
		}
		try {
			new BindingImpl("", null);
		} catch(IllegalArgumentException e) {
		} catch(Exception e) {
			fail();
		}
	}

}
