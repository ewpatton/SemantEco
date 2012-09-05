package edu.rpi.tw.eScience.WaterQualityPortal.epa;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.Map.Entry;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDFS;

import edu.rpi.tw.eScience.WaterQualityPortal.data.WaterDataProvider;
import edu.rpi.tw.eScience.WaterQualityPortal.model.Ontology;

@Deprecated
public class EpaDataAgent implements WaterDataProvider {
	static int BUFFER_SIZE = 4096;
	//You can change the up most directory
	static String upMostDir = "/tmp/";	
	//You can change the locations of the python scripts
	static String scriptExtractFac="src/python/epaFac.py";
	static String scriptExtractSearchResult="src/python/epaCgi.py";
	//
	static String searchByZipTarget = "http://www.epa-echo.gov/cgi-bin/ideaotis.cgi";
	static String searchByZipContent = "idea_active=Y&idea_database=PBL&media_tool=ECHOI&idea_client=otis_pba&idea_pcs_migrate=Y&func_nametype=CASE&func_nametype=FACILITY&idea_linkage=LINKED+NONLINKED&idea_db_filter=INC+AFS+ICI+FRS+PCS+ICP+RCR+TRI+DEM+NEI&idea_report=OTISECHO+PARM+SORTNAME_tricommas_pencommas_DEMRADIUS%3D3_violqtrsmax%3D12&otis_custom_col=7%2C21%2C12%2C24%2C13%2C19%2C18%2C23%2C15%2C29&idea_major=&idea_zip_any=";
	//12180&zip=12180
	static String zipPostFix="&zip=";
	static String soupDataFile="epaCgiSoupData";
	static String geoTargetPre = "http://maps.googleapis.com/maps/api/geocode/xml?address=";
	static String geoTargetPost ="&sensor=false";
	static String getContent = "";
	static String geoDataFile="facilityGeo";
	static String soupAddressFile="epaCgiSoupAddress";
	static String facilityPagePre = "http://www.epa-echo.gov/cgi-bin/get1cReport.cgi?tool=echo&IDNumber=";
	static String facilityFolder = "facPage/";
	static String facilityHtmlPre = "epaFacHtml";
	static String facilitySoupPre = "epaFacSoup";	
	//OCV
	static String OCVFolder = "OCVPage/";
	static String OCVHtmlPre = "epaOCVHtml";
	//
	static String csvTarget = "http://www.epa-echo.gov/cgi-bin/effluentdata.cgi";
	static String CSVFolder = "CSV/";
	static String CSVFilePost = ".csv";
	//Solids
	static String SolidsFolder = "Solids/";
	static String SolidsHtmlPre = "epaSolidsHtml";	
	static int numQtr=12;
	//
	public boolean downloadFacPage = true;
	public boolean downloadGeoData = true;
	public boolean downloadOCVPage = true;
	public boolean downloadCSVFile = true;
	
	//HashMap<Integer, Facility> facilities = null;
	ArrayList<Facility> facilities = null;
	ArrayList<Facility> facilitiesWithViolations = null;
	HashMap<String, MeasurementConstraint> measurementConstraints = null;
	EpaCommAgent commAgent = null;
	EpaCSVAgent csvAgent = null;
	EpaUtil utilAgent = null;
	File basePath = null;
	
	public EpaDataAgent(){
		facilities = new ArrayList<Facility>();
		facilitiesWithViolations = new ArrayList<Facility>();
		measurementConstraints = new HashMap<String, MeasurementConstraint> ();
		commAgent = new EpaCommAgent();
		csvAgent = new EpaCSVAgent();
		utilAgent = new EpaUtil();
		basePath = new File("/tmp/");
	}
	
	public EpaDataAgent(File basePath) {
		facilities = new ArrayList<Facility>();
		facilitiesWithViolations = new ArrayList<Facility>();
		measurementConstraints = new HashMap<String, MeasurementConstraint> ();
		commAgent = new EpaCommAgent();
		csvAgent = new EpaCSVAgent();
		utilAgent = new EpaUtil();
		this.basePath = basePath.getAbsoluteFile();
	}

