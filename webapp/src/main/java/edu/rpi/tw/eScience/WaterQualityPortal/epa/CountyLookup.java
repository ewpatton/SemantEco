package edu.rpi.tw.eScience.WaterQualityPortal.epa;

import java.io.*;
import java.util.*;

@Deprecated
public class CountyLookup {
	String state;
	String stateCode;
	HashMap<String, String> countyCode2Name;
	HashMap<String, String> countyName2Code;
	static String baseDir = "/media/DATA/source/usgs-gov/qwwebservices-codes/version/2011-Mar-20/manual/county-info/";
	
	CountyLookup(String state, String stateCode){
		this.state = state;
		this.stateCode = stateCode;
		countyCode2Name = new HashMap<String, String>();
		countyName2Code = new HashMap<String, String>();
		buildLookupTables();
	}
	
	public String toString(){
		StringBuffer sbuf = new StringBuffer();
		sbuf.append("State: "+state + "StateCode: "+stateCode+"\n");
		sbuf.append("countyCode2Name\n");
		sbuf.append(countyCode2Name.toString());
		sbuf.append("\ncountyName2Code\n");
		sbuf.append(countyName2Code.toString()+"\n");
		return sbuf.toString();		
	}
	
	public String code2Name(String ctyCode){
		String ctyName = countyCode2Name.get(ctyCode);
		if(ctyName == null){
			ctyName = "";
			System.err.println("\nIn CountyCodeLookup, can't get the name for code: "+ctyCode);
		}
		return ctyName;	
	}
	
	public String name2Code(String ctyName){
		String ctyCode = countyName2Code.get(ctyName.toUpperCase());
		if(ctyCode == null){
			ctyCode = "";
			System.err.println("In CountyCodeLookup, can't get the code for name: "+ctyName);
			return "";
		}
		return ctyCode;	
	}
	
	private void buildLookupTables(){
		String fileName = "US:" + stateCode + "-county-info.csv";
		String infoFile = baseDir+fileName;
		FileInputStream fIn = null;
		BufferedReader reader = null;
		try{
			fIn =  new FileInputStream(infoFile);
			reader = new BufferedReader(new InputStreamReader(fIn));	
			String strLine;
			//skip the 1st line
			strLine = reader.readLine();
			while ((strLine = reader.readLine()) != null)   {
				//System.out.println (strLine);
				String[] parts = strLine.split(",");
				String countyCode = state+parts[2].trim();
				String countyName = parts[3].trim();
				if(countyCode2Name.get(countyCode)==null)
					countyCode2Name.put(countyCode, countyName);
				if(countyName2Code.get(countyName)==null)
					countyName2Code.put(countyName, countyCode);				
			}
		}
		catch (Exception e) {
			System.err.println("In buildLookupTables, err");
			e.printStackTrace();
		}finally {
			//Close the BufferedReader and BufferedWriter			
			try {
				if (reader!=null)
					reader.close ();
			} catch (IOException ex) {
				System.err.println("In buildLookupTables, closing the reader");
				ex.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		CountyLookup lookup = new CountyLookup("RI", "44");
		System.out.print(lookup);
		System.out.print("\n"+lookup.code2Name("RI001"));
		System.out.print("\n"+lookup.name2Code("NEWPORT"));
	}

}
