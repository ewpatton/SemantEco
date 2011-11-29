package edu.rpi.tw.eScience.WaterQualityPortal.WebService;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public interface Cache {
	/**
	 * Checks whether a URI is cached by this cache or not
	 * 
	 * @param uri Resource to check
	 * @return true if the resource is in the cache, false otherwise
	 * @throws IOException If there are issues communicating with the underlying cache
	 * mechanism, an IOException will be thrown. The nature of the IOException depends
	 * on the underlying cache mechanism (e.g. a SQLException may be the cause of the
	 * IOException for SQL-backed caches).
	 */
	public boolean isCached(String uri) throws IOException;
	/**
	 * Convenience function for URI objects. See {@link #isCached(String)}.
	 * 
	 * @param uri
	 * @return
	 * @throws IOException
	 */
	public boolean isCached(URI uri) throws IOException;
	public String cachedContents(String uri) throws IOException;
	public String cachedContents(URI uri) throws IOException;
	public boolean isStale(String uri) throws IOException;
	public boolean isStale(URI uri) throws IOException;
	public void add(String uri, String content, Calendar modified, Calendar expires) throws IOException;
	public void add(URI uri, String content, Calendar modified, Calendar expires) throws IOException;
	public void update(String uri, String content, Calendar modified, Calendar expires) throws IOException;
	public void update(URI uri, String content, Calendar modified, Calendar expires) throws IOException;
	public void registerStalenessTest(String uri, CacheStalenessCheck test);
	public void registerStalenessTest(URI uri, CacheStalenessCheck test);
	
	public interface CacheStalenessCheck {
		/**
		 * Performs a check of the indicated URI based on the modified date
		 * and returns a string containing the modified content if the resource
		 * has been modified.
		 * 
		 * @param uri Resource to check for staleness
		 * @param modified Date the resource was modified
		 * @return Contents of the resource if changed, null otherwise
		 * @throws IOException If the underlying mechanism used by the cache checker fails
		 * an IOException will be thrown to indicate the issue.
		 */
		public String isStale(String uri, Calendar modified) throws IOException;
		/**
		 * Returns the last modified date of the resource, dependent on the underlying implementation.
		 * 
		 * @return A Calendar object encoding the last modified date.
		 */
		public Calendar getModifiedDate();
		/**
		 * Returns the expiration date of the resource, if any.
		 * 
		 * @return A Calendar object encoding the expiration date.
		 */
		public Calendar getExpirationDate();
	}
	
	public static final CacheStalenessCheck HttpIfModifiedSince = new CacheStalenessCheck() {

		final SimpleDateFormat http_date = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
		Calendar modifiedDate = Calendar.getInstance();
		Calendar expirationDate = Calendar.getInstance();
		
		@Override
		public String isStale(String uri, Calendar modified) throws IOException {
			String result = "";
			try {
				URL url = new URL(uri);
				String date = http_date.format(modified);
				HttpURLConnection conn = (HttpURLConnection)url.openConnection();
				conn.addRequestProperty("If-Modified-Since", date);
				conn.connect();
				if(0!=conn.getExpiration()) {
					expirationDate.setTimeInMillis(conn.getExpiration());
				}
				if(conn.getResponseCode()==304) {
					conn.disconnect();
					return null;
				}
				if(0!=conn.getExpiration()) {
					expirationDate.setTimeInMillis(conn.getExpiration());
				}
				else {
					expirationDate = Calendar.getInstance();
					expirationDate.add(Calendar.MONTH, 1);
				}
				modifiedDate.setTimeInMillis(conn.getLastModified());
				InputStream is = conn.getInputStream();
				int i;
				while((i=is.read())!=-1) {
					result += Character.toString((char)i);
				}
				is.close();
				conn.disconnect();
				return result;
				
			}
			catch(IOException e) {
				throw e;
			}
			catch(Exception e) {
				throw new IOException(e);
			}
		}

		@Override
		public Calendar getModifiedDate() {
			return modifiedDate;
		}

		@Override
		public Calendar getExpirationDate() {
			return expirationDate;
		}
		
	};
}
