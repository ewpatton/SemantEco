package edu.rpi.tw.eScience.WaterQualityPortal.WebService;

public interface CachingQueryEngine extends QueryEngine {
	public void setCache(Cache cache);
	public Cache getCache();
}
