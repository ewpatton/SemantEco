package edu.rpi.tw.escience.WaterQualityPortal.epa.foia;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

@Deprecated
public class FoiaDmrAgent {
	private FoiaTranslator translator=null;
	private FoiaUnitConverter unitConv=null;
	private FoiaOverrideAgent overrideAgt=null;
	static public Logger dmrLogger = Logger.getLogger(FoiaDmrAgent.class.getName());
    static {
    	//BasicConfigurator.configure();
    	PropertyConfigurator.configure("./log4j.properties");
    }
	FoiaDmrAgent(String dir, String overrideFile){
		translator = new FoiaTranslator();
		unitConv = new FoiaUnitConverter();		
		overrideAgt = new FoiaOverrideAgent(dir, overrideFile);
	}

	/*
	 * The function extract and write the following fields:
		//		"\"ValueTypeCode\", \"MeasVio Value\", \"MeasVio Percent\"," +
		//		"\"Limit Value\", \"LimitOperator\", \"Limit Standard\"," +	
		//		"\"Statistical Base Code\", \"Statistical Base Desc\"," +
		//"\"UNIT_CODE\", \"Unit Short Name\", \"Unit Long Name\", "+
	 */
	public void processMeasurementPerType(int type, String rcd, StringBuilder strBuilder){
		//Firstly, process unit to see if we need to convert the measurement value  
		String unit=null, repUnit=null, limitValue="";
		String limitStr="";
		//# is a char that means no operator in the measured/limit value
		//used to differentiate the special reported operator from < or >
		char repOp='#';
		Double measuredValue;
		if(type<=2){
			//	    LQUC     002       36-  37     Quantity Unit Code   
			//    	RUNT     002      105- 106     Reported Quantity Unit
			unit=rcd.substring(35, 37).trim();
			repUnit=rcd.substring(104, 106).trim();
		}
		else{
			//		LCUC     002      186- 187     Concentration Unit Code
			//	    RCUN     002      133- 134     Reported Concentration Unit
			unit=rcd.substring(185, 187).trim();
			repUnit=rcd.substring(132, 134).trim();
		}				

		//		"\"ValueTypeCode\", \"MeasVio Value\", \"MeasVio Percent\"," +
		//		"\"Limit Value\", \"LimitOperator\", \"Limit Standard\"," +	
		//		"\"Statistical Base Code\", \"Statistical Base Desc\"," +

		switch (type) {
		case 1://		Q1 is Quantity Average 
			strBuilder.append("\"Q1\", "); 	
			//		    MQAV     008      107- 114     MeasVio Quantity Average  
			strBuilder.append("\""); 	
			measuredValue=FoiaUtil.numStr2Double(rcd.substring(106, 114).trim(), rcd, false);
			if(!repUnit.isEmpty()){
				dmrLogger.info("calling unit convert");
				dmrLogger.info("repUnit: "+ repUnit);
				dmrLogger.info("record: "+rcd);
				strBuilder.append(unitConv.convert(repUnit, unit, measuredValue));
			}
			else
				strBuilder.append(FoiaUtil.double2Str(measuredValue));
			strBuilder.append("\",");	
			// VQAV     005      115- 119     Meas/Vio Percent Quantity Avg
			strBuilder.append("\""); 		
			strBuilder.append(FoiaUtil.procNumStr(rcd.substring(114, 119).trim(), rcd, false));
			strBuilder.append("\",");
			//		    LQAV     008       38-  45     Quantity Average Limit  
			strBuilder.append("\""); 	
			limitStr=rcd.substring(37, 45).trim();
			if(limitStr.length()>0)
				repOp=limitStr.charAt(0);
			limitValue=FoiaUtil.procNumStr(limitStr, rcd, true);
			strBuilder.append(limitValue);
			strBuilder.append("\",");
			//		    LimitOperator  
			if(limitValue.isEmpty())
				strBuilder.append("\"\", "); 
			else if(repOp=='<' || repOp=='>')
				strBuilder.append("\""+repOp+"\", "); 
			else
				strBuilder.append("\"<=\", "); 		
			//		    Limit Standard  
			strBuilder.append("\"\","); 
			//		    LQAS     002       54-  55     Quantity Average Base Code   		
			String LQAS=rcd.substring(53, 55).trim();	
			strBuilder.append("\""); 
			strBuilder.append(LQAS);
			strBuilder.append("\",");
			strBuilder.append("\""); 
			strBuilder.append(translator.sbCode2LongName(LQAS));
			strBuilder.append("\",");			
			break;
		case 2://		Q2 is Quantity Maximum 
			strBuilder.append("\"Q2\",");
			//		    MQMX     008      120- 127     MeasVio Quantity Max
			strBuilder.append("\""); 	
			measuredValue=FoiaUtil.numStr2Double(rcd.substring(119, 127).trim(), rcd, false);
			if(!repUnit.isEmpty()){
				dmrLogger.info("calling unit convert");
				dmrLogger.info("repUnit: "+ repUnit);
				dmrLogger.info("record: "+rcd);
				strBuilder.append(unitConv.convert(repUnit, unit, measuredValue));
			}
			else
				strBuilder.append(FoiaUtil.double2Str(measuredValue));
			strBuilder.append("\",");
			//			VQMX     005      128- 132     MeasVio Percent – Quantity Max 
			strBuilder.append("\""); 		
			strBuilder.append(FoiaUtil.procNumStr(rcd.substring(127, 132).trim(), rcd, false));
			strBuilder.append("\",");
			//		    LQMX     008       46-  53     Quantity Max Limit
			strBuilder.append("\""); 	
			limitStr=rcd.substring(45, 53).trim();
			if(limitStr.length()>0)
				repOp=limitStr.charAt(0);
			limitValue=FoiaUtil.procNumStr(limitStr, rcd, true);
			strBuilder.append(limitValue);
			strBuilder.append("\",");
			//		    LimitOperator  
			if(limitValue.isEmpty())
				strBuilder.append("\"\", "); 
			else if(repOp=='<' || repOp=='>')
				strBuilder.append("\""+repOp+"\", "); 
			else
				strBuilder.append("\"<=\", "); 	
			//		    Limit Standard  
			strBuilder.append("\"\","); 
			//		    Quantity Max Base Code    
			strBuilder.append("\"\", "); 
			//base code desc
			strBuilder.append("\"\", "); 
			break;
		case 3://		C1 - Concentration Minimum 
			strBuilder.append("\"C1\","); 	
			//		    MCMN     008      137- 144     MeasVio Concentration Min
			strBuilder.append("\""); 
			measuredValue=FoiaUtil.numStr2Double(rcd.substring(136, 144).trim(), rcd, false);
			if(!repUnit.isEmpty()){
				dmrLogger.info("calling unit convert");
				dmrLogger.info("repUnit: "+ repUnit);
				dmrLogger.info("record: "+rcd);
				strBuilder.append(unitConv.convert(repUnit, unit, measuredValue));
			}else
				strBuilder.append(FoiaUtil.double2Str(measuredValue));
			strBuilder.append("\",");
			//			VCMN     005      145- 149     MeasVio Percent Concentra Min
			strBuilder.append("\""); 		
			strBuilder.append(FoiaUtil.procNumStr(rcd.substring(144, 149).trim(), rcd, false));
			strBuilder.append("\",");
			//		    LCMN     008       56-  63     Concentration Min Limit     
			strBuilder.append("\""); 	
			limitStr=rcd.substring(55, 63).trim();
			if(limitStr.length()>0)
				repOp=limitStr.charAt(0);
			limitValue=FoiaUtil.procNumStr(limitStr, rcd, true);
			strBuilder.append(limitValue);
			strBuilder.append("\",");
			//		    LimitOperator  
			//orType 0 is for Conc Lim Min
			if(limitValue.isEmpty())
				strBuilder.append("\"\", "); 
			else if(repOp=='<' || repOp=='>')
				strBuilder.append("\""+repOp+"\", "); 			
			else{
				if(overrideAgt.isOverriden(0, rcd))
					strBuilder.append("\"<=\", "); 
				else
					strBuilder.append("\">=\", "); 	
			}
			//		    Limit Standard  
			strBuilder.append("\"\","); 
			//		    LCMS     002       64-  65     Concentration Min Base Code	
			String LCMS=rcd.substring(63, 65).trim();
			strBuilder.append("\""); 	
			strBuilder.append(LCMS);
			strBuilder.append("\",");
			strBuilder.append("\""); 	
			strBuilder.append(translator.sbCode2LongName(LCMS));
			strBuilder.append("\",");
			break;
		case 4://		C2 - Concentration Average 
			strBuilder.append("\"C2\","); 
			//		    MCAV     008      150- 157     MeasVio Concentration Avg
			strBuilder.append("\""); 
			measuredValue=FoiaUtil.numStr2Double(rcd.substring(149, 157).trim(), rcd, false);
			if(!repUnit.isEmpty()){
				dmrLogger.info("calling unit convert");
				dmrLogger.info("repUnit: "+ repUnit);
				dmrLogger.info("record: "+rcd);
				strBuilder.append(unitConv.convert(repUnit, unit, measuredValue));
			}else
				strBuilder.append(FoiaUtil.double2Str(measuredValue));
			strBuilder.append("\",");
			//			VCAV     005      158- 162     MeasVio Percent Concen Avg
			strBuilder.append("\""); 		
			strBuilder.append(FoiaUtil.procNumStr(rcd.substring(157, 162).trim(), rcd, false));
			strBuilder.append("\",");
			//		    LCAV     008       66-  73     Concentration Average Limit
			strBuilder.append("\""); 
			limitStr=rcd.substring(65, 73).trim();
			if(limitStr.length()>0)
				repOp=limitStr.charAt(0);
			limitValue=FoiaUtil.procNumStr(limitStr, rcd, true);
			strBuilder.append(limitValue);
			strBuilder.append("\",");
			//		    LimitOperator  
			//orType 1 is for Conc Lim Avg
			if(limitValue.isEmpty())
				strBuilder.append("\"\", "); 
			else if(repOp=='<' || repOp=='>')
				strBuilder.append("\""+repOp+"\", "); 
			else{
				if(overrideAgt.isOverriden(1, rcd))
					strBuilder.append("\">=\", "); 
				else
					strBuilder.append("\"<=\", "); 	
			}
			//		    Limit Standard  
			strBuilder.append("\"\","); 
			//		    LCAS     002       74-  75     Concentration Avg Base Code	
			String LCAS=rcd.substring(73, 75).trim();
			strBuilder.append("\""); 	
			strBuilder.append(LCAS);
			strBuilder.append("\",");
			strBuilder.append("\""); 	
			strBuilder.append(translator.sbCode2LongName(LCAS));
			strBuilder.append("\",");
			break;
		case 5://		C3 - Concentration Maximum 
			strBuilder.append("\"C3\","); 	
			//		    MCMX     008      163- 170     MeasVio Concentration Max
			strBuilder.append("\""); 	
			measuredValue=FoiaUtil.numStr2Double(rcd.substring(162, 170).trim(), rcd, false);
			if(!repUnit.isEmpty()){
				dmrLogger.info("calling unit convert");
				dmrLogger.info("repUnit: "+ repUnit);
				dmrLogger.info("record: "+rcd);
				strBuilder.append(unitConv.convert(repUnit, unit, measuredValue));
			}else
				strBuilder.append(FoiaUtil.double2Str(measuredValue));
			strBuilder.append("\",");
			//			VCMX     005      171- 175     MeasVio Percent Concen Max
			strBuilder.append("\""); 		
			strBuilder.append(FoiaUtil.procNumStr(rcd.substring(170, 175).trim(), rcd, false));
			strBuilder.append("\",");
			//		    LCMX     008       76-  83     Concentration Limit Max  		
			strBuilder.append("\""); 
			limitStr=rcd.substring(75, 83).trim();
			if(limitStr.length()>0)
				repOp=limitStr.charAt(0);
			limitValue=FoiaUtil.procNumStr(limitStr, rcd, true);
			strBuilder.append(limitValue);
			strBuilder.append("\",");
			//		    LimitOperator  
			if(limitValue.isEmpty())
				strBuilder.append("\"\", ");
			else if(repOp=='<' || repOp=='>')
				strBuilder.append("\""+repOp+"\", "); 
			else
				strBuilder.append("\"<=\", "); 
			//		    LCSX     021       84- 104     Concentration Max Limit Standard  
			strBuilder.append("\""); 		
			strBuilder.append(FoiaUtil.procNumStr(rcd.substring(83, 104).trim(), rcd, false));
			strBuilder.append("\",");
			//		    LCXS     002      135- 136     Stat-Lim Conc Max Base Code	
			String LCXS=rcd.substring(134, 136).trim();
			strBuilder.append("\""); 
			strBuilder.append(LCXS);
			strBuilder.append("\",");
			strBuilder.append("\""); 
			strBuilder.append(translator.sbCode2LongName(LCXS));
			strBuilder.append("\",");
			break;
		default:
			System.err.println("processMeasurementPerType:" +
					" Unknown invoking value type for record: "+rcd);
			break;
		}	

		strBuilder.append("\""); 						
		strBuilder.append(unit);
		strBuilder.append("\",");
		strBuilder.append("\""); 						
		strBuilder.append(translator.unitCode2ShortName(unit));
		strBuilder.append("\",");
		strBuilder.append("\""); 						
		strBuilder.append(translator.unitCode2LongName(unit));
		strBuilder.append("\", ");
	}

