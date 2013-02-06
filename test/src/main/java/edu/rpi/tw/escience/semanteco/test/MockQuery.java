package edu.rpi.tw.escience.semanteco.test;

import java.util.List;
import java.util.Set;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;

import edu.rpi.tw.escience.semanteco.query.BlankNode;
import edu.rpi.tw.escience.semanteco.query.GraphComponent;
import edu.rpi.tw.escience.semanteco.query.GraphComponentCollection;
import edu.rpi.tw.escience.semanteco.query.GraphPattern;
import edu.rpi.tw.escience.semanteco.query.NamedGraphComponent;
import edu.rpi.tw.escience.semanteco.query.OptionalComponent;
import edu.rpi.tw.escience.semanteco.query.Query;
import edu.rpi.tw.escience.semanteco.query.QueryResource;
import edu.rpi.tw.escience.semanteco.query.UnionComponent;
import edu.rpi.tw.escience.semanteco.query.Variable;

/**
 * MockQuery provides a base interface for unit tests
 * to mock up a query object. All methods throw
 * UnsupportedOperationException unless explicitly
 * overridden by a subclass.
 * @author ewpatton
 *
 */
public class MockQuery implements Query {

	@Override
	public void addPattern(QueryResource subject, QueryResource predicate,
			QueryResource object) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addPattern(QueryResource subject, QueryResource predicate,
			QueryResource object, boolean b) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addPattern(QueryResource subject, QueryResource predicate,
			String object, XSDDatatype type) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addFilter(String cond) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addGraphComponent(GraphComponent component) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<GraphComponent> getComponents() {
		throw new UnsupportedOperationException();
	}

	@Override
	public GraphComponentCollection getConstructComponent() {
		throw new UnsupportedOperationException();
	}

	@Override
	public NamedGraphComponent getNamedGraph(String uri) {
		throw new UnsupportedOperationException();
	}

	@Override
	public UnionComponent createUnion() {
		throw new UnsupportedOperationException();
	}

	@Override
	public OptionalComponent createOptional() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Variable getVariable(String uri) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Variable createVariable(String uri) {
		throw new UnsupportedOperationException();
	}

	@Override
	public BlankNode createBlankNode() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setType(Type type) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Type getType() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addFrom(String uri) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<String> getFrom() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addFromNamed(String uri) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<String> getFromNamed() {
		throw new UnsupportedOperationException();
	}

	@Override
	public QueryResource getResource(String uri) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setVariables(Set<Variable> object) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<Variable> getVariables() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addGroupBy(Variable var) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addOrderBy(Variable var, SortType sort) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setDistinct(boolean distinct) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isDistinct() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setReduced(boolean reduced) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isReduced() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setLimit(long limit) {
		throw new UnsupportedOperationException();
	}

	@Override
	public long getLimit() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setOffset(long offset) {
		throw new UnsupportedOperationException();
	}

	@Override
	public long getOffset() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addOrderBy(String expr, SortType sort) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Variable createVariableExpression(String expr) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasVariable(String var) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<GraphComponentCollection> findGraphComponentsWithPattern(
			QueryResource subject, QueryResource predicate, QueryResource object) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<GraphComponentCollection> findGraphComponentsWithPattern(
			QueryResource subject, QueryResource predicate, String value,
			XSDDatatype type) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<GraphComponentCollection> findGraphComponentsWithPattern(
			GraphPattern pattern) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setNamespace(String prefix, String namespace) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getNamespace(String prefix) {
		throw new UnsupportedOperationException();
	}

	@Override
	public GraphComponentCollection createGraphComponentCollection() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addBind(String expr, Variable var) {
		throw new UnsupportedOperationException();
	}

	@Override
	public QueryResource createPropertyPath(String string) {
		throw new UnsupportedOperationException();
	}

}
