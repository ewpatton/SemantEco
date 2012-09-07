package edu.rpi.tw.escience.WaterQualityPortal.epa.foia;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.Map.Entry;

import com.csvreader.*;

@Deprecated
public class FoiaDataAgent {
	
	HashMap<String, String> unitCode2ShortName;
	HashMap<String, String> unitCode2LongName;
	
	FoiaDataAgent(){
		unitCode2ShortName= new HashMap<String, String>();
		unitCode2LongName= new HashMap<String, String>();
		//buildUnitLookupTables();
		buildUnitLookupTablesFromCSV();
		
	}
	
	public void printUnitCodeForTest(){
        System.out.println("Retrieving all keys from the HashMap: unitCode2ShortName");        
        Iterator<String> iteratorKey = unitCode2ShortName.keySet().iterator();
       
        while(iteratorKey. hasNext()){        
            System.out.println(iteratorKey.next()+",");
        }       
	}
	
	public void printUnitShortNameForTest(){
        System.out.println("Retrieving all values from the HashMap: unitCode2ShortName");
        Iterator<Entry<String, String>> iteratorValue = unitCode2ShortName.entrySet().iterator();
       
        while(iteratorValue. hasNext()){        
            System.out.println(iteratorValue.next().getValue()+"|");
        }		
	}
	
	public void printUnitLongNameForTest(){        
        System.out.println("Retrieving all values from the HashMap: unitCode2LongName");
        Iterator<Entry<String, String>> iteratorValue = unitCode2LongName.entrySet().iterator();
       
        while(iteratorValue. hasNext()){        
            System.out.println(iteratorValue.next().getValue()+"|");
        }     
		
	}
	
	public String toString(){
		StringBuffer sbuf = new StringBuffer();
		sbuf.append("unitCode2ShortName\n");
		sbuf.append(unitCode2ShortName.toString());
		sbuf.append("\nunitCode2LongName\n");
		sbuf.append(unitCode2LongName.toString()+"\n");
		return sbuf.toString();		
	}
	
	@SuppressWarnings("unused")
	private void buildUnitLookupTables(){
		String infoFile = "./procTABLE180a.txt";
		String delimiter = "\\|";
		FileInputStream fIn = null;
		BufferedReader reader = null;
		try{
			fIn =  new FileInputStream(infoFile);
			reader = new BufferedReader(new InputStreamReader(fIn));	
			String strLine;

			while ((strLine = reader.readLine()) != null)   {
				//System.out.println (strLine);	
				if(strLine.startsWith("1Page")){
					//skip the next 3 lines
					strLine = reader.readLine();
					strLine = reader.readLine();
					strLine = reader.readLine();					
				}
				else{
					//System.out.println (strLine);	
					String[] parts = strLine.split(delimiter);
					String unitCode = parts[0].trim();
					String unitShortName = parts[1].trim();
					String unitLongName = parts[3].trim();
					if(unitCode2ShortName.get(unitCode)==null)
						unitCode2ShortName.put(unitCode, unitShortName);
					if(unitCode2LongName.get(unitCode)==null)
						unitCode2LongName.put(unitCode, unitLongName);					
				}				
						
			}
		}
		catch (Exception e) {
			System.err.println("In buildUnitLookupTables, err");
			e.printStackTrace();
		}finally {
			//Close the BufferedReader and BufferedWriter			
			try {
				if (reader!=null)
					reader.close ();
			} catch (IOException ex) {
				System.err.println("In buildUnitLookupTables, closing the reader");
				ex.printStackTrace();
			}
		}
	}
	
	private void buildUnitLookupTablesFromCSV(){
		String infoFile = "./ICISTable_UnitCodes.csv";
		CsvReader reader = null;
		String unitCode = null, unitShortName=null, unitLongName=null;
		@SuppressWarnings("unused")
		int recordNum = 0;
	
		try {			
			reader = new CsvReader(infoFile);		
			reader.readHeaders();
			recordNum++;

			while (reader.readRecord())
			{			
				recordNum++;
				unitCode = reader.get("UNIT_CODE").trim();
				unitShortName=reader.get("UNIT_SHORT_DESC").trim();
				unitLongName=reader.get("UNIT_DESC").trim();
				if(unitCode2ShortName.get(unitCode)==null)
					unitCode2ShortName.put(unitCode, unitShortName);
				if(unitCode2LongName.get(unitCode)==null)
					unitCode2LongName.put(unitCode, unitLongName);		
				
				//System.out.println("Record " + recordNum);
				//System.out.println(reader.getRawRecord());
				//find the next element

			}//end of while
	
		} catch (FileNotFoundException e) {
			System.err.println("In buildUnitLookupTablesFromCSV(), file name: " + infoFile);
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("In buildUnitLookupTablesFromCSV(), file name: " + infoFile);
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		}
		finally{
			reader.close();
		}

	}
	
