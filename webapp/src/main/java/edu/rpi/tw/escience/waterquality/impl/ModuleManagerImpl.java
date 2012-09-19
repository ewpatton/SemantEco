package edu.rpi.tw.escience.waterquality.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.apache.commons.vfs2.FileChangeEvent;
import org.apache.commons.vfs2.FileListener;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.events.CreateEvent;
import org.apache.commons.vfs2.impl.DefaultFileMonitor;
import org.apache.log4j.Logger;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;

import edu.rpi.tw.escience.waterquality.Module;
import edu.rpi.tw.escience.waterquality.ModuleManager;
import edu.rpi.tw.escience.waterquality.Request;
import edu.rpi.tw.escience.waterquality.SemantAquaUI;
import edu.rpi.tw.escience.waterquality.query.Query;

/**
 * ModuleManagerImpl provides the default implementation of the ModuleManager interface
 * for the SemantAqua portal. It is responsible for monitoring the modules directory
 * and installing and configuring new modules or removing deleted modules from the 
 * runtime of the portal as needed.
 * 
 * @author ewpatton
 *
 */
public class ModuleManagerImpl implements ModuleManager, FileListener {

	private static final int BUFSIZE = 8096;
	
	private List<Module> modules = new LinkedList<Module>();
	private Map<String, Module> moduleMap = new HashMap<String, Module>();
	private Logger log = Logger.getLogger(ModuleManagerImpl.class);
	private Map<String, ModuleClassLoader> classLoaders = new HashMap<String, ModuleClassLoader>();
	private String path = null;
	private volatile long lastModified = 0;
	private static final String MODULE_ERROR = "Unable to instantiate module ";
	private static final int REFRESH_RATE = 5000;
	private static final String RES_DIR = "META-INF/res/";
	
	/**
	 * Default constructor
	 */
	public ModuleManagerImpl() {
		log.trace("ModuleManagerImpl");
	}
	
	/**
	 * Constructs a ModuleManagerImpl that will monitor the specified
	 * path for new JAR files and remove any modules originating from
	 * a removed JAR file in that path.
	 * @param path
	 */
	public ModuleManagerImpl(String path) {
		log.trace("ModuleManagerImpl");
		this.path = path;
		try {
			log.debug("Starting VFS file manager monitor");
			FileSystemManager manager = VFS.getManager();
			FileObject file = manager.resolveFile(path);
			DefaultFileMonitor fm = new DefaultFileMonitor(this);
			fm.setDelay(REFRESH_RATE);
			fm.setRecursive(true);
			log.debug("Monitoring "+file);
			fm.addFile(file);
			fm.start();
			File[] activeModules = new File(path).listFiles();
			for(int i=0;i<activeModules.length;i++) {
				if(activeModules[i].getAbsolutePath().endsWith(".jar")) {
					FileObject modJar = manager.resolveFile("file:"+activeModules[i].getAbsolutePath());
					if(modJar != null) {
						fileCreated(new CreateEvent(modJar));
					}
				}
			}
		} catch (FileSystemException e) {
			log.warn("Unable to start file manager", e);
		}
	}
	
	@Override
	public Module getModuleByName(String name) {
		log.trace("getModuleByName");
		return moduleMap.get(name);
	}

	@Override
	public void buildUserInterface(SemantAquaUI ui, Request request) {
		log.trace("buildUserInterface");
		log.debug("Modules: "+modules.size());
		for(Module module : modules) {
			log.debug("Visiting module "+module.getName());
			module.visit(ui, request);
		}
	}

	@Override
	public void buildOntologyModel(OntModel model, Request request) {
		log.trace("buildOntologyModel");
		for(Module module : modules) {
			module.visit((OntModel)model, request);
		}
	}

	@Override
	public void buildDataModel(Model model, Request request) {
		log.trace("buildDataModel");
		for(Module module : modules) {
			module.visit((Model)model, request);
		}
	}

