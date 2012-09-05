package test;

import org.junit.Test;

import edu.rpi.tw.escience.waterquality.datasource.DataSourceModule;
import edu.rpi.tw.escience.waterquality.test.TestModuleConfiguration;

import junit.framework.TestCase;

public class DataSourceModuleTests extends TestCase {

	@Test
	public void testGetName() {
		DataSourceModule module = new DataSourceModule();
		assertEquals("Data Source", module.getName());
	}
	
	@Test
	public void testVersion() {
		DataSourceModule module = new DataSourceModule();
		assertEquals(1, module.getMajorVersion());
		assertEquals(0, module.getMinorVersion());
		assertNull(module.getExtraVersion());
	}
	
	@Test
	public void testModuleConfiguration() {
		TestModuleConfiguration config = new TestModuleConfiguration();
		DataSourceModule module = new DataSourceModule();
		module.setModuleConfiguration(config);
	}
	
}
