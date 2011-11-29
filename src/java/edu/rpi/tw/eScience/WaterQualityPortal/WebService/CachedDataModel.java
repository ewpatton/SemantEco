package edu.rpi.tw.eScience.WaterQualityPortal.WebService;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.TreeSet;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;

public class CachedDataModel extends OntModelWrapper {
	
	TreeSet<String> uris = new TreeSet<String>();
	Cache cache = null;
	
	public CachedDataModel(OntModel model) {
		super(model);
	}

	public void setCache(Cache c) {
		cache = c;
	}
	
	public Cache getCache() {
		return cache;
	}
	
	public Model read(String arg0) {
		if(uris.contains(arg0)) return this;
		if(cache!=null) {
			String result;
			try {
				if(cache.isCached(arg0) && !cache.isStale(arg0)) {
					result = cache.cachedContents(arg0);
				}
				else {
					result = "";
					int i;
					URL url = new URL(arg0);
					URLConnection conn = url.openConnection();
					conn.connect();
					long modifiedTime = conn.getLastModified();
					long expiresTime = conn.getExpiration();
					if(modifiedTime == 0) modifiedTime = conn.getDate();
					InputStream is = conn.getInputStream();
					while((i=is.read())!=-1) {
						result += Character.toString((char)i);
					}
					try {
						is.close();
					}
					catch(Exception e1) { };
					Calendar modified, expires;
					(modified = Calendar.getInstance()).setTimeInMillis(modifiedTime);
					(expires = Calendar.getInstance()).setTimeInMillis(expiresTime);
					if(cache.isCached(arg0))
						cache.update(arg0, result, modified, expires);
					else
						cache.add(arg0, result, modified, expires);
				}
			}
			catch(IOException e) {
				e.printStackTrace();
				return this;
			}
			super.read(arg0);
			return this;
		}
		else {
			super.read(arg0);
			uris.add(arg0);
			return this;
		}
	}
}
