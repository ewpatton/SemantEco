package edu.rpi.tw.eScience.WaterQualityPortal.WebService;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.query.BindingQueryPlan;
import com.hp.hpl.jena.graph.query.QueryHandler;
import com.hp.hpl.jena.ontology.AllDifferent;
import com.hp.hpl.jena.ontology.AllValuesFromRestriction;
import com.hp.hpl.jena.ontology.AnnotationProperty;
import com.hp.hpl.jena.ontology.CardinalityQRestriction;
import com.hp.hpl.jena.ontology.CardinalityRestriction;
import com.hp.hpl.jena.ontology.ComplementClass;
import com.hp.hpl.jena.ontology.DataRange;
import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.EnumeratedClass;
import com.hp.hpl.jena.ontology.FunctionalProperty;
import com.hp.hpl.jena.ontology.HasValueRestriction;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.IntersectionClass;
import com.hp.hpl.jena.ontology.InverseFunctionalProperty;
import com.hp.hpl.jena.ontology.MaxCardinalityQRestriction;
import com.hp.hpl.jena.ontology.MaxCardinalityRestriction;
import com.hp.hpl.jena.ontology.MinCardinalityQRestriction;
import com.hp.hpl.jena.ontology.MinCardinalityRestriction;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.ontology.Ontology;
import com.hp.hpl.jena.ontology.Profile;
import com.hp.hpl.jena.ontology.QualifiedRestriction;
import com.hp.hpl.jena.ontology.Restriction;
import com.hp.hpl.jena.ontology.SomeValuesFromRestriction;
import com.hp.hpl.jena.ontology.SymmetricProperty;
import com.hp.hpl.jena.ontology.TransitiveProperty;
import com.hp.hpl.jena.ontology.UnionClass;
import com.hp.hpl.jena.rdf.model.Alt;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.Bag;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelChangedListener;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.NsIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFList;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.RDFReader;
import com.hp.hpl.jena.rdf.model.RDFWriter;
import com.hp.hpl.jena.rdf.model.RSIterator;
import com.hp.hpl.jena.rdf.model.ReifiedStatement;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceF;
import com.hp.hpl.jena.rdf.model.Selector;
import com.hp.hpl.jena.rdf.model.Seq;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.reasoner.Derivation;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ValidityReport;
import com.hp.hpl.jena.shared.Command;
import com.hp.hpl.jena.shared.Lock;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.shared.ReificationStyle;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

@SuppressWarnings("deprecation")
public class OntModelWrapper implements OntModel {
	
	OntModel m;

	public OntModelWrapper() {
		
	}
	
	public OntModelWrapper(OntModel model) {
		this.m = model;
	}
	
	@Override
	public Model getDeductionsModel() {
		return m.getDeductionsModel();
	}

	@Override
	public Iterator<Derivation> getDerivation(Statement arg0) {
		return m.getDerivation(arg0);
	}

	@Override
	public Model getRawModel() {
		return m.getRawModel();
	}

	@Override
	public Reasoner getReasoner() {
		return m.getReasoner();
	}

	@Override
	public StmtIterator listStatements(Resource arg0, Property arg1,
			RDFNode arg2, Model arg3) {
		return m.listStatements();
	}

	@Override
	public void prepare() {
		m.prepare();
	}

	@Override
	public void rebind() {
		m.rebind();
	}

	@Override
	public void reset() {
		m.reset();
	}

	@Override
	public void setDerivationLogging(boolean arg0) {
		m.setDerivationLogging(arg0);
	}

	@Override
	public ValidityReport validate() {
		return m.validate();
	}

	@Override
	public Model abort() {
		return m.abort();
	}

	@Override
	public Model add(Statement arg0) {
		return m.add(arg0);
	}

	@Override
	public Model add(Statement[] arg0) {
		return m.add(arg0);
	}

	@Override
	public Model add(List<Statement> arg0) {
		return m.add(arg0);
	}

	@Override
	public Model add(StmtIterator arg0) {
		return m.add(arg0);
	}

	@Override
	public Model add(Model arg0) {
		return m.add(arg0);
	}

	@Override
	public Model add(Model arg0, boolean arg1) {
		return m.add(arg0);
	}

	@Override
	public Model begin() {
		return m.begin();
	}

	@Override
	public void close() {
		m.close();
	}

	@Override
	public Model commit() {
		return m.commit();
	}

	@Override
	public boolean contains(Statement arg0) {
		return m.contains(arg0);
	}

	@Override
	public boolean contains(Resource arg0, Property arg1) {
		return m.contains(arg0, arg1);
	}

	@Override
	public boolean contains(Resource arg0, Property arg1, RDFNode arg2) {
		return m.contains(arg0, arg1, arg2);
	}

	@Override
	public boolean containsAll(StmtIterator arg0) {
		return m.containsAll(arg0);
	}

	@Override
	public boolean containsAll(Model arg0) {
		return m.containsAll(arg0);
	}

	@Override
	public boolean containsAny(StmtIterator arg0) {
		return m.containsAny(arg0);
	}

	@Override
	public boolean containsAny(Model arg0) {
		return m.containsAny(arg0);
	}

	@Override
	public boolean containsResource(RDFNode arg0) {
		return m.containsResource(arg0);
	}

	@Override
	public RDFList createList() {
		return m.createList();
	}

	@Override
	public RDFList createList(Iterator<? extends RDFNode> arg0) {
		return m.createList(arg0);
	}

