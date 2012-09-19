package test;

import java.nio.CharBuffer;

import org.apache.catalina.websocket.WsOutbound;
import org.apache.log4j.Logger;
import org.junit.Test;

import edu.rpi.tw.escience.waterquality.ClientRequest;
import edu.rpi.tw.escience.waterquality.util.SemantAquaConfiguration;

import junit.framework.TestCase;

public class ClientRequestTest extends TestCase {

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
	
	protected static class TestConfiguration extends SemantAquaConfiguration {
		private static final long serialVersionUID = 1L;
		public TestConfiguration() {
			super();
			this.setProperty("debug", "true");
			install(this);
		}
	}
	
	@Test
	public void test() {
		new TestConfiguration();
		TestWsOutbound ws = new TestWsOutbound();
		Logger req = new ClientRequest(getClass().getName(), null, ws);
		req.info("Test");
	}
	
}
