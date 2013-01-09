package edu.rpi.tw.escience.semanteco.test;

import java.io.IOException;
import java.io.InputStream;

import edu.rpi.tw.escience.semanteco.Module;
import edu.rpi.tw.escience.semanteco.ModuleConfiguration;
import edu.rpi.tw.escience.semanteco.Resource;

/**
 * TestStringResource is used to provide
 * a string-based resource constructed via
 * the {@link ModuleConfiguration#generateStringResource(String)} method.
 * @author ewpatton
 *
 */
public class TestStringResource extends MockResource implements Resource {

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
