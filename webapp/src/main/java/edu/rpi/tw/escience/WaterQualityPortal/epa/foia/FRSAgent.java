package edu.rpi.tw.escience.WaterQualityPortal.epa.foia;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import com.csvreader.CsvReader;

@Deprecated
public class FRSAgent {
	//static String perm2UINFile = "./WAUIN.CSV"; 
	HashMap<String, String> perm2UIN;	
	
	FRSAgent(String srcFile){
		perm2UIN = new HashMap<String, String>();
		buildPerm2UINLookupTablesFromCSV(srcFile);		
	}
	
	private void buildPerm2UINLookupTablesFromCSV(String srcFile){		
		CsvReader reader = null;
		String perm = null, uin=null;
		@SuppressWarnings("unused")
		int recordNum = 0;
	
		try {			
			reader = new CsvReader(srcFile);		
			reader.readHeaders();
			recordNum++;

			while (reader.readRecord())
			{			
				recordNum++;
				perm = reader.get("V_EF_PROGRAM_FACILITY_EZ.PGM_SYS_ID").trim();
				uin=reader.get("V_EF_PROGRAM_FACILITY_EZ.REGISTRY_ID").trim();
				if(perm2UIN.get(perm)==null)
					perm2UIN.put(perm, uin);				
				//System.out.println("Record " + recordNum);
				//System.out.println(reader.getRawRecord());
				//find the next element

			}//end of while
	
		} catch (FileNotFoundException e) {
			System.err.println("In buildPerm2UINLookupTablesFromCSV(), file name: " + srcFile);
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("In buildPerm2UINLookupTablesFromCSV(), file name: " + srcFile);
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		}
		finally{
			reader.close();
		}
	}

	public String perm2UIN(String perm){
		if(perm==null || perm.length()==0)
			return "";
		String uin = perm2UIN.get(perm);
		if(uin == null){
			uin = "";
			System.err.println("\nIn perm2UIN, can't get the UIN for perm: "+perm);
		}
		return uin;	
	}
	
	public static void main(String[] args) {
		String WAFile = "./WAUIN.CSV"; 
		FRSAgent frsagent = new FRSAgent(WAFile);
		//FoiaUtil.printKeyOfHashMap(frsagent.perm2UIN);
		//FoiaUtil.printValueOfHashMap(frsagent.perm2UIN);
		System.out.println(frsagent.perm2UIN("WAR007386"));
	}

}
