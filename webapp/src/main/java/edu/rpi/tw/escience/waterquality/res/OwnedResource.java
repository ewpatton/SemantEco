package edu.rpi.tw.escience.waterquality.res;

import java.lang.ref.WeakReference;

import edu.rpi.tw.escience.waterquality.Module;
import edu.rpi.tw.escience.waterquality.Resource;

/**
 * OwnedResource is the base class for the various Resource implementations.
 * It keeps a weak reference to the module it belongs to for the purpose
 * of locating files on disk.
 * 
 * @author ewpatton
 *
 */
public abstract class OwnedResource implements Resource {

	private WeakReference<Module> owner = null; 
	
	/**
	 * Default constructor
	 * @param owner
	 */
	public OwnedResource(Module owner) {
		this.owner = new WeakReference<Module>(owner);
	}
	
	@Override
	public final Module getOwner() {
		if(owner == null) {
			return null;
		}
		return owner.get();
	}
	
	@Override
	public boolean isJspResource() {
		return false;
	}

}
