package edu.rpi.tw.eScience.WaterQualityPortal.epa;

import java.io.*;
import java.net.*;

public class EpaCommAgent {
	static int BUFFER_SIZE = 4096;
	
	protected void getCSVFile(String target, String content, String outputFile){
		//String target = "http://www.epa-echo.gov/cgi-bin/effluentdata.cgi";
		//String content = "permit=NY0261343&pipe=all&paramtr=all&monlocn=all&period=all&outt=all&date=20070701%7C20100630&charts=viol&tool=echo&filetype=csv";
		
		//Phase1: do the post
		//System.out.println("Starting getCSVFile");
		URL url = null;
		HttpURLConnection conn = null; 
		
		try {			
			url = new URL(target);
			conn = (HttpURLConnection)url.openConnection();			
			// Set connection parameters.
			conn.setDoInput (true);
			conn.setDoOutput (true);
			conn.setUseCaches (false);
			// Make server believe we are form data...

			//System.out.println("About to post\nURL: "+target+ "content: " + content);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Content-Length", String.valueOf(content.length()));
			conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			conn.setRequestProperty("Keep-Alive", "115");
			conn.setRequestProperty("Connection", "keep-alive");
			conn.setRequestProperty("Accept-Language", "en-us,en;q=0.5");
			conn.setRequestProperty("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");

			DataOutputStream out;
			// Write out the bytes of the content string to the stream.
			out = new DataOutputStream (conn.getOutputStream ());

			out.writeBytes(content);
			out.flush ();
			out.close ();				


		} catch (MalformedURLException e) {
			System.err.println("In getCSVFile(), writing");
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		}	
		catch (IOException e) {
			System.out.println("In getCSVFile(), writing");
			e.printStackTrace();
		}
		
		//Phase2: get the reply
		//Content-Type" content=content="text/html; charset=iso-8859-1"
		String contentType = conn.getContentType();		
		char[] buffer = null;
		int numChars=0;
		BufferedReader in = null;
		BufferedWriter bufferedWriter = null;
		// Read response from the input stream.
		try{	
			in = new BufferedReader (new InputStreamReader(conn.getInputStream ()));
			bufferedWriter = new BufferedWriter(new FileWriter(outputFile));				
			// Read (and print) till end of file
			buffer = new char[BUFFER_SIZE];
		    while ((numChars=in.read(buffer,0, BUFFER_SIZE))!= -1)
		    	bufferedWriter.write(buffer,0, numChars);
		    	//replyBuffer.append(buffer,0, numChars);	
			
		} catch (IOException e) {
			System.err.println("In getCSVFile(), reading data and writing to file");
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		} finally {
            //Close the BufferedReader		
            try {
            	conn = null;
            	if (in!=null)
            		in.close ();
            	if(bufferedWriter!=null){
            		bufferedWriter.flush();
            		bufferedWriter.close();
            	}
            } catch (IOException ex) {
            	System.out.println("In getCSVFile(), closing the BufferedReader");
                ex.printStackTrace();
            }
        }
		
		//System.out.print(replyBuffer);		
	}

	
	private void doPost(String outputFile) {
		String target = "http://www.epa-echo.gov/cgi-bin/ideaotis.cgi";
		String content = "idea_active=Y&idea_database=PBL&media_tool=ECHOI&idea_client=otis_pba&idea_pcs_migrate=Y&func_nametype=CASE&func_nametype=FACILITY&idea_linkage=LINKED+NONLINKED&idea_db_filter=INC+AFS+ICI+FRS+PCS+ICP+RCR+TRI+DEM+NEI&idea_report=OTISECHO+PARM+SORTNAME_tricommas_pencommas_DEMRADIUS%3D3_violqtrsmax%3D12&otis_custom_col=7%2C21%2C12%2C24%2C13%2C19%2C18%2C23%2C15%2C29&idea_major=&idea_zip_any=12180&zip=12180";

		//System.out.println("About to post\nURL: "+target+ "content: " + content);
		StringBuilder response = new StringBuilder();
		URL url = null;

		try {
			url = new URL(target);			  
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}	

		HttpURLConnection conn = null;  		
		try {			
			conn = (HttpURLConnection)url.openConnection();
			
			// Set connection parameters.
			conn.setDoInput (true);
			conn.setDoOutput (true);
			conn.setUseCaches (false);
			// Make server believe we are form data...
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Content-Length", String.valueOf(content.length()));
			//conn.setRequestProperty("Content-Language", "en-US");
			
			DataOutputStream out;
			// Write out the bytes of the content string to the stream.
			out = new DataOutputStream (conn.getOutputStream ());

			out.writeBytes(content);
			out.flush ();
			out.close ();
		} catch (IOException e) {
			System.out.println("In sendPost(), writing");
			e.printStackTrace();
		}

		BufferedReader in = null;
		BufferedWriter bufferedWriter = null;
		// Read response from the input stream.
		try{			
			in = new BufferedReader (new InputStreamReader(conn.getInputStream ()));
			String temp;
			bufferedWriter = new BufferedWriter(new FileWriter(outputFile));
			
			while ((temp = in.readLine()) != null){
				response.append(temp);
				response.append("\n");
				bufferedWriter.write(temp);
	            //bufferedWriter.newLine();
			}
			temp = null;
			
		} catch (IOException e) {
			System.out.println("In sendPost(), reading data and writing to file");
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		} finally {
            //Close the BufferedReader and BufferedWriter			
            try {
            	if (in!=null)
            		in.close ();
                if (bufferedWriter != null) {
                    bufferedWriter.flush();
                    bufferedWriter.close();
                }
            } catch (IOException ex) {
            	System.out.println("In sendPost(), closing the BufferedReader and BufferedWriter");
                ex.printStackTrace();
            }
        }
		//System.out.println("Server response:\n'" + response + "'");
		//return response; 
	}
	
