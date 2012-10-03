package edu.rpi.tw.escience.waterquality.test;

import java.util.List;
import java.util.Set;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;

import edu.rpi.tw.escience.waterquality.query.BlankNode;
import edu.rpi.tw.escience.waterquality.query.GraphComponent;
import edu.rpi.tw.escience.waterquality.query.GraphComponentCollection;
import edu.rpi.tw.escience.waterquality.query.NamedGraphComponent;
import edu.rpi.tw.escience.waterquality.query.OptionalComponent;
import edu.rpi.tw.escience.waterquality.query.Query;
import edu.rpi.tw.escience.waterquality.query.QueryResource;
import edu.rpi.tw.escience.waterquality.query.UnionComponent;
import edu.rpi.tw.escience.waterquality.query.Variable;

public class MockQuery implements Query {

	@Override
	public void addPattern(QueryResource subject, QueryResource predicate,
			QueryResource object) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addPattern(QueryResource subject, QueryResource predicate,
			String object, XSDDatatype type) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addFilter(String cond) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addGraphComponent(GraphComponent component) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<GraphComponent> getComponents() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GraphComponentCollection getConstructComponent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NamedGraphComponent getNamedGraph(String uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UnionComponent createUnion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OptionalComponent createOptional() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Variable getVariable(String uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Variable createVariable(String uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BlankNode createBlankNode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setType(Type type) {
		// TODO Auto-generated method stub

	}

	@Override
	public Type getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addFrom(String uri) {
		// TODO Auto-generated method stub

	}

	@Override
	public Set<String> getFrom() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addFromNamed(String uri) {
		// TODO Auto-generated method stub

	}

	@Override
	public Set<String> getFromNamed() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QueryResource getResource(String uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setVariables(Set<Variable> object) {
		// TODO Auto-generated method stub

	}

	@Override
	public Set<Variable> getVariables() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addGroupBy(Variable var) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addOrderBy(Variable var, SortType sort) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDistinct(boolean distinct) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isDistinct() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setReduced(boolean reduced) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isReduced() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setLimit(long limit) {
		// TODO Auto-generated method stub

	}

	@Override
	public long getLimit() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setOffset(long offset) {
		// TODO Auto-generated method stub

	}

	@Override
	public long getOffset() {
		// TODO Auto-generated method stub
		return 0;
	}

}
