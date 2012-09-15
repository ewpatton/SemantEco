package test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.rpi.tw.escience.waterquality.impl.ModuleClassLoader;

import junit.framework.TestCase;

public class ModuleClassLoaderTest extends TestCase {

	private static final int BUFSIZE = 8192;
	
	protected static File getTestDir() {
		final File modDir = new File("target/test/WEB-INF/modules/");
		modDir.mkdirs();
		return modDir;
	}

	@BeforeClass
	public void setUpAll() {
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.DEBUG);
	}
	
	@Before
	public void setUp() {
		final File modDir = getTestDir();
		modDir.delete();
		modDir.mkdirs();
	}
	
	@Test
	public void test() throws Exception {
		File jar = createTestJar();
		try {
			new ModuleClassLoader(getTestDir().getAbsolutePath()+"/unknown.jar");
			fail("Did not get IOException trying to open unknown jar");
		}
		catch(Exception e) {
			
		}
		ModuleClassLoader mcl = new ModuleClassLoader(jar.getAbsolutePath());
		assertTrue(mcl.getModules().size() > 0);
		mcl.loadClass("test.MockModule");
		try {
			if(null!=mcl.loadClass("test.FakeModule")) {
				fail();
			}
		}
		catch(Exception e) {
			
		}
		jar = createInvalidJar();
		try {
			new ModuleClassLoader(jar.getAbsolutePath());
			fail("Did not get an IllegalArgumentException for giving invalid jar");
		}
		catch(Exception e) {
			
		}
	}
	
	protected File createInvalidJar() throws Exception {
		final File modDir = getTestDir();
		final File target = new File(modDir, "invalid-module.jar");
		ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(target));
		zos.putNextEntry(new ZipEntry("META-INF/"));
		zos.closeEntry();
		zos.putNextEntry(new ZipEntry("META-INF/res/"));
		zos.closeEntry();
		zos.putNextEntry(new ZipEntry("META-INF/res/hello.txt"));
		PrintStream ps = new PrintStream(zos);
		ps.println("Hello world!");
		zos.closeEntry();
		ps.close();
		return target;
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
		zos.putNextEntry(new ZipEntry("test/MockModule2.class"));
		zos.closeEntry();
		zos.putNextEntry(new ZipEntry("test/ModuleManagerImplTest.class"));
		cls = getClass().getClassLoader().getResourceAsStream("test/ModuleManagerImplTest.class");
		while((read = cls.read(buffer)) > 0) {
			zos.write(buffer, 0, read);
		}
		cls.close();
		zos.closeEntry();
		ps.close();
		return target;
	}

}
