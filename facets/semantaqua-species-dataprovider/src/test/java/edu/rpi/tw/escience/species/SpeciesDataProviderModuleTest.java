package edu.rpi.tw.escience.species;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;

import edu.rpi.tw.escience.waterquality.query.Query;
import edu.rpi.tw.escience.waterquality.SemantAquaUI;
import edu.rpi.tw.escience.waterquality.test.TestModuleConfiguration;
import edu.rpi.tw.escience.species.SpeciesDataProviderModule;

import junit.framework.TestCase;

public class SpeciesDataProviderModuleTest extends TestCase {
	
	@Test
	public void testVisitModel() {
		SpeciesDataProviderModule module = new SpeciesDataProviderModule();
		TestModuleConfiguration config = new TestModuleConfiguration();
		module.setModuleConfiguration(config);
		module.visit((Model)null, null);
	}
	
	@Test
	public void testVisitOntModel() {
		SpeciesDataProviderModule module = new SpeciesDataProviderModule();
		TestModuleConfiguration config = new TestModuleConfiguration();
		module.setModuleConfiguration(config);
		module.visit((OntModel)null, null);
	}
	
	@Test
	public void testVisitQuery() {
		SpeciesDataProviderModule module = new SpeciesDataProviderModule();
		TestModuleConfiguration config = new TestModuleConfiguration();
		module.setModuleConfiguration(config);
		module.visit((Query)null, null);
	}
	
	/*
	@Test
	public void testVisitUI() {
		SpeciesDataProviderModule module = new SpeciesDataProviderModule();
		TestModuleConfiguration config = new TestModuleConfiguration();
		module.setModuleConfiguration(config);
		module.visit((SemantAquaUI)null, null);
	}
	*/
	
	@Test
	public void testProperties() {
		SpeciesDataProviderModule module = new SpeciesDataProviderModule();
		assertNotNull(module.getName());
		assertFalse(module.getName().equals(""));
		assertEquals(1, module.getMajorVersion());
		assertEquals(0, module.getMinorVersion());
		assertNull(module.getExtraVersion());
	}

}
