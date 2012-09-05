package edu.rpi.tw.eScience.WaterQualityPortal.epa;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import com.csvreader.*;

@Deprecated
public class FoiaDataAgent {
	
	HashMap<String, String> unitCode2ShortName;
	HashMap<String, String> unitCode2LongName;
	
	FoiaDataAgent(){
		unitCode2ShortName= new HashMap<String, String>();
		unitCode2LongName= new HashMap<String, String>();
		buildUnitLookupTables();
		
	}
	
	public String toString(){
		StringBuffer sbuf = new StringBuffer();
		sbuf.append("unitCode2ShortName\n");
		sbuf.append(unitCode2ShortName.toString());
		sbuf.append("\nunitCode2LongName\n");
		sbuf.append(unitCode2LongName.toString()+"\n");
		return sbuf.toString();		
	}
	
	private void buildUnitLookupTables(){
		String infoFile = "/media/DATA/epaMetaData/procTABLE180a.txt";
		String delimiter = "\\|";
		FileInputStream fIn = null;
		BufferedReader reader = null;
		try{
			fIn =  new FileInputStream(infoFile);
			reader = new BufferedReader(new InputStreamReader(fIn));	
			String strLine;

			while ((strLine = reader.readLine()) != null)   {
				System.out.println (strLine);	
				if(strLine.startsWith("1Page")){
					//skip the next 3 lines
					strLine = reader.readLine();
					strLine = reader.readLine();
					strLine = reader.readLine();					
				}
				else{
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
	
	@SuppressWarnings("unused")
	public void CSVRead(String inputfileName, String postContent, Facility curFac, HashMap<String, MeasurementConstraint> constraintsMap){		
		CsvReader reader = null;
		int recordNum = 0;
		String curUnitCode = null;
	
		try {			
			reader = new CsvReader(inputfileName);		

			reader.readHeaders();
			recordNum++;

			while (reader.readRecord())
			{			
				recordNum++;
				curUnitCode = reader.get("UNIT_CODE");				
				//System.out.println("Record " + recordNum + ": "+elementName);
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
		System.out.print(agent);

	}

}
