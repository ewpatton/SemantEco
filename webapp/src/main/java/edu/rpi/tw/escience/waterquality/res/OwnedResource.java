package edu.rpi.tw.escience.waterquality.res;

import java.lang.ref.WeakReference;

import edu.rpi.tw.escience.waterquality.Module;
import edu.rpi.tw.escience.waterquality.Resource;

public abstract class OwnedResource implements Resource {

	private WeakReference<Module> owner = null; 
	
	public OwnedResource(Module owner) {
		this.owner = new WeakReference<Module>(owner);
	}
	
	@Override
	final public Module getOwner() {
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
