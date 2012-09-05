package edu.rpi.tw.eScience.WaterQualityPortal.regulations;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import com.csvreader.CsvReader;

import edu.rpi.tw.eScience.WaterQualityPortal.util.NameUtil;

public class IcisRegulationAgent {
	
	int ruleIdCount = 0;
	
	public void procFile(String inputfileName, String outFile){
		HashMap<String, EPAFacilityRegulationRule> regMap = new HashMap<String, EPAFacilityRegulationRule>();
		procOneCSVFile(inputfileName, regMap);
		FacilityRegulationUtil.saveMap(outFile, regMap);
	}

	public void procOneCSVFile(String inputfileName, HashMap<String, EPAFacilityRegulationRule> regMap){		
		CsvReader reader = null;
		@SuppressWarnings("unused")
		int recordNum = 0;
		String elementName= null;
		//for constraint
		String cmpType;
		String cmpOp;
		String cmpValue;
		String cmpUnit;
		String ruleName=null;

		try {			
			reader = new CsvReader(inputfileName);	
			reader.readHeaders();
			recordNum++;

			while (reader.readRecord())
			{			
				recordNum++;
				String rcd=reader.getRawRecord();
				elementName = reader.get("PARAMETER_DESC");								
				//System.out.println("Record " + recordNum + ": "+elementName);

				cmpValue = reader.get("LIMIT_VALUE_NMBR");	
				if(cmpValue.compareTo("")!=0)
				{					
					elementName = NameUtil.processElementName(elementName);
					cmpType = reader.get("VALUE_TYPE_CODE");
					cmpOp = reader.get("LIMITVALUEQUALIFIERCODE");						
					cmpUnit = reader.get("UNIT_SHORT_NAME");
					EPAFacilityRegulationRule curRule = new EPAFacilityRegulationRule(ruleIdCount++, elementName, cmpType, cmpOp, cmpValue, cmpUnit, rcd);

					ruleName = elementName+cmpType;
					EPAFacilityRegulationRule existingRule = regMap.get(ruleName);
					if(existingRule==null)
						regMap.put(ruleName, curRule);
					else if(curRule.compareTo(existingRule)==-1)
						regMap.put(ruleName, curRule);
				}
			}//end of while
			} catch (FileNotFoundException e) {
				System.err.println("In procOneCSVFile(), file name: " + inputfileName);
				System.err.println("Error: " + e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				System.err.println("In procOneCSVFile(), file name: " + inputfileName);
				System.err.println("Error: " + e.getMessage());
				e.printStackTrace();
			}
			finally{
				reader.close();
			}
	}
	
	@SuppressWarnings("unused")
	public static void main(String[] args) throws FileNotFoundException {
		IcisRegulationAgent agent = new IcisRegulationAgent();
		String state="NH"; //RI, MA, CA, NY
		String dirName = "/media/DATA/source/epa-gov/enforcement-and-compliance-history-online-echo-measurements/version/2011-Mar-19/source/"+state;
		String outFile = "/media/DATA/epaMetaData/IcisRegulations/EPA_Regulations_"+state+".csv";;
		String inputFile = "/media/DATA/source/epa-gov/foia-measurements-nh/version/2011-Jul-23/manual/proc_head3000FOIA_DMRs_NH.csv";
		agent.procFile(inputFile, outFile);
	}
}
