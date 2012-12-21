package edu.rpi.tw.escience.characteristics;

import junit.framework.TestCase;

import org.junit.Test;

public class CharacteristicsModuleTest extends TestCase {
	
	@Test
	public void testVisitModel() {
		
	}
	
	/*
	@Test
	public void testVisitModel() {
		CharacteristicsModule module = new CharacteristicsModule();
		TestModuleConfiguration config = new TestModuleConfiguration();
		module.setModuleConfiguration(config);
		module.visit((Model)null, null);
	}
	
	@Test
	public void testVisitOntModel() {
		CharacteristicsModule module = new CharacteristicsModule();
		TestModuleConfiguration config = new TestModuleConfiguration();
		module.setModuleConfiguration(config);
		module.visit((OntModel)null, null);
	}
	
	@Test
	public void testVisitQuery() {
		CharacteristicsModule module = new CharacteristicsModule();
		TestModuleConfiguration config = new TestModuleConfiguration();
		module.setModuleConfiguration(config);
		module.visit((Query)null, null);
	}
	
/*
	@Test
	public void testVisitUI() {
		CharacteristicsModule module = new CharacteristicsModule();
		TestModuleConfiguration config = new TestModuleConfiguration();
		module.setModuleConfiguration(config);
		module.visit((SemantEcoUI)null, null);
	}
	
	
	
	@Test
	public void testProperties() {
		CharacteristicsModule module = new CharacteristicsModule();
		assertNotNull(module.getName());
		assertFalse(module.getName().equals(""));
		assertEquals(1, module.getMajorVersion());
		assertEquals(0, module.getMinorVersion());
		assertNull(module.getExtraVersion());
	}
	
	*/

}
