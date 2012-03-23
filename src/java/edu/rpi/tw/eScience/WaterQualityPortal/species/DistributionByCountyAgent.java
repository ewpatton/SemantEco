package edu.rpi.tw.eScience.WaterQualityPortal.species;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.HashMap;

import com.csvreader.CsvReader;

import edu.rpi.tw.eScience.WaterQualityPortal.epa.foia.FoiaTranslator;
import edu.rpi.tw.eScience.WaterQualityPortal.epa.foia.FoiaUtil;

/*
 * add scientific names 
 * use tables to get long status*/
public class DistributionByCountyAgent {	
	static HashMap<String, String> stateStatusTable=new HashMap<String, String>();
	static HashMap<String, String> fedStatusTable=new HashMap<String, String>();
	
	static{
		buildStateStatusTable();
		buildfedStatusTable();
	}
	
	static private void buildStateStatusTable(){
		stateStatusTable.put("SE", "State Endangered");
		stateStatusTable.put("ST", "State Threatened");
		stateStatusTable.put("SC", "State Candidate");
		stateStatusTable.put("SS", "State Sensitive");
		stateStatusTable.put("SS", "State Monitored");		
	}
	
	static private void buildfedStatusTable(){
		fedStatusTable.put("FE", "Federal Endangered");
		fedStatusTable.put("FT", "Federal Threatened");
		fedStatusTable.put("FC", "Federal Candidate");
		fedStatusTable.put("FCo", "Federal Species of Concern");
	}

	private void printArr(String[] strArr){
		for(String str:strArr){
			System.out.println(str);
		}		
	}
	
	private String[] procSpeciesName(String name){
		String[] names = null;
		//for cases with :
		int pos=name.indexOf(":");
		if(pos!=-1){
			name=name.substring(pos+1);
			names=name.split(", ");
			return names;
		}
		//for cases with /
		pos = name.indexOf('/');
		if(pos!=-1){
			name=name.substring(0, pos);
		}
		names=new String[0];
		names[0]=name;
		return names;
	}
	
	
	private List<String> procSpeciesNames(String[] names){
		List<String> nameList = new ArrayList<String> ();
		for(String name:names){
			int pos = name.indexOf('/');
			if(pos!=-1)
				name=name.substring(0, pos);
			
			pos=name.indexOf(":");
			
			
			nameList.add(name);
			System.out.println(name);
		}			
		return nameList;		
	}
	
	private void processCSV(String inputFile, String outputFile){		
		CsvReader reader = null;
		int recordNum = 0;
		BufferedWriter bufferedWriter = null;
		
		String subject = null, unitShortName=null, unitLongName=null;
		String[] headers=null;
		String[] stateStatus=null, fedStatus=null;
	
		try {			
			bufferedWriter = new BufferedWriter(new FileWriter(outputFile));
			reader = new CsvReader(inputFile);		
			reader.readHeaders();
			headers=reader.getHeaders();
			int numHeaders=headers.length;
			printArr(headers);
			//System.out.println("Num of headers: "+numHeaders);
			recordNum++;
			
			bufferedWriter.write("\"County\", \"SpeciesName\", " +
					"\"StateStatus\", \"FedStatus\""+
					"\n");

			while (reader.readRecord())
			{			
				recordNum++;
				subject = reader.get("Species/ Habitat").trim();
	
				if(subject.compareTo("State Status")==0){
					stateStatus=reader.getRawRecord().split(",");					
					//printArr(stateStatus);
				}
				if(subject.compareTo("FedStatus")==0){
					fedStatus=reader.getRawRecord().split(",");					
					//printArr(fedStatus);
				}				
				if(recordNum>=10){
					for(int i=21;i<numHeaders;i++){
						String spc = headers[i];
						String st=stateStatus[i];
						String fed=fedStatus[i];
						bufferedWriter.write("\""+subject+"\", \""+spc+
								"\", \""+st+"\", \""+fed+"\"\n");						
					}
				}
				//System.out.println("Record " + recordNum);
				//System.out.println(reader.getRawRecord());
				//find the next element

			}//end of while
	
		} catch (FileNotFoundException e) {
			System.err.println("In processCSV(), file name: " + inputFile);
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("In processCSV(), file name: " + inputFile);
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		}
		finally{
			try{
			reader.close();
			if (bufferedWriter != null) {
				bufferedWriter.flush();
				bufferedWriter.close();
			}
			}catch (IOException ex) {
				System.err.println("In processCSV, closing the reader and BufferedWriter");
				ex.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		DistributionByCountyAgent agent = new DistributionByCountyAgent();		
		String waFile = "./2012_distribution_by_county.csv";
		String out = "./wa_2012_distribution_by_county.csv";
		agent.processCSV(waFile, out);

	}
}
