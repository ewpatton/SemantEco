#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;

import edu.rpi.tw.escience.waterquality.query.Query;
import edu.rpi.tw.escience.waterquality.SemantAquaUI;
import edu.rpi.tw.escience.waterquality.test.TestModuleConfiguration;
import ${package}.${moduleName}Module;

import junit.framework.TestCase;

public class ${moduleName}ModuleTest extends TestCase {
	
	@Test
	public void testVisitModel() {
		${moduleName}Module module = new ${moduleName}Module();
		TestModuleConfiguration config = new TestModuleConfiguration();
		module.setModuleConfiguration(config);
		module.visit((Model)null, null);
	}
	
	@Test
	public void testVisitOntModel() {
		${moduleName}Module module = new ${moduleName}Module();
		TestModuleConfiguration config = new TestModuleConfiguration();
		module.setModuleConfiguration(config);
		module.visit((OntModel)null, null);
	}
	
	@Test
	public void testVisitQuery() {
		${moduleName}Module module = new ${moduleName}Module();
		TestModuleConfiguration config = new TestModuleConfiguration();
		module.setModuleConfiguration(config);
		module.visit((Query)null, null);
	}
	
	@Test
	public void testVisitUI() {
		${moduleName}Module module = new ${moduleName}Module();
		TestModuleConfiguration config = new TestModuleConfiguration();
		module.setModuleConfiguration(config);
		module.visit((SemantAquaUI)null, null);
	}
	
	@Test
	public void testProperties() {
		${moduleName}Module module = new ${moduleName}Module();
		assertNotNull(module.getName());
		assertFalse(module.getName().equals(""));
		assertEquals(1, module.getMajorVersion());
		assertEquals(0, module.getMinorVersion());
		assertNull(module.getExtraVersion());
	}

}
