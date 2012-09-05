package edu.rpi.tw.eScience.WaterQualityPortal.regulations;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import edu.rpi.tw.eScience.WaterQualityPortal.epa.foia.FoiaOverrideAgent;
import edu.rpi.tw.eScience.WaterQualityPortal.epa.foia.FoiaTranslator;

@Deprecated
public class PcsFacilityRegulationAgent {
	int ruleIdCount = 0;
	private FoiaTranslator translator=null;
	//private FoiaUnitConverter unitConv=null;
	private FoiaOverrideAgent overrideAgt=null;
	static public Logger pcsRegulationLogger = Logger.getLogger(PcsFacilityRegulationAgent.class.getName());
    static {
    	//BasicConfigurator.configure();
    	PropertyConfigurator.configure("./log4j.properties");
    }
    
    PcsFacilityRegulationAgent (String dir, String overrideFile){
		translator = new FoiaTranslator();
		//unitConv = new FoiaUnitConverter();		
		overrideAgt = new FoiaOverrideAgent(dir, overrideFile);
	}
	
	public void procFile(String inputFileName, String outputFile){
		System.out.println("Input: "+inputFileName+"Output: "+outputFile);
		FileInputStream fIn = null;
		BufferedReader reader = null;
		HashMap<String, EPAFacilityRegulationRule> regMap = new HashMap<String, EPAFacilityRegulationRule>();

		try{
			fIn =  new FileInputStream(inputFileName);
			reader = new BufferedReader(new InputStreamReader(fIn));
			
			String strLine;			
			while ((strLine = reader.readLine()) != null)   {
				//System.out.println (strLine);				
				//			    NODI     001      176- 176     No Data Indicator		
				//only write the records that have data
				if(strLine.substring(175, 176).trim().isEmpty()){
					//processOneRecord(strLine, bufferedWriter);
					//process type 1 to 5
					for(int i=1;i<=5;i++)
						procRecordPerType(i, strLine, regMap);
				}
			}//end of while
			FacilityRegulationUtil.saveMap(outputFile, regMap);

		}
		catch (Exception e) {
			System.err.println("In processFile, err");
			e.printStackTrace();
		}finally {
			//Close the BufferedReader		
			try {
				if (reader!=null)
					reader.close ();
			} catch (IOException ex) {
				System.err.println("In processFile, closing the reader and BufferedWriter");
				ex.printStackTrace();
			}
		}
	}
	
