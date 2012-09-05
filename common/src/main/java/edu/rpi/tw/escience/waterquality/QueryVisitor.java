package edu.rpi.tw.escience.waterquality;

import java.util.Map;

/**
 * An object implementing the QueryVisitor 
 * 
 * @author ewpatton
 *
 */
public interface QueryVisitor {
	String getName();
	void visit(Query query, Map<String, String> params);
}
