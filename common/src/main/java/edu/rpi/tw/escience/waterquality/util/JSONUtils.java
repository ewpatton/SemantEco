package edu.rpi.tw.escience.waterquality.util;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * JSONUtils provides mechanisms to convert JSONObjects and
 * JSONArrays into Maps and Lists, respectively.
 * 
 * @author ewpatton
 *
 */
public final class JSONUtils {
	
	/**
	 * Hidden to prevent initialization
	 */
	private JSONUtils() {
		
	}
	
	/**
	 * Converts a JSONArray object to a List for
	 * a specified type.
	 * @param array Input JSONArray to convert to a List
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> toList(JSONArray array) {
		List<T> result = new ArrayList<T>();
		for(int i=0;i<array.length();i++) {
			try {
				T o = (T)array.get(i);
				result.add(o);
			}
			catch(JSONException e) {
				result.add(null);
			}
		}
		return result;
	}
}