	public String unitCode2ShortName(String code){
		String shortName = unitCode2ShortName.get(code);
		if(shortName == null){
			shortName = "";
			System.err.println("\nIn unitCode2ShortName, can't get the name for unit code: "+code);
		}
		return shortName;	
	}
	
	public String unitCode2LongName(String code){
		String longName = unitCode2LongName.get(code);
		if(longName == null){
			longName = "";
			System.err.println("\nIn unitCode2ShortName, can't get the name for unit code: "+code);
		}
		return longName;	
	}
	
	public void processFile(String inputfileName){
		BufferedWriter bufferedWriter = null;
		int fnIndex=inputfileName.lastIndexOf('/');
		String outputFileName=inputfileName.substring(0,fnIndex+1)+"proc_"
			+inputfileName.substring(fnIndex+1);
		//System.err.println(outputFileName);
		
		try{
			bufferedWriter = new BufferedWriter(new FileWriter(outputFileName));
			CSVRead(inputfileName, bufferedWriter);

		}
		catch (Exception e) {
			System.err.println("In processFile, err");
			e.printStackTrace();
		}finally {
			//Close the BufferedWriter			
			try {
				if (bufferedWriter != null) {
					bufferedWriter.flush();
					bufferedWriter.close();
				}
			} catch (IOException ex) {
				System.err.println("In processFile, closing the BufferedWriter");
				ex.printStackTrace();
			}
		}
	}
	
	
	public void CSVRead(String inputfileName, BufferedWriter bufferedWriter){		
		CsvReader reader = null;
		@SuppressWarnings("unused")
		int recordNum = 0;
		String curUnitCode = null, curUnitShortName=null, curUnitLongName=null;
	
		try {			
			reader = new CsvReader(inputfileName);		

			reader.readHeaders();
			String[] headers=reader.getHeaders();
			int lenHeaders=headers.length;
			int i=0;
			for(i=0;i<lenHeaders-1;i++)
				bufferedWriter.write("\""+headers[i]+"\",");
			bufferedWriter.write("\""+headers[i]+"\"");
			bufferedWriter.write(",\"UNIT_SHORT_NAME\",\"UNIT_LONG_NAME\"");
			bufferedWriter.write("\n");
			recordNum++;

			while (reader.readRecord())
			{			
				recordNum++;
				curUnitCode = reader.get("UNIT_CODE");	
				curUnitShortName=unitCode2ShortName(curUnitCode);
				curUnitLongName=unitCode2LongName(curUnitCode);				
				
				//System.out.println("Record " + recordNum);
				//System.out.println(reader.getRawRecord());
				
				//bufferedWriter.write(reader.getRawRecord()+'\n');
				bufferedWriter.write(reader.getRawRecord()+", \""+curUnitShortName+"\""
						+", \""+curUnitLongName+"\""
						+'\n');
				//find the next element

			}//end of while
	
		} catch (FileNotFoundException e) {
			System.err.println("In CSVRead(), file name: " + inputfileName);
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("In CSVRead(), file name: " + inputfileName);
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		}
		finally{
			reader.close();
		}

	}

	public static void main(String[] args) {
		FoiaDataAgent agent = new FoiaDataAgent();
		//agent.printUnitCodeForTest();
		//agent.printUnitShortNameForTest();
		//agent.printUnitLongNameForTest();
		//System.out.print(agent);
		if (args.length <= 0) {
			System.out.println("Usage: ./FoiaDataAgent inputFileName");
			System.exit(0);
		}
		//String inputFile="/media/DATA/epaMetaData/head3000FOIA_DMRs_NH.csv";
		String inputFile = args[0];
		agent.processFile(inputFile);
	}

}
