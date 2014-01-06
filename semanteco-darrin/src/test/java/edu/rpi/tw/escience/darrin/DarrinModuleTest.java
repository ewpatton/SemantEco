package edu.rpi.tw.escience.darrin;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;

import edu.rpi.tw.escience.semanteco.query.Query;
import edu.rpi.tw.escience.semanteco.SemantEcoUI;
import edu.rpi.tw.escience.semanteco.test.TestModuleConfiguration;
import edu.rpi.tw.escience.semanteco.test.TestUI;
import edu.rpi.tw.escience.darrin.DarrinModule;

import junit.framework.TestCase;

public class DarrinModuleTest extends TestCase {
	
	@Test
	public void testVisitModel() {
		DarrinModule module = new DarrinModule();
		TestModuleConfiguration config = new TestModuleConfiguration();
		module.setModuleConfiguration(config);
		module.visit((Model)null, null, null);
	}
	
	@Test
	public void testVisitOntModel() {
		DarrinModule module = new DarrinModule();
		TestModuleConfiguration config = new TestModuleConfiguration();
		module.setModuleConfiguration(config);
		module.visit((OntModel)null, null, null);
	}
	
	@Test
	public void testVisitQuery() {
		DarrinModule module = new DarrinModule();
		TestModuleConfiguration config = new TestModuleConfiguration();
		module.setModuleConfiguration(config);
		module.visit((Query)null, null);
	}
	
	@Test
	public void testVisitUI() {
		DarrinModule module = new DarrinModule();
		TestModuleConfiguration config = new TestModuleConfiguration();
		module.setModuleConfiguration(config);
		//module.visit((SemantEcoUI)null, null);
		module.visit((SemantEcoUI)new TestUI(), null);

	}
	
	@Test
	public void testProperties() {
		DarrinModule module = new DarrinModule();
		assertNotNull(module.getName());
		assertFalse(module.getName().equals(""));
		assertEquals(1, module.getMajorVersion());
		assertEquals(0, module.getMinorVersion());
		assertNull(module.getExtraVersion());
	}

}