	@Override
	public RDFList createList(RDFNode[] arg0) {
		return m.createList(arg0);
	}

	@Override
	public Literal createLiteral(String arg0, String arg1) {
		return m.createLiteral(arg0, arg1);
	}

	@Override
	public Literal createLiteral(String arg0, boolean arg1) {
		return m.createLiteral(arg0, arg1);
	}

	@Override
	public Property createProperty(String arg0, String arg1) {
		return m.createProperty(arg0, arg1);
	}

	@Override
	public ReifiedStatement createReifiedStatement(Statement arg0) {
		return m.createReifiedStatement(arg0);
	}

	@Override
	public ReifiedStatement createReifiedStatement(String arg0, Statement arg1) {
		return m.createReifiedStatement(arg0, arg1);
	}

	@Override
	public Resource createResource() {
		return m.createResource();
	}

	@Override
	public Resource createResource(AnonId arg0) {
		return m.createResource(arg0);
	}

	@Override
	public Resource createResource(String arg0) {
		return m.createResource(arg0);
	}

	@Override
	public Statement createStatement(Resource arg0, Property arg1, RDFNode arg2) {
		return m.createStatement(arg0, arg1, arg2);
	}

	@Override
	public Literal createTypedLiteral(Object arg0) {
		return m.createTypedLiteral(arg0);
	}

	@Override
	public Literal createTypedLiteral(String arg0, RDFDatatype arg1) {
		return m.createTypedLiteral(arg0, arg1);
	}

	@Override
	public Literal createTypedLiteral(Object arg0, RDFDatatype arg1) {
		return m.createTypedLiteral(arg0, arg1);
	}

	@Override
	public Model difference(Model arg0) {
		return m.difference(arg0);
	}

	@Override
	public Object executeInTransaction(Command arg0) {
		return m.executeInTransaction(arg0);
	}

	@Override
	public Resource getAnyReifiedStatement(Statement arg0) {
		return m.getAnyReifiedStatement(arg0);
	}

	@Override
	public Lock getLock() {
		return m.getLock();
	}

	@Override
	public Property getProperty(String arg0, String arg1) {
		return m.getProperty(arg0, arg1);
	}

	@Override
	public Statement getProperty(Resource arg0, Property arg1) {
		return m.getProperty(arg0, arg1);
	}

	@Override
	public ReificationStyle getReificationStyle() {
		return m.getReificationStyle();
	}

	@Override
	public Statement getRequiredProperty(Resource arg0, Property arg1) {
		return m.getRequiredProperty(arg0, arg1);
	}

	@Override
	public Resource getResource(String arg0) {
		return m.getResource(arg0);
	}

	@Override
	public boolean independent() {
		return m.independent();
	}

	@Override
	public Model intersection(Model arg0) {
		return m.intersection(arg0);
	}

	@Override
	public boolean isClosed() {
		return m.isClosed();
	}

	@Override
	public boolean isEmpty() {
		return m.isEmpty();
	}

	@Override
	public boolean isIsomorphicWith(Model arg0) {
		return m.isIsomorphicWith(arg0);
	}

	@Override
	public boolean isReified(Statement arg0) {
		return m.isReified(arg0);
	}

	@Override
	public NsIterator listNameSpaces() {
		return m.listNameSpaces();
	}

	@Override
	public NodeIterator listObjects() {
		return m.listObjects();
	}

	@Override
	public NodeIterator listObjectsOfProperty(Property arg0) {
		return m.listObjectsOfProperty(arg0);
	}

	@Override
	public NodeIterator listObjectsOfProperty(Resource arg0, Property arg1) {
		return m.listObjectsOfProperty(arg0, arg1);
	}

	@Override
	public RSIterator listReifiedStatements() {
		return m.listReifiedStatements();
	}

	@Override
	public RSIterator listReifiedStatements(Statement arg0) {
		return m.listReifiedStatements(arg0);
	}

	@Override
	public ResIterator listResourcesWithProperty(Property arg0) {
		return m.listResourcesWithProperty(arg0);
	}

	@Override
	public ResIterator listResourcesWithProperty(Property arg0, RDFNode arg1) {
		return m.listResourcesWithProperty(arg0, arg1);
	}

	@Override
	public StmtIterator listStatements() {
		return m.listStatements();
	}

	@Override
	public StmtIterator listStatements(Selector arg0) {
		return m.listStatements(arg0);
	}

	@Override
	public StmtIterator listStatements(Resource arg0, Property arg1,
			RDFNode arg2) {
		return m.listStatements(arg0, arg1, arg2);
	}

	@Override
	public ResIterator listSubjects() {
		return m.listSubjects();
	}

	@Override
	public ResIterator listSubjectsWithProperty(Property arg0) {
		return m.listSubjectsWithProperty(arg0);
	}

	@Override
	public ResIterator listSubjectsWithProperty(Property arg0, RDFNode arg1) {
		return m.listSubjectsWithProperty(arg0, arg1);
	}

	@Override
	public Model notifyEvent(Object arg0) {
		return m.notifyEvent(arg0);
	}

	@Override
	public Model query(Selector arg0) {
		return m.query(arg0);
	}

	@Override
	public Model read(String arg0) {
		return m.read(arg0);
	}

	@Override
	public Model read(InputStream arg0, String arg1) {
		return m.read(arg0, arg1);
	}

	@Override
	public Model read(Reader arg0, String arg1) {
		return m.read(arg0, arg1);
	}

