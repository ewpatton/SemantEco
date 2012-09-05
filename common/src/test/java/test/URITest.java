package test;

import org.junit.Test;

import edu.rpi.tw.escience.waterquality.query.URI;

import junit.framework.TestCase;

public class URITest extends TestCase {
	
	private static final String testUri = "http://example.com/testuri";
	
	@Test
	public void testConstructor() {
		new URI(testUri);
	}
	
	@Test
	public void testGetter() {
		URI temp = new URI(testUri);
		assertEquals(testUri, temp.getUri());
	}
	
	@Test
	public void testToString() {
		URI temp = new URI(testUri);
		assertEquals("<"+testUri+">", temp.toString());
	}
	
	@Test
	public void testHashCode() {
		URI temp = new URI(testUri);
		java.net.URI temp2 = java.net.URI.create(testUri);
		assertEquals(31+temp2.hashCode(), temp.hashCode());
	}
	
	@Test
	public void testEquals() {
		URI temp = new URI(testUri);
		URI temp2 = new URI(testUri);
		URI temp3 = new URI(testUri+"3");
		assertTrue(temp.equals(temp));
		assertFalse(temp.equals(null));
		assertFalse(temp.equals(new Object()));
		assertTrue(temp.equals(temp2));
		assertFalse(temp.equals(temp3));
	}
}
