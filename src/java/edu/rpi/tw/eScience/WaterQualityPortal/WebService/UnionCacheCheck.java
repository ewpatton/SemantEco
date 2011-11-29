package edu.rpi.tw.eScience.WaterQualityPortal.WebService;

import java.io.IOException;
import java.util.Calendar;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import edu.rpi.tw.eScience.WaterQualityPortal.WebService.Cache.CacheStalenessCheck;

public class UnionCacheCheck implements CacheStalenessCheck {
	TreeMap<String, CacheStalenessCheck> checks = new TreeMap<String, CacheStalenessCheck>();
	TreeMap<String, Calendar> modified_dates = new TreeMap<String, Calendar>();
	TreeMap<String, String> results = new TreeMap<String, String>();
	Calendar lastModified = null;

	@Override
	public String isStale(String uri, Calendar modified) throws IOException {
		// TODO finish this method
		results.clear();
		for(Entry<String, CacheStalenessCheck> e : checks.entrySet()) {
			String uri2 = e.getKey();
			String result = e.getValue().isStale(uri2, modified_dates.get(uri2));
			if(result!=null) {
				results.put(uri2, result);
			}
		}
		return null;
	}
	
	public void put(String uri, Calendar modified, CacheStalenessCheck check) {
		if(lastModified==null||lastModified.before(modified)) {
			lastModified = (Calendar)modified.clone();
		}
		modified_dates.put(uri, modified);
		checks.put(uri, check);
	}
	
	public void remove(String uri) {
		checks.remove(uri);
		modified_dates.remove(uri);
	}
	
	public Map<String, String> getUpdatedGraphs() {
		return results;
	}

	@Override
	public Calendar getModifiedDate() {
		return lastModified;
	}

	@Override
	public Calendar getExpirationDate() {
		Calendar expirationDate = null;
		for(CacheStalenessCheck c : checks.values()) {
			Calendar date = c.getExpirationDate();
			if(expirationDate == null || expirationDate.before(date)) {
				expirationDate = date;
			}
		}
		return (Calendar)expirationDate.clone();
	}
	
}
