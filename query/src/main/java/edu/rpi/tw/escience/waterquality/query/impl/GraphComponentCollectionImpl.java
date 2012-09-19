package edu.rpi.tw.escience.waterquality.query.impl;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;

import edu.rpi.tw.escience.waterquality.query.GraphComponent;
import edu.rpi.tw.escience.waterquality.query.GraphComponentCollection;
import edu.rpi.tw.escience.waterquality.query.QueryResource;

/**
 * GraphComponentCollectionImpl provides a default implementation of the
 * GraphComponentCollection and serves as the base class for a number of
 * other graph feature classes.
 * 
 * @author ewpatton
 *
 */
public class GraphComponentCollectionImpl implements GraphComponentCollection {

	private List<GraphComponent> components = new ArrayList<GraphComponent>();
	
	@Override
	public void addPattern(QueryResource subject, QueryResource predicate,
			QueryResource object) {
		components.add(new GraphPatternImpl(subject, predicate, object));
	}

	@Override
	public void addPattern(QueryResource subject, QueryResource predicate,
			String object, XSDDatatype type) {
		components.add(new GraphPatternImpl(subject, predicate, object, type));
	}

	@Override
	public void addFilter(String cond) {
		components.add(new FilterComponentImpl(cond));
	}

	@Override
	public void addGraphComponent(GraphComponent component) {
		components.add(component);
	}

	@Override
	public List<GraphComponent> getComponents() {
		return Collections.unmodifiableList(components);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + components.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		if(obj == null) {
			return false;
		}
		if(getClass() != obj.getClass()) {
			return false;
		}
		GraphComponentCollectionImpl other = (GraphComponentCollectionImpl) obj;
		return components.equals(other.components);
	}
	
	@Override
	public String toString() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(out);
		ps.println("{");
		for(GraphComponent i : components) {
			ps.println(i);
		}
		ps.print("}");
		return out.toString();
	}

}
