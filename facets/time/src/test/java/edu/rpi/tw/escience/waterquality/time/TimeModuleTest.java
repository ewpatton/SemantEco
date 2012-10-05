package edu.rpi.tw.escience.waterquality.time;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;

import edu.rpi.tw.escience.waterquality.Resource;
import edu.rpi.tw.escience.waterquality.SemantAquaUI;
import edu.rpi.tw.escience.waterquality.test.TestModuleConfiguration;

import junit.framework.TestCase;

public class TimeModuleTest extends TestCase {
	
	class TestSemantAquaUI implements SemantAquaUI {

		List<Resource> facets = new ArrayList<Resource>();
		
		@Override
		public void addScript(Resource script) {
			throw new IllegalArgumentException();
		}

		@Override
		public void addStylesheet(Resource stylesheet) {
			throw new IllegalArgumentException();
		}

		@Override
		public void addFacet(Resource facet) {
			facets.add(facet);
		}

		@Override
		public List<Resource> getFacets() {
			return facets;
		}

		@Override
		public List<Resource> getScripts() {
			return null;
		}

		@Override
		public List<Resource> getStylesheets() {
			return null;
		}
		
	}
	
	@Test
	public void testVisitModel() {
		TimeModule module = new TimeModule();
		module.visit((Model)null, null);
	}
	
	@Test
	public void testVisitOntModel() {
		TimeModule module = new TimeModule();
		module.visit((OntModel)null, null);
	}
	
	@Test
	public void testVisitQuery() {
		TimeModule module = new TimeModule();
		TestModuleConfiguration config = new TestModuleConfiguration();
		module.setModuleConfiguration(config);
	}
	
	@Test
	public void testVisitUI() {
		TimeModule module = new TimeModule();
		TestSemantAquaUI ui = new TestSemantAquaUI();
		TestModuleConfiguration config = new TestModuleConfiguration();
		module.setModuleConfiguration(config);
		module.visit(ui, null);
	}
	
	@Test
	public void testProperties() {
		TimeModule module = new TimeModule();
		assertEquals("Time", module.getName());
		assertEquals(1, module.getMajorVersion());
		assertEquals(0, module.getMinorVersion());
		assertNull(module.getExtraVersion());
	}

}
