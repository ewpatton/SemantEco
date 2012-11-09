package edu.rpi.tw.escience.waterquality.query.impl;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;

import edu.rpi.tw.escience.waterquality.query.BlankNode;
import edu.rpi.tw.escience.waterquality.query.GraphComponent;
import edu.rpi.tw.escience.waterquality.query.GraphComponentCollection;
import edu.rpi.tw.escience.waterquality.query.GraphPattern;
import edu.rpi.tw.escience.waterquality.query.NamedGraphComponent;
import edu.rpi.tw.escience.waterquality.query.OptionalComponent;
import edu.rpi.tw.escience.waterquality.query.Query;
import edu.rpi.tw.escience.waterquality.query.QueryResource;
import edu.rpi.tw.escience.waterquality.query.URI;
import edu.rpi.tw.escience.waterquality.query.UnionComponent;
import edu.rpi.tw.escience.waterquality.query.Variable;

/**
 * QueryImpl is the default implementation of the Query interface and provides
 * the bulk of the logic required for programatically generating SPARQL queries
 * used by the SemantAqua portal.
 * 
 * @author ewpatton
 *
 */
public class QueryImpl implements Query {

	private Map<String, String> prefixes = new HashMap<String, String>();
	
	private Type type = Type.SELECT;
	
	private GraphComponentCollection constructClause = null;
	private GraphComponentCollection whereClause = new GraphComponentCollectionImpl();
	
	private Map<String, NamedGraphComponent> namedGraphs
		= new HashMap<String, NamedGraphComponent>();
	
	private Set<Variable> variables = null;
	
	private Set<String> fromList = new TreeSet<String>();
	private Set<String> fromNamedList = new TreeSet<String>();
	
	private Map<String, QueryResource> resources = new HashMap<String, QueryResource>();
	
	private boolean distinct = false;
	private boolean reduced = false;
	
	private long limit = -1;
	private long offset = -1;
	
	private static class OrderByEntry {
		private Variable variable;
		private String expression;
		private SortType direction;
	}
	
	private List<OrderByEntry> orderList;
	private List<Variable> groupList;
	
	/**
	 * Constructs a SELECT query
	 */
	public QueryImpl() {
		this(Type.SELECT);
	}
	
	/**
	 * Constructs a query of the specified type
	 * @param type Either ASK, CONSTRUCT, DESCRIBE, or SELECT
	 */
	public QueryImpl(Type type) {
		if(type == null) {
			throw new IllegalArgumentException("type cannot be null");
		}
		setType(type);
		prefixes.put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		prefixes.put("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
		prefixes.put("owl", "http://www.w3.org/2002/07/owl#");
		prefixes.put("xsd", "http://www.w3.org/2001/XMLSchema#");
	}
	
	@Override
	public void addPattern(QueryResource subject, QueryResource predicate,
			QueryResource object) {
		addGraphComponent(new GraphPatternImpl(subject, predicate, object));
	}

	@Override
	public void addPattern(QueryResource subject, QueryResource predicate,
			String object, XSDDatatype type) {
		addGraphComponent(new GraphPatternImpl(subject, predicate, object, type));
	}

	@Override
	public void addFilter(String cond) {
		addGraphComponent(new FilterComponentImpl(cond));
	}

	@Override
	public void addGraphComponent(GraphComponent component) {
		whereClause.addGraphComponent(component);
	}

	@Override
	public GraphComponentCollection getConstructComponent() {
		return constructClause;
	}

	@Override
	public NamedGraphComponent getNamedGraph(String uri) {
		if(!namedGraphs.containsKey(uri)) {
			namedGraphs.put(uri, new NamedGraphComponentImpl(uri));
			whereClause.addGraphComponent(namedGraphs.get(uri));
		}
		return namedGraphs.get(uri);
	}

	@Override
	public UnionComponent createUnion() {
		return new UnionComponentImpl();
	}

	@Override
	public OptionalComponent createOptional() {
		return new OptionalComponentImpl();
	}

	@Override
	public Variable getVariable(String uri) {
		if(!resources.containsKey(uri)) {
			return createVariable(uri);
		}
		return (Variable) resources.get(uri);
	}

	@Override
	public Variable createVariable(String uri) {
		if(resources.containsKey(uri)) {
			throw new IllegalArgumentException("Variable '"+uri+"' already exists");
		}
		VariableImpl var = new VariableImpl(uri);
		resources.put(uri, var);
		return var;
	}

	@Override
	public BlankNode createBlankNode() {
		return new BlankNodeImpl();
	}

	@Override
	public final void setType(Type type) {
		if(type == null) {
			throw new IllegalArgumentException("type cannot be null");
		}
		this.type = type;
		if(type == Type.CONSTRUCT && constructClause == null) {
			constructClause = new GraphComponentCollectionImpl();
		}
	}

	@Override
	public final Type getType() {
		return type;
	}

	@Override
	public void addFrom(String uri) {
		fromList.add(uri);
	}

	@Override
	public void addFromNamed(String uri) {
		fromNamedList.add(uri);
	}

	@Override
	public QueryResource getResource(String uri) {
		if(!resources.containsKey(uri)) {
			resources.put(uri, new URI(uri));
		}
		return resources.get(uri);
	}

	@Override
	public void setVariables(Set<Variable> object) {
		if(object == null) {
			this.variables = null;
		}
		else {
			this.variables = new LinkedHashSet<Variable>(object);
		}
	}

	@Override
	public Set<Variable> getVariables() {
		if(this.variables == null) {
			return this.variables;
		}
		return Collections.unmodifiableSet(this.variables);
	}
	
	@Override
	public String toString() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(out);
		writeHeader(ps);
		if(type == Type.CONSTRUCT) {
			writeConstruct(ps);
		}
		else {
			writeVars(ps);
		}
		writeWhereClause(ps);
		writeSolutionModifier(ps);
		return out.toString();
	}
	
