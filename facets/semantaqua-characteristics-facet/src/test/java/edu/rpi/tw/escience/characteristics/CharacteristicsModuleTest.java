package edu.rpi.tw.escience.characteristics;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;

import edu.rpi.tw.escience.waterquality.query.Query;
import edu.rpi.tw.escience.waterquality.SemantAquaUI;
import edu.rpi.tw.escience.waterquality.test.TestModuleConfiguration;
import edu.rpi.tw.escience.characteristics.CharacteristicsModule;

import junit.framework.TestCase;

public class CharacteristicsModuleTest extends TestCase {
	
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
	
	@Test
	public void testVisitUI() {
		CharacteristicsModule module = new CharacteristicsModule();
		TestModuleConfiguration config = new TestModuleConfiguration();
		module.setModuleConfiguration(config);
		module.visit((SemantAquaUI)null, null);
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

}
