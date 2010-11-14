package edu.rpi.tw.eScience.WaterQualityPortal.epa;

import java.io.BufferedWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;


public class FacilityMeasurement {
	String elementName;
	int testNumber;
	String date;
	String value;
	String unit;
	
	@Override
	public String toString() {
		String result = "";
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		result += "<FacilityMeasurement>\n";		
		result += "<hasDate rdf:datatype=\"&xsd;dateTime\">"+df.format(date)+"</hasDate>\n";
        result += "<hasUnit>"+unit+"</hasUnit>\n";
        result += "<hasMeasurement>"+value+"</hasMeasurement>\n";
        result += "<hasElement>\n";
        result += "<Element rdf:about=\"#"+elementName+"\"/>\n";
        result += "</hasElement>\n";
        result += "<hasTestNumber rdf:datatype=\"&xsd;nonNegativeInteger\">"+testNumber+"</hasTestNumber>\n";
		result += "</FacilityMeasurement>\n";
		return result;
	}
	
	public FacilityMeasurement(String curElementName, int curTestNumber, 
			String curDate, String curValue, String curUnit){
		elementName = curElementName;
		testNumber = curTestNumber;
		date = curDate;
		value = curValue;
		unit = curUnit;		
	}
	
	public void printToFile(BufferedWriter out){
		try{
			out.write("elementName: "+ elementName +"\n");
			out.write("testNumber: "+ testNumber +"\n");
			out.write("date: "+ date +"\n");
			out.write("value: "+ value +"\n");
			out.write("unit: "+ unit +"\n");
		} catch (IOException e) {
			System.out.println("In printToFile, err in writing to file");
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		}		
	}
	
	public void printFacility(){
		System.out.println("elementName: "+ elementName +"\n");
		System.out.println("testNumber: "+ testNumber +"\n");
		System.out.println("date: "+ date +"\n");
		System.out.println("value: "+ value +"\n");
		System.out.println("unit: "+ unit +"\n");
	}

}