	private void writeHeader(PrintStream out) {
		writePrefix(out);
		switch(type) {
		case SELECT:
			out.print("SELECT ");
			break;
		case DESCRIBE:
			out.print("DESCRIBE ");
			break;
		case CONSTRUCT:
			out.print("CONSTRUCT ");
			break;
		case ASK:
			out.print("ASK ");
			break;
		}
	}
	
	private void writePrefix(PrintStream out) {
		for(Entry<String, String> i : prefixes.entrySet()) {
			out.println("PREFIX "+i.getKey()+": <"+i.getValue()+">");
		}
	}
	
	private void writeConstruct(PrintStream out) {
		out.println(constructClause.toString());
	}
	
	private void writeVars(PrintStream out) {
		if(reduced) {
			out.println("REDUCED ");
		}
		if(distinct) {
			out.println("DISTINCT ");
		}
		if(variables == null) {
			out.println("*");
		}
		else if(variables.size() == 0) {
			out.println("*");
		}
		else {
			Iterator<Variable> i;
			i = variables.iterator();
			while(i.hasNext()) {
				out.print(i.next()+" ");
			}
			out.println();
		}
	}
	
	private void writeWhereClause(PrintStream out) {
		Iterator<String> i;
		i = fromList.iterator();
		while(i.hasNext()) {
			out.println("FROM <"+i.next()+">");
		}
		i = fromNamedList.iterator();
		while(i.hasNext()) {
			out.println("FROM NAMED <"+i.next()+">");
		}
		out.println("WHERE ");
		out.println(whereClause.toString());
	}
	
	private void writeSolutionModifier(PrintStream out) {
		writeGroupClause(out);
		writeOrderByClause(out);
		writeLimitOffsetClauses(out);
	}
	
	private void writeGroupClause(PrintStream out) {
		if(groupList != null) {
			out.print("GROUP BY");
			for(Variable i : groupList) {
				out.print(" ");
				out.print(i.toString());
			}
			out.println();
		}
	}
	
	private void writeOrderByClause(PrintStream out) {
		if(orderList != null) {
			out.print("ORDER BY");
			for(OrderByEntry i : orderList) {
				out.print(" ");
				if(i.direction == SortType.ASC) {
					out.print("ASC(");
				}
				else {
					out.print("DESC(");
				}
				if(i.variable != null) {
					out.print(i.variable);
				}
				else {
					out.print(i.expression);
				}
				out.print(")");
			}
			out.println();
		}
	}
	
	private void writeLimitOffsetClauses(PrintStream out) {
		if(offset != -1) {
			out.println("OFFSET "+offset);
		}
		if(limit != -1) {
			out.println("LIMIT "+limit);
		}
	}

	@Override
	public List<GraphComponent> getComponents() {
		return whereClause.getComponents();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((constructClause == null) ? 0 : constructClause.hashCode());
		result = prime * result + fromList.hashCode();
		result = prime * result + fromNamedList.hashCode();
		result = prime * result + namedGraphs.hashCode();
		result = prime * result + resources.hashCode();
		result = prime * result + type.hashCode();
		result = prime * result
				+ ((variables == null) ? 0 : variables.hashCode());
		result = prime * result + whereClause.hashCode();
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
		return compareFields((QueryImpl)obj);
	}
	