	@Override
	public Model read(String arg0, String arg1) {
		return m.read(arg0, arg1);
	}

	@Override
	public Model read(InputStream arg0, String arg1, String arg2) {
		return m.read(arg0, arg1, arg2);
	}

	@Override
	public Model read(Reader arg0, String arg1, String arg2) {
		return m.read(arg0, arg1, arg2);
	}

	@Override
	public Model read(String arg0, String arg1, String arg2) {
		return m.read(arg0, arg1, arg2);
	}

	@Override
	public Model register(ModelChangedListener arg0) {
		return m.register(arg0);
	}

	@Override
	public Model remove(Statement[] arg0) {
		return m.remove(arg0);
	}

	@Override
	public Model remove(List<Statement> arg0) {
		return m.remove(arg0);
	}

	@Override
	public Model remove(Statement arg0) {
		return m.remove(arg0);
	}

	@Override
	public Model removeAll() {
		return m.removeAll();
	}

	@Override
	public Model removeAll(Resource arg0, Property arg1, RDFNode arg2) {
		return m.removeAll(arg0, arg1, arg2);
	}

	@Override
	public void removeAllReifications(Statement arg0) {
		m.removeAllReifications(arg0);
	}

	@Override
	public void removeReification(ReifiedStatement arg0) {
		m.removeReification(arg0);
	}

	@Override
	public long size() {
		return m.size();
	}

	@Override
	public boolean supportsSetOperations() {
		return m.supportsSetOperations();
	}

	@Override
	public boolean supportsTransactions() {
		return m.supportsTransactions();
	}

	@Override
	public Model union(Model arg0) {
		return m.union(arg0);
	}

	@Override
	public Model unregister(ModelChangedListener arg0) {
		return m.unregister(arg0);
	}

	@Override
	public Model add(Resource arg0, Property arg1, RDFNode arg2) {
		return m.add(arg0, arg1, arg2);
	}

	@Override
	public Model add(Resource arg0, Property arg1, String arg2) {
		return m.add(arg0, arg1, arg2);
	}

	@Override
	public Model add(Resource arg0, Property arg1, String arg2, RDFDatatype arg3) {
		return m.add(arg0, arg1, arg2, arg3);
	}

	@Override
	public Model add(Resource arg0, Property arg1, String arg2, boolean arg3) {
		return m.add(arg0, arg1, arg2, arg3);
	}

	@Override
	public Model add(Resource arg0, Property arg1, String arg2, String arg3) {
		return m.add(arg0, arg1, arg2, arg3);
	}

	@Override
	public Model addLiteral(Resource arg0, Property arg1, boolean arg2) {
		return m.addLiteral(arg0, arg1, arg2);
	}

	@Override
	public Model addLiteral(Resource arg0, Property arg1, long arg2) {
		return m.addLiteral(arg0, arg1, arg2);
	}

	@Override
	public Model addLiteral(Resource arg0, Property arg1, int arg2) {
		return m.addLiteral(arg0, arg1, arg2);
	}

	@Override
	public Model addLiteral(Resource arg0, Property arg1, char arg2) {
		return m.addLiteral(arg0, arg1, arg2);
	}

	@Override
	public Model addLiteral(Resource arg0, Property arg1, float arg2) {
		return m.addLiteral(arg0, arg1, arg2);
	}

	@Override
	public Model addLiteral(Resource arg0, Property arg1, double arg2) {
		return m.addLiteral(arg0, arg1, arg2);
	}

	@Override
	public Model addLiteral(Resource arg0, Property arg1, Object arg2) {
		return m.addLiteral(arg0, arg1, arg2);
	}

	@Override
	public Model addLiteral(Resource arg0, Property arg1, Literal arg2) {
		return m.addLiteral(arg0, arg1, arg2);
	}

	@Override
	public boolean contains(Resource arg0, Property arg1, String arg2) {
		return m.contains(arg0, arg1, arg2);
	}

	@Override
	public boolean contains(Resource arg0, Property arg1, String arg2,
			String arg3) {
		return m.contains(arg0, arg1, arg2);
	}

	@Override
	public boolean containsLiteral(Resource arg0, Property arg1, boolean arg2) {
		return m.containsLiteral(arg0, arg1, arg2);
	}

	@Override
	public boolean containsLiteral(Resource arg0, Property arg1, long arg2) {
		return m.containsLiteral(arg0, arg1, arg2);
	}

	@Override
	public boolean containsLiteral(Resource arg0, Property arg1, int arg2) {
		return m.containsLiteral(arg0, arg1, arg2);
	}

	@Override
	public boolean containsLiteral(Resource arg0, Property arg1, char arg2) {
		return m.containsLiteral(arg0, arg1, arg2);
	}

	@Override
	public boolean containsLiteral(Resource arg0, Property arg1, float arg2) {
		return m.containsLiteral(arg0, arg1, arg2);
	}

	@Override
	public boolean containsLiteral(Resource arg0, Property arg1, double arg2) {
		return m.containsLiteral(arg0, arg1, arg2);
	}

	@Override
	public boolean containsLiteral(Resource arg0, Property arg1, Object arg2) {
		return m.containsLiteral(arg0, arg1, arg2);
	}

	@Override
	public Alt createAlt() {
		return m.createAlt();
	}

	@Override
	public Alt createAlt(String arg0) {
		return m.createAlt(arg0);
	}

	@Override
	public Bag createBag() {
		return m.createBag();
	}