	private void queryOCVPages(String zipCode){
		Facility curFacility=null;		
		Iterator<Facility> itr = facilitiesWithViolations.iterator();  
		while (itr.hasNext()) {  
			curFacility = (Facility)itr.next();
			processOCVPage(zipCode, curFacility); 			
		}
	}
	
	private void queryFacilityPages(String zipCode){
		Facility curFacility=null;		
		Iterator<Facility> itr = facilities.iterator(); 
		
        while (itr.hasNext()) {
        	curFacility = (Facility)itr.next();
        	//only inspect the facilities that have inspections and non compliances
        	if(curFacility.numInspection>0 && (curFacility.numQtrNC>0 || curFacility.numEE>0))
        	{
        		procFacilityPage(zipCode, curFacility); 
        		facilitiesWithViolations.add(curFacility);
        	}        	
        }
	}
	
	/*
	static String facilityPagePre = "http://www.epa-echo.gov/cgi-bin/get1cReport.cgi?tool=echo&IDNumber=";
	static String facilitySoupDir = upMostDir+"facPage/";
	static String facilityHtmlPre = "epaFacHtml";
	static String facilitySoupPre = "epaFacSoup";
		*/	
	private void procFacilityPage(String zipCode, Facility curFac){
		//mkDir
		File facilityDir = new File(basePath,zipCode+"/"+facilityFolder);
		facilityDir.mkdirs();
		//download html
		String IDNum = curFac.ID;
		String getTarget = facilityPagePre + IDNum;
		File facHtml = new File(facilityDir,facilityHtmlPre + IDNum);
		if(downloadFacPage==true)
			commAgent.doCommunication(0, getTarget, getContent, facHtml.getAbsolutePath());
		//invoke python script
		String curArgs[] = new String [2]; 
		curArgs[0] = facHtml.getAbsolutePath();
		File facSoupPath = new File(facilityDir,facilitySoupPre + IDNum);
		curArgs[1] = facSoupPath.getAbsolutePath();
		pythonExe(scriptExtractFac, curArgs, 2);
		//
		getDataFromFacilitySoup(facSoupPath, curFac);
	}
	
	private void processOCVPage(String zipCode, Facility curFac){
		String csvPostContent = null;
		//mkDirs
		File ocvDir = new File(basePath,zipCode+"/"+OCVFolder);
		ocvDir.mkdirs();
		File csvDir = new File(basePath,zipCode+"/"+CSVFolder);
		csvDir.mkdirs();
		//
		String IDNum = curFac.ID;
		File ocvHtml = new File(ocvDir,OCVHtmlPre + IDNum);
		String getTarget = curFac.OCVLink;
		if(getTarget!=null) {
			//download the OVL HTML
			if(downloadOCVPage==true)
				commAgent.doCommunication(0, curFac.OCVLink, getContent, ocvHtml.getAbsolutePath());
			//download the CSV file
			csvPostContent = getCSVPostContentFromOCV(ocvHtml);
			if(csvPostContent != null){
				String csvResult = csvDir+IDNum+CSVFilePost;//
				if(downloadCSVFile ==true)
					commAgent.getCSVFile(csvTarget, csvPostContent, csvResult);				
				csvAgent.CSVRead(csvResult, csvPostContent, curFac, measurementConstraints);
			}
		}
	}
	
