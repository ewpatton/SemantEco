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
import edu.rpi.tw.escience.waterquality.query.UnionComponent;
import edu.rpi.tw.escience.waterquality.query.Variable;

public class UnionComponentImpl implements UnionComponent {

	List<GraphComponentCollection> graphs = new ArrayList<GraphComponentCollection>();
	
	@Override
	public void addPattern(QueryResource subject, QueryResource predicate,
			QueryResource object) {
		throw new UnsupportedOperationException("UnionComponent does not support addPattern()");
	}

	@Override
	public void addPattern(QueryResource subject, QueryResource predicate,
			String object, XSDDatatype type) {
		throw new UnsupportedOperationException("UnionComponent does not support addPattern()");
	}

	@Override
	public void addFilter(String cond) {
		throw new UnsupportedOperationException("UnionComponent does not support addFilter()");
	}

	@Override
	public void addGraphComponent(GraphComponent component) {
		throw new UnsupportedOperationException("UnionComponent does not support addGraphComponent()");
	}

	@Override
	public List<GraphComponent> getComponents() {
		return Collections.unmodifiableList((List<? extends GraphComponent>)graphs);
	}

	@Override
	public void addBind(String expr, Variable var) {
		throw new UnsupportedOperationException("UnionComponent does not support addBind()");
	}

	@Override
	public int size() {
		return graphs.size();
	}

	@Override
	public GraphComponentCollection getUnionComponent(int i) {
		while(i >= graphs.size()) {
			graphs.add(new GraphComponentCollectionImpl());
		}
		return graphs.get(i);
	}
	
	@Override
	public String toString() {
		boolean first = true;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(out);
		for(GraphComponentCollection c : graphs) {
			if(!first) {
				ps.print(" UNION ");
			}
			ps.print(c);
			first = false;
		}
		return out.toString();
	}

}