	@Override
	public Bag createBag(String arg0) {
		return m.createBag(arg0);
	}

	@Override
	public Literal createLiteral(String arg0) {
		return m.createLiteral(arg0);
	}

	@Override
	public Statement createLiteralStatement(Resource arg0, Property arg1,
			boolean arg2) {
		return m.createLiteralStatement(arg0, arg1, arg2);
	}

	@Override
	public Statement createLiteralStatement(Resource arg0, Property arg1,
			float arg2) {
		return m.createLiteralStatement(arg0, arg1, arg2);
	}

	@Override
	public Statement createLiteralStatement(Resource arg0, Property arg1,
			double arg2) {
		return m.createLiteralStatement(arg0, arg1, arg2);
	}

	@Override
	public Statement createLiteralStatement(Resource arg0, Property arg1,
			long arg2) {
		return m.createLiteralStatement(arg0, arg1, arg2);
	}

	@Override
	public Statement createLiteralStatement(Resource arg0, Property arg1,
			int arg2) {
		return m.createLiteralStatement(arg0, arg1, arg2);
	}

	@Override
	public Statement createLiteralStatement(Resource arg0, Property arg1,
			char arg2) {
		return m.createLiteralStatement(arg0, arg1, arg2);
	}

	@Override
	public Statement createLiteralStatement(Resource arg0, Property arg1,
			Object arg2) {
		return m.createLiteralStatement(arg0, arg1, arg2);
	}

	@Override
	public Property createProperty(String arg0) {
		return m.createProperty(arg0);
	}

	@Override
	public Resource createResource(Resource arg0) {
		return m.createResource(arg0);
	}

	@Override
	public Resource createResource(ResourceF arg0) {
		return m.createResource(arg0);
	}

	@Override
	public Resource createResource(String arg0, Resource arg1) {
		return m.createResource(arg0, arg1);
	}

	@Override
	public Resource createResource(String arg0, ResourceF arg1) {
		return m.createResource(arg0, arg1);
	}

	@Override
	public Seq createSeq() {
		return m.createSeq();
	}

	@Override
	public Seq createSeq(String arg0) {
		return m.createSeq();
	}

	@Override
	public Statement createStatement(Resource arg0, Property arg1, String arg2) {
		return m.createStatement(arg0, arg1, arg2);
	}

	@Override
	public Statement createStatement(Resource arg0, Property arg1, String arg2,
			String arg3) {
		return m.createStatement(arg0, arg1, arg2, arg3);
	}

	@Override
	public Statement createStatement(Resource arg0, Property arg1, String arg2,
			boolean arg3) {
		return m.createStatement(arg0, arg1, arg2, arg3);
	}

	@Override
	public Statement createStatement(Resource arg0, Property arg1, String arg2,
			String arg3, boolean arg4) {
		return m.createStatement(arg0, arg1, arg2, arg3, arg4);
	}

	@Override
	public Literal createTypedLiteral(boolean arg0) {
		return m.createTypedLiteral(arg0);
	}

	@Override
	public Literal createTypedLiteral(int arg0) {
		return m.createTypedLiteral(arg0);
	}

	@Override
	public Literal createTypedLiteral(long arg0) {
		return m.createTypedLiteral(arg0);
	}

	@Override
	public Literal createTypedLiteral(Calendar arg0) {
		return m.createTypedLiteral(arg0);
	}

	@Override
	public Literal createTypedLiteral(char arg0) {
		return m.createTypedLiteral(arg0);
	}

	@Override
	public Literal createTypedLiteral(float arg0) {
		return m.createTypedLiteral(arg0);
	}

	@Override
	public Literal createTypedLiteral(double arg0) {
		return m.createTypedLiteral(arg0);
	}

	@Override
	public Literal createTypedLiteral(String arg0) {
		return m.createTypedLiteral(arg0);
	}

	@Override
	public Literal createTypedLiteral(String arg0, String arg1) {
		return m.createTypedLiteral(arg0, arg1);
	}

	@Override
	public Literal createTypedLiteral(Object arg0, String arg1) {
		return m.createTypedLiteral(arg0, arg1);
	}

	@Override
	public Alt getAlt(String arg0) {
		return m.getAlt(arg0);
	}

	@Override
	public Alt getAlt(Resource arg0) {
		return m.getAlt(arg0);
	}

	@Override
	public Bag getBag(String arg0) {
		return m.getBag(arg0);
	}

	@Override
	public Bag getBag(Resource arg0) {
		return m.getBag(arg0);
	}

	@Override
	public Property getProperty(String arg0) {
		return m.getProperty(arg0);
	}

	@Override
	public RDFNode getRDFNode(Node arg0) {
		return m.getRDFNode(arg0);
	}

	@Override
	public Resource getResource(String arg0, ResourceF arg1) {
		return m.getResource(arg0, arg1);
	}

	@Override
	public Seq getSeq(String arg0) {
		return m.getSeq(arg0);
	}

	@Override
	public Seq getSeq(Resource arg0) {
		return m.getSeq(arg0);
	}

	@Override
	public StmtIterator listLiteralStatements(Resource arg0, Property arg1,
			boolean arg2) {
		return m.listLiteralStatements(arg0, arg1, arg2);
	}

	@Override
	public StmtIterator listLiteralStatements(Resource arg0, Property arg1,
			char arg2) {
		return m.listLiteralStatements(arg0, arg1, arg2);
	}

