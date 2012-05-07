package edu.rpi.tw.eScience.WaterQualityPortal.epa.foia;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.csvreader.CsvReader;

public class FoiaTranslator {
	static String paraTable = "./TABLE160a.txt"; 
	static String unitTable = "./ICISPCS_UnitCodes.csv";
	//Statistical Base Codes
	static String sbCodeTable = "./ICISTable_StatisticalBaseCodes.csv";

	HashMap<String, String> paraCode2Name;	
	HashMap<String, String> unitCode2ShortName;
	HashMap<String, String> unitCode2LongName;
	HashMap<String, String> sbCode2LongName;
	
	FoiaTranslator(){
		paraCode2Name = new HashMap<String, String>();
		unitCode2ShortName= new HashMap<String, String>();
		unitCode2LongName= new HashMap<String, String>();
		sbCode2LongName= new HashMap<String, String>();
		buildParaLookupTable();
		buildUnitLookupTablesFromCSV();
		buildSBCodeLookupTablesFromCSV();		
	}
	
	
	public String paraCode2Name(String code){
		String name = paraCode2Name.get(code);
		if(name == null){
			name = "";
			System.err.println("\nIn unitCode2ShortName, can't get the name for unit code: "+code);
		}
		return name;	
	}
	
	private String formatStr(String src) {
		char[] chars = src.toLowerCase().toCharArray();
		if (Character.isLetter(chars[0]))
			chars[0] = Character.toUpperCase(chars[0]);
		if(chars.length>=2 && chars[0]=='P'&&chars[1]=='h')
			if(chars.length==2 || Character.isWhitespace(chars[2]))
					chars[1] = Character.toUpperCase(chars[1]);

		return String.valueOf(chars);
	}
	
	private void buildParaLookupTable(){
		FileInputStream fIn = null;
		BufferedReader reader = null;
		
		try{
			fIn =  new FileInputStream(paraTable);
			reader = new BufferedReader(new InputStreamReader(fIn));		
			String strLine;
			String code=null, name=null;
			while ((strLine = reader.readLine()) != null)   {
				if(strLine.startsWith("1Page")){
					for(int i=0;i<4&&strLine!=null;i++){
						strLine = reader.readLine();
						//System.out.println("line "+ i + strLine);
					}
				}
				code=strLine.substring(1, 6);
				name=strLine.substring(6).trim().replaceAll(" +", " ");				
				if(paraCode2Name.get(code)==null)
					paraCode2Name.put(code, formatStr(name));
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
				System.err.println("In fixFile, closing the reader");
				ex.printStackTrace();
			}
		}
	}
	
	
	private void buildUnitLookupTablesFromCSV(){		
		CsvReader reader = null;
		String unitCode = null, unitShortName=null, unitLongName=null;
		int recordNum = 0;
	
		try {			
			reader = new CsvReader(unitTable);		
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
			System.err.println("In buildUnitLookupTablesFromCSV(), file name: " + unitTable);
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("In buildUnitLookupTablesFromCSV(), file name: " + unitTable);
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		}
		finally{
			reader.close();
		}
	}
	
	private void buildSBCodeLookupTablesFromCSV(){		
		CsvReader reader = null;
		String sbCode = null, sbLongName=null;
		int recordNum = 0;
	
		try {			
			reader = new CsvReader(sbCodeTable);		
			reader.readHeaders();
			recordNum++;

			while (reader.readRecord())
			{			
				recordNum++;
				sbCode = reader.get("STATISTICAL_BASE_CODE").trim();
				sbLongName=reader.get("STATISTICAL_BASE_LONG_DESC").trim();

				if(sbCode2LongName.get(sbCode)==null)
					sbCode2LongName.put(sbCode, sbLongName);	
				
				//System.out.println("Record " + recordNum);
				//System.out.println(reader.getRawRecord());
			}//end of while
	
		} catch (FileNotFoundException e) {
			System.err.println("In buildSBCodeLookupTablesFromCSV(), file name: " + sbCodeTable);
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("In buildSBCodeLookupTablesFromCSV(), file name: " + sbCodeTable);
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		}
		finally{
			reader.close();
		}
	}
	
	public String sbCode2LongName(String code){
		if(code==null || code.length()==0)
			return "";
		String longName = sbCode2LongName.get(code);
		if(longName == null){
			longName = "";
			System.err.println("\nIn sbCode2LongName, can't get the name for unit code: "+code);
		}
		return longName;	
	}
	
	public String unitCode2ShortName(String code){
		if(code==null || code.length()==0)
			return "";
		String shortName = unitCode2ShortName.get(code);
		if(shortName == null){
			shortName = "";
			System.err.println("\nIn unitCode2ShortName, can't get the name for unit code: "+code);
		}
		return shortName;	
	}
	
	public String unitCode2LongName(String code){
		if(code==null || code.length()==0)
			return "";
		String longName = unitCode2LongName.get(code);
		if(longName == null){
			longName = "";
			System.err.println("\nIn unitCode2ShortName, can't get the name for unit code: "+code);
		}
		return longName;	
	}
	
	public String limitTypeShort2LongName(String src){
		if(src.equals("1")||src.equals("I"))
			return "Initial";
		else if(src.equals("3")||src.equals("M"))
			return "Interim";
		else if(src.equals("5")||src.equals("F"))
			return "Final";
		else{
			System.err.println("Unknown limit type: "+src);
			return "";
		}
	}
	
//	public String procValue(String src){			
//		if(src==null || src.length()==0)
//			return "";
//		double value=0;
//		try{
//			value=Double.parseDouble(src);
//			return decFormat.format(value);
//		}
//		catch(NumberFormatException e){
//			return "";
//		}		
//	}
	
//	//if(src.equals("DELMON")||src.equals("OPTMON")||src.equals("ADDMON"))
//	public String procLimit(String src){
//		if(src==null || src.length()==0)
//			return "";
//		try{
//			Double.parseDouble(src);
//			if(src.charAt(0)=='.')
//				return "0"+src;
//			else
//				return src;
//		}
//		catch(NumberFormatException e){
//			return "";
//		}	
//	}
		
	public static void main(String[] args) {
		FoiaTranslator translator = new FoiaTranslator();		
		FoiaUtil.printValueOfHashMap(translator.paraCode2Name);
		//System.out.println(translator.paraCode2Name("-----"));
		//translator.printValueOfHashMap(translator.paraCode2Name);
		//translator.printHashMap(translator.paraCode2Name);
		//System.out.println(translator.paraCode2Name("71900"));
		//translator.printHashMap(translator.sbCode2LongName);
		//System.out.println(translator.sbCode2LongName("DB"));	
	}

}
