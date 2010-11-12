package edu.rpi.tw.eScience.WaterQualityPortal.epa;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class EpaUtil {
	
	public void printFacilityArrayList(ArrayList<Facility> facAL, String fileName){
		BufferedWriter out=null;
		Facility curFacility=null;
		try {
			out = new BufferedWriter(new FileWriter(fileName));
			System.out.println("Facility ArrayList");
			Iterator itr = facAL.iterator();  
	        while (itr.hasNext()) {  
	        	curFacility = (Facility)itr.next();
	        	curFacility.printToFile(out);        	
	        }
	        
		} catch (IOException e) {
			System.out.println("printFacilitiesToFile, err");
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
