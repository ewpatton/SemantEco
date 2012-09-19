package edu.rpi.tw.escience.waterquality.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.log4j.Logger;

import edu.rpi.tw.escience.waterquality.Module;

/**
 * ModuleClassLoader is used to load classes from a module's JAR file.
 * It is also responsible for verifying the integrity of the JAR and
 * locating any classes that implement Module so that they can be
 * retrieved by the ModuleManager for later use.
 * 
 * @author ewpatton
 *
 */
public class ModuleClassLoader extends ClassLoader {

	private Map<String, Class<?>> classes = new HashMap<String, Class<?>>();
	private Set<Class<? extends Module>> modules = new HashSet<Class<? extends Module>>();
	private static final int BUFSIZE = 8192;
	private Logger log = Logger.getLogger(ModuleClassLoader.class);
	
	/**
	 * Constructs a ModuleClassLoader from the JAR at the specified
	 * path.
	 * @param path
	 */
	@SuppressWarnings("unchecked")
	public ModuleClassLoader(String path) {
		super(Thread.currentThread().getContextClassLoader());
		log.trace("ModuleClassLoader");
		final Class<?> module = Module.class;
		try {
			final JarFile file = new JarFile(path);
			final Enumeration<JarEntry> entries = file.entries();
			while(entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				log.debug("Found entry in jar: "+entry.getName());
				if(entry.getName().endsWith(".class")) {
					Class<?> cls = registerClass(file, entry);
					if(cls == null) {
						continue;
					}
					if(module.isAssignableFrom(cls)) {
						modules.add((Class<? extends Module>) cls);
					}
				}
			}
			if(modules.size() == 0) {
				throw new IllegalArgumentException("Not a valid module: "+path);
			}
		}
		catch(IOException e) {
			throw new IllegalArgumentException("Not a valid module: "+path, e);
		}
	}
	
	protected Class<?> findClass(String name) {
		return classes.get(name);
	}
	
	protected final Class<?> registerClass(JarFile file, JarEntry entry) throws IOException {
		final String clsName = entry.getName().replaceAll("/", ".").substring(0, entry.getName().length()-".class".length());
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final InputStream is = file.getInputStream(entry);
		final byte[] buffer = new byte[BUFSIZE];
		int read = 0;
		while((read = is.read(buffer))>0) {
			baos.write(buffer, 0, read);
		}
		is.close();
		final byte[] byteCode = baos.toByteArray();
		Class<?> cls = null;
		try {
			cls = defineClass(clsName, byteCode, 0, byteCode.length);
		}
		catch(ClassFormatError e) {
			log.warn("Invalid class file", e);
		}
		catch(NoClassDefFoundError e) {
			log.warn("No class definition", e);
		}
		if(cls != null) {
			classes.put(clsName, cls);
		}
		return cls;
	}
	
	/**
	 * Gets the set of module classes found in the JAR file
	 * loaded by this ModuleClassLoader.
	 * @return
	 */
	public Set<Class<? extends Module>> getModules() {
		return Collections.unmodifiableSet(modules);
	}
	
}
