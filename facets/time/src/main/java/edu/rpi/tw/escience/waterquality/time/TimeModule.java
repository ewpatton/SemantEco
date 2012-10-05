package edu.rpi.tw.escience.waterquality.time;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;

import edu.rpi.tw.escience.waterquality.Module;
import edu.rpi.tw.escience.waterquality.ModuleConfiguration;
import edu.rpi.tw.escience.waterquality.Request;
import edu.rpi.tw.escience.waterquality.Resource;
import edu.rpi.tw.escience.waterquality.SemantAquaUI;
import edu.rpi.tw.escience.waterquality.query.GraphComponentCollection;
import edu.rpi.tw.escience.waterquality.query.Query;
import edu.rpi.tw.escience.waterquality.query.Query.Type;
import edu.rpi.tw.escience.waterquality.query.QueryResource;
import edu.rpi.tw.escience.waterquality.query.Variable;

import static edu.rpi.tw.escience.waterquality.query.Query.VAR_NS;
import static edu.rpi.tw.escience.waterquality.query.Query.RDF_NS;

/**
 * The TimeModule provides a facet for users to control how far back data
 * are queried and uses that information to control what data are copied from
 * the triple store into the local Model so that it can be reasoned over
 * when the regulations are applied by the RegulationModule.
 * 
 * @author ewpatton
 *
 */
public class TimeModule implements Module {

	private ModuleConfiguration config = null;
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	public static final String TIME_NS = "http://www.w3.org/2006/time#";
	private static final String TIME_VAR = "time";
	
	@Override
	public void visit(final Model model, final Request request) {
		// no need to modify the A-Box
	}

	@Override
	public void visit(final OntModel model, final Request request) {
		// no need to modify the T-Box
	}

	@Override
	public void visit(final Query query, final Request request) {
		// if the query references time assume the caller
		// knows what it's doing
		if(query.hasVariable(VAR_NS+TIME_VAR)) {
			return;
		}
		// if the query doesn't have a measurement
		// variable we won't modify anything
		if(!query.hasVariable(VAR_NS+"measurement")) {
			return;
		}
		
		// extract the time passed from the client
		final String[] args = request.getParam(TIME_VAR);
		String time = "";
		if(args.length > 0) {
			time = args[0];
		}
		
		// process request based on query type
		updateWhereClause(query, time);
		if(query.getType().equals(Type.CONSTRUCT)) {
			handleConstructQuery(query);
		}
	}
	
	protected void handleConstructQuery(final Query query) {
		// add ?measurement time:inXSDDateTime ?time to the graph
		final Variable measurement = query.getVariable(VAR_NS+"measurement");
		final Variable timeVar = query.getVariable(VAR_NS+TIME_VAR);
		final QueryResource timeInXSDDateTime = query.getResource(TIME_NS+"inXSDDateTime");
		query.getConstructComponent().addPattern(measurement, timeInXSDDateTime, timeVar);
	}
	
	protected void updateWhereClause(final Query query, final String deltaT) {
		// find components that mention the type of the measurement
		final Variable measurement = query.getVariable(VAR_NS+"measurement");
		final QueryResource rdfType = query.getResource(RDF_NS+"type");
		List<GraphComponentCollection> graphs = query.findGraphComponentsWithPattern(measurement, rdfType, null);
		
		// if there are no graphs, we can't do anything
		if(graphs.size() == 0) {
			return;
		}
		
		final GraphComponentCollection graph = graphs.get(0);
		final Variable timeVar = query.getVariable(VAR_NS+TIME_VAR);
		final QueryResource timeInXSDDateTime = query.getResource(TIME_NS+"inXSDDateTime");
		
		// add item to the found graph
		graph.addPattern(measurement, timeInXSDDateTime, timeVar);

		// process the deltaT from the client and add a filter
		if(deltaT.isEmpty()) {
			return;
		}
		final Calendar time = processTimeParam(deltaT);
		final String xsdTime = sdf.format(time);
		graph.addFilter("?"+TIME_VAR+" > xsd:dateTime(\""+xsdTime+"\")");
	}

	@Override
	public void visit(final SemantAquaUI ui, final Request request) {
		// we just have the one display item, no javascript
		Resource res = config.getResource("time.jsp");
		ui.addFacet(res);
	}

	@Override
	public String getName() {
		return "Time";
	}

	@Override
	public int getMajorVersion() {
		return 1;
	}

	@Override
	public int getMinorVersion() {
		return 0;
	}

	@Override
	public String getExtraVersion() {
		return null;
	}

	@Override
	public void setModuleConfiguration(final ModuleConfiguration config) {
		this.config = config;
	}
	
	protected Calendar processTimeParam(final String time) {
		if(time == null || time.equals("")) {
			return null;
		}
		Calendar c = Calendar.getInstance();
		char type = time.charAt(time.length()-1);
		String offset = time.substring(1, time.length()-1);
		int move = Integer.parseInt(offset);
		switch(type) {
		case 'Y':
			c.add(Calendar.YEAR, move);
			break;
		case 'M':
			c.add(Calendar.MONTH, move);
			break;
		}
		return c;
	}

}