	private void doGet(String outputFile) {
		String target = "http://www.epa-echo.gov/cgi-bin/get1cReport.cgi?tool=echo&IDNumber=110012303854";
		
		//System.out.println("About to get\nURL: "+target);
		StringBuilder response = new StringBuilder();
		URL url = null;

		try {
			url = new URL(target);			  
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}	

		HttpURLConnection conn = null;  		
		try {			
			conn = (HttpURLConnection)url.openConnection();
			
			// Set connection parameters.
			conn.setDoInput (true);
			conn.setDoOutput (true);
			conn.setUseCaches (false);
			// Make server believe we are form data...
			conn.setRequestMethod("GET");			
			conn.connect();			

		} catch (IOException e) {
			System.out.println("In doGet(), writing");
			e.printStackTrace();
		}
		
		//Content-Type" content=content="text/html; charset=iso-8859-1"
		String contentType = conn.getContentType();
		int charsetIndex = contentType.indexOf("charset=");
		String charset = null;
		if(charsetIndex!=-1)
			charset = contentType.substring(charsetIndex+8);
		//System.out.println("In doGet(), charset: "+charset);
		
		char[] buffer = null;
		int numChars=0;
		BufferedReader in = null;
		BufferedWriter bufferedWriter = null;
		// Read response from the input stream.
		try{			
			in = new BufferedReader (new InputStreamReader(conn.getInputStream (), charset));
			String temp;
			bufferedWriter =new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), charset));
			
			// Read (and print) till end of file
			buffer = new char[BUFFER_SIZE];
		    while ((numChars=in.read(buffer,0, BUFFER_SIZE))!= -1)
		    		  bufferedWriter.write(buffer,0, numChars);	
			
		} catch (IOException e) {
			System.out.println("In doGet(), reading data and writing to file");
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		} finally {
            //Close the BufferedReader and BufferedWriter			
            try {
            	conn = null;
            	if (in!=null)
            		in.close ();
                if (bufferedWriter != null) {
                    bufferedWriter.flush();
                    bufferedWriter.close();
                }
            } catch (IOException ex) {
            	System.out.println("In sendPost(), closing the BufferedReader and BufferedWriter");
                ex.printStackTrace();
            }
        }

		//System.out.println("Server response:\n'" + response + "'");
		//return response; 
	}
	
	protected void doCommunication(int commType,String target, String content, String outputFile) {
		long start = System.currentTimeMillis();
		//System.out.println("Starting doCommunication");
		//StringBuilder response = new StringBuilder();
		URL url = null;
		HttpURLConnection conn = null; 
		
		try {			
			url = new URL(target);
			conn = (HttpURLConnection)url.openConnection();			
			// Set connection parameters.
			conn.setDoInput (true);
			conn.setDoOutput (true);
			conn.setUseCaches (false);
			// Make server believe we are form data...
			if(commType==0){
				//System.out.println("About to get\nURL: "+target);
				conn.setRequestMethod("GET");			
				conn.connect();
			}
			else if(commType==1){
				//System.out.println("About to post\nURL: "+target+ "content: " + content);
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				conn.setRequestProperty("Content-Length", String.valueOf(content.length()));
				//conn.setRequestProperty("Content-Language", "en-US");
				
				DataOutputStream out;
				// Write out the bytes of the content string to the stream.
				out = new DataOutputStream (conn.getOutputStream ());

				out.writeBytes(content);
				out.flush ();
				out.close ();				
			}
			else {
				System.err.println("In doCommunication, commType is unkown");
				System.exit(0);
			}

		} catch (MalformedURLException e) {
			System.err.println("In doCommunication(), writing");
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		}	
		catch (IOException e) {
			System.out.println("In doCommunication(), writing");
			e.printStackTrace();
		}
		
		//Content-Type" content=content="text/html; charset=iso-8859-1"
		String contentType = conn.getContentType();
		int charsetIndex = contentType.indexOf("charset=");
		String charset = null;
		if(charsetIndex!= -1)
			charset = contentType.substring(charsetIndex+8);
		//System.out.println("In doGet(), charset: "+charset);
		
		char[] buffer = null;
		int numChars=0;
		BufferedReader in = null;
		BufferedWriter bufferedWriter = null;
		// Read response from the input stream.
		try{	
			if(charset!=null){
				in = new BufferedReader (new InputStreamReader(conn.getInputStream (), charset));
				bufferedWriter =new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), charset));
			}
			else {
				in = new BufferedReader (new InputStreamReader(conn.getInputStream ()));
				bufferedWriter = new BufferedWriter(new FileWriter(outputFile));
			}
						// Read (and print) till end of file
			buffer = new char[BUFFER_SIZE];
		    while ((numChars=in.read(buffer,0, BUFFER_SIZE))!= -1)
		    		  bufferedWriter.write(buffer,0, numChars);	
			
		} catch (IOException e) {
			System.err.println("In doGet(), reading data and writing to file");
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		} finally {
            //Close the BufferedReader and BufferedWriter			
            try {
            	conn = null;
            	if (in!=null)
            		in.close ();
                if (bufferedWriter != null) {
                    bufferedWriter.flush();
                    bufferedWriter.close();
                }
            } catch (IOException ex) {
            	System.out.println("In sendPost(), closing the BufferedReader and BufferedWriter");
                ex.printStackTrace();
            }
        }

		//System.out.println("Server response:\n'" + response + "'");
		//return response; 
		System.err.println("Communication complete in "+(System.currentTimeMillis()-start)+" ms");
	}

	
	protected void doCommunication(int commType,String target, String content, BufferedWriter bufferedWriter) {
		long start = System.currentTimeMillis();
		//System.out.println("Starting doCommunication");
		//StringBuilder response = new StringBuilder();
		URL url = null;
		HttpURLConnection conn = null; 
		
		try {			
			url = new URL(target);
			conn = (HttpURLConnection)url.openConnection();			
			// Set connection parameters.
			conn.setDoInput (true);
			conn.setDoOutput (true);
			conn.setUseCaches (false);
			// Make server believe we are form data...
			if(commType==0){
				//System.out.println("About to get\nURL: "+target);
				conn.setRequestMethod("GET");			
				conn.connect();
			}
			else if(commType==1){
				//System.out.println("About to post\nURL: "+target+ "content: " + content);
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				conn.setRequestProperty("Content-Length", String.valueOf(content.length()));
				//conn.setRequestProperty("Content-Language", "en-US");
				
				DataOutputStream out;
				// Write out the bytes of the content string to the stream.
				out = new DataOutputStream (conn.getOutputStream ());

				out.writeBytes(content);
				out.flush ();
				out.close ();				
			}
			else {
				System.err.println("In doCommunication, commType is unkown");
			}

		} catch (MalformedURLException e) {
			System.err.println("In doCommunication(), writing");
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		}	
		catch (IOException e) {
			System.out.println("In doCommunication(), writing");
			e.printStackTrace();
		}
		
		//Content-Type" content=content="text/html; charset=iso-8859-1"
		String contentType = conn.getContentType();
		int charsetIndex = contentType.indexOf("charset=");
		String charset = null;
		if(charsetIndex!= -1)
			charset = contentType.substring(charsetIndex+8);
		//System.out.println("In doGet(), charset: "+charset);
		
		char[] buffer = null;
		int numChars=0;
		BufferedReader in = null;
		// Read response from the input stream.
		try{	
			if(charset!=null)
				in = new BufferedReader (new InputStreamReader(conn.getInputStream (), charset));
			else
				in = new BufferedReader (new InputStreamReader(conn.getInputStream ()));
			// Read (and print) till end of file
			buffer = new char[BUFFER_SIZE];
		    while ((numChars=in.read(buffer,0, BUFFER_SIZE))!= -1)
		    		  bufferedWriter.write(buffer,0, numChars);	
			
		} catch (IOException e) {
			System.err.println("In doGet(), reading data and writing to file");
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		} finally {
            //Close the BufferedReader and BufferedWriter			
            try {
            	conn = null;
            	if (in!=null)
            		in.close ();
            } catch (IOException ex) {
            	System.out.println("In doCommunication(), closing the BufferedReader");
                ex.printStackTrace();
            }
        }
		System.err.println("Communication complete in "+(System.currentTimeMillis()-start)+" ms");
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		EpaCommAgent comAgent = new EpaCommAgent();		
		String cgiTarget = "http://www.epa-echo.gov/cgi-bin/ideaotis.cgi";
		String cgiContent = "idea_active=Y&idea_database=PBL&media_tool=ECHOI&idea_client=otis_pba&idea_pcs_migrate=Y&func_nametype=CASE&func_nametype=FACILITY&idea_linkage=LINKED+NONLINKED&idea_db_filter=INC+AFS+ICI+FRS+PCS+ICP+RCR+TRI+DEM+NEI&idea_report=OTISECHO+PARM+SORTNAME_tricommas_pencommas_DEMRADIUS%3D3_violqtrsmax%3D12&otis_custom_col=7%2C21%2C12%2C24%2C13%2C19%2C18%2C23%2C15%2C29&idea_major=&idea_zip_any=12180&zip=12180";
		String cgiResult = "/home/ping/research/python/water/comm/epaCgiResult";
		//String getTarget = "http://www.epa-echo.gov/cgi-bin/get1cReport.cgi?tool=echo&IDNumber=110012303854";
		String facilityID = "110002041530";
		String getTarget = "http://www.epa-echo.gov/cgi-bin/get1cReport.cgi?tool=echo&IDNumber="+facilityID;
		String getContent = "";
		String getResult = "/home/ping/research/python/water/comm/facilityResult"+facilityID;
		
		//comAgent.doCommunication(1, cgiTarget, cgiContent, cgiResult);
		//comAgent.doCommunication(0, getTarget, getContent, getResult);
		
		String csvTarget = "http://www.epa-echo.gov/cgi-bin/effluentdata.cgi";
		String csvContent = "permit=NY0261343&pipe=all&paramtr=all&monlocn=all&period=all&outt=all&date=20070701%7C20100630&charts=viol&tool=echo&filetype=csv";
		String csvResult = "/home/ping/research/python/water/csv/epaCSVResult.csv";
		comAgent.getCSVFile(csvTarget, csvContent, csvResult);
	}

}
