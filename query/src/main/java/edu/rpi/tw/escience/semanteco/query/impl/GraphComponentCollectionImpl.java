package edu.rpi.tw.escience.semanteco.query.impl;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;

import edu.rpi.tw.escience.semanteco.query.GraphComponent;
import edu.rpi.tw.escience.semanteco.query.GraphComponentCollection;
import edu.rpi.tw.escience.semanteco.query.NamedGraphComponent;
import edu.rpi.tw.escience.semanteco.query.QueryResource;
import edu.rpi.tw.escience.semanteco.query.Variable;

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
	private Map<String, NamedGraphComponent> namedGraphs
		= new HashMap<String, NamedGraphComponent>();
	
	@Override
	public void addPattern(QueryResource subject, QueryResource predicate,
			QueryResource object) {
		assert(subject != null);
		assert(predicate != null);
		assert(object != null);
		components.add(new GraphPatternImpl(subject, predicate, object));
	}

	@Override
	public void addPattern(QueryResource subject, QueryResource predicate,
			String object, XSDDatatype type) {
		assert(subject != null);
		assert(predicate != null);
		assert(object != null);
		components.add(new GraphPatternImpl(subject, predicate, object, type));
	}

	@Override
	public void addPattern(QueryResource subject, QueryResource predicate,
			QueryResource object, boolean transitive) {
		assert(subject != null);
		assert(predicate != null);
		assert(object != null);
		components.add(new GraphPatternImpl(subject, predicate, object, transitive));
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
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			PrintStream ps = null;
			ps = new PrintStream(out, true, "UTF-8");
			ps.println("{");
			for(GraphComponent i : components) {
				ps.println(i);
			}
			ps.print("}");
			return out.toString("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// utf-8 should always be supported, but we still need to return.
			return "(error)";
		}
	}

	@Override
	public void addBind(String expr, Variable var) {
		components.add(new BindingImpl(expr, var));
	}

	@Override
	public NamedGraphComponent getNamedGraph(String uri) {
		return getNamedGraph(uri, true);
	}

	@Override
	public NamedGraphComponent getNamedGraph(String uri, boolean autoAdd) {
		if(!namedGraphs.containsKey(uri)) {
			namedGraphs.put(uri, new NamedGraphComponentImpl(uri));
			if(autoAdd) {
				components.add(namedGraphs.get(uri));
			}
		}
		return namedGraphs.get(uri);
	}

}
