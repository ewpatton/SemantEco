package edu.rpi.tw.eScience.WaterQualityPortal.epa;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class EpaDataAgent {
	static int BUFFER_SIZE = 4096;
	static String searchByZipTarget = "http://www.epa-echo.gov/cgi-bin/ideaotis.cgi";
	static String searchByZipContent = "idea_active=Y&idea_database=PBL&media_tool=ECHOI&idea_client=otis_pba&idea_pcs_migrate=Y&func_nametype=CASE&func_nametype=FACILITY&idea_linkage=LINKED+NONLINKED&idea_db_filter=INC+AFS+ICI+FRS+PCS+ICP+RCR+TRI+DEM+NEI&idea_report=OTISECHO+PARM+SORTNAME_tricommas_pencommas_DEMRADIUS%3D3_violqtrsmax%3D12&otis_custom_col=7%2C21%2C12%2C24%2C13%2C19%2C18%2C23%2C15%2C29&idea_major=&idea_zip_any=";
	//12180&zip=12180
	static String zipPostFix="&zip=";
	static String upMostDir = "/home/ping/research/python/water/CgiSoupOutput/";
	static String scriptExtractSearchResult="/home/ping/research/python/water/epaCgi.py";
	static String soupDataFile="/epaCgiSoupData";
	HashMap facilities = new HashMap();	
	
	private void doCommunication(int commType,String target, String content, String outputFile) {
		System.out.println("Starting doCommunication");
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
				System.out.println("About to get\nURL: "+target);
				conn.setRequestMethod("GET");			
				conn.connect();
			}
			else if(commType==1){
				System.out.println("About to post\nURL: "+target+ "content: " + content);
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
		String charset = contentType.substring(charsetIndex+8);
		//System.out.println("In doGet(), charset: "+charset);
		
		char[] buffer = null;
		int numChars=0;
		BufferedReader in = null;
		BufferedWriter bufferedWriter = null;
		// Read response from the input stream.
		try{			
			in = new BufferedReader (new InputStreamReader(conn.getInputStream (), charset));
			bufferedWriter =new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), charset));
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
	}
	
	void mkDir(String dirName){
		File dir = new File(upMostDir+dirName);
		boolean dirOK = false;
		if(!dir.exists()){
			dirOK = dir.mkdir();
			if(!dirOK){
				System.out.println("Cannot create the directory '" + dir);
				System.exit(0);
			}
		}		
	}
	
	private void readDataFromFile(String fileName){
		String curLine;
		String curID;
		String curName;
		String curAddressLine1;
		String curAddressLine2;
		int curNumInspection=0;
		int curNumQtrNC=0;
		int curNumEE=0;
		int insIndex=0;//for curNumInspection
		int qtrIndex=0;//for curNumQtrNC
		int eeIndex=0;//for curNumEE
		String strNumInspection;
		String strNumQtrNC;
		String strNumEE;
		
		try {
			FileInputStream fIn =  new FileInputStream(fileName);
			BufferedReader reader = new BufferedReader(new InputStreamReader(fIn));
			
			curLine = reader.readLine();
			while (curLine != null) { 
				//ID
				if(curLine.indexOf("FRS ID")==-1) {
					System.err.println("In readDataFromFile, err in reading RFS ID");
					System.exit(0);						
				}
				curID = curLine.substring(curLine.indexOf(':')+2, curLine.length()-1);
				//Name
				curLine = reader.readLine();
				if(curLine == null || curLine.indexOf("Name:")==-1) {
					System.err.println("In readDataFromFile, err in reading Name");
					System.exit(0);						
				}
				curName = curLine.substring(curLine.indexOf(':')+2, curLine.length()-1);
				//Address line 1
				curLine = reader.readLine();
				if(curLine == null || curLine.indexOf("AL1:")==-1) {
					System.err.println("In readDataFromFile, err in reading AddressLine1");
					System.exit(0);						
				}
				curAddressLine1 = curLine.substring(curLine.indexOf(':')+2, curLine.length()-1);
				//Address line 2
				curLine = reader.readLine();
				if(curLine == null || curLine.indexOf("AL2:")==-1) {
					System.err.println("In readDataFromFile, err in reading AddressLine1");
					System.exit(0);						
				}
				curAddressLine2 = curLine.substring(curLine.indexOf(':')+2, curLine.length()-1);
				curNumInspection=0;
				curNumQtrNC=0;
				curNumEE=0;
				//NumInspection, NumQtrNC, curNumEE
				while(true){
					curLine = reader.readLine();
					if(curLine == null)//end of file
						break;			
					if(curLine.indexOf("FRS ID") != -1) 
						break;
					insIndex = curLine.indexOf("I");
					qtrIndex = curLine.indexOf("Q");
					eeIndex = curLine.indexOf("E");
					if(insIndex == -1 || qtrIndex == -1 || eeIndex == -1) {
						System.err.println("In readDataFromFile, err in reading the number line");
						System.exit(0);						
					}
					strNumInspection = curLine.substring(insIndex+1, qtrIndex);
					strNumQtrNC = curLine.substring(qtrIndex+1, eeIndex);
					strNumEE = curLine.substring(eeIndex+1, curLine.length());
					curNumInspection += convertNumInspection(strNumInspection);
					curNumQtrNC += convertNumQtrNC(strNumQtrNC);
					curNumEE += convertNumEE(strNumEE);						
				}//end of the inner while
				Facility curFacility = new Facility(curID, curName, curAddressLine1, curAddressLine2,
						curNumInspection, curNumQtrNC, curNumEE);
				facilities.put(curID, curFacility);				
			}//end of the outer while
		}
		catch (Exception e) {
			e.printStackTrace();
		}		
		
	}
	
	private void printFacilitiesToFile(String fileName){
		BufferedWriter out=null;
		try {
			out = new BufferedWriter(new FileWriter(fileName));
			System.out.println("HashMap of Facilities");
			Iterator itrOut = facilities.entrySet().iterator();  
	        while (itrOut.hasNext()) {  
	        	Map.Entry entryOut = (Map.Entry)itrOut.next();
	        	String ID = (String)entryOut.getKey();
	        	Facility curFacility = (Facility)entryOut.getValue();
	        	curFacility.printToFile(out);        	
	        }
	        
		} catch (IOException e) {
			System.out.println("printFacilitiesToFile, err");
			e.printStackTrace();
		} finally {
            //Close the BufferedWriter			
            try {            	
                if (out != null) {
                	out.flush();
                	out.close();
                }
            } catch (IOException ex) {
            	System.out.println("In printFacilitiesToFile, closing the BufferedWriter");
                ex.printStackTrace();
            }
        }		
	}
	
	
	private int convertNumInspection(String strNumInspection){
		int valueNumInspection;
		if(strNumInspection.compareTo("&nbsp;")==0)
			valueNumInspection = 0;
		else
			//to copy with the special format
			valueNumInspection = Integer.parseInt(strNumInspection.substring(3));		
		
		return valueNumInspection;		
	}
	
	private int convertNumQtrNC(String strNumQtrNC){
		int valueNumQtrNC;		
		if(strNumQtrNC.compareTo("&nbsp;")==0 || strNumQtrNC.compareTo("n/a")==0)
			valueNumQtrNC = 0;
		else
		{
			int j=0;
			for(j=0; j<strNumQtrNC.length(); j++){
				if(!Character.isWhitespace(strNumQtrNC.charAt(j)))
							break;		
			}
			valueNumQtrNC = Integer.parseInt(strNumQtrNC.substring(j));	    		 
		}
		//to copy with the special format
			
		
		return valueNumQtrNC;		
	}
	
	private int convertNumEE(String strNumEE){
		int valueNumEE;		
		if(strNumEE.indexOf("&nbsp")!=-1 || 
				strNumEE.indexOf("no limit") !=-1 || strNumEE.indexOf("incomp")!=-1)
			valueNumEE = 0;
		else
			valueNumEE = Integer.parseInt(strNumEE);
		
		return valueNumEE;		
	}
	
	private void pythonExe(String script, String[] scriptArgs, int argLength){
		Runtime rt = Runtime.getRuntime();
		String[] cmd = new String[argLength+2];
		cmd[0] = "python";
		cmd[1] = script;
		//Get script arguments, e.g. cmd[2] = Filepath;
		for(int i=0;i<argLength;i++)
			cmd[i+2] = scriptArgs[i];
		try {
			Process pr = rt.exec(cmd);
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	protected void startFetch(String zipCode) {		
		String curDir = upMostDir+zipCode;
		String commContent = searchByZipContent+zipCode+zipPostFix+zipCode;
		//output file
		String searchByZipResult = curDir+"/searchByZipResult";
		//doCommunication(1, searchByZipTarget, commContent, searchByZipResult);
		//invoke python script
		String curArgs[] = new String [2]; 
		curArgs[0] = searchByZipResult;
		curArgs[1] = curDir;
		pythonExe(scriptExtractSearchResult, curArgs, 2);
		//
		String soupDataPath = curDir+soupDataFile;
		readDataFromFile(soupDataPath);
		
	}
	
	void startQuery(String zipCode){
		//mkdir
		//mkDir(zipCode);
		//prepare to start the communication
		startFetch(zipCode);
		//
		String facilitiesFile = upMostDir+zipCode+"/facilities";
		printFacilitiesToFile(facilitiesFile);	
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		EpaDataAgent dataAgent = new EpaDataAgent();	
		dataAgent.startQuery("12180");//12208 12180
		
		/*
		String tmp = "FRS ID: 110004374748\n";
		//output: 110004374748\nTestDone
		//String tmp2 = tmp.substring(tmp.indexOf(':')+2);
		//output: 110004374748TestDone
		String tmp2 = tmp.substring(tmp.indexOf(':')+2, tmp.length()-1);
		System.out.print(tmp2);
		System.out.println("TestDone");
		*/
		/*
		String curLine = "I&nbsp;Qn/aEno limit data"+"\n";
		int insIndex = curLine.indexOf("I");
		int qtrIndex = curLine.indexOf("Q");
		int eeIndex = curLine.indexOf("E");
		if(insIndex == -1 || qtrIndex == -1 || eeIndex == -1) {
			System.err.println("In readDataFromFile, err in reading the number line");
			System.exit(0);						
		}
		String strNumInspection = curLine.substring(insIndex+1, qtrIndex);
		String strNumQtrNC = curLine.substring(qtrIndex+1, eeIndex);
		String strNumEE = curLine.substring(eeIndex+1, curLine.length()-1);
		System.out.print(strNumInspection);
		System.out.print(strNumQtrNC);
		System.out.print(strNumEE);
		System.out.println("TestDone");
		*/

	}

}
