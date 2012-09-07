package edu.rpi.tw.escience.WaterQualityPortal.WebService;

import java.io.File;

import edu.rpi.tw.escience.WaterQualityPortal.zip.ZipCodeLookup;

@Deprecated
public class Session {
	String id;
	ZipCodeLookup zipCode;
	WaterAgentInstance instance;
	long timeout;
	File basePath;
	
	public Session(String zip) throws Exception {
		id = Integer.toString(Long.toString(System.currentTimeMillis()).hashCode());
		basePath = new File("/tmp/wqp/"+id+"/");
		basePath.mkdirs();
		long start=System.currentTimeMillis();
		timeout = System.currentTimeMillis()+300000;
		zipCode = ZipCodeLookup.execute(zip);
		System.err.println("Performed zip code lookup in "+(System.currentTimeMillis()-start)+" ms");
		start = System.currentTimeMillis();
		instance = new WaterAgentInstance(zipCode,basePath);
		System.err.println("Created water agent instance in "+(System.currentTimeMillis()-start)+" ms");
	}
	
	public String getId() {
		return id;
	}
	
	public String performQuery(String query) {
		timeout = System.currentTimeMillis()+300000;
		return instance.performQuery(query);
	}
	
	public ZipCodeLookup getZipCode() {
		return zipCode;
	}
	
	public long getTimeout() {
		return timeout;
	}
	
	boolean deletePath(File f) {
		if(f.isDirectory()) {
			File[] contents = f.listFiles();
			for(int i=0;i<contents.length;i++) {
				deletePath(contents[i]);
			}
		}
		return f.delete();
	}
	
	public void clearSession() {
		deletePath(basePath);
		zipCode = null;
		instance = null;
		basePath = null;
	}
}
