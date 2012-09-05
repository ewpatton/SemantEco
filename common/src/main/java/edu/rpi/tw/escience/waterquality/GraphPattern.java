package edu.rpi.tw.escience.waterquality;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;

public interface GraphPattern extends GraphComponent {
	void setSubject(QueryResource subject);
	QueryResource getSubject();
	void setPredicate(QueryResource predicate);
	QueryResource getPredicate();
	void setObject(QueryResource object);
	QueryResource getObject();
	void setObject(String value, XSDDatatype type);
	String getValue();
}
