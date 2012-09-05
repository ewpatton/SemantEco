package test;

import java.util.List;

import org.json.JSONArray;
import org.junit.Test;

import edu.rpi.tw.escience.waterquality.util.JSONUtils;

import junit.framework.TestCase;

public class JSONUtilsTest extends TestCase {

	@Test
	public void testArrayToList() {
		JSONArray arr = new JSONArray();
		arr.put("Hello");
		arr.put("World");
		List<String> arr2 = JSONUtils.toList(arr);
		assertEquals(2, arr2.size());
		assertEquals("Hello", arr2.get(0));
		assertEquals("World", arr2.get(1));
	}
	
}
