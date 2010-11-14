package edu.rpi.tw.eScience.WaterQualityPortal.epa;

import java.io.BufferedWriter;
import java.io.IOException;

public class FacilityMeasurement {
	String elementName;
	int testNumber;
	String date;
	String value;
	String unit;
	
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
