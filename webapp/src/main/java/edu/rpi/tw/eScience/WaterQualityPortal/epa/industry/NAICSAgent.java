package edu.rpi.tw.eScience.WaterQualityPortal.epa.industry;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import com.csvreader.CsvReader;

@Deprecated
public class NAICSAgent {
	public static boolean DEBUG = false;
	HashMap<String, String> uin2naics;
	
	public NAICSAgent(String codeFile){
		uin2naics = new HashMap<String, String>();
		buildLookupTable(codeFile);
	}
	
	public String uin2NaicsCode(String uin){
		if(uin==null || uin.length()==0)
			return "";
		String naics = uin2naics.get(uin);
		if(naics == null){
			naics = "";
			System.err.println("\nIn uin2NaicsCode, can't get the name for uin: "+uin);
		}
		return naics;	
	}
	
	public void buildLookupTable(String codeFile){
		CsvReader reader = null;
		String uin = null, naics=null;
		@SuppressWarnings("unused")
		int recordNum = 0;
	
		try {			
			reader = new CsvReader(codeFile);		
			reader.readHeaders();
			recordNum++;

			while (reader.readRecord())
			{			
				recordNum++;
				uin = reader.get("V_EF_NAICS_EZ.REGISTRY_ID").trim();
				naics=reader.get("_EF_NAICS_EZ.NAICS_CODE").trim();
				if(uin2naics.get(uin)==null)
					uin2naics.put(uin, naics);
				//System.out.println("Record " + recordNum);
				//System.out.println(reader.getRawRecord());
			}//end of while
	
		} catch (FileNotFoundException e) {
			System.err.println("In buildLookupTable(), file name: " + codeFile);
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("In buildLookupTable(), file name: " + codeFile);
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		}
		finally{
			reader.close();
		}		
	}
	
	public void addNAICS(String facFile, String output){
		FileInputStream fIn = null;
		BufferedReader reader = null;
		BufferedWriter bufferedWriter = null;

		try{
			fIn =  new FileInputStream(facFile);
			reader = new BufferedReader(new InputStreamReader(fIn));
			bufferedWriter = new BufferedWriter(new FileWriter(output));
			String strLine;
			//skip the 1st line of column names
			strLine = reader.readLine();
			bufferedWriter.write(strLine+"NAICS|\n");
			
			while ((strLine = reader.readLine()) != null)   {
				//System.out.println (strLine);
				addNAICSforOneFacility(strLine, bufferedWriter);
			}
		}
		catch (Exception e) {
			System.err.println("In addNAICS, err");
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
				System.err.println("In addNAICS, closing the reader and BufferedWriter");
				ex.printStackTrace();
			}
		}			
	}
	
	public void addNAICSforOneFacility(String fclData, BufferedWriter bufWriter){
		String delimiter = "\\|";
		
		if(DEBUG)
			System.out.println(fclData);
		String[] parts = fclData.split(delimiter);
		if(DEBUG){
			for(int i =0; i < parts.length ; i++)
				System.out.println(parts[i]);
		}
		
		String uin=parts[1];
		String naics = uin2NaicsCode(uin);
		
		//write to file
		try {
			bufWriter.write(fclData+naics+"|\n");
		} catch (IOException e) {
			System.err.println("In addNAICSforOneFacility, err when writing to the output file");
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {

		String codeFile = "data/industry/RI_NAICS.CSV";
		String facFile="data/industry/fixed-RI-ICP01.TXT";
		String output="data/industry/naics-RI-ICP01.TXT";		
		//agent.buildLookupTable(codeFile);
		//FoiaUtil.printHashMap(agent.uin2naics);
		NAICSAgent agent = new NAICSAgent(codeFile);
		agent.addNAICS(facFile, output);
	}


}
