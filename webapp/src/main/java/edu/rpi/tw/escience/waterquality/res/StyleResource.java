package edu.rpi.tw.escience.waterquality.res;

import java.io.IOException;
import java.io.InputStream;

import edu.rpi.tw.escience.waterquality.Module;

/**
 * StyleResource provides a reference to a cascading
 * stylesheet used by the module for skinning its facet.
 * 
 * @author ewpatton
 *
 */
public class StyleResource extends OwnedResource {
	
	private String path = null;
	
	/**
	 * Constructs a new StyleResource for a module
	 * from the specified path.
	 * @param owner
	 * @param path
	 */
	public StyleResource(Module owner, String path) {
		super(owner);
		this.path = path;
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public InputStream open() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
