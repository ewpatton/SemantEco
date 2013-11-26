package edu.rpi.tw.escience.semanteco.regulation;

import java.util.LinkedHashSet;
import java.util.Set;

import edu.rpi.tw.escience.semanteco.ModuleConfiguration;
import edu.rpi.tw.escience.semanteco.query.GraphComponentCollection;
import edu.rpi.tw.escience.semanteco.query.OptionalComponent;
import edu.rpi.tw.escience.semanteco.query.Query;
import edu.rpi.tw.escience.semanteco.query.QueryResource;
import edu.rpi.tw.escience.semanteco.query.Variable;
import edu.rpi.tw.escience.semanteco.util.QueryResourceUtils;
import edu.rpi.tw.escience.semanteco.util.QueryVariableUtils;
import static edu.rpi.tw.escience.semanteco.query.Query.VAR_NS;

public final class ThresholdQueryBuilder {
	private static final String PROP_VAR = "p";
	private static final String LIST_VAR = "list";

	public final Query build(ModuleConfiguration config) {
		final Query query = config.getQueryFactory().newQuery();
		final QueryVariableUtils var = new QueryVariableUtils(query);
		final QueryResourceUtils res = new QueryResourceUtils(query);

		final Variable cls = query.getVariable(VAR_NS + "cls");
		final Variable bn = query.createBlankNode();
		final Variable charRes = query.getVariable(VAR_NS + "charRes");
		final Variable limitRes = query.getVariable(VAR_NS + "limitRes");
		final Variable unitRes = query.getVariable(VAR_NS + "unitRes");
		final Variable p = query.getVariable(VAR_NS + PROP_VAR);
		final Variable list = query.getVariable(VAR_NS + LIST_VAR);

		final QueryResource path = query.createPropertyPath("owl:someValuesFrom/owl:withRestrictions/rdf:rest*/rdf:first");

		Set<Variable> vars = new LinkedHashSet<Variable>();
		vars.add(var.characteristic());
		vars.add(var.operation());
		vars.add(var.limit());
		vars.add(var.unit());
		query.setVariables(vars);

		query.addPattern(cls, res.rdfType(), res.owlClass());
		query.addPattern(cls, res.owlIntersectionOf(), list);
		query.addPattern(list, res.rdfListPropPath(), charRes);
		query.addPattern(charRes, res.owlOnProperty(), res.escimHasCharacteristic());
		query.addPattern(charRes, res.owlHasValue(), var.characteristic());
		query.addPattern(list, res.rdfListPropPath(), limitRes);
		query.addPattern(limitRes, path, bn);
		query.addPattern(bn, p, var.limit());
		query.addPattern(list, res.rdfListPropPath(), unitRes);
		query.addPattern(unitRes, res.owlOnProperty(), res.unitHasUnit());
		query.addPattern(unitRes, res.owlHasValue(), var.unit());

		addOpMatch(query, query, "xsd:minInclusive", "<", var.operation());
		addOpMatch(query, query, "xsd:maxInclusive", ">", var.operation());
		addOpMatch(query, query, "xsd:minExclusive", "<=", var.operation());
		addOpMatch(query, query, "xsd:maxExclusive", ">=", var.operation());

		return query;
	}

	private final void addOpMatch(final Query query, 
			final GraphComponentCollection graph, 
			final String xsdOp, final String mathOp, 
			final Variable mathVar) {
		OptionalComponent optional = query.createOptional();
		graph.addGraphComponent(optional);
		optional.addFilter("?"+PROP_VAR+" = "+xsdOp);
		optional.addBind("\""+mathOp+"\"", mathVar);
	}
}
