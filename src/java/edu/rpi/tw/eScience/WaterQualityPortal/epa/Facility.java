package edu.rpi.tw.eScience.WaterQualityPortal.epa;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;

public class Facility {
	String ID;
	String name;
	String addressLine1;
	String addressLine2;
	int numInspection;
	int numQtrNC;
	int numEE;
	double lat;
	double lng;
	ArrayList<String> qtrDurList=null;
	ArrayList<String> NCBoolList=null;//Non-compliance in Quarter	
	
	public Facility(String curID){
		ID = curID;
	}
	
	public Facility(String curID, String curName, String curAddressLine1, String curAddressLine2, 
			int curNumInspection, int curNumQtrNC, int curNumEE){
		ID = curID;
		name = curName;
		addressLine1 = curAddressLine1;
		addressLine2 = curAddressLine2;
		numInspection = curNumInspection;
		numQtrNC = curNumQtrNC;
		numEE = curNumEE;		
		lat = 0;
		lng = 0;
		qtrDurList=null;
		NCBoolList=null;
	}
	
	public void setQtrDurList(ArrayList<String> newQtrDurList){
		qtrDurList = newQtrDurList;		
	}
	
	public void setNCBoolList(ArrayList<String> newNCBoolList){
		NCBoolList = newNCBoolList;		
	}
	
	public void setLocation(String strLat, String strLng){
		lat = Double.parseDouble(strLat);
		lng = Double.parseDouble(strLng);		
	}
	
	public void printToFile(BufferedWriter out){
		try{
			out.write("ID: "+ID+"\n");
			out.write("Name: "+name+"\n");
			out.write("AddressLine1: "+addressLine1+"\n");
			out.write("AddressLine2: "+addressLine2+"\n");
			out.write("Lat: "+lat+"\n");
			out.write("Lng: "+lng+"\n");			
			out.write("numInspection: "+numInspection+"\n");
			out.write("numQtrNC: "+numQtrNC+"\n");
			out.write("numEE: "+numEE+"\n");
			out.write("Qtr Dur List: "+qtrDurList+"\n");
			out.write("NCBoolList: "+NCBoolList+"\n");
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
		System.out.println("Lat: "+lat+"\n");
		System.out.println("Lng: "+lng+"\n");
		System.out.println("numInspection: "+numInspection);
		System.out.println("numQtrNC: "+numQtrNC);
		System.out.println("Qtr Dur List: "+qtrDurList);
		System.out.println("NCBoolList: "+NCBoolList);
		System.out.println("numEE: "+numEE);		
	}
	

}
