package edu.rpi.tw.escience.WaterQualityPortal.WebService;

public class Configuration {
	public static final String TRIPLE_STORE = "http://sparql.tw.rpi.edu/virtuoso/sparql";
	public static final int LISTEN_PORT = 14490;
	public static final int WORKERS = 30;
	public static final String LISTEN_ADDR = "localhost";
	public static final String CACHEDB_URL = "jdbc:mysql://localhost:3306";
	public static final String CACHEDB_USER = "semanteco";
	public static final String CACHEDB_PASS = "sdf9x*51YVeu(#.-4nX";
	public static final String CACHEDB_DB = "sparql_cache";
	public static final String CACHEDB_PREFIX = "water_";
}
