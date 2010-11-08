package edu.rpi.tw.eScience.WaterQualityPortal.epa;

import java.io.BufferedWriter;
import java.io.IOException;

public class Facility {
	String ID;
	String name;
	String addressLine1;
	String addressLine2;
	int numInspection;
	int numQtrNC;
	int numEE;
	
	public Facility(String curID, String curName, String curAddressLine1, String curAddressLine2, 
			int curNumInspection, int curNumQtrNC, int curNumEE){
		ID = curID;
		name = curName;
		addressLine1 = curAddressLine1;
		addressLine2 = curAddressLine2;
		numInspection = curNumInspection;
		numQtrNC = curNumQtrNC;
		numEE = curNumEE;		
	}
	
	public void printToFile(BufferedWriter out){
		try{
			out.write("ID: "+ID+"\n");
			out.write("Name: "+name+"\n");
			out.write("AddressLine1: "+addressLine1+"\n");
			out.write("AddressLine2: "+addressLine2+"\n");
			out.write("numInspection: "+numInspection+"\n");
			out.write("numQtrNC: "+numQtrNC+"\n");
			out.write("numEE: "+numEE+"\n");				
		} catch (IOException e) {
			System.out.println("In printToFile, err in writing to file");
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		}
		
	}
	public void printFacility(){
		System.out.println("ID: "+ID);
		System.out.println("Name: "+name);
		System.out.println("AddressLine1: "+addressLine1);
		System.out.println("AddressLine2: "+addressLine2);
		System.out.println("numInspection: "+numInspection);
		System.out.println("numQtrNC: "+numQtrNC);
		System.out.println("numEE: "+numEE);		
	}
	

}
