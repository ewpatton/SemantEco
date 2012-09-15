package test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.events.CreateEvent;
import org.junit.Before;
import org.junit.Test;

import edu.rpi.tw.escience.waterquality.Module;
import edu.rpi.tw.escience.waterquality.impl.ModuleManagerImpl;
import edu.rpi.tw.escience.waterquality.util.SemantAquaConfiguration;

import junit.framework.TestCase;

public class ModuleManagerImplTest extends TestCase {

	private static final int BUFSIZE = 8192;
	
	protected static File getTestDir() {
		final File modDir = new File("test/WEB-INF/modules/");
		modDir.mkdirs();
		return modDir;
	}
	
	protected static class TestConfig extends SemantAquaConfiguration {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public TestConfig() {
			SemantAquaConfiguration.setConfig(this);
		}
	}
	
	@Before
	public void setUp() {
		final File modDir = getTestDir();
		modDir.delete();
		modDir.mkdirs();
		new TestConfig();
	}
	
	@Test
	public void testConstructors() {
		final File modDir = getTestDir();
		new ModuleManagerImpl();
		new ModuleManagerImpl(modDir.getAbsolutePath());
	}
	
	@Test
	public void testGetModuleByName() {
		
	}
	
	@Test
	public void testBuildUI() {
		
	}
	
	@Test
	public void testBuildOntModel() {
		
	}
	
	@Test
	public void testBuildDataModel() {
		
	}
	
	@Test
	public void testUpdateFragment() {
		
	}
	
	@Test
	public void testAugmentQuery() {
		
	}
	
	@Test
	public void testListModules() {
		
	}
	
	@Test
	public void testFileCreated() throws Exception {
		try {
			final File testJar = createTestJar();
			FileObject obj = null;
			FileSystemManager fsManager = VFS.getManager();
			obj = fsManager.resolveFile("file:"+testJar.getAbsolutePath());
			assertNotNull("Unable to find "+testJar.getAbsolutePath(), obj);
			TestModuleManagerImpl test = new TestModuleManagerImpl();
			test.fileCreated(new CreateEvent(obj));
			Module m = test.getModuleByName("MockModule");
			assertNotNull("MockModule not found in test jar", m);
		}
		catch(Exception e) {
			e.printStackTrace();
			fail("Test failed due to exception");
		}
	}
	
	@Test
	public void testFileDeleted() {
		
	}
	
	@Test
	public void testFileChanged() {
		
	}
	
	protected static class TestModuleManagerImpl extends ModuleManagerImpl {
		public TestModuleManagerImpl() {
			super(getTestDir().getAbsolutePath());
		}
		
		public String explodeJar(final String path) {
			return super.explodeJar(path);
		}
		
		public void installModule(Module module, String path, InputStream properties) {
			super.installModule(module, path, properties);
		}
		
		public void uninstallModule(Module module) {
			super.uninstallModule(module);
		}
	}
	
	protected File createTestJar() throws Exception {
		final File modDir = getTestDir();
		final File target = new File(modDir, "test-module.jar");
		ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(target));
		zos.putNextEntry(new ZipEntry("META-INF/"));
		zos.closeEntry();
		zos.putNextEntry(new ZipEntry("META-INF/res/"));
		zos.closeEntry();
		zos.putNextEntry(new ZipEntry("META-INF/res/hello.txt"));
		PrintStream ps = new PrintStream(zos);
		ps.println("Hello world!");
		zos.closeEntry();
		zos.putNextEntry(new ZipEntry("test/"));
		zos.closeEntry();
		zos.putNextEntry(new ZipEntry("test/MockModule.class"));
		InputStream cls = getClass().getClassLoader().getResourceAsStream("test/MockModule.class");
		final byte[] buffer = new byte[BUFSIZE];
		int read = 0;
		while((read = cls.read(buffer)) > 0) {
			zos.write(buffer, 0, read);
		}
		cls.close();
		zos.closeEntry();
		ps.close();
		return target;
	}
	
	@Test
	public void testExplodeJar() {
		try {
			File jar = createTestJar();
			TestModuleManagerImpl test = new TestModuleManagerImpl();
			test.explodeJar(jar.getAbsolutePath());
			final File testFile = new File(getTestDir(), "../../resources/test-module/hello.txt");
			final File testMetaInf = new File(getTestDir(), "../../resources/test-module/META-INF/");
			assertTrue("hello.txt was not extracted from the test module", testFile.exists());
			assertFalse("incorrectly extracting meta-inf folder", testMetaInf.exists());
		}
		catch(Exception e) {
			e.printStackTrace();
			fail("Test failed due to exeception");
		}
	}
	
}