	@Override
	public StmtIterator listLiteralStatements(Resource arg0, Property arg1,
			long arg2) {
		return m.listLiteralStatements(arg0, arg1, arg2);
	}

	@Override
	public StmtIterator listLiteralStatements(Resource arg0, Property arg1,
			float arg2) {
		return m.listLiteralStatements(arg0, arg1, arg2);
	}

	@Override
	public StmtIterator listLiteralStatements(Resource arg0, Property arg1,
			double arg2) {
		return m.listLiteralStatements(arg0, arg1, arg2);
	}

	@Override
	public ResIterator listResourcesWithProperty(Property arg0, boolean arg1) {
		return m.listResourcesWithProperty(arg0, arg1);
	}

	@Override
	public ResIterator listResourcesWithProperty(Property arg0, long arg1) {
		return m.listResourcesWithProperty(arg0, arg1);
	}

	@Override
	public ResIterator listResourcesWithProperty(Property arg0, char arg1) {
		return m.listResourcesWithProperty(arg0, arg1);
	}

	@Override
	public ResIterator listResourcesWithProperty(Property arg0, float arg1) {
		return m.listResourcesWithProperty(arg0, arg1);
	}

	@Override
	public ResIterator listResourcesWithProperty(Property arg0, double arg1) {
		return m.listResourcesWithProperty(arg0, arg1);
	}

	@Override
	public ResIterator listResourcesWithProperty(Property arg0, Object arg1) {
		return m.listResourcesWithProperty(arg0, arg1);
	}

	@Override
	public StmtIterator listStatements(Resource arg0, Property arg1, String arg2) {
		return m.listStatements(arg0, arg1, arg2);
	}

	@Override
	public StmtIterator listStatements(Resource arg0, Property arg1,
			String arg2, String arg3) {
		return m.listStatements(arg0, arg1, arg2, arg3);
	}

	@Override
	public ResIterator listSubjectsWithProperty(Property arg0, String arg1) {
		return m.listSubjectsWithProperty(arg0, arg1);
	}

	@Override
	public ResIterator listSubjectsWithProperty(Property arg0, String arg1,
			String arg2) {
		return m.listSubjectsWithProperty(arg0, arg1, arg2);
	}

	@Override
	public Model remove(StmtIterator arg0) {
		return m.remove(arg0);
	}

	@Override
	public Model remove(Model arg0) {
		return m.remove(arg0);
	}

	@Override
	public Model remove(Model arg0, boolean arg1) {
		return m.remove(arg0, arg1);
	}

	@Override
	public Model remove(Resource arg0, Property arg1, RDFNode arg2) {
		return m.remove(arg0, arg1, arg2);
	}

	@Override
	public RDFNode asRDFNode(Node arg0) {
		return m.asRDFNode(arg0);
	}

	@Override
	public Statement asStatement(Triple arg0) {
		return m.asStatement(arg0);
	}

	@Override
	public Graph getGraph() {
		return m.getGraph();
	}

	@Override
	public QueryHandler queryHandler() {
		return m.queryHandler();
	}

	@Override
	public Resource wrapAsResource(Node arg0) {
		return m.wrapAsResource(arg0);
	}

	@Override
	public RDFReader getReader() {
		return m.getReader();
	}

	@Override
	public RDFReader getReader(String arg0) {
		return m.getReader(arg0);
	}

	@Override
	public String setReaderClassName(String arg0, String arg1) {
		return m.setReaderClassName(arg0, arg1);
	}

	@Override
	public RDFWriter getWriter() {
		return m.getWriter();
	}

	@Override
	public RDFWriter getWriter(String arg0) {
		return m.getWriter(arg0);
	}

	@Override
	public String setWriterClassName(String arg0, String arg1) {
		return m.setWriterClassName(arg0, arg1);
	}

	@Override
	public String expandPrefix(String arg0) {
		return m.expandPrefix(arg0);
	}

	@Override
	public Map<String, String> getNsPrefixMap() {
		return m.getNsPrefixMap();
	}

	@Override
	public String getNsPrefixURI(String arg0) {
		return m.getNsPrefixURI(arg0);
	}

	@Override
	public String getNsURIPrefix(String arg0) {
		return m.getNsURIPrefix(arg0);
	}

	@Override
	public PrefixMapping lock() {
		return m.lock();
	}

	@Override
	public String qnameFor(String arg0) {
		return m.qnameFor(arg0);
	}

	@Override
	public PrefixMapping removeNsPrefix(String arg0) {
		return m.removeNsPrefix(arg0);
	}

	@Override
	public boolean samePrefixMappingAs(PrefixMapping arg0) {
		return m.samePrefixMappingAs(arg0);
	}

	@Override
	public PrefixMapping setNsPrefix(String arg0, String arg1) {
		return m.setNsPrefix(arg0, arg1);
	}

	@Override
	public PrefixMapping setNsPrefixes(PrefixMapping arg0) {
		return m.setNsPrefixes(arg0);
	}

	@Override
	public PrefixMapping setNsPrefixes(Map<String, String> arg0) {
		return m.setNsPrefixes(arg0);
	}

	@Override
	public String shortForm(String arg0) {
		return m.shortForm(arg0);
	}

	@Override
	public PrefixMapping withDefaultMappings(PrefixMapping arg0) {
		return m.withDefaultMappings(arg0);
	}

	@Override
	public void enterCriticalSection(boolean arg0) {
		m.enterCriticalSection(arg0);
	}

	@Override
	public void leaveCriticalSection() {
		m.leaveCriticalSection();
	}

