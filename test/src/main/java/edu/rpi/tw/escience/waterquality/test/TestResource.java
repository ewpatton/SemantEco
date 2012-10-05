package edu.rpi.tw.escience.waterquality.test;

import java.io.IOException;
import java.io.InputStream;

import edu.rpi.tw.escience.waterquality.Module;
import edu.rpi.tw.escience.waterquality.Resource;

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
