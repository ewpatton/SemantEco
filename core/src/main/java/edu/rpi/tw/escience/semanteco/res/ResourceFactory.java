package edu.rpi.tw.escience.semanteco.res;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import edu.rpi.tw.escience.semanteco.Module;
import edu.rpi.tw.escience.semanteco.Resource;

public final class ResourceFactory {

	private static final Map<String, Class<? extends OwnedResource>> typeMap =
			new HashMap<String, Class<? extends OwnedResource>>();

	public static final void registerType(String suffix,
			Class<? extends OwnedResource> clazz) {
		typeMap.put(suffix, clazz);
	}

	public static final Resource newInstance(Module owner, String path) {
		for(Entry<String, Class<? extends OwnedResource>> entry :
			typeMap.entrySet()) {
			if(path.endsWith(entry.getKey())) {
				try {
					Constructor<? extends OwnedResource> constructor =
							entry.getValue().getConstructor(Module.class,
									String.class);
					return constructor.newInstance(owner, path);
				} catch (Exception e) {
					throw new IllegalArgumentException("Unable to instantiate resource object due to internal error", e);
				}
			}
		}
		return new GenericResource(owner, path);
	}
}
