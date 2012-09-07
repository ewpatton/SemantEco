package edu.rpi.tw.escience.WaterQualityPortal.WebService;

public interface CachingQueryEngine extends QueryEngine {
	public void setCache(Cache cache);
	public Cache getCache();
}
