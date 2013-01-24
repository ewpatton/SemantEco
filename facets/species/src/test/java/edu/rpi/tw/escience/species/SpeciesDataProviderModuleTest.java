package edu.rpi.tw.escience.species;

import junit.framework.TestCase;

import org.junit.Test;

import edu.rpi.tw.escience.semanteco.test.TestModuleConfiguration;

public class SpeciesDataProviderModuleTest extends TestCase {
	
	@Test
	public void testVisitModel() {
		SpeciesDataProviderModule module = new SpeciesDataProviderModule();
		TestModuleConfiguration config = new TestModuleConfiguration();
		module.setModuleConfiguration(config);
		//module.visit((Model)null, null);
	}
	
	@Test
	public void testVisitOntModel() {
		SpeciesDataProviderModule module = new SpeciesDataProviderModule();
		TestModuleConfiguration config = new TestModuleConfiguration();
		module.setModuleConfiguration(config);
		//module.visit((OntModel)null, null);
	}
	
	@Test
	public void testVisitQuery() {
		SpeciesDataProviderModule module = new SpeciesDataProviderModule();
		TestModuleConfiguration config = new TestModuleConfiguration();
		module.setModuleConfiguration(config);
		//module.visit((Query)null, null);
	}
	
	/*
	@Test
	public void testVisitUI() {
		SpeciesDataProviderModule module = new SpeciesDataProviderModule();
		TestModuleConfiguration config = new TestModuleConfiguration();
		module.setModuleConfiguration(config);
		module.visit((SemantEcoUI)null, null);
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