	@Override
	public void addLoadedImport(String arg0) {
		m.addLoadedImport(arg0);
	}

	@Override
	public void addSubModel(Model arg0) {
		m.addSubModel(arg0);
	}

	@Override
	public void addSubModel(Model arg0, boolean arg1) {
		m.addSubModel(arg0, arg1);
	}

	@Override
	public int countSubModels() {
		return m.countSubModels();
	}

	@Override
	public AllDifferent createAllDifferent() {
		return m.createAllDifferent();
	}

	@Override
	public AllDifferent createAllDifferent(RDFList arg0) {
		return m.createAllDifferent(arg0);
	}

	@Override
	public AllValuesFromRestriction createAllValuesFromRestriction(String arg0,
			Property arg1, Resource arg2) {
		return m.createAllValuesFromRestriction(arg0, arg1, arg2);
	}

	@Override
	public AnnotationProperty createAnnotationProperty(String arg0) {
		return m.createAnnotationProperty(arg0);
	}

	@Override
	public CardinalityQRestriction createCardinalityQRestriction(String arg0,
			Property arg1, int arg2, OntClass arg3) {
		return m.createCardinalityQRestriction(arg0, arg1, arg2, arg3);
	}

	@Override
	public CardinalityRestriction createCardinalityRestriction(String arg0,
			Property arg1, int arg2) {
		return m.createCardinalityRestriction(arg0, arg1, arg2);
	}

	@Override
	public OntClass createClass() {
		return m.createClass();
	}

	@Override
	public OntClass createClass(String arg0) {
		return m.createClass(arg0);
	}

	@Override
	public ComplementClass createComplementClass(String arg0, Resource arg1) {
		return m.createComplementClass(arg0, arg1);
	}

	@Override
	public DataRange createDataRange(RDFList arg0) {
		return m.createDataRange(arg0);
	}

	@Override
	public DatatypeProperty createDatatypeProperty(String arg0) {
		return m.createDatatypeProperty(arg0);
	}

	@Override
	public DatatypeProperty createDatatypeProperty(String arg0, boolean arg1) {
		return m.createDatatypeProperty(arg0, arg1);
	}

	@Override
	public EnumeratedClass createEnumeratedClass(String arg0, RDFList arg1) {
		return m.createEnumeratedClass(arg0, arg1);
	}

	@Override
	public HasValueRestriction createHasValueRestriction(String arg0,
			Property arg1, RDFNode arg2) {
		return m.createHasValueRestriction(arg0, arg1, arg2);
	}

	@Override
	public Individual createIndividual(Resource arg0) {
		return m.createIndividual(arg0);
	}

	@Override
	public Individual createIndividual(String arg0, Resource arg1) {
		return m.createIndividual(arg0, arg1);
	}

	@Override
	public IntersectionClass createIntersectionClass(String arg0, RDFList arg1) {
		return m.createIntersectionClass(arg0, arg1);
	}

	@Override
	public InverseFunctionalProperty createInverseFunctionalProperty(String arg0) {
		return m.createInverseFunctionalProperty(arg0);
	}

	@Override
	public InverseFunctionalProperty createInverseFunctionalProperty(
			String arg0, boolean arg1) {
		return m.createInverseFunctionalProperty(arg0, arg1);
	}

	@Override
	public MaxCardinalityQRestriction createMaxCardinalityQRestriction(
			String arg0, Property arg1, int arg2, OntClass arg3) {
		return m.createMaxCardinalityQRestriction(arg0, arg1, arg2, arg3);
	}

	@Override
	public MaxCardinalityRestriction createMaxCardinalityRestriction(
			String arg0, Property arg1, int arg2) {
		return m.createMaxCardinalityRestriction(arg0, arg1, arg2);
	}

	@Override
	public MinCardinalityQRestriction createMinCardinalityQRestriction(
			String arg0, Property arg1, int arg2, OntClass arg3) {
		return m.createMinCardinalityQRestriction(arg0, arg1, arg2, arg3);
	}

	@Override
	public MinCardinalityRestriction createMinCardinalityRestriction(
			String arg0, Property arg1, int arg2) {
		return m.createMinCardinalityRestriction(arg0, arg1, arg2);
	}

	@Override
	public ObjectProperty createObjectProperty(String arg0) {
		return m.createObjectProperty(arg0);
	}

	@Override
	public ObjectProperty createObjectProperty(String arg0, boolean arg1) {
		return m.createObjectProperty(arg0, arg1);
	}

	@Override
	public OntProperty createOntProperty(String arg0) {
		return m.createOntProperty(arg0);
	}

	@Override
	public OntResource createOntResource(String arg0) {
		return m.createOntResource(arg0);
	}

	@Override
	public <T extends OntResource> T createOntResource(Class<T> arg0,
			Resource arg1, String arg2) {
		return m.createOntResource(arg0, arg1, arg2);
	}

	@Override
	public Ontology createOntology(String arg0) {
		return m.createOntology(arg0);
	}

	@Override
	public Restriction createRestriction(Property arg0) {
		return m.createRestriction(arg0);
	}

	@Override
	public Restriction createRestriction(String arg0, Property arg1) {
		return m.createRestriction(arg0, arg1);
	}

	@Override
	public SomeValuesFromRestriction createSomeValuesFromRestriction(
			String arg0, Property arg1, Resource arg2) {
		return m.createSomeValuesFromRestriction(arg0, arg1, arg2);
	}

