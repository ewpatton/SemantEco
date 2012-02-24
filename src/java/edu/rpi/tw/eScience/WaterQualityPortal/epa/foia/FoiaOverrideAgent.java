package edu.rpi.tw.eScience.WaterQualityPortal.epa.foia;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class FoiaOverrideAgent {
	HashMap<String, String> minMap;
	HashMap<String, String> avgMap;
	static String overrideObj = "";

	FoiaOverrideAgent(String dir, String inputFileName){
		minMap = new HashMap<String, String>();
		avgMap = new HashMap<String, String>();
		buildOverrideTable(dir, inputFileName);
	}

	public void buildOverrideTable(String dir, String inputFileName){
		FileInputStream fIn = null;
		BufferedReader reader = null;
		BufferedWriter bufferedWriter = null;
		String outputFileName = "proc-"+inputFileName;
		try{
			fIn =  new FileInputStream(dir+inputFileName);
			reader = new BufferedReader(new InputStreamReader(fIn));
			bufferedWriter = new BufferedWriter(new FileWriter(dir+outputFileName));			
			String strLine;

			while ((strLine = reader.readLine()) != null)   {
				//System.out.println (strLine);
				//LCMO     001      109- 109     Stat-Lim Conc Lim Min Override
				char minFlag=strLine.charAt(108);
				//LCAO     001      110- 110     Stat-Lim Conc Lim Avg Override
				char avgFlag=strLine.charAt(109);
				if(minFlag=='Y'){
					bufferedWriter.write(strLine+"\n");
					processOneRecordPerType(0, strLine);
				}
				if(avgFlag=='Y'){
					bufferedWriter.write(strLine+"\n");
					processOneRecordPerType(1, strLine);
				}			
			}

		}
		catch (Exception e) {
			System.err.println("In processFile, err");
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
				System.err.println("In processFile, closing the reader and BufferedWriter");
				ex.printStackTrace();
			}
		}
	}
	
	public boolean isOverriden(int orType, String rcd){		
/*		NPID     009        1-   9     Facility ID Number          
	    PDSG     004       10-  13     Limit Discharge/Rep Designator    
	    LIPQ     001       14-  14     Pipe Set Qualifier
	    PRAM     005       15-  19     Paramater Name       
	    MLOC     001       20-  20     Monitoring Location       
	    SEAN     001       21-  21     Season Number   
	    LTYP     001       22-  22     Limit Type         
	    MODN     001       23-  23     Modification Number            
	    ELSD     006       24-  29     Mod Period Start Date */	
		
		String curKey=rcd.substring(0, 29).trim();
		switch (orType){
		case 0://minFlag
			if(minMap.get(curKey)!=null){
				System.err.println("The min override flag for "+curKey+" has been set.");
				return true;
			}
			else
				return false;
		case 1://avgFlag
			if(avgMap.get(curKey)!=null){
				System.err.println("The avg override flag for "+curKey+" has been set.");
				return true;
			}
			else
				return false;
		default:
			System.err.println("In processOneRecordPerType, unknown type: "+orType);
			System.exit(-1);
		}
		return false;
	}

	public void processOneRecordPerType(int orType, String rcd){
/*	    NPID     009        1-   9     Facility ID Number          
	    PDSG     004       10-  13     Limit Discharge/Rep Designator    
	    LIPQ     001       14-  14     Pipe Set Qualifier
	    PRAM     005       15-  19     Parameter Name  
	    MLOC     001       20-  20     Monitoring Location       
	    SEAN     001       21-  21     Season Number
	    LTYP     001       22-  22     Limit Type         
	    MODN     001       23-  23     Modification Number  
		ELSD     006       24-  29     Mod Period Start Date */			
	 
		String curKey=rcd.substring(0, 29).trim();
		switch (orType){
		case 0://minFlag
			if(minMap.get(curKey)!=null)
				System.err.println("The min override flag for "+curKey+" has been set before.");
			else
				minMap.put(curKey, overrideObj);
			break;
		case 1://avgFlag
			if(avgMap.get(curKey)!=null)
				System.err.println("The avg override flag for "+curKey+" has been set before.");
			else
				avgMap.put(curKey, overrideObj);
			break;
		default:
			System.err.println("In processOneRecordPerType, unknown type: "+orType);
			System.exit(-1);
			break;
		}
	}
	

	public static void main(String[] args) {        
		String dir="input/override/";
		String inputFile = "WA-F01613P-Ping-Wang-Overrides.txt";
		//String outputFile = "proc-"+inputFile;
		FoiaOverrideAgent orAgent = new FoiaOverrideAgent(dir, inputFile);
		//orAgent.processFile(dir+inputFile, dir+outputFile);
		System.out.println("Min Map");
		FoiaUtil.printKeyOfHashMap(orAgent.minMap);
		System.out.println("Avg Map");
		FoiaUtil.printKeyOfHashMap(orAgent.avgMap);
		//FoiaUtil.printValueOfHashMap(frsagent.perm2UIN);
		//System.out.println(frsagent.perm2UIN("WAR007386"));
	}

}
