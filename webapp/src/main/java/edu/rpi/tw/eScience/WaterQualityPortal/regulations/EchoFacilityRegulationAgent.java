package edu.rpi.tw.eScience.WaterQualityPortal.regulations;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.csvreader.CsvReader;

import edu.rpi.tw.eScience.WaterQualityPortal.util.NameUtil;

public class EchoFacilityRegulationAgent {
	int idCount = 0;
	int ruleIdCount = 0;
	static String csvTarget = "http://www.epa-echo.gov/cgi-bin/effluentdata.cgi";


/*	protected String processElementName(String elementName){
		System.out.println(elementName);
		String prd = elementName.replaceAll("\\.", "_");		
		//System.out.println(prd);
		prd = prd.replaceAll(",", "_");
		//System.out.println(prd);
		prd = prd.replaceAll("'", "_");
		//System.out.println(prd);
		prd = prd.replaceAll("&", "_"); 
		//System.out.println(prd);
		prd = prd.replaceAll("\\(", "_");
		//System.out.println(prd);
		prd = prd.replaceAll("\\)", "_");
		//System.out.println(prd);
		prd = prd.replaceAll("\\/", "_");
		//System.out.println(prd)
		prd = prd.replaceAll("\\+", "_");
		//System.out.println(prd)
		prd = prd.replaceAll("\\%", "_");
		//System.out.println(prd)		
		prd = prd.replaceAll("\\s", "_");
		prd = prd.replaceAll("_+", "_");
		if(prd.startsWith("_"))
			prd=prd.substring(1, prd.length());		
		if(prd.endsWith("_"))
			prd=prd.substring(0, prd.length()-1);
		System.out.println(prd);
		return prd;
	}*/
	
	public void procFiles(String dirName, String outFile){
		File dir = new File(dirName);
		if(dir.isDirectory()==false){
			System.err.println(dirName + "is not a directory.");
			System.exit(-1);
		}
		HashMap<String, EPAFacilityRegulationRule> regMap = new HashMap<String, EPAFacilityRegulationRule>();
		for (File child : dir.listFiles()){
			String fileName = child.getName();
			String filePath = child.getAbsolutePath();
			if(".".equals(fileName) || "..".equals(fileName))
				continue;
			if(filePath.endsWith(".csv")){
				System.out.println("Process "+ fileName);
				procOneCSVFile(filePath, regMap);
			}
		}	
		FacilityRegulationUtil.saveMap(outFile, regMap);
	}

	
	static public void printValueOfMap(HashMap<String, EPAFacilityRegulationRule> curMap){
		System.out.println("The Values of the Map:"); 
		Iterator<Entry<String, EPAFacilityRegulationRule>> iteratorValue = curMap.entrySet().iterator();

		int num=0;
		while(iteratorValue.hasNext()){        
			System.out.println(iteratorValue.next().getValue());
			num++;
		}		
		System.out.println("Num: "+num);
	}
	
