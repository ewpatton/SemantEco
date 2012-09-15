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
import edu.rpi.tw.escience.waterquality.SemantAquaUI;
import edu.rpi.tw.escience.waterquality.query.Query;
import edu.rpi.tw.escience.waterquality.util.JavaScriptGenerator;

public class ModuleManagerImpl implements ModuleManager, FileListener {

	private static final int BUFSIZE = 8096;
	
	private List<Module> modules = new LinkedList<Module>();
	private Map<String, Module> moduleMap = new HashMap<String, Module>();
	private Logger log = Logger.getLogger(ModuleManagerImpl.class);
	private Map<String, ModuleClassLoader> classLoaders = new HashMap<String, ModuleClassLoader>();
	private String path = null;
	private volatile long lastModified = 0;
	
	public ModuleManagerImpl() {
		log.trace("ModuleManagerImpl");
	}
	
	public ModuleManagerImpl(String path) {
		log.trace("ModuleManagerImpl");
		this.path = path;
		try {
			log.debug("Starting VFS file manager monitor");
			FileSystemManager manager = VFS.getManager();
			FileObject file = manager.resolveFile(path);
			DefaultFileMonitor fm = new DefaultFileMonitor(this);
			fm.setDelay(5000);
			fm.setRecursive(true);
			log.debug("Monitoring "+file);
			fm.addFile(file);
			fm.start();
			File[] modules = new File(path).listFiles();
			for(int i=0;i<modules.length;i++) {
				if(modules[i].getAbsolutePath().endsWith(".jar")) {
					FileObject modJar = manager.resolveFile("file:"+modules[i].getAbsolutePath());
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
	public void buildUserInterface(SemantAquaUI ui, Map<String, String> params) {
		log.trace("buildUserInterface");
		log.debug("Modules: "+modules.size());
		for(Module module : modules) {
			log.debug("Visiting module "+module.getName());
			module.visit(ui, params);
		}
	}

	@Override
	public void buildOntologyModel(OntModel model, Map<String, String> params) {
		log.trace("buildOntologyModel");
		for(Module module : modules) {
			module.visit((OntModel)model, params);
		}
	}

	@Override
	public void buildDataModel(Model model, Map<String, String> params) {
		log.trace("buildDataModel");
		for(Module module : modules) {
			module.visit((Model)model, params);
		}
	}

	@Override
	public String updateFragmentForFacet(Module module,
			Map<String, String> params) {
		log.trace("updateFragmentForFacet");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void augmentQuery(Query query, Map<String, String> params) {
		log.trace("augmentQuery");
		augmentQuery(query, params, null);
	}

	@Override
	public void augmentQuery(Query query, Map<String, String> params,
			Module originator) {
		log.trace("augmentQuery");
		for(Module module : modules) {
			if(module == originator) {
				continue;
			}
			module.visit(query, params);
		}
	}

	@Override
	public List<Module> listModules() {
		log.trace("listModules");
		return Collections.unmodifiableList(modules);
	}

	@Override
	public void fileCreated(FileChangeEvent event) {
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
				Set<Class<? extends Module>> newModules = loader.getModules();
				if(newModules.size() == 0) {
					return;
				}
				final String path = explodeJar(event.getFile().getName().getPath());
				for(Class<? extends Module> i : newModules) {
					Module module = null;
					try {
						log.debug("Generating new module instance");
						module = i.newInstance();
					} catch (InstantiationException e) {
						log.warn("Unable to instantiate module "+i.getSimpleName(), e);
					} catch (IllegalAccessException e) {
						log.warn("Unable to instantiate module "+i.getSimpleName(), e);
					}
					if(module != null) {
						// TODO change this to a regex
						InputStream properties = null;
						JarFile jar = null;
						try {
							jar = new JarFile(event.getFile().getName().getPath());
							ZipEntry entry = jar.getEntry("module.properties");
							if(entry != null) {
								properties = jar.getInputStream(entry);
							}
						}
						catch(IOException e) {
							
						}
						finally {
							installModule(module, path, properties);
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
				resetLastModified();
			}
		}
	}
	
	protected String explodeJar(final String path) {
		log.trace("Exploding jar");
		try {
			final String name = path.substring(path.lastIndexOf('/')+1, path.length()-4);
			final File resDir = new File(this.path+"/../../resources/"+name+"/");
			if(!resDir.exists()) {
				resDir.mkdirs();
			}
			JarFile jar = new JarFile(path);
			Enumeration<JarEntry> entries = jar.entries();
			while(entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				if(entry.getName().startsWith("META-INF/res/") &&
						!entry.getName().endsWith("/")) {
					File dest = new File(resDir, entry.getName().substring(13));
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
	
	protected void copy(final InputStream is, final OutputStream os) 
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
	public void fileDeleted(FileChangeEvent event) {
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
				log.warn("Unable to instantiate module "+i.getSimpleName(), e);
			} catch (IllegalAccessException e) {
				log.warn("Unable to instantiate module "+i.getSimpleName(), e);
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
	
	protected void installModule(Module module, String path, InputStream properties) {
		log.debug("Installing module '"+module.getName()+"' version "+module.getMajorVersion()+"."+module.getMinorVersion()+
				(module.getExtraVersion() != null ? "-"+module.getExtraVersion() : ""));
		final String name = JavaScriptGenerator.cleanName(module.getName());
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
	
	protected void configureModule(Module module, String path, InputStream properties) {
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
	public void fileChanged(FileChangeEvent event) {
		// TODO Auto-generated method stub
		log.trace("fileChanged");
		resetLastModified();
	}
	
	protected void resetLastModified() {
		lastModified = System.currentTimeMillis();
	}
	
	@Override
	public long getLastModified() {
		return lastModified;
	}

}
