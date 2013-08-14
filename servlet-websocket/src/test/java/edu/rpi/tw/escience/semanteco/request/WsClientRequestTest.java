package edu.rpi.tw.escience.semanteco.request;

import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.catalina.websocket.WsOutbound;
import org.apache.log4j.Logger;
import org.junit.Test;

import edu.rpi.tw.escience.semanteco.Domain;
import edu.rpi.tw.escience.semanteco.test.TestModuleManager;
import edu.rpi.tw.escience.semanteco.util.SemantEcoConfiguration;

import junit.framework.TestCase;

public class WsClientRequestTest extends TestCase {

	protected static class TestWsOutbound extends WsOutbound {

		public TestWsOutbound() {
			super(null);
		}
		
		@Override
		public void writeTextMessage(CharBuffer cb) {
			
		}
		
		@Override
		public void flush() {
			
		}
		
	}
	
	protected static class TestConfiguration extends SemantEcoConfiguration {
		private static final long serialVersionUID = 1L;
		public TestConfiguration() {
			super(null);
			this.setProperty("debug", "true");
			install(this);
		}
	}
	
	@Test
	public void test() {
		new TestConfiguration();
		TestWsOutbound ws = new TestWsOutbound();
		Logger req = new WsClientRequest(getClass().getName(), null, null,
				new TestModuleManager() {
			public List<Domain> listDomains() {
				return new ArrayList<Domain>();
			}
		}, ws, null);
		req.info("Test");
	}
	
}