	@Override
	public SymmetricProperty createSymmetricProperty(String arg0) {
		return m.createSymmetricProperty(arg0);
	}

	@Override
	public SymmetricProperty createSymmetricProperty(String arg0, boolean arg1) {
		return m.createSymmetricProperty(arg0, arg1);
	}

	@Override
	public TransitiveProperty createTransitiveProperty(String arg0) {
		return m.createTransitiveProperty(arg0);
	}

	@Override
	public TransitiveProperty createTransitiveProperty(String arg0, boolean arg1) {
		return m.createTransitiveProperty(arg0, arg1);
	}

	@Override
	public UnionClass createUnionClass(String arg0, RDFList arg1) {
		return m.createUnionClass(arg0, arg1);
	}

	@Override
	public AllValuesFromRestriction getAllValuesFromRestriction(String arg0) {
		return m.getAllValuesFromRestriction(arg0);
	}

	@Override
	public AnnotationProperty getAnnotationProperty(String arg0) {
		return m.getAnnotationProperty(arg0);
	}

	@Override
	public Model getBaseModel() {
		return m.getBaseModel();
	}

	@Override
	public CardinalityQRestriction getCardinalityQRestriction(String arg0) {
		return m.getCardinalityQRestriction(arg0);
	}

	@Override
	public CardinalityRestriction getCardinalityRestriction(String arg0) {
		return m.getCardinalityRestriction(arg0);
	}

	@Override
	public ComplementClass getComplementClass(String arg0) {
		return m.getComplementClass(arg0);
	}

	@Override
	public DatatypeProperty getDatatypeProperty(String arg0) {
		return m.getDatatypeProperty(arg0);
	}

	@Override
	public OntDocumentManager getDocumentManager() {
		return m.getDocumentManager();
	}

	@Override
	public boolean getDynamicImports() {
		return m.getDynamicImports();
	}

	@Override
	public EnumeratedClass getEnumeratedClass(String arg0) {
		return m.getEnumeratedClass(arg0);
	}

	@Override
	public HasValueRestriction getHasValueRestriction(String arg0) {
		return m.getHasValueRestriction(arg0);
	}

	@Override
	public ModelMaker getImportModelMaker() {
		return m.getImportModelMaker();
	}

	@Override
	public OntModel getImportedModel(String arg0) {
		return m.getImportedModel(arg0);
	}

	@Override
	public Individual getIndividual(String arg0) {
		return m.getIndividual(arg0);
	}

	@Override
	public IntersectionClass getIntersectionClass(String arg0) {
		return m.getIntersectionClass(arg0);
	}

	@Override
	public InverseFunctionalProperty getInverseFunctionalProperty(String arg0) {
		return m.getInverseFunctionalProperty(arg0);
	}

	@Override
	public MaxCardinalityQRestriction getMaxCardinalityQRestriction(String arg0) {
		return m.getMaxCardinalityQRestriction(arg0);
	}

	@Override
	public MaxCardinalityRestriction getMaxCardinalityRestriction(String arg0) {
		return m.getMaxCardinalityRestriction(arg0);
	}

	@Override
	public MinCardinalityQRestriction getMinCardinalityQRestriction(String arg0) {
		return m.getMinCardinalityQRestriction(arg0);
	}

	@Override
	public MinCardinalityRestriction getMinCardinalityRestriction(String arg0) {
		return m.getMinCardinalityRestriction(arg0);
	}

	@Override
	public ModelMaker getModelMaker() {
		return m.getModelMaker();
	}

	@Override
	public ObjectProperty getObjectProperty(String arg0) {
		return m.getObjectProperty(arg0);
	}

	@Override
	public OntClass getOntClass(String arg0) {
		return m.getOntClass(arg0);
	}

	@Override
	public OntProperty getOntProperty(String arg0) {
		return m.getOntProperty(arg0);
	}

	@Override
	public OntResource getOntResource(String arg0) {
		return m.getOntResource(arg0);
	}

	@Override
	public OntResource getOntResource(Resource arg0) {
		return m.getOntResource(arg0);
	}

	@Override
	public Ontology getOntology(String arg0) {
		return m.getOntology(arg0);
	}

	@Override
	public Profile getProfile() {
		return m.getProfile();
	}

	@Override
	public QualifiedRestriction getQualifiedRestriction(String arg0) {
		return m.getQualifiedRestriction(arg0);
	}

	@Override
	public Restriction getRestriction(String arg0) {
		return m.getRestriction(arg0);
	}

	@Override
	public SomeValuesFromRestriction getSomeValuesFromRestriction(String arg0) {
		return m.getSomeValuesFromRestriction(arg0);
	}

	@Override
	public OntModelSpec getSpecification() {
		return m.getSpecification();
	}

	@Override
	public List<Graph> getSubGraphs() {
		return m.getSubGraphs();
	}

	@Override
	public SymmetricProperty getSymmetricProperty(String arg0) {
		return m.getSymmetricProperty(arg0);
	}

	@Override
	public TransitiveProperty getTransitiveProperty(String arg0) {
		return m.getTransitiveProperty(arg0);
	}

	@Override
	public UnionClass getUnionClass(String arg0) {
		return m.getUnionClass(arg0);
	}

	@Override
	public boolean hasLoadedImport(String arg0) {
		return m.hasLoadedImport(arg0);
	}

	@Override
	public boolean isInBaseModel(RDFNode arg0) {
		return m.isInBaseModel(arg0);
	}

