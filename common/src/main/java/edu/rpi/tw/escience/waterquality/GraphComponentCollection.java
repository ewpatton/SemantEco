package edu.rpi.tw.escience.waterquality;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;

public interface GraphComponentCollection extends GraphComponent {
	void addPattern(QueryResource subject,
			QueryResource predicate,
			QueryResource object);
	void addPattern(QueryResource subject,
			QueryResource predicate,
			String object,
			XSDDatatype type);
	void addFilter(String cond);
	void addGraphComponent(GraphComponent component);
}