	public void procOneCSVFile(String inputfileName, HashMap<String, EPAFacilityRegulationRule> regMap){		
		CsvReader reader = null;
		@SuppressWarnings("unused")
		int recordNum = 0;
		String focusedName = null;
		String elementName= null;
		//for constraint
		String strCmpOp;
		String cmpValue;
		String cmpUnit;
		String ruleName=null;
		//HashMap<String, MeasurementConstraint> coliformConstraints = null;

		try {			
			reader = new CsvReader(inputfileName);	
			reader.readHeaders();
			recordNum++;

			while (reader.readRecord())
			{			
				recordNum++;
				String rcd=reader.getRawRecord();
				elementName = reader.get(13);								
				//System.out.println("Record " + recordNum + ": "+elementName);
				//find the next element
				if(elementName.compareTo("")!=0){
					focusedName = NameUtil.processElementName(elementName);
				}

				cmpValue = reader.get("C1_LVAL");	
				if(cmpValue.compareTo("")!=0)
				{
					strCmpOp = reader.get("C1_LSENSE");						
					cmpUnit = reader.get("C1_LUNIT");
					EPAFacilityRegulationRule ruleC1 = new EPAFacilityRegulationRule(ruleIdCount++, focusedName, "C1", strCmpOp, cmpValue, cmpUnit, rcd);

					ruleName = focusedName+"C1";
					EPAFacilityRegulationRule existingRule = regMap.get(ruleName);
					if(existingRule==null)
						regMap.put(ruleName, ruleC1);
					else if(ruleC1.compareTo(existingRule)==-1)
						regMap.put(ruleName, ruleC1);
				}
				//
				cmpValue = reader.get("C2_LVAL");	
				if(cmpValue.compareTo("")!=0)
				{
					strCmpOp = reader.get("C2_LSENSE");
					cmpUnit = reader.get("C2_LUNIT");					
					EPAFacilityRegulationRule ruleC2 = new EPAFacilityRegulationRule(ruleIdCount++, focusedName, "C2", strCmpOp, cmpValue, cmpUnit, rcd);

					ruleName = focusedName+"C2";
					EPAFacilityRegulationRule existingRule = regMap.get(ruleName);
					if(existingRule==null)
						regMap.put(ruleName, ruleC2);
					else if(ruleC2.compareTo(existingRule)==-1)
						regMap.put(ruleName, ruleC2);
				}
				//
				cmpValue = reader.get("C3_LVAL");	
				if(cmpValue.compareTo("")!=0)
				{
					strCmpOp = reader.get("C3_LSENSE");
					cmpUnit = reader.get("C3_LUNIT");						
					EPAFacilityRegulationRule ruleC3 = new EPAFacilityRegulationRule(ruleIdCount++, focusedName, "C3", strCmpOp, cmpValue, cmpUnit, rcd);

					ruleName = focusedName+"C3";
					EPAFacilityRegulationRule existingRule = regMap.get(ruleName);
					if(existingRule==null)
						regMap.put(ruleName, ruleC3);
					else if(ruleC3.compareTo(existingRule)==-1)
						regMap.put(ruleName, ruleC3);
				}
				//
				cmpValue = reader.get("Q1_LVAL");	
				if(cmpValue.compareTo("")!=0)
				{
					strCmpOp = reader.get("Q1_LSENSE");
					cmpUnit = reader.get("Q1_LUNIT");
					EPAFacilityRegulationRule ruleQ1 = new EPAFacilityRegulationRule(ruleIdCount++, focusedName, "Q1", strCmpOp, cmpValue, cmpUnit, rcd);

					ruleName = focusedName+"Q1";
					EPAFacilityRegulationRule existingRule = regMap.get(ruleName);
					if(existingRule==null)
						regMap.put(ruleName, ruleQ1);
					else if(ruleQ1.compareTo(existingRule)==-1)
						regMap.put(ruleName, ruleQ1);
				}
				//
				cmpValue = reader.get("Q2_LVAL");
				if(cmpValue.compareTo("")!=0)
				{
					strCmpOp = reader.get("Q2_LSENSE");
					cmpUnit = reader.get("Q2_LUNIT");
					EPAFacilityRegulationRule ruleQ2 = new EPAFacilityRegulationRule(ruleIdCount++, focusedName, "Q2", strCmpOp, cmpValue, cmpUnit, rcd);

					ruleName = focusedName+"Q2";
					EPAFacilityRegulationRule existingRule = regMap.get(ruleName);
					if(existingRule==null)
						regMap.put(ruleName, ruleQ2);
					else if(ruleQ2.compareTo(existingRule)==-1)
						regMap.put(ruleName, ruleQ2);
				}

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

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		EchoFacilityRegulationAgent agent = new EchoFacilityRegulationAgent();
		String state="RI"; //RI, MA, CA, NY
		String dirName = "/media/DATA/source/epa-gov/enforcement-and-compliance-history-online-echo-measurements/version/2011-Mar-19/source/"+state;
		String outFile = "/media/DATA/source/epa-gov/enforcement-and-compliance-history-online-echo-measurements/version/2011-Mar-19/regulations/EPA_Regulations_"+state+".csv";
		agent.procFiles(dirName, outFile);		
		

/*		//for testing the func: processElementName
		//Temperature_water_deg_fahrenheit in RI0000035.csv
		agent.processElementName("Temperature, water deg. (fahrenheit)");
		//Nitrogen_total_as_N in RI0100005.csv
		agent.processElementName("Nitrogen, total (as N)");
		//Oil_Grease in RI0100005.csv
		agent.processElementName("Oil & Grease");
		//4_4_-DDD in RI0023779.csv
		agent.processElementName("4,4'-DDD");
		//DDT_DDD_DDE_sum_of_p_p_o_p_isomers
		agent.processElementName("DDT/DDD/DDE, sum of p,p' & o,p' isomers");
		//Uranium_natural_total_in_pci_L
		agent.processElementName("Uranium, natural, total (in pci/L)");
		//Radium_226_radium_228_total
		agent.processElementName("Radium 226 + radium 228, total");
		//Surv_Statre_96Hr_Acute_Atherinops_Affns
		agent.processElementName("% Surv Statre 96Hr Acute Atherinops Affns");
*/			
	}
}
