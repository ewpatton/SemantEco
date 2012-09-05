package edu.rpi.tw.eScience.WaterQualityPortal.species;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import edu.rpi.tw.eScience.WaterQualityPortal.epa.foia.FoiaTranslator;
import edu.rpi.tw.eScience.WaterQualityPortal.epa.foia.FoiaUtil;

public class FipsCodeAgent {
	static String prefix="\"US:53:";
	static int prefixLen=prefix.length();	
	String inputFile=null;
	HashMap<String, String> fipsCode2Name;	
	HashMap<String, String> fipsName2Code;
	
	FipsCodeAgent(String inputFile){
		this.inputFile = inputFile;
		fipsCode2Name = new HashMap<String, String>();
		fipsName2Code = new HashMap<String, String>();
		buildLookupTables(inputFile);
	}
	
	public Set<String> getCountyFips(){
		return fipsCode2Name.keySet();		
	}
	
	public String code2Name(String code){
		String name=fipsCode2Name.get(code);
		if(name == null){
			name = "";
			System.err.println("\nIn FipsCodeAgent.code2Name, can't get the name for code: "+code);
		}
		return name;
	}

	public String name2Code(String name){
		if(name==null)
			return "";
		String code=fipsName2Code.get(name.toUpperCase());
		if(code == null){
			code = "";
			System.err.println("\nIn FipsCodeAgent.name2Code, can't get the code for name: "+name);
		}
		return code;
	}
	
	private void buildLookupTables(String inputFile){
		FileInputStream fIn = null;
		BufferedReader reader = null;
		
		try{
			fIn =  new FileInputStream(inputFile);
			reader = new BufferedReader(new InputStreamReader(fIn));		
			String strLine;
			String code="", desc="", name="";
			int posCode, posName;
			while ((strLine = reader.readLine()) != null)   {
				posCode=strLine.indexOf('|');
				if(posCode!=-1)
					code=strLine.substring(posCode-3, posCode);
				desc=strLine.substring(posCode+1);
				String[] parts=desc.split(",");
				if(parts.length!=3){
					System.err.println("record not well formated: "+strLine);
				}
				else
					name=parts[2].trim();
				
				if(fipsCode2Name.get(code)==null)
					fipsCode2Name.put(code, name);
				if(fipsName2Code.get(name)==null)
					fipsName2Code.put(name, code);
			}
		}
		catch (Exception e) {
			System.err.println("In buildParaLookupTable, err");
			e.printStackTrace();
		}finally {
			//Close the BufferedReader 			
			try {
				if (reader!=null)
					reader.close ();
			} catch (IOException ex) {
				System.err.println("In FipsCodeAgent, closing the reader");
				ex.printStackTrace();
			}
		}
	}
	public static void main(String[] args) {
		String input="./53-county-code.txt";
		FipsCodeAgent agent = new FipsCodeAgent(input);		
		//FoiaUtil.printHashMap(agent.fipsCode2Name);
		//FoiaUtil.printValueOfHashMap(agent.fipsCode2Name);
		//FoiaUtil.printHashMap(agent.fipsName2Code);

	}
}
