package edu.rpi.tw.escience.dataone;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;

import edu.rpi.tw.escience.semanteco.query.Query;
import edu.rpi.tw.escience.semanteco.SemantEcoUI;
import edu.rpi.tw.escience.semanteco.test.TestModuleConfiguration;
import edu.rpi.tw.escience.dataone.DataoneSolrModule;

import junit.framework.TestCase;

public class DataoneSolrModuleTest extends TestCase {
	
	@Test
	public void testVisitModel() {
		DataoneSolrModule module = new DataoneSolrModule();
		TestModuleConfiguration config = new TestModuleConfiguration();
		module.setModuleConfiguration(config);
		module.visit((Model)null, null, null);
	}
	
	@Test
	public void testVisitOntModel() {
		DataoneSolrModule module = new DataoneSolrModule();
		TestModuleConfiguration config = new TestModuleConfiguration();
		module.setModuleConfiguration(config);
		module.visit((OntModel)null, null, null);
	}
	
	@Test
	public void testVisitQuery() {
		DataoneSolrModule module = new DataoneSolrModule();
		TestModuleConfiguration config = new TestModuleConfiguration();
		module.setModuleConfiguration(config);
		module.visit((Query)null, null);
	}
	
	@Test
	public void testVisitUI() {
		/*
		DataoneSolrModule module = new DataoneSolrModule();
		TestModuleConfiguration config = new TestModuleConfiguration();
		module.setModuleConfiguration(config);
		module.visit((SemantEcoUI)null, null);
		*/
	}
	
	@Test
	public void testProperties() {
		DataoneSolrModule module = new DataoneSolrModule();
		assertNotNull(module.getName());
		assertFalse(module.getName().equals(""));
		assertEquals(1, module.getMajorVersion());
		assertEquals(0, module.getMinorVersion());
		assertNull(module.getExtraVersion());
	}

}
