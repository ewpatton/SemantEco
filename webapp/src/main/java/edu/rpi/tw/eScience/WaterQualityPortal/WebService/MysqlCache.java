package edu.rpi.tw.eScience.WaterQualityPortal.WebService;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONObject;

public class MysqlCache implements Cache {
	
	String url, user, pass, db;
	Connection conn = null;
	String prefix = "";
	Map<String, CacheStalenessCheck> staleness = new TreeMap<String, CacheStalenessCheck>();
	
	public MysqlCache() {
		url = null;
		user = null;
		pass = null;
		db = null;
	}
	
	public MysqlCache(String url, String user, String pass, String db) throws IOException {
		this.url = url;
		this.user = user;
		this.pass = pass;
		this.db = db;
		connect();
		verify();
	}
	
	public MysqlCache(String url, String user, String pass, String db, String prefix) throws IOException {
		this.url = url;
		this.user = user;
		this.pass = pass;
		this.db = db;
		this.prefix = prefix;
		connect();
		verify();
	}
	
	protected void verify() throws IOException {
		try {
			Statement st = conn.createStatement();
			st.executeUpdate("CREATE TABLE IF NOT EXISTS "+prefix+"cache(hash bigint unsigned not null primary key, modified datetime not null, expires datetime, content longtext)");
		}
		catch(SQLException e) {
			throw new IOException(e);
		}
	}
	
	public void connect() throws IOException {
		if(this.url == null)
			throw new IllegalArgumentException("URL cannot be null");
		if(this.user == null)
			throw new IllegalArgumentException("User cannot be null");
		if(this.pass == null)
			throw new IllegalArgumentException("Pass cannot be null");
		if(this.db == null)
			throw new IllegalArgumentException("DB cannot be null");
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(url+"/"+db,user,pass);
		}
		catch(Exception e) {
			throw new IOException(e);
		}
	}
	
	public void close() throws IOException {
		if(conn != null)
		try {
			conn.close();
		}
		catch(SQLException e) {
			throw new IOException(e);
		}
	}

	@Override
	public boolean isCached(String uri) throws IOException {
		try {
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery("SELECT content FROM "+prefix+"cache WHERE hash = "+uri.hashCode());
			if(rs.first()) {
				rs.close();
				return true;
			}
			else {
				rs.close();
				return false;
			}
		}
		catch(SQLException e) {
			throw new IOException(e);
		}
	}

	@Override
	public boolean isCached(URI uri) throws IOException {
		return isCached(uri.toASCIIString());
	}

	@Override
	public String cachedContents(String uri) throws IOException {
		try {
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery("SELECT content FROM "+prefix+"cache WHERE hash = "+uri.hashCode());
			if(rs.first()) {
				String res = rs.getString("content");
				rs.close();
				return res;
			}
			else {
				rs.close();
				return null;
			}
		}
		catch(SQLException e) {
			throw new IOException(e);
		}
	}

	@Override
	public String cachedContents(URI uri) throws IOException {
		return cachedContents(uri.toASCIIString());
	}

	@Override
	public boolean isStale(String uri) throws IOException {
		try {
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery("SELECT modified, expires FROM "+prefix+"cache WHERE hash = "+uri.hashCode());
			if(rs.first()) {
				if(rs.getDate("expires").getTime()!=0 && rs.getDate("expires").before(new java.sql.Date(System.currentTimeMillis())))
					return true;
				java.sql.Date date = rs.getDate("modified");
				rs.close();
				URL url;
				if(staleness.containsKey(uri)) {
					Calendar c = Calendar.getInstance();
					c.setTime(date);
					String str = staleness.get(uri).isStale(uri, c);
					if(str==null) return false;
					
					return true;
				}
				else {
					url = new URL(uri);
					HttpURLConnection conn = (HttpURLConnection)url.openConnection();
					conn.setIfModifiedSince(date.getTime());
					conn.connect();
					if(conn.getResponseCode()==304) {
						conn.disconnect();
						return false;
					}
					else {
						try {
							String response = "";
							InputStream is = conn.getInputStream();
							int i;
							while((i=is.read())!=0) {
								response += Character.toString((char)i);
							}
							JSONObject obj = new JSONObject(response);
							JSONArray ans = obj.getJSONObject("results").getJSONArray("bindings");
							String xsddate = ans.getJSONObject(0).getString("value");
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
							Date d = sdf.parse(xsddate);
							st.executeUpdate("UPDATE "+prefix+"cache " +
											 "SET modified = \""+(new java.sql.Date(d.getTime()))+"\", " +
											 "expires = \""+(new java.sql.Date(conn.getExpiration()))+"\" "+
											 "WHERE hash = "+uri.hashCode());
						}
						catch(Exception e) {
							
						}
						conn.disconnect();
						return true;
					}
				}
			}
			else {
				rs.close();
				return true;
			}
		}
		catch(SQLException e) {
			throw new IOException(e);
		}
	}

	@Override
	public boolean isStale(URI uri) throws IOException {
		return isStale(uri.toASCIIString());
	}

	@Override
	public void add(String uri, String content, Calendar modified, Calendar expires) throws IOException {
		java.sql.Date modifiedDate = new java.sql.Date(modified.getTime().getTime());
		java.sql.Date expiresDate = new java.sql.Date(expires.getTime().getTime());
		try {
			Statement st = conn.createStatement();
			st.executeUpdate("INSERT INTO "+prefix+"cache "+
							 "VALUES ("+uri.hashCode()+", \""+content+"\", \""+
							 modifiedDate+"\", \""+expiresDate+"\")");
		}
		catch(SQLException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void add(URI uri, String content, Calendar modified, Calendar expires) throws IOException {
		add(uri.toASCIIString(), content, modified, expires);
	}

	@Override
	public void update(String uri, String content, Calendar modified,
			Calendar expires) throws IOException {
		java.sql.Date modifiedDate = new java.sql.Date(modified.getTime().getTime());
		java.sql.Date expiresDate = new java.sql.Date(expires.getTime().getTime());
		try {
			Statement st = conn.createStatement();
			st.executeUpdate("UPDATE "+prefix+"cache "+
							 "SET content=\""+content+"\", "+
							 "modified = \""+modifiedDate+"\", "+
							 "expires = \""+expiresDate+"\" "+
							 "WHERE hash = "+uri.hashCode());
			st.close();
		}
		catch(SQLException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void update(URI uri, String content, Calendar modified,
			Calendar expires) throws IOException {
		update(uri.toASCIIString(), content, modified, expires);
	}

	@Override
	public void registerStalenessTest(String uri, CacheStalenessCheck test) {
		staleness.put(uri, test);
	}

	@Override
	public void registerStalenessTest(URI uri, CacheStalenessCheck test) {
		registerStalenessTest(uri.toASCIIString(), test);
	}

}
