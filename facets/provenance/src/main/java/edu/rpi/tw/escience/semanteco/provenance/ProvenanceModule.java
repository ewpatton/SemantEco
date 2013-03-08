package edu.rpi.tw.escience.semanteco.provenance;

import static edu.rpi.tw.escience.semanteco.query.Query.RDF_NS;
import static edu.rpi.tw.escience.semanteco.query.Query.VAR_NS;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;

import com.clarkparsia.pellet.owlapiv3.PelletReasoner;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;

import edu.rpi.tw.escience.semanteco.Module;
import edu.rpi.tw.escience.semanteco.ModuleConfiguration;
import edu.rpi.tw.escience.semanteco.ModuleManager;
import edu.rpi.tw.escience.semanteco.QueryMethod;
import edu.rpi.tw.escience.semanteco.Request;
import edu.rpi.tw.escience.semanteco.Resource;
import edu.rpi.tw.escience.semanteco.SemantEcoUI;
//import edu.rpi.tw.escience.semanteco.impl.ModuleManagerFactory;
import edu.rpi.tw.escience.semanteco.query.GraphComponentCollection;
import edu.rpi.tw.escience.semanteco.query.Query;
import edu.rpi.tw.escience.semanteco.query.QueryResource;
import edu.rpi.tw.escience.semanteco.query.Variable;
import edu.rpi.tw.escience.semanteco.query.Query.Type;

public class ProvenanceModule implements Module {

	private ModuleConfiguration config = null;
	private String site = null;
	public static final String QUERY_NS = "http://aquarius.tw.rpi.edu/projects/semantaqua/data-source/query-variable/";
	private static final String SITE_VAR = "site";
	private static final String FACILITY_VAR = "facility";
	private static final String POLLUTED_VAR = "polluted";
	private static final String LABEL_VAR = "label";
	private static final String POL_NS = "http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#";
	private static final String WGS_NS = "http://www.w3.org/2003/01/geo/wgs84_pos#";
	private static final String RDFS_NS = "http://www.w3.org/2000/01/rdf-schema#";
	private static final String UNIT_NS = "http://sweet.jpl.nasa.gov/2.1/reprSciUnits.owl#";
	private static final String OWL_NS = "http://www.w3.org/2002/07/owl#";
	private static final String XSD_NS = "http://www.w3.org/2001/XMLSchema#";
	private static final String PROP_VAR = "p";
	