	/*			bufferedWriter.write("\"Facility ID Number\", \"LimitDischargeRepDesignator\", " +
					"\"Pipe Set Qualifier\", \"ParamCode\", \"Paramater Name\", \"Monitoring Location\", "+
					"\"Season Number\", \"Limit Type\", \"Modification Number\", " +
					"\"Mod Period Start Date\", \"Mod Period End Date\", " +
					"\"ValueTypeCode\", \"MeasVio Value\", \"MeasVio Percent\"," +
					"\"Limit Value\", \"LimitOperator\", \"Limit Standard\"," +	
					"\"Statistical Base Code\", \"Statistical Base Desc\"," +
					"\"UNIT_CODE\", \"Unit Short Name\", \"Unit Long Name\", "+
					"\"NoDataIndicator\", " +
					"\"MeasurementViolationDate\", \"MeasurementViolationCode\"" +
			"\n");*/
	public void processOneRecordPerType(int type, String rcd, BufferedWriter bufWriter){
		StringBuilder strBuilder= new StringBuilder();		
		//		 NPID     009        1-   9     Facility ID Number  
		strBuilder.append("\""); 		strBuilder.append(rcd.substring(0, 9).trim());
		strBuilder.append("\",");
		//		    PDSG     004       10-  13     Limit Discharge/Rep Designator   
		strBuilder.append("\""); 		strBuilder.append(rcd.substring(9, 13).trim());
		strBuilder.append("\",");
		//		    LIPQ     001       14-  14     Pipe Set Qualifier
		strBuilder.append("\""); 		strBuilder.append(rcd.substring(13, 14).trim());
		strBuilder.append("\",");
		//		    PRAM     005       15-  19     Paramater Name  
		String paraCode=rcd.substring(14, 19).trim();
		strBuilder.append("\""); 		strBuilder.append(paraCode);
		strBuilder.append("\",");
		strBuilder.append("\""); 		strBuilder.append(translator.paraCode2Name(paraCode));
		strBuilder.append("\",");
		//		    MLOC     001       20-  20     Monitoring Location  
		strBuilder.append("\""); 		strBuilder.append(rcd.substring(19, 20).trim());
		strBuilder.append("\",");
		//		    SEAN     001       21-  21     Season Number   
		strBuilder.append("\""); 		strBuilder.append(rcd.substring(20, 21).trim());
		strBuilder.append("\",");
		//		    LTYP     001       22-  22     Limit Type       
		strBuilder.append("\""); 		
		String limitInShort=rcd.substring(21, 22).trim();
		strBuilder.append(translator.limitTypeShort2LongName(limitInShort));
		strBuilder.append("\",");
		//		    MODN     001       23-  23     Modification Number      
		strBuilder.append("\""); 		strBuilder.append(rcd.substring(22, 23).trim());
		strBuilder.append("\",");
		//		    ELSD     006       24-  29     Mod Period Start Date 
		strBuilder.append("\""); 		strBuilder.append(rcd.substring(23, 29).trim());
		strBuilder.append("\",");
		//		    ELED     006       30-  35     Mod Period End Date 
		strBuilder.append("\""); 		strBuilder.append(rcd.substring(29, 35).trim());
		strBuilder.append("\",");
		//		"\"ValueTypeCode\", \"MeasVio Value\", \"MeasVio Percent\"," +
		//		"\"Limit Value\", \"LimitOperator\", \"Limit Standard\"," +	
		//		"\"Statistical Base Code\", \"Statistical Base Desc\"," +
		//"\"UNIT_CODE\", \"Unit Short Name\", \"Unit Long Name\", "+
		processMeasurementPerType(type, rcd, strBuilder);
		
		//	    NODI     001      176- 176     No Data Indicator
		strBuilder.append("\""); 		strBuilder.append(rcd.substring(175, 176).trim());
		strBuilder.append("\", ");
		//	    MVDT     006      177- 182     Measurement Violation Date
		strBuilder.append("\""); 		strBuilder.append(rcd.substring(176, 182).trim());
		strBuilder.append("\", ");
		//	    MVIO     003      183- 185     Measurement Violation Code
		strBuilder.append("\""); 		strBuilder.append(rcd.substring(182, 185).trim());
		strBuilder.append("\"");
		//
		strBuilder.append("\n");		
		try {
			bufWriter.write(strBuilder.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}

	public void processFile(String inputFileName, String outputFile){
		System.out.println("Input: "+inputFileName+"Output: "+outputFile);
		FileInputStream fIn = null;
		BufferedReader reader = null;
		BufferedWriter bufferedWriter = null;

		try{
			fIn =  new FileInputStream(inputFileName);
			reader = new BufferedReader(new InputStreamReader(fIn));
			bufferedWriter = new BufferedWriter(new FileWriter(outputFile));			
			String strLine;

			bufferedWriter.write("\"Facility ID Number\", \"LimitDischargeRepDesignator\", " +
					"\"Pipe Set Qualifier\", \"ParamCode\", \"Paramater Name\", \"Monitoring Location\", "+
					"\"Season Number\", \"Limit Type\", \"Modification Number\", " +
					"\"Mod Period Start Date\", \"Mod Period End Date\", " +
					"\"ValueTypeCode\", \"MeasVio Value\", \"MeasVio Percent\"," +
					"\"Limit Value\", \"LimitOperator\", \"Limit Standard\"," +	
					"\"Statistical Base Code\", \"Statistical Base Desc\"," +
					"\"UNIT_CODE\", \"Unit Short Name\", \"Unit Long Name\", "+
					"\"NoDataIndicator\", " +
					"\"MeasurementViolationDate\", \"MeasurementViolationCode\"" +
					"\n");

			//			bufferedWriter.write("\"Facility ID Number\", \"Limit Discharge/Rep Designator\", " +
			//					"\"Pipe Set Qualifier\", \"Paramater Name\", \"Monitoring Location\", " +
			//					"\"Season Number\", \"Limit Type\", \"Modification Number\", \"Mod Period Start Date\", " +
			//					"\"Mod Period End Date\", \"Quantity Unit Short Name\", \"Quantity Unit Long Name\", \"Quantity Average Limit\", \"Quantity Max Limit\", " +
			//					"\"Quantity Average Base Code\", \"Concentration Min Limit\", \"Concentration Min Base Code\", " +
			//					"\"Concentration Average Limit\", \"Concentration Avg Base Code\", \"Concentration Limit Max\", " +
			//					"\"Concentration Max Limit Standard\", \"MeasVio Quantity Average\", " +
			//					"\"MeasVio Percent Quantity Avg\", \"MeasVio Quantity Max\", \"MeasVio Percent Quantity Max\", " +
			//					"\"Stat-Lim Conc Max Base Code\", \"MeasVio Concentration Min\", " +
			//					"\"MeasVio Percent Concentra Min\", \"MeasVio Concentration Avg\", \"MeasVio Percent Concen Avg\", " +
			//					"\"MeasVio Concentration Max\", \"MeasVio Percent Concen Max\", \"No Data Indicator\", " +
			//					"\"Measurement Violation Date\", \"Measurement Violation Code\", " +
			//					"\"Concentration Unit Short Name\", \"Concentration Unit Long Name\"\n");


			while ((strLine = reader.readLine()) != null)   {
				//System.out.println (strLine);				
				//			    NODI     001      176- 176     No Data Indicator		
				//only write the records that have data
				if(strLine.substring(175, 176).trim().isEmpty()){
					//processOneRecord(strLine, bufferedWriter);
					//process type 1 to 5
					for(int i=1;i<=5;i++)
						processOneRecordPerType(i, strLine, bufferedWriter);
				}
			}

		}
		catch (Exception e) {
			System.err.println("In processFile, err");
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
				System.err.println("In processFile, closing the reader and BufferedWriter");
				ex.printStackTrace();
			}
		}
	}
	
	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) {
		///media/DATA/epaMetaData/dmr/h2000WA-F-01613M
		if (args.length <= 2) {
			System.out.println("Usage: ./FoiaDmrAgent BaseDir overrideFile inputFile");
			System.exit(0);
		}
		String dir=args[0];//"input/override/";
		String overrideFile = args[1];//"WA-F01613P-Ping-Wang-Overrides.txt";
		String inputFile = args[0]+args[2];
		String outputFile=inputFile.replace('#', '-')+".csv";
		FoiaDmrAgent dmrAgent = new FoiaDmrAgent(dir, overrideFile);
		dmrAgent.processFile(inputFile, outputFile);

		//String dir="/media/DATA/epaMetaData/dmr/";
		//String inputFile="h2000WA-F-01613M"; //h2000WA-F-01613M "head1000F#01613M"
		//String procFile="proc"+inputFile;
		//String outputFile=inputFile.replace('#', '-')+".csv";
		//facAgent.preprocessFile(dir+inputFile, dir+procFile);
		//facAgent.processFile(dir+inputFile, dir+outputFile);



	}
}