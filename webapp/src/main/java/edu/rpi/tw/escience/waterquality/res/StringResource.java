package edu.rpi.tw.escience.waterquality.res;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import edu.rpi.tw.escience.waterquality.Module;

public class StringResource extends OwnedResource {

	private String content = null;
	
	public StringResource(Module module, String content) {
		super(module);
		this.content = content;
	}

	@Override
	public String getPath() {
		return null;
	}

	@Override
	public InputStream open() throws IOException {
		return new ByteArrayInputStream(content.getBytes("UTF-8"));
	}

	@Override
	public boolean isJspResource() {
		return false;
	}
	
	@Override
	public String toString() {
		return content;
	}

}