	private boolean compareFields(QueryImpl other) {
		if(!compare(constructClause, other.constructClause)) {
			return false;
		}
		if(!compare(fromList, other.fromList)) {
			return false;
		}
		if(!compare(fromNamedList, other.fromNamedList)) {
			return false;
		}
		if(!compare(namedGraphs, other.namedGraphs)) {
			return false;
		}
		if(!compare(resources, other.resources)) {
			return false;
		}
		if(!compare(type, other.type)) {
			return false;
		}
		if(!compare(variables, other.variables)) {
			return false;
		}
		if(!compare(whereClause, other.whereClause)) {
			return false;
		}
		return true;
	}
	
	private boolean compare(Object left, Object right) {
		if(left == null && right == null) {
			return true;
		}
		if(left == null) {
			return false;
		}
		return left.equals(right);
	}

	@Override
	public Set<String> getFrom() {
		return Collections.unmodifiableSet(fromList);
	}

	@Override
	public Set<String> getFromNamed() {
		return Collections.unmodifiableSet(fromNamedList);
	}

	@Override
	public void addGroupBy(Variable var) {
		if(groupList == null) {
			groupList = new LinkedList<Variable>();
		}
		groupList.add(var);
	}

	@Override
	public void addOrderBy(Variable var, SortType sort) {
		if(orderList == null) {
			orderList = new LinkedList<OrderByEntry>();
		}
		OrderByEntry entry = new OrderByEntry();
		entry.direction = sort;
		entry.variable = var;
		orderList.add(entry);
	}

	@Override
	public void setDistinct(boolean distinct) {
		reduced = false;
		this.distinct = distinct;
	}

	@Override
	public boolean isDistinct() {
		return distinct;
	}

	@Override
	public void setReduced(boolean reduced) {
		distinct = false;
		this.reduced = reduced;
	}

	@Override
	public boolean isReduced() {
		return reduced;
	}

	@Override
	public void setLimit(long limit) {
		this.limit = limit;
	}

	@Override
	public long getLimit() {
		return this.limit;
	}

	@Override
	public void setOffset(long offset) {
		this.offset = offset;
	}

	@Override
	public long getOffset() {
		return this.offset;
	}

	@Override
	public void addOrderBy(String expr, SortType sort) {
		if(orderList == null) {
			orderList = new LinkedList<OrderByEntry>();
		}
		OrderByEntry entry = new OrderByEntry();
		entry.expression = expr;
		entry.direction = sort;
		orderList.add(entry);
	}

	@Override
	public Variable createVariableExpression(String expr) {
		return new VariableExprImpl(expr);
	}

	@Override
	public boolean hasVariable(String var) {
		return resources.containsKey(var);
	}
	
	protected List<GraphComponentCollection> findGraphComponentWithPattern(final GraphComponentCollection start, 
			final GraphPatternImpl pattern) {
		final List<GraphComponentCollection> results = new ArrayList<GraphComponentCollection>();
		List<GraphComponent> components = start.getComponents();
		boolean addThis = false;
		for(GraphComponent i : components) {
			if(i instanceof GraphPatternImpl) {
				if(((GraphPatternImpl) i).matches(pattern)) {
					addThis = true;
				}
			}
			else if(i instanceof GraphComponentCollection) {
				results.addAll(findGraphComponentWithPattern((GraphComponentCollection)i, pattern));
			}
		}
		if(addThis) {
			results.add(start);
		}
		return results;
	}
	
	@Override
	public List<GraphComponentCollection> findGraphComponentsWithPattern(
			QueryResource subject, QueryResource predicate, QueryResource object) {
		final GraphPatternImpl pattern = new GraphPatternImpl(subject, predicate, object);
		return findGraphComponentWithPattern(whereClause, pattern);
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
		prefixes.put(prefix, namespace);
	}

	@Override
	public String getNamespace(String prefix) {
		return prefixes.get(prefix);
	}

	@Override
	public GraphComponentCollection createGraphComponentCollection() {
		return new GraphComponentCollectionImpl();
	}

	@Override
	public void addBind(String expr, Variable var) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public QueryResource createPropertyPath(String path) {
		return new PropertyPathImpl(path);
	}
	
}
