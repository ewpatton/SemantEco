package edu.rpi.tw.escience.waterquality.res;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import edu.rpi.tw.escience.waterquality.Module;

/**
 * StringResource provides a wrapper for String content
 * to be pushed to the client as a resource without
 * having to write it to a file.
 * 
 * @author ewpatton
 *
 */
public class StringResource extends OwnedResource {

	private String content = null;
	
	/**
	 * Constructs a new StringResource for a module using
	 * the specified content.
	 * @param module
	 * @param content
	 */
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