	@Override
	public String updateFragmentForFacet(Module module,
			Request request) {
		log.trace("updateFragmentForFacet");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void augmentQuery(Query query, Request request) {
		log.trace("augmentQuery");
		augmentQuery(query, request, null);
	}

	@Override
	public void augmentQuery(Query query, Request request, Module originator) {
		log.trace("augmentQuery");
		for(Module module : modules) {
			if(module == originator) {
				continue;
			}
			module.visit(query, request);
		}
	}

	@Override
	public List<Module> listModules() {
		log.trace("listModules");
		return Collections.unmodifiableList(modules);
	}

	@Override
	public final void fileCreated(FileChangeEvent event) {
		log.trace("fileCreated");
		log.debug("Observed file created: "+event.getFile().getName().getPath());
		if(event.getFile().getName().getExtension().equals("jar")) {
			ModuleClassLoader loader = null;
			try {
				loader = new ModuleClassLoader(event.getFile().getName().getPath());
			}
			catch(IllegalArgumentException e) {
				return;
			}
			if(loader != null) {
				log.debug("Generated classloader");
				classLoaders.put(event.getFile().getName().getPath(), loader);
				final String resPath = explodeJar(event.getFile().getName().getPath());
				processLoader(loader, event.getFile().getName().getPath(),
						resPath);
				resetLastModified();
			}
		}
	}
	
	protected final void processLoader(final ModuleClassLoader loader, 
			final String jarPath, final String resPath) {
		Set<Class<? extends Module>> newModules = loader.getModules();
		for(Class<? extends Module> i : newModules) {
			Module module = null;
			try {
				log.debug("Generating new module instance");
				module = i.newInstance();
			} catch (InstantiationException e) {
				log.warn(MODULE_ERROR+i.getSimpleName(), e);
			} catch (IllegalAccessException e) {
				log.warn(MODULE_ERROR+i.getSimpleName(), e);
			}
			if(module != null) {
				InputStream properties = null;
				JarFile jar = null;
				try {
					jar = new JarFile(jarPath);
					ZipEntry entry = jar.getEntry("module.properties");
					if(entry != null) {
						properties = jar.getInputStream(entry);
					}
				}
				catch(IOException e) {
					
				}
				finally {
					installModule(module, resPath, properties);
					try {
						if(properties != null) {
							properties.close();
						}
						if(jar != null) {
							jar.close();
						}
					}
					catch(IOException e) {
						// don't care
					}
				}
			}
		}
	}
	
	protected final String explodeJar(final String path) {
		log.trace("Exploding jar");
		try {
			final String name = path.substring(path.lastIndexOf('/')+1, path.length()-".jar".length());
			final File resDir = new File(this.path+"/../../resources/"+name+"/");
			if(!resDir.exists()) {
				resDir.mkdirs();
			}
			JarFile jar = new JarFile(path);
			Enumeration<JarEntry> entries = jar.entries();
			while(entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				// TODO change this to a regex
				if(entry.getName().startsWith(RES_DIR) &&
						!entry.getName().endsWith("/")) {
					File dest = new File(resDir, entry.getName().substring(RES_DIR.length()));
					new File(dest.getParent()).mkdirs();
					copy(jar.getInputStream(entry), new FileOutputStream(dest));
				}
			}
			return "resources/"+name+"/";
		}
		catch (IOException e) {
			log.warn("Unable to explode jar: "+path);
		}
		return null;
	}
	
	protected final void copy(final InputStream is, final OutputStream os) 
			throws IOException {
		final byte[] buffer = new byte[BUFSIZE];
		int read = 0;
		while((read = is.read(buffer)) > 0) {
			os.write(buffer, 0, read);
		}
		is.close();
		os.close();
	}

	@Override
	public final void fileDeleted(FileChangeEvent event) {
		log.trace("fileDeleted");
		ModuleClassLoader loader = classLoaders.get(event.getFile().getName().getPath());
		if(loader == null) {
			return;
		}
		Set<Class<? extends Module>> newModules = loader.getModules();
		if(newModules.size() == 0) {
			return;
		}
		for(Class<? extends Module> i : newModules) {
			Module module = null;
			try {
				module = i.newInstance();
			} catch (InstantiationException e) {
				log.warn(MODULE_ERROR+i.getSimpleName(), e);
			} catch (IllegalAccessException e) {
				log.warn(MODULE_ERROR+i.getSimpleName(), e);
			}
			if(module != null) {
				uninstallModule(module);
			}
		}
		classLoaders.remove(event.getFile().getName().getPath());
		resetLastModified();
	}
	
	protected void uninstallModule(Module module) {
		log.debug("Uninstalling module "+module.getName()+" version "+module.getMajorVersion()+"."+module.getMinorVersion()+
				(module.getExtraVersion() != null ? "-"+module.getExtraVersion() : ""));
		if(moduleMap.containsKey(module.getName())) {
			moduleMap.remove(module.getName());
			modules.remove(module);
		}
	}
	
	protected final void installModule(Module module, String path, InputStream properties) {
		log.debug("Installing module '"+module.getName()+"' version "+module.getMajorVersion()+"."+module.getMinorVersion()+
				(module.getExtraVersion() != null ? "-"+module.getExtraVersion() : ""));
		final String name = module.getClass().getSimpleName();
		if(moduleMap.containsKey(name)) {
			Module oldModule = moduleMap.get(name);
			int pos = modules.indexOf(oldModule);
			modules.set(pos, module);
		}
		else {
			modules.add(module);
		}
		moduleMap.put(name, module);
		configureModule(module, path, properties);
	}
	
	protected final void configureModule(Module module, String path, InputStream properties) {
		ModuleConfigurationImpl config = new ModuleConfigurationImpl(module, path);
		if(properties != null) {
			try {
				config.load(properties);
			} catch (IOException e) {
				log.warn("Unable to load properties for module '"+module.getName()+"'", e);
			}
		}
		module.setModuleConfiguration(config);
	}

	@Override
	public final void fileChanged(FileChangeEvent event) {
		// TODO Auto-generated method stub
		log.trace("fileChanged");
		resetLastModified();
	}
	
	protected final void resetLastModified() {
		lastModified = System.currentTimeMillis();
	}
	
	@Override
	public long getLastModified() {
		return lastModified;
	}

}