	@Override
	public boolean isInBaseModel(Statement arg0) {
		return m.isInBaseModel(arg0);
	}

	@Override
	public ExtendedIterator<AllDifferent> listAllDifferent() {
		return m.listAllDifferent();
	}

	@Override
	public ExtendedIterator<OntProperty> listAllOntProperties() {
		return m.listAllOntProperties();
	}

	@Override
	public ExtendedIterator<AnnotationProperty> listAnnotationProperties() {
		return m.listAnnotationProperties();
	}

	@Override
	public ExtendedIterator<OntClass> listClasses() {
		return m.listClasses();
	}

	@Override
	public ExtendedIterator<ComplementClass> listComplementClasses() {
		return m.listComplementClasses();
	}

	@Override
	public ExtendedIterator<DataRange> listDataRanges() {
		return m.listDataRanges();
	}

	@Override
	public ExtendedIterator<DatatypeProperty> listDatatypeProperties() {
		return m.listDatatypeProperties();
	}

	@Override
	public ExtendedIterator<EnumeratedClass> listEnumeratedClasses() {
		return m.listEnumeratedClasses();
	}

	@Override
	public ExtendedIterator<FunctionalProperty> listFunctionalProperties() {
		return m.listFunctionalProperties();
	}

	@Override
	public ExtendedIterator<OntClass> listHierarchyRootClasses() {
		return m.listHierarchyRootClasses();
	}

	@Override
	public ExtendedIterator<OntModel> listImportedModels() {
		return m.listImportedModels();
	}

	@Override
	public Set<String> listImportedOntologyURIs() {
		return m.listImportedOntologyURIs();
	}

	@Override
	public Set<String> listImportedOntologyURIs(boolean arg0) {
		return m.listImportedOntologyURIs(arg0);
	}

	@Override
	public ExtendedIterator<Individual> listIndividuals() {
		return m.listIndividuals();
	}

	@Override
	public ExtendedIterator<Individual> listIndividuals(Resource arg0) {
		return m.listIndividuals(arg0);
	}

	@Override
	public ExtendedIterator<IntersectionClass> listIntersectionClasses() {
		return m.listIntersectionClasses();
	}

	@Override
	public ExtendedIterator<InverseFunctionalProperty> listInverseFunctionalProperties() {
		return m.listInverseFunctionalProperties();
	}

	@Override
	public ExtendedIterator<OntClass> listNamedClasses() {
		return m.listNamedClasses();
	}

	@Override
	public ExtendedIterator<ObjectProperty> listObjectProperties() {
		return m.listObjectProperties();
	}

	@Override
	public ExtendedIterator<OntProperty> listOntProperties() {
		return m.listOntProperties();
	}

	@Override
	public ExtendedIterator<Ontology> listOntologies() {
		return m.listOntologies();
	}

	@Override
	public ExtendedIterator<Restriction> listRestrictions() {
		return m.listRestrictions();
	}

	@Override
	public ExtendedIterator<OntModel> listSubModels() {
		return m.listSubModels();
	}

	@Override
	public ExtendedIterator<OntModel> listSubModels(boolean arg0) {
		return m.listSubModels(arg0);
	}

	@Override
	public ExtendedIterator<SymmetricProperty> listSymmetricProperties() {
		return m.listSymmetricProperties();
	}

	@Override
	public ExtendedIterator<TransitiveProperty> listTransitiveProperties() {
		return m.listTransitiveProperties();
	}

	@Override
	public ExtendedIterator<UnionClass> listUnionClasses() {
		return m.listUnionClasses();
	}

	@Override
	public void loadImports() {
		m.loadImports();
	}

	@Override
	public <T extends RDFNode> ExtendedIterator<T> queryFor(
			BindingQueryPlan arg0, List<BindingQueryPlan> arg1, Class<T> arg2) {
		return m.queryFor(arg0, arg1, arg2);
	}

	@Override
	public void removeLoadedImport(String arg0) {
		m.removeLoadedImport(arg0);
	}

	@Override
	public void removeSubModel(Model arg0) {
		m.removeSubModel(arg0);
	}

	@Override
	public void removeSubModel(Model arg0, boolean arg1) {
		m.removeSubModel(arg0, arg1);
	}

	@Override
	public void setDynamicImports(boolean arg0) {
		m.setDynamicImports(arg0);
	}

	@Override
	public void setStrictMode(boolean arg0) {
		m.setStrictMode(arg0);
	}

	@Override
	public boolean strictMode() {
		return m.strictMode();
	}

	@Override
	public Model write(Writer arg0) {
		return m.write(arg0);
	}

	@Override
	public Model write(OutputStream arg0) {
		return m.write(arg0);
	}

	@Override
	public Model write(Writer arg0, String arg1) {
		return m.write(arg0, arg1);
	}

	@Override
	public Model write(OutputStream arg0, String arg1) {
		return m.write(arg0, arg1);
	}

	@Override
	public Model write(Writer arg0, String arg1, String arg2) {
		return m.writeAll(arg0, arg1, arg2);
	}

	@Override
	public Model write(OutputStream arg0, String arg1, String arg2) {
		return m.writeAll(arg0, arg1, arg2);
	}

	@Override
	public Model writeAll(Writer arg0, String arg1, String arg2) {
		return m.writeAll(arg0, arg1, arg2);
	}

	@Override
	public Model writeAll(OutputStream arg0, String arg1, String arg2) {
		return m.writeAll(arg0, arg1, arg2);
	}

}
