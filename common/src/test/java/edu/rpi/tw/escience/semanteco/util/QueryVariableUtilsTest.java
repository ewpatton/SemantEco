package edu.rpi.tw.escience.semanteco.util;

import java.util.List;
import java.util.Set;

import org.junit.Test;

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

import junit.framework.TestCase;

public class QueryVariableUtilsTest extends TestCase {
	
	private static class TestQueryImpl implements Query {

		@Override
		public void addPattern(QueryResource subject, QueryResource predicate,
				QueryResource object) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void addPattern(QueryResource subject, QueryResource predicate,
				QueryResource object, boolean transitive) {
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
		public void addBind(String expr, Variable var) {
			// TODO Auto-generated method stub
			
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
		public Variable createVariableExpression(String expr) {
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

		@Override
		public void addOrderBy(String expr, SortType sort) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean hasVariable(String var) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public List<GraphComponentCollection> findGraphComponentsWithPattern(
				QueryResource subject, QueryResource predicate,
				QueryResource object) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<GraphComponentCollection> findGraphComponentsWithPattern(
				QueryResource subject, QueryResource predicate, String value,
				XSDDatatype type) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<GraphComponentCollection> findGraphComponentsWithPattern(
				GraphPattern pattern) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void setNamespace(String prefix, String namespace) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public String getNamespace(String prefix) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public GraphComponentCollection createGraphComponentCollection() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public QueryResource createPropertyPath(String string) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	@Test
	public void testUtil() {
		
	}
}
