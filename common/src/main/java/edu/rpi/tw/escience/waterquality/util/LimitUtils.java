package edu.rpi.tw.escience.waterquality.util;

import org.json.JSONObject;

import edu.rpi.tw.escience.waterquality.Request;

public final class LimitUtils {
	private LimitUtils() {
		
	}
	
	public static final int getLimit(final Request request, final String id) {
		int limit = 0;
		try {
			JSONObject limits = new JSONObject(request.getParam("limit")[0]);
			JSONObject specs = limits.getJSONObject(id);
			limit = specs.getInt("limit");
		}
		catch(Exception e) {
			
		}
		return limit;
	}
	
	public static final int getOffset(final Request request, final String id) {
		int offset = 0;
		try {
			JSONObject limits = new JSONObject(request.getParam("limit"));
			JSONObject specs = limits.getJSONObject(id);
			offset = specs.getInt("offset");
		}
		catch(Exception e) {
			
		}
		return offset;
	}
}
