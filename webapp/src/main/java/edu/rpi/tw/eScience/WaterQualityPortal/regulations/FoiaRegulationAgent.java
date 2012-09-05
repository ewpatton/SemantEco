package edu.rpi.tw.eScience.WaterQualityPortal.regulations;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import com.csvreader.CsvReader;

import edu.rpi.tw.eScience.WaterQualityPortal.util.ConfigReader;
import edu.rpi.tw.eScience.WaterQualityPortal.util.NameUtil;

public class FoiaRegulationAgent {
	
	int ruleIdCount = 0;
	
	String elementNameCol;
	String testTypeCol;
	String cmpOpCol;
	String cmpValueCol;
	String cmpUnitCol;
	
	public void config(String confFile){
		ConfigReader reader = new ConfigReader(confFile);
		elementNameCol = reader.getProperty("ElementName");
		testTypeCol = reader.getProperty("TestType");
		cmpOpCol = reader.getProperty("ComparisonOperator");
		cmpValueCol = reader.getProperty("LimitValue");
		cmpUnitCol = reader.getProperty("LimitUnit");
	}
	
	public void procFile(String confFile, String inputfileName, String outFile){
		config(confFile);
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
		String testType;
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
				elementName = reader.get(elementNameCol);								
				//System.out.println("Record " + recordNum + ": "+elementName);

				cmpValue = reader.get(cmpValueCol);	
				if(cmpValue.compareTo("")!=0)
				{					
					elementName = NameUtil.processElementName(elementName);
					testType = reader.get(testTypeCol);
					cmpOp = reader.get(cmpOpCol);						
					cmpUnit = reader.get(cmpUnitCol);
					EPAFacilityRegulationRule curRule = new EPAFacilityRegulationRule(ruleIdCount++, elementName, testType, cmpOp, cmpValue, cmpUnit, rcd);

					ruleName = elementName+testType;
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
		FoiaRegulationAgent agent = new FoiaRegulationAgent();
		String confIcis="data/config/icis.config";
		String state="WA"; //RI, MA, CA, NY, NH
		String dirName = "/media/DATA/source/epa-gov/enforcement-and-compliance-history-online-echo-measurements/version/2011-Mar-19/source/"+state;
		String outFile = "/media/DATA/epaMetaData/IcisRegulations/EPA_Regulations_"+state+".csv";
		String confPcs="data/config/pcs.config";
		String inputFile = "/media/DATA/source/epa-gov/foia-measurements-nh/version/2011-Jul-23/manual/proc_head3000FOIA_DMRs_NH.csv";
		String inputFile2 = "/home/ping/source/epa-gov/foia-measurements-wa/version/2011-Jul-23/manual/h2000WA-F-01613M.csv";
		String outFile2 = "/media/DATA/epaMetaData/PcsRegulations/EPA_Regulations_"+state+".csv";
		agent.procFile(confPcs, inputFile2, outFile2);
	}
}
