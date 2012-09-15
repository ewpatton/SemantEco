package edu.rpi.tw.escience.waterquality.res;

import java.io.IOException;
import java.io.InputStream;

import edu.rpi.tw.escience.waterquality.Module;

public class ScriptResource extends OwnedResource {
	
	private String path = null;
	
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
