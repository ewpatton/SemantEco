package edu.rpi.tw.escience.waterquality.util;

import org.json.JSONObject;

import edu.rpi.tw.escience.waterquality.Request;

/**
 * LimitUtils can be used by various modules to process the structure
 * of the limit parameter passed by the client in a RESTful request.
 * The parameter is a JSON object of the form:
 * 
 * {
 *   {"site": {"offset": ..., "limit": ..., "count": ...}},
 *   {"facility": {"offset": ..., "limit": ..., "count": ...}}
 * }
 * 
 * @author ewpatton
 *
 */
public final class LimitUtils {
	private LimitUtils() {
		
	}
	
	/**
	 * Gets the limit field for the appropriate subcategory in the limit field
	 * @param request RESTful request object
	 * @param id An identifier for a field in the limit parameter, e.g. "site"
	 * @return An integer representing the value of the limit field for the specified id
	 */
	public static int getLimit(final Request request, final String id) {
		int limit = 0;
		try {
			JSONObject limits = (JSONObject)request.getParam("limits");
			JSONObject specs = limits.getJSONObject(id);
			limit = specs.getInt("limit");
		}
		catch(Exception e) {
			
		}
		return limit;
	}
	
	/**
	 * Gets the offset field for the appropriate subcategory in the limit field
	 * @param request RESTful request object
	 * @param id An identifier for a field in the limit parameter, e.g. "site"
	 * @return An integer representing the value of the offset field for the specified id
	 */
	public static int getOffset(final Request request, final String id) {
		int offset = 0;
		try {
			JSONObject limits = (JSONObject)request.getParam("limits");
			JSONObject specs = limits.getJSONObject(id);
			offset = specs.getInt("offset");
		}
		catch(Exception e) {
			
		}
		return offset;
	}
}
