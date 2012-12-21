package test;

import org.junit.Test;

import edu.rpi.tw.escience.semanteco.query.impl.BlankNodeImpl;

import junit.framework.TestCase;

public class BlankNodeTest extends TestCase {
	@Test
	public void testConstructor() {
		new BlankNodeImpl();
	}
}
