package edu.rpi.tw.escience.waterquality.res;

import java.io.IOException;
import java.io.InputStream;

import edu.rpi.tw.escience.waterquality.Module;

/**
 * ScriptResource provides a reference to a JavaScript
 * file provided by a specific module.
 * 
 * @author ewpatton
 *
 */
public class ScriptResource extends OwnedResource {
	
	private String path = null;
	
	/**
	 * Constructs a new ScriptResource for a module
	 * at the specified path.
	 * @param owner
	 * @param path
	 */
	public ScriptResource(Module owner, String path) {
		super(owner);
		this.path = path;
	}

	@Override
	public String getPath() {
		return this.path;
	}

	@Override
	public InputStream open() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