	/*
	 *  <input type="hidden" name="permit"	value="RI0100005" />
 <input type="hidden" name="pipe"	value="all" />
 <input type="hidden" name="paramtr"	value="all" />
 <input type="hidden" name="monlocn"	value="all" />
 <input type="hidden" name="period"	value="all" />
 <input type="hidden" name="outt"	value="all" />
 <input type="hidden" name="date"	value="20070701|20100630" />
 <input type="hidden" name="charts"	value="viol" />
 <input type="hidden" name="tool"	value="echo" />
 <input type="hidden" name="filetype" />
	 * */
	private String getCSVPostContentFromOCV(File ocvFile){
		String curLine;	
		FileInputStream fIn = null;
		BufferedReader reader = null;
		//Facility curFacility=null;
		String curValue=null;
		ArrayList<String> values = new ArrayList<String> (9);
		String csvPostContent=null;
		CharSequence orgCS = "|";
		CharSequence newCS = "%7C";
		
		try {
			fIn =  new FileInputStream(ocvFile);
			reader = new BufferedReader(new InputStreamReader(fIn));

			while ((curLine = reader.readLine()) != null) { 
				//System.out.println(curLine);
				if(curLine.startsWith("<form name=\"download")==true) {
					if(curLine.startsWith("<form name=\"download\" method=\"POST\" action=\"/cgi-bin/effluentdata.cgi\"")==true){
						for(int  i=0; i<9; i++){							
							curLine = reader.readLine();							
							curValue = curLine.substring(curLine.indexOf("value=")+7, curLine.length()-4);
							//System.out.println("Current Value: "+curValue);							
							values.add(curValue);
						}
						break;
					}//end of the inner if						
				}//end of the out if				
			}//end of while
			//System.out.print(values);
			csvPostContent = "permit="+values.get(0)+"&pipe="+values.get(1)+"&paramtr="+values.get(2)+
			"&monlocn="+values.get(3)+"&period="+values.get(4)+"&outt="+values.get(5)+
			"&date="+values.get(6).replace(orgCS, newCS)+"&charts="+values.get(7)+
			"&tool="+values.get(8)+"&filetype=csv";
			//System.out.println("csvPostContent: "+csvPostContent);			
		} catch (Exception e) {
				System.err.println("In getCSVLinkFromOCV, err");
				e.printStackTrace();
			}finally {
				//Close the BufferedReader and BufferedWriter			
				try {
					if (reader!=null)
						reader.close ();
				} catch (IOException ex) {
					System.err.println("In getCSVLinkFromOCV(), closing the reader and BufferedWriter");
					ex.printStackTrace();
				}
			}
		return csvPostContent;
		
	}
	
	private void getDataFromFacilitySoup(File fileName, Facility curFac){
		//link of Only Charts with Violations
		String linkChartsWithViolations=null;
		ArrayList<String> qtrDurList=null;
		ArrayList<String> NCBoolList=null;
		
		String curLine;
		FileInputStream fIn = null;
		BufferedReader reader = null;
		int httpIndex = -1;

		
		try {
			fIn =  new FileInputStream(fileName);
			reader = new BufferedReader(new InputStreamReader(fIn));
			
			curLine = reader.readLine();
			if(curLine==null){
				//System.err.println("Facility "+curFac.ID+" has No Soup Data");
				reader.close();
				return;
			}
			if(curLine.indexOf("No CWA")!=-1){
				//System.err.println("Facility "+curFac.ID+" has No CWA");
				reader.close();
				return;
			}
			
			if(curLine.indexOf("effluents.cgi")!=-1){
				httpIndex = curLine.indexOf("http:");
				linkChartsWithViolations = curLine.substring(httpIndex,
						curLine.indexOf('\'', httpIndex));
				curFac.setOCVLink(linkChartsWithViolations);
				//for test
				//System.out.println("linkChartsWithViolations: "+linkChartsWithViolations);
			}
			else if(curLine.compareTo("No Only Charts with Violations")==0){
				linkChartsWithViolations = null;
			}
			else {
				//System.err.println("In getDataFromFacilitySoup, err in reading link");
				//System.err.println("linkChartsWithViolations: "+linkChartsWithViolations);
				//System.err.println("Current Facility:");
				//curFac.printFacility();
				//System.exit(0);
				reader.close();
				return;
			}
			
			while ((curLine = reader.readLine()) != null) { 
				if(curLine.compareTo("qtr Duration List")==0){
					qtrDurList = new ArrayList<String>(numQtr);
					for(int i=0;i<numQtr;i++){
						curLine = reader.readLine();
						qtrDurList.add(i, curLine.substring(0, curLine.length()-1));
					}	
					curFac.setQtrDurList(qtrDurList);
				}//end of if
				
				if(curLine.compareTo("NC Boolean List")==0){
					NCBoolList = new ArrayList<String>(numQtr);
					for(int i=0;i<numQtr;i++){
						curLine = reader.readLine();
						NCBoolList.add(i, curLine.substring(1));
					}
					curFac.setNCBoolList(NCBoolList);
					//break from the while, since we don't need other data for now
					break;
				}//end of if				
			}//end of while
		} catch (Exception e) {
			System.err.println("In getDataFromFacilitySoup, err in reading file");
			System.err.println("linkChartsWithViolations: "+linkChartsWithViolations);
			System.err.println("Current Facility:");
			curFac.printFacility();
			e.printStackTrace();
		}	
		
	}
	
