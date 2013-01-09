package edu.rpi.tw.escience.semanteco.test;

import java.io.IOException;
import java.io.InputStream;

import edu.rpi.tw.escience.semanteco.Module;
import edu.rpi.tw.escience.semanteco.Resource;

/**
 * TestResource provides a base implementation of Resource
 * that, unlike the MockResource, does not throw exceptions
 * if an operation should return correctly.
 * @author ewpatton
 *
 */
public class TestResource extends MockResource implements Resource {

	@Override
	public String getPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStream open() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isJspResource() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Module getOwner() {
		// TODO Auto-generated method stub
		return null;
	}

}
