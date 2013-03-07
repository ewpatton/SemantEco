package edu.rpi.tw.escience.semanteco.provenance;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;

import edu.rpi.tw.escience.semanteco.query.Query;
import edu.rpi.tw.escience.semanteco.SemantEcoUI;
import edu.rpi.tw.escience.semanteco.test.TestModuleConfiguration;
import edu.rpi.tw.escience.semanteco.provenance.ProvenanceModule;

import junit.framework.TestCase;

public class ProvenanceModuleTest extends TestCase {
	
	@Test
	public void testVisitModel() {
		ProvenanceModule module = new ProvenanceModule();
		TestModuleConfiguration config = new TestModuleConfiguration();
		module.setModuleConfiguration(config);
		module.visit((Model)null, null);
	}
	
	@Test
	public void testVisitOntModel() {
		ProvenanceModule module = new ProvenanceModule();
		TestModuleConfiguration config = new TestModuleConfiguration();
		module.setModuleConfiguration(config);
		module.visit((OntModel)null, null);
	}
	
	@Test
	public void testVisitQuery() {
		ProvenanceModule module = new ProvenanceModule();
		TestModuleConfiguration config = new TestModuleConfiguration();
		module.setModuleConfiguration(config);
		module.visit((Query)null, null);
	}
	
	@Test
	public void testVisitUI() {
		ProvenanceModule module = new ProvenanceModule();
		TestModuleConfiguration config = new TestModuleConfiguration();
		module.setModuleConfiguration(config);
		module.visit((SemantEcoUI)null, null);
	}
	
	@Test
	public void testProperties() {
		ProvenanceModule module = new ProvenanceModule();
		assertNotNull(module.getName());
		assertFalse(module.getName().equals(""));
		assertEquals(1, module.getMajorVersion());
		assertEquals(0, module.getMinorVersion());
		assertNull(module.getExtraVersion());
	}

}