	/**
	 * Based on the measurement site parmeter, gets RDF data adding provenance using a CONSTRUCT query and returns to client corresponding RDF
	 * @param request
	 * @return
	 */
	@QueryMethod
	public String queryForProvenance(final Request request){
		
		//get the site URI
		site = (String)request.getParam("uri");
		

		//get the combined model
		Model model = request.getCombinedModel();
		
		//sparql construct (not graph specific, on the model)
		final Query query = config.getQueryFactory().newQuery(Type.CONSTRUCT);
		final GraphComponentCollection construct = query.getConstructComponent();

		//set up variables and resources
		final QueryResource siteResource = query.getResource(site);
		final QueryResource rdfType = query.getResource(RDF_NS+"type");
		final QueryResource polMeasurementSite = query.getResource(POL_NS+"MeasurementSite");
		final QueryResource wgsLat = query.getResource(WGS_NS+"lat");
		final QueryResource wgsLong = query.getResource(WGS_NS+"long");
		final Variable lat = query.getVariable(VAR_NS+"lat");
		final Variable lng = query.getVariable(VAR_NS+"lng");
		//no need to set variables in a construct
		final QueryResource hasMeasurement = query.getResource(POL_NS +"hasMeasurement");
		final Variable measurement = query.getVariable(VAR_NS+"measurement");
		final QueryResource polHasCounty = query.getResource(POL_NS+"hasCounty");
		final QueryResource polHasState = query.getResource(POL_NS+"hasState");
		final QueryResource polHasCharacteristic = query.getResource(POL_NS+"hasCharacteristic");
		final QueryResource polHasValue = query.getResource(POL_NS+"hasValue");
		final QueryResource unitHasUnit = query.getResource(UNIT_NS+"hasUnit");
		final QueryResource subClassOf = query.getResource(RDFS_NS+"subClassOf");
		final QueryResource type = query.getResource(RDF_NS+"type");
		final QueryResource WaterMeasurement = query.getResource("http://escience.rpi.edu/ontology/semanteco/2/0/water.owl#WaterMeasurement");
		final QueryResource RegulationViolation = query.getResource("http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#RegulationViolation");
		final Variable element = query.getVariable(QUERY_NS+"element");
		final Variable unit = query.getVariable(QUERY_NS+"unit");
		final Variable value = query.getVariable(QUERY_NS+"value");
		final Variable stateCode = query.getVariable(QUERY_NS+"state");
		final Variable countyCode = query.getVariable(QUERY_NS+"county");
		final Variable MeasurementType = query.getVariable(QUERY_NS+"MeasurementType");
		
		construct.addPattern(siteResource, rdfType, polMeasurementSite);
		construct.addPattern(siteResource, wgsLat, lat);
		construct.addPattern(siteResource, wgsLong, lng);
		
		construct.addPattern(siteResource, hasMeasurement, measurement);
		construct.addPattern(measurement, polHasCharacteristic, element);
		construct.addPattern(measurement, polHasValue, value);
		construct.addPattern(measurement, unitHasUnit, unit);
		construct.addPattern(measurement, type, MeasurementType);

		
		//we also want to query which regulationViolation class it is a member of
		
		
		query.addPattern(siteResource, rdfType, polMeasurementSite);
		query.addPattern(siteResource, wgsLat, lat);
		query.addPattern(siteResource, wgsLong, lng);
		
		query.addPattern(siteResource, hasMeasurement, measurement);
		query.addPattern(measurement, polHasCharacteristic, element);
		query.addPattern(measurement, polHasValue, value);
		query.addPattern(measurement, unitHasUnit, unit);
		
		query.addPattern(measurement, type, MeasurementType);
		query.addPattern(MeasurementType, subClassOf, RegulationViolation);
		
		//Request.getURL() (URL Object3  but do .toString())

		
		
		/*
		 * semantecodata : violation - excessiveArsenic - Meas12
			a semantecovocab : Violation ;
			prov : specializationOf src1 : Measurement -12;
			prov : wasDerivedFrom src1 : Measurement -12;
			prov : wasDerivedFrom src2 : ExcessiveArsenic ;
			void : inDataSet semantecodata : bbqDataSet -223.
		 */
		
		/*	 
		 semantecodata : bbqDataSet -223
		pml3 : authoredBy
		<http :// purl .org/twc/ semanteco / source / semanteco >;
		dcterms : contributor
		<http :// purl .org/twc/ semanteco / source /epa -gov >.		 	 
		 */
		
		/*
		 src1 : Measurement -12
		void : inDataSet semantecodata : graph11 .
		semantecodata : graph11
		prov : wasDerivedFrom src1 : CSVFile112 .
		src1 : CSVFile112
		prov : wasQuotedFrom http :// epa.gov/ DataURL4 .
		http :// epa.gov/ DataURL4
		pml3 : authoredBy
		<http :// purl .org/twc/ semanteco / source /epa -gov > .
		 */
		
		//even though I am retrieving the combined model , best still to use "execute" ?
		//return config.getQueryExecutor(request).accept("text/turtle").execute(query, model);			
		return config.getQueryExecutor(request).accept("text/turtle").executeLocalQuery(query);			

		
	}
	
	
	@QueryMethod
	public String owlApiTester(final Request request){
		
		//assume you have the jena model converted into an owl model, by calling OwlapiQueryExecutorImpl.executeLocalQuery
		Model model = request.getCombinedModel();
		ByteArrayOutputStream bufferedModel = new ByteArrayOutputStream();
		model.write(bufferedModel);
		ByteArrayInputStream byteStream = new ByteArrayInputStream(bufferedModel.toByteArray());
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		try {
			OWLOntology ontology = manager.loadOntologyFromOntologyDocument(byteStream);
			//return executeOwlapiQuery(query, ontology, manager);
			PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner(ontology);
			OWLDataFactory dataFactory = manager.getOWLDataFactory();
			OWLClass PollutedSite = dataFactory.getOWLClass(IRI.create("http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#PollutedSite"));	
			reasoner.precomputeInferences(InferenceType.CLASS_ASSERTIONS);
			NodeSet<OWLNamedIndividual> indivSet = reasoner.getInstances(PollutedSite, false);
			
			//loop over each individual
			for(final Node<OWLNamedIndividual> indiv : indivSet){
				
				ontology.getAxioms(AxiomType<T> OWLIndividualAxiom);
			
			}
			
			
			ontology.getAxioms();
			
			//if this works, use the owlapi to iterate over the axioms of an individual and get the measurement data based on properties.
			OWLNamedIndividual name;
			//name.
			
			/*
			 * Alternatively you may want to try using OPPL
			 */
			
			return temp.toString();
			
		
			

		} catch (OWLOntologyCreationException e) {
			//log.warn("OWL API unable to parse combined model exported by Jena.");
		}
		
		
		//what you are implementing here is the executeOwlapiQuery of that class.
		
	
		
		return null;
	}
	
	@Override
	public void visit(final Model model, final Request request) {
		// TODO populate data model
	}

	@Override
	public void visit(final OntModel model, final Request request) {
		// TODO populate ontology model
	}

	@Override
	public void visit(final Query query, final Request request) {
		// TODO modify queries
	}
	
	@Override
	public void visit(final SemantEcoUI ui, final Request request) {
		// TODO add resources to display
	}

	@Override
	public String getName() {
		return "Provenance";
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

}
