package test;

import junit.framework.TestCase;

import org.junit.Test;

import edu.rpi.tw.escience.waterquality.util.NameUtils;

public class NameUtilsTest extends TestCase {

	@Test
	public void testCleanName() {
		String result = NameUtils.cleanName("TestName");
		assertEquals("test-name", result);
	}

}
