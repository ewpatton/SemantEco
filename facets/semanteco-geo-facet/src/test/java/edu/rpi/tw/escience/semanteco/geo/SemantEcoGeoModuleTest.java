package edu.rpi.tw.escience.semanteco.geo;

import org.junit.Test;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;

import edu.rpi.tw.escience.semanteco.query.Query;
import edu.rpi.tw.escience.semanteco.SemantEcoUI;
import edu.rpi.tw.escience.semanteco.test.TestModuleConfiguration;
import edu.rpi.tw.escience.semanteco.test.TestUI;
import edu.rpi.tw.escience.semanteco.test.TestUI;
import edu.rpi.tw.escience.semanteco.geo.SemantEcoGeoModule;

import junit.framework.TestCase;

public class SemantEcoGeoModuleTest extends TestCase {
	
	@Test
	public void testVisitModel() {
		SemantEcoGeoModule module = new SemantEcoGeoModule();
		TestModuleConfiguration config = new TestModuleConfiguration();
		module.setModuleConfiguration(config);
		//module.visit((Model)null, null, null);
	}
	
	@Test
	public void testVisitOntModel() {
		SemantEcoGeoModule module = new SemantEcoGeoModule();
		TestModuleConfiguration config = new TestModuleConfiguration();
		module.setModuleConfiguration(config);
		module.visit((OntModel)null, null, null);
	}
	
	@Test
	public void testVisitQuery() {
		SemantEcoGeoModule module = new SemantEcoGeoModule();
		TestModuleConfiguration config = new TestModuleConfiguration();
		module.setModuleConfiguration(config);
		module.visit((Query)null, null);
	}
	
	@Test
	public void testVisitUI() {
		SemantEcoGeoModule module = new SemantEcoGeoModule();
		TestModuleConfiguration config = new TestModuleConfiguration();
		module.setModuleConfiguration(config);
		module.visit((SemantEcoUI)new TestUI(), null);
	}
	
	@Test
	public void testProperties() {
		SemantEcoGeoModule module = new SemantEcoGeoModule();
		assertNotNull(module.getName());
		assertFalse(module.getName().equals(""));
		assertEquals(1, module.getMajorVersion());
		assertEquals(0, module.getMinorVersion());
		assertNull(module.getExtraVersion());
	}

}
