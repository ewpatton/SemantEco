package edu.rpi.tw.escience.waterquality.test;

import java.io.IOException;
import java.io.InputStream;

import edu.rpi.tw.escience.waterquality.Module;
import edu.rpi.tw.escience.waterquality.Resource;

/**
 * MockResource provides a base implementation of
 * Resource that unit tests can override to support
 * the exact functionality required for performing
 * the unit test. All methods throw an UnsupportedOperationException
 * by default unless explicitly overridden by a subclass.
 * @author ewpatton
 *
 */
public class MockResource implements Resource {

	@Override
	public String getPath() {
		throw new UnsupportedOperationException();
	}

	@Override
	public InputStream open() throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isJspResource() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Module getOwner() {
		throw new UnsupportedOperationException();
	}

}