	/*
	 * The function extract and write the following fields:
		//		"\"ValueTypeCode\", \"MeasVio Value\", \"MeasVio Percent\"," +
		//		"\"Limit Value\", \"LimitOperator\", \"Limit Standard\"," +	
		//		"\"Statistical Base Code\", \"Statistical Base Desc\"," +
		//"\"UNIT_CODE\", \"Unit Short Name\", \"Unit Long Name\", "+
	 */
	public void procRecordPerType(int type, String rcd, 
			HashMap<String, EPAFacilityRegulationRule> regMap){
		//for constraint
		String testType="";
		String cmpOp="";
		String limitStr="";	
		String cmpUnit="";
		String ruleName=null;		
		//		    PRAM     005       15-  19     Paramater Name  
		String paraCode=rcd.substring(14, 19).trim();
		String elementName = translator.paraCode2Name(paraCode);
		//Firstly, process unit to see if we need to convert the measurement value  
		String unit=null;	
		//# is a char that means no operator in the measured/limit value
		//used to differentiate the special reported operator from < or >
		char repOp='#';
		if(type<=2){
			//	    LQUC     002       36-  37     Quantity Unit Code   
			//    	RUNT     002      105- 106     Reported Quantity Unit
			unit=rcd.substring(35, 37).trim();
		}
		else{
			//		LCUC     002      186- 187     Concentration Unit Code
			//	    RCUN     002      133- 134     Reported Concentration Unit
			unit=rcd.substring(185, 187).trim();
		}				

		//		"\"ValueTypeCode\", \"MeasVio Value\", \"MeasVio Percent\"," +
		//		"\"Limit Value\", \"LimitOperator\", \"Limit Standard\"," +	
		//		"\"Statistical Base Code\", \"Statistical Base Desc\"," +

		switch (type) {
		case 1://		Q1 is Quantity Average 
			testType="Q1";
			//		    LQAV     008       38-  45     Quantity Average Limit  
			limitStr=rcd.substring(37, 45).trim();			
			if(limitStr.length()>0)
				repOp=limitStr.charAt(0);
			//limitValue=FoiaUtil.procNumStr(limitStr, rcd, true);
			//		    LimitOperator  
			if(repOp=='<' || repOp=='>')
				cmpOp = ""+repOp;							
			else
				cmpOp="<=";			
			break;
		case 2://		Q2 is Quantity Maximum 
			testType="Q2";			
			//		    LQMX     008       46-  53     Quantity Max Limit
			limitStr=rcd.substring(45, 53).trim();
			if(limitStr.length()>0)
				repOp=limitStr.charAt(0);
			//limitValue=FoiaUtil.procNumStr(limitStr, rcd, true);
			//		    LimitOperator  
			if(repOp=='<' || repOp=='>')
				cmpOp = ""+repOp; 
			else
				cmpOp="<=";	
			break;
		case 3://		C1 - Concentration Minimum 
			testType="C1";
			//		    LCMN     008       56-  63     Concentration Min Limit     
			limitStr=rcd.substring(55, 63).trim();
			if(limitStr.length()>0)
				repOp=limitStr.charAt(0);
			//limitValue=FoiaUtil.procNumStr(limitStr, rcd, true);
			//		    LimitOperator  
			//orType 0 is for Conc Lim Min
			if(repOp=='<' || repOp=='>')
				cmpOp = ""+repOp; 			
			else{
				if(overrideAgt.isOverriden(0, rcd)) 
					cmpOp="<=";	
				else
					cmpOp=">=";	
			}
			break;
		case 4://		C2 - Concentration Average 
			testType="C2";	
			//		    LCAV     008       66-  73     Concentration Average Limit
			limitStr=rcd.substring(65, 73).trim();
			if(limitStr.length()>0)
				repOp=limitStr.charAt(0);
			//		    LimitOperator  
			//orType 1 is for Conc Lim Avg
			if(repOp=='<' || repOp=='>')
				cmpOp = ""+repOp;
			else{
				if(overrideAgt.isOverriden(1, rcd))
					cmpOp=">=";
				else
					cmpOp="<=";		
			}
			break;
		case 5://		C3 - Concentration Maximum 
			testType="C3";	 			
			//		    LCMX     008       76-  83     Concentration Limit Max  		
			limitStr=rcd.substring(75, 83).trim();
			if(limitStr.length()>0)
				repOp=limitStr.charAt(0);
			//		    LimitOperator  
			if(repOp=='<' || repOp=='>')
				cmpOp = ""+repOp; 
			else
				cmpOp="<=";	
			break;
		default:
			System.err.println("processMeasurementPerType:" +
					" Unknown invoking value type for record: "+rcd);
			break;
		}	
		
		if(limitStr!=null && limitStr.compareTo("")!=0)
		{
			cmpUnit = translator.unitCode2ShortName(unit);

			EPAFacilityRegulationRule curRule = new EPAFacilityRegulationRule(ruleIdCount++, 
					elementName, testType, cmpOp, limitStr, cmpUnit, rcd);

			ruleName = elementName+testType;
			EPAFacilityRegulationRule existingRule = regMap.get(ruleName);
			if(existingRule==null)
				regMap.put(ruleName, curRule);
			else if(curRule.compareTo(existingRule)==-1)
				regMap.put(ruleName, curRule);
		}
	}

	@SuppressWarnings("unused")
	public static void main(String[] args) throws FileNotFoundException {
		//PcsFacilityRegulationAgent agent = new PcsFacilityRegulationAgent();
		String state="WA"; //RI, MA, CA, NY
		String outFile = "/media/DATA/epaMetaData/PcsRegulations/EPA_Regulations_"+state+".csv";;
		String inputFile = "~/source/epa-gov/foia-measurements-wa/version/2011-Jul-23/manual/h2000WA-F-01613M.csv";
		//agent.procFile(inputFile, outFile);
	}
}
