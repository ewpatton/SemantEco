package edu.rpi.tw.eScience.WaterQualityPortal.epa;

import java.io.BufferedWriter;
import java.io.IOException;

public class MeasurementConstraint {
	String elementName;
	int testNumber;
	//0: <, 1: <=, 2: ==, 3: >=, 4: >
	int cmpType;
	String cmpValue;
	String cmpUnit;
	
	public MeasurementConstraint(String curElementName, int curTestNumber, 
			int curCmpType, String curCmpValue, String curCmpUnit){
		elementName = curElementName;
		testNumber = curTestNumber;
		cmpType = curCmpType;
		cmpValue = curCmpValue;
		cmpUnit = curCmpUnit;		
	}
	
	public void printToFile(BufferedWriter out){
		try{
			out.write("elementName: "+ elementName +"\n");
			out.write("testNumber: "+ testNumber +"\n");
			switch (cmpType){
			case 0: out.write("cmpType: "+ "<" +"\n"); break;
			case 1: out.write("cmpType: "+ "<=" +"\n"); break;
			case 2: out.write("cmpType: "+ "==" +"\n"); break;
			case 3: out.write("cmpType: "+ ">=" +"\n"); break;
			case 4: out.write("cmpType: "+ ">" +"\n"); break;
			}
			out.write("cmpValue: "+ cmpValue +"\n");
			out.write("cmpUnit: "+ cmpUnit +"\n");
		} catch (IOException e) {
			System.out.println("In printToFile, err in writing to file");
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		}		
	}
	
	public void printFacility(){
		System.out.println("elementName: "+ elementName +"\n");
		System.out.println("testNumber: "+ testNumber +"\n");
		switch (cmpType){
		case 0: System.out.println("cmpType: "+ "<" +"\n"); break;
		case 1: System.out.println("cmpType: "+ "<=" +"\n"); break;
		case 2: System.out.println("cmpType: "+ "==" +"\n"); break;
		case 3: System.out.println("cmpType: "+ ">=" +"\n"); break;
		case 4: System.out.println("cmpType: "+ ">" +"\n"); break;
		}
		System.out.println("cmpValue: "+ cmpValue +"\n");
		System.out.println("cmpUnit: "+ cmpUnit +"\n");
	}

}
