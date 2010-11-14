package edu.rpi.tw.eScience.WaterQualityPortal.epa;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class EpaUtil {
	
	public void printMeasurementConstraintArrayList(ArrayList<MeasurementConstraint> facCons, String fileName){
		BufferedWriter out=null;
		MeasurementConstraint curMC=null;
		try {
			out = new BufferedWriter(new FileWriter(fileName));
			System.out.println("Measurement Constraint ArrayList");
			Iterator<MeasurementConstraint> itr = facCons.iterator();  
	        while (itr.hasNext()) {  
	        	curMC = (MeasurementConstraint)itr.next();
	        	curMC.printToFile(out);        	
	        }
	        
		} catch (IOException e) {
			System.out.println("printMeasurementConstraintArrayList, err");
			e.printStackTrace();
		} finally {
            //Close the BufferedWriter			
            try {            	
                if (out != null) {
                	out.flush();
                	out.close();
                }
            } catch (IOException ex) {
            	System.out.println("In printMeasurementConstraintArrayList, closing the BufferedWriter");
                ex.printStackTrace();
            }
        }
	}

	
	public void printFacilityMeasurmentArrayList(ArrayList<FacilityMeasurement> facAL, String fileName){
		BufferedWriter out=null;
		FacilityMeasurement curFM=null;
		try {
			out = new BufferedWriter(new FileWriter(fileName));
			System.out.println("Facility Measurement ArrayList");
			Iterator<FacilityMeasurement> itr = facAL.iterator();  
	        while (itr.hasNext()) {  
	        	curFM = (FacilityMeasurement)itr.next();
	        	curFM.printToFile(out);        	
	        }
	        
		} catch (IOException e) {
			System.out.println("printFacilityMeasurmentArrayList, err");
			e.printStackTrace();
		} finally {
            //Close the BufferedWriter			
            try {            	
                if (out != null) {
                	out.flush();
                	out.close();
                }
            } catch (IOException ex) {
            	System.out.println("In printFacilityMeasurmentArrayList, closing the BufferedWriter");
                ex.printStackTrace();
            }
        }
	}
	
	public void printFacilityArrayList(ArrayList<Facility> facAL, String fileName){
		BufferedWriter out=null;
		Facility curFacility=null;
		try {
			out = new BufferedWriter(new FileWriter(fileName));
			System.out.println("Facility ArrayList");
			Iterator<Facility> itr = facAL.iterator();  
	        while (itr.hasNext()) {  
	        	curFacility = (Facility)itr.next();
	        	curFacility.printToFile(out);        	
	        }
	        
		} catch (IOException e) {
			System.out.println("printFacilityArrayList, err");
			e.printStackTrace();
		} finally {
            //Close the BufferedWriter			
            try {            	
                if (out != null) {
                	out.flush();
                	out.close();
                }
            } catch (IOException ex) {
            	System.out.println("In printFacilityArrayList, closing the BufferedWriter");
                ex.printStackTrace();
            }
        }		
	}
}