	private void getLocation(File fileName, File resultFile){
		String curLine;
		String getTarget = null;
		FileInputStream fIn = null;
		BufferedReader reader = null;
		BufferedWriter bufferedWriter = null;
		
		try {
			fIn =  new FileInputStream(fileName);
			reader = new BufferedReader(new InputStreamReader(fIn));							
			bufferedWriter = new BufferedWriter(new FileWriter(resultFile));


			while ((curLine = reader.readLine()) != null) { 				
				//http://maps.googleapis.com/maps/api/geocode/xml?address=1600+Amphitheatre+Parkway,+Mountain+View,+CA&sensor=false
				String address = curLine.replace(' ', '+');
				getTarget = geoTargetPre+address+geoTargetPost;
				//Pause for 2 seconds
	            Thread.sleep(200);
				commAgent.doCommunication(0, getTarget, getContent, bufferedWriter);
				bufferedWriter.write('\n');	
			}		
		}
		catch (Exception e) {
			System.err.println("In getLocation, err");
			e.printStackTrace();
		}finally {
			//Close the BufferedReader and BufferedWriter			
			try {
				if (reader!=null)
					reader.close ();
				if (bufferedWriter != null) {
					bufferedWriter.flush();
					bufferedWriter.close();
				}
			} catch (IOException ex) {
				System.err.println("In getLocation(), closing the reader and BufferedWriter");
				ex.printStackTrace();
			}
		}

	}
	
/*	private String convertAddress(String address){
		String result=null;
		result = address.replace(' ', '+');
		return result;		
	}
	*/
	
	void mkDir(String dirName){
		File dir = new File(basePath, dirName);
		boolean dirOK = false;
		if(!dir.exists()){
			dirOK = dir.mkdirs();
			if(!dirOK){
				System.err.println("Cannot create the directory '" + dir);
				//System.exit(0);
			}
		}		
	}
	
