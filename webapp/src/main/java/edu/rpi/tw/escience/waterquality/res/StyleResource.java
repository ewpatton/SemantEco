package edu.rpi.tw.escience.waterquality.res;

import java.io.IOException;
import java.io.InputStream;

import edu.rpi.tw.escience.waterquality.Module;

public class StyleResource extends OwnedResource {
	
	private String path = null;
	
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
