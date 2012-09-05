package edu.rpi.tw.eScience.WaterQualityPortal.regulations;

import java.io.BufferedWriter;

import edu.rpi.tw.eScience.WaterQualityPortal.epa.foia.FoiaUtil;

public class EPAFacilityRegulationRule {
	static double DELTA = 0.0000001; 
	int id;
	String elementName;
	String testType;
	String cmpOperator;
	double cmpValue;
	String cmpUnit;

	public EPAFacilityRegulationRule(int id, String curElementName, String curTestType, 
			String curCmpOperator, String curCmpValue, String curCmpUnit, String rcd){
		elementName = curElementName;
		testType = curTestType;
		cmpOperator = curCmpOperator;
		cmpValue = FoiaUtil.numStr2Double(curCmpValue, rcd, true);				
		cmpUnit = curCmpUnit;		
	}
	
	/*
	 * return -1 if this rule is more strict
	 * return 0 if this rule and otherRule are the same
	 * return 1 if this rule is less strict
	 * return -2 if this rule and otherRule are of different types*/
	public int compareTo(EPAFacilityRegulationRule otherRule){
		if(!testType.equals(otherRule.testType)){
			System.err.println("can't compare two rules of different types");
			return -2;
		}
		if (cmpOperator.equals(">=") || cmpOperator.equals(">")) {
			if(cmpValue < otherRule.cmpValue )
				return 1;
			else if ((cmpValue - otherRule.cmpValue) <= DELTA )
				return 0;
			else 
				return -1;			
		}
		else { //for the other 3 compare operators
			if(cmpValue < otherRule.cmpValue )
				return -1;
			else if ((cmpValue - otherRule.cmpValue) <= DELTA )
				return 0;
			else 
				return 1;	
		}			
	}
	
	@Override
	public String toString() {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("\""+elementName+"\", "); 	
		strBuilder.append("\""+testType+"\", "); 
		strBuilder.append("\""+cmpOperator+"\", "); 
		strBuilder.append("\""+cmpValue+"\", "); 
		strBuilder.append("\""+cmpUnit+"\""); 	
		strBuilder.append("\n");	
		return strBuilder.toString();		
	}
	
	public void print(StringBuilder strBuilder) {
		strBuilder.append("\""+elementName+"\", "); 	
		strBuilder.append("\""+testType+"\", "); 
		strBuilder.append("\""+cmpOperator+"\", "); 
		strBuilder.append("\""+cmpValue+"\", "); 
		strBuilder.append("\""+cmpUnit+"\""); 	
		strBuilder.append("\n");				
	}
}