	private void getGeoFromXML(File fileName){
		String curLine;	
		FileInputStream fIn = null;
		BufferedReader reader = null;
		String curLat, curLng, curStatus;
		int latIndex=-1, lngIndex=-1, stsIndex=-1; 
		Integer mapIndex=-1;
		Facility curFacility=null;
		
		try {
			fIn =  new FileInputStream(fileName);
			reader = new BufferedReader(new InputStreamReader(fIn));

			while ((curLine = reader.readLine()) != null) { 
				//start of a new XML
				if(curLine.indexOf("?xml")!=-1) {
					mapIndex ++;
					curLine = reader.readLine();
					curLine = reader.readLine();
					stsIndex = curLine.indexOf("<status>");
					if(stsIndex == -1) {
						System.err.println("In getGeoFromXML, err in reading <status>");
						System.exit(0);	
					}
					else {
						curStatus = curLine.substring(stsIndex+8, curLine.indexOf("</status>"));
						if(curStatus.compareTo("OK") != 0){
							System.err.println("In getGeoFromXML, abnormal XML status: "+curStatus);
							continue;//go to the next XML
						}
					}
					
					while(true){
						curLine = reader.readLine();
						if(curLine == null)//end of file
							break;	
						
						//get the 1st pair of lat and lng 
						if((latIndex = curLine.indexOf("<lat>")) != -1) {
							curLat = curLine.substring(latIndex+5, curLine.indexOf("</lat>"));	
							curLine = reader.readLine();
							if((lngIndex = curLine.indexOf("<lng>")) == -1){
									System.err.println("In getGeoFromXML, err in reading LNG");
									System.exit(0);	
							}
							curLng = curLine.substring(lngIndex+5, curLine.indexOf("</lng>"));
							curFacility = facilities.get(mapIndex);
							if(curFacility == null){
								System.err.println("In getGeoFromXML, err in getting Facility with index: "+mapIndex);
								System.exit(0);	
							}
							curFacility.setLocation(curLat, curLng);
							break;//go to the next XML
						}//end of if find <lat>
					}//end of the inner while				
				}//end of if find ?xml
			}//end of the out while		
		}
		catch (Exception e) {
			System.err.println("In getGeoFromXML, err");
			e.printStackTrace();
		}finally {
			//Close the BufferedReader and BufferedWriter			
			try {
				if (reader!=null)
					reader.close ();
			} catch (IOException ex) {
				System.err.println("In getLocation(), closing the reader and BufferedWriter");
				ex.printStackTrace();
			}
		}
		
	}
	private void getFacilityFromFile(String zipCode, File fileName){
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
		Integer rowNum=0;
		String postContent =null;
		
		try {
			FileInputStream fIn =  new FileInputStream(fileName);
			BufferedReader reader = new BufferedReader(new InputStreamReader(fIn));
			
			curLine = reader.readLine();
			while (curLine != null) { 
				//ID
				if(curLine.indexOf("FRS ID")==-1) {
					System.err.println("In readDataFromFile, err in reading RFS ID");
				}
				curID = curLine.substring(curLine.indexOf(':')+2, curLine.length()-1);
				
				//Name
				curLine = reader.readLine();
				if(curLine == null || curLine.indexOf("Name:")==-1) {
					System.err.println("In readDataFromFile, err in reading Name");
				}
				curName = curLine.substring(curLine.indexOf(':')+2, curLine.length());
				//Address line 1
				curLine = reader.readLine();
				if(curLine == null || curLine.indexOf("AL1:")==-1) {
					System.err.println("In readDataFromFile, err in reading AddressLine1");
				}
				curAddressLine1 = curLine.substring(curLine.indexOf(':')+2, curLine.length());
				//Address line 2
				curLine = reader.readLine();
				if(curLine == null || curLine.indexOf("AL2:")==-1) {
					System.err.println("In readDataFromFile, err in reading AddressLine1");
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
					}
					strNumInspection = curLine.substring(insIndex+1, qtrIndex);
					strNumQtrNC = curLine.substring(qtrIndex+1, eeIndex);
					strNumEE = curLine.substring(eeIndex+1, curLine.length());
					curNumInspection += convertNumInspection(strNumInspection);
					curNumQtrNC += convertNumQtrNC(strNumQtrNC);
					curNumEE += convertNumEE(strNumEE);						
				}//end of the inner while
				Facility curFacility = new Facility(curID.trim(), curName, curAddressLine1, curAddressLine2,
						curNumInspection, curNumQtrNC, curNumEE);
				postContent = searchByZipContent+zipCode+zipPostFix+zipCode;
				curFacility.setSourceDocument(searchByZipTarget, postContent, rowNum);
				rowNum++;
				facilities.add(curFacility);				
			}//end of the outer while
			reader.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}		
		
	}
	
	private void printFacilitiesToFile(String fileName){
		BufferedWriter out=null;
		Facility curFacility = null;
		try {
			out = new BufferedWriter(new FileWriter(fileName));
			//System.err.println("Facilities");
			Iterator<Facility> itr = facilities.iterator();  
	        while (itr.hasNext()) {
	        	curFacility = (Facility)itr.next();
	        	curFacility.printToFile(out); 
	        }	        
		} catch (IOException e) {
			System.err.println("printFacilitiesToFile, err");
			e.printStackTrace();
		} finally {
            //Close the BufferedWriter			
            try {            	
                if (out != null) {
                	out.flush();
                	out.close();
                }
            } catch (IOException ex) {
            	System.err.println("In printFacilitiesToFile, closing the BufferedWriter");
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
	        pr.waitFor();
	        pr.destroy();
		} catch (IOException e) {
			System.err.println("In pythonExe, IOException: " + e);
			e.printStackTrace();
		} catch (InterruptedException e) {
			System.err.println("In pythonExe, InterruptedException: " + e);
			e.printStackTrace();
		}		
	}

	protected void startProcess(String zipCode) {
		long start = System.currentTimeMillis();
		File curDir = new File(basePath,zipCode+"/");
		String commContent = searchByZipContent+zipCode+zipPostFix+zipCode;
		//output file
		File searchByZipResult = new File(curDir,"searchByZipResult");
		if(searchByZipResult.exists()) {
			downloadCSVFile = false;
			downloadGeoData = false;
			downloadFacPage = false;
			downloadOCVPage = false;
		}
		//Step 1
		if(this.downloadCSVFile) {
			commAgent.doCommunication(1, searchByZipTarget, commContent, searchByZipResult.getAbsolutePath());
		}
		System.err.println("Downloaded CSV in "+(System.currentTimeMillis()-start)+" ms");
		start = System.currentTimeMillis();
		//invoke python script
		String curArgs[] = new String [2]; 
		curArgs[0] = searchByZipResult.getAbsolutePath();
		curArgs[1] = curDir.getAbsolutePath();
		//Step 2
		curDir.mkdirs();
		if(this.downloadCSVFile) {
			pythonExe(scriptExtractSearchResult, curArgs, 2);
		}
		System.err.println("Python analysis in "+(System.currentTimeMillis()-start)+" ms");
		//
		File soupDataPath = new File(curDir,soupDataFile);
		//Step 3
		start = System.currentTimeMillis();
		getFacilityFromFile(zipCode, soupDataPath);
		System.err.println("Read facilities in "+(System.currentTimeMillis()-start)+" ms");
		//
		File addressPath = new File(curDir,soupAddressFile);
		File geoPath = new File(curDir,geoDataFile);
		start = System.currentTimeMillis();
		if(downloadGeoData==true) {
			getLocation(addressPath, geoPath);		
			getGeoFromXML(geoPath);
		}
		System.err.println("Geocoded facilites in "+(System.currentTimeMillis()-start)+" ms");
		//
		start = System.currentTimeMillis();
		queryFacilityPages(zipCode);
		System.err.println("Queried facilites in "+(System.currentTimeMillis()-start)+" ms");
		//
		//String facilitiesFileTest = upMostDir+zipCode+"/facilitiesTest";
		//printFacilitiesToFile(facilitiesFileTest);	
		//
		start = System.currentTimeMillis();
		queryOCVPages(zipCode);
		System.err.println("Queried OCV in "+(System.currentTimeMillis()-start)+" ms");
		
	}
	
	void startQuery(String zipCode){
		//mkdir
		mkDir(zipCode);
		//prepare to start the communication
		startProcess(zipCode);
		//
		String facilitiesFile = new File(basePath,zipCode+"/facilities").getAbsolutePath();
		printFacilitiesToFile(facilitiesFile);	
		//
		String constraintsFile = new File(basePath,zipCode+"/constraints").getAbsolutePath();
		utilAgent.printMeasurementConstraintMap(measurementConstraints, constraintsFile);
		
	}
	
	public void saveToRdf(String zipCode){
		String rdfFacilities = upMostDir+zipCode+"/rdfFacilities";
		utilAgent.saveFacilities(facilities, rdfFacilities);
		String rdfMeasurementConstraints = upMostDir+zipCode+"/rdfMeasurementConstraints"; 
		utilAgent.saveMeasurementConstraints(measurementConstraints, rdfMeasurementConstraints);
	}
	
	public boolean getData(String zipCode, OntModel owlModel, Model pmlModel) {
		MeasurementConstraint curC = null;
		try {
			Resource epa = pmlModel.createResource(Ontology.EPA.NS+"EPA",pmlModel.createResource(Ontology.PMLP.Organization));
			epa.addLiteral(RDFS.label, "Environmental Protection Agency");
			startQuery(zipCode);
			
			System.err.println("Constraints: "+measurementConstraints.size());
			Iterator<Entry<String, MeasurementConstraint>> itrOut = measurementConstraints.entrySet().iterator();  
	        while (itrOut.hasNext()) {  
	            Entry<String, MeasurementConstraint> entryOut = itrOut.next();
	            curC = (MeasurementConstraint)entryOut.getValue();
	            curC.asOntClass(owlModel, pmlModel); 
	        }
	        
			for(Facility m : facilities) {
				m.asIndividual(owlModel, pmlModel);
			}
			
			return true;
		}
		catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}


	/**
	 * @param args
	 */
	public static void old_main(String[] args) {
		EpaDataAgent dataAgent = new EpaDataAgent();
		//EpaUtil util = new EpaUtil();
		String zipCode = "02809";//12208 12180
		//dataAgent.downloadFacPage = false;
		//dataAgent.downloadGeoData = false;
		//dataAgent.downloadOCVPage = false;
		//dataAgent.downloadCSVFile = false;
		dataAgent.startQuery(zipCode);
		dataAgent.saveToRdf(zipCode);
		
		
		//dataAgent.getCSVLinkFromOCV("/home/ping/research/python/water/ocv/OCVRI0100005.html");

		/*
		//download OCV html
		String ocvHtml = "/home/ping/research/python/water/CgiSoupOutput/temp/"+OCVHtmlPre+"NY0261343";
		String target = "http://www.epa-echo.gov/cgi-bin/effluents.cgi?permit=NY0261343&amp;charts=viols&amp;monlocn=all&amp;outt=all";
		String getTarget = target.replace("&amp;", "&");
		//System.out.println(getTarget);
		dataAgent.doCommunication(0, getTarget, getContent, ocvHtml);
		*/
		
		/*
		//download solids html
		String solidsHtml = "/home/ping/research/python/water/CgiSoupOutput/temp/"+SolidsHtmlPre+"NY0261343";
		String solidsTarget = "http://www.epa-echo.gov"+
			"/cgi-bin/effluent1.cgi?permit=NY0261343&pipe=001&paramtr=00530&monlocn=1&period=1&outt=all&date=20070701%7C20100630&charts=viol&tool=echo";
		dataAgent.commAgent.doCommunication(0, solidsTarget, getContent, solidsHtml);
		/*
		/*
		 * test procFacilityPage
		Facility fac = new Facility("110012303854");
		dataAgent.procFacilityPage(fac);
		fac.printFacility();
		*/
		
		/*
		 * test dataAgent.getDataFromFacilitySoup
		Facility fac = new Facility("110012303854");		
		dataAgent.getDataFromFacilitySoup("/home/ping/research/python/water/fac/epaFacSoup110012303854",
				fac);
		fac.printFacility();
		*/
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
		/*
		String curLine = "javascript: void window.open('http://www.epa-echo.gov/cgi-bin/effluents.cgi?permit=NY0261343&charts=viols&monlocn=all&outt=all','','height=480,width=800,resizable=yes,scrollbars=yes,menubar=yes,toolbar=yes,screenX=10,screenY=10')\n";
		int httpIndex = curLine.indexOf("http:");
		String linkChartsWithViolations = curLine.substring(httpIndex,
				curLine.indexOf('\'', httpIndex));
		System.out.print(linkChartsWithViolations);
		*/
		
		/*		
		 * //test if an obj in 2 lists are the same obj
		Facility fac1 = new Facility("100000000001");
		Facility fac2 = new Facility("100000000002");
		Facility fac3 = new Facility("100000000003");
		ArrayList<Facility> facList1 = new ArrayList<Facility> ();
		ArrayList<Facility> facList2 = new ArrayList<Facility> ();
		facList1.add(fac1);
		facList1.add(fac2);
		facList1.add(fac3);
		facList2.add(fac1);
		facList2.add(fac2);
		util.printFacilityArrayList(facList1, 
		"/home/ping/research/python/water/CgiSoupOutput/temp/facList1");
		util.printFacilityArrayList(facList2, 
		"/home/ping/research/python/water/CgiSoupOutput/temp/facList2");
		fac1.setOCVLink("abc");
		util.printFacilityArrayList(facList1, 
		"/home/ping/research/python/water/CgiSoupOutput/temp/changedfacList1");
		util.printFacilityArrayList(facList2, 
		"/home/ping/research/python/water/CgiSoupOutput/temp/changedfacList2");
		 */

	}

	@Override
	public boolean getData(OntModel owlModel, Model pmlModel) {
		return getData(zip, owlModel, pmlModel);
	}

	@Override
	public boolean getData(OntModel owlModel, Model pmlModel, Date start,
			Date end) {
		return false;
	}

	@Override
	public String getName() {
		return "Environmental Protection Agency";
	}

	@Override
	public URL getURL() {
		try {
			return new URL("http://www.epa-echo.gov/echo/");
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}

	String zip;
	
	@Override
	public void setUserSource(String county, String state, String zip) {
		this.zip = zip;
	}

}
