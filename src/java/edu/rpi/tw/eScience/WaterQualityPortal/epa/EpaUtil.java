package edu.rpi.tw.eScience.WaterQualityPortal.epa;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

public class EpaUtil {
	
	public void saveFacilities(ArrayList<Facility> facAL, String fileName){
		BufferedWriter out=null;
		Facility curFacility = null;
		String rdfFac = null;
		try {
			out = new BufferedWriter(new FileWriter(fileName));

			Iterator<Facility> itr = facAL.iterator();  
	        while (itr.hasNext()) {
	        	curFacility = (Facility)itr.next();
	        	rdfFac = curFacility.toString();
	        	out.write(rdfFac);
	        }	        
		} catch (IOException e) {
			//System.err.println("saveFacilities, err");
			e.printStackTrace();
		} finally {
            //Close the BufferedWriter			
            try {            	
                if (out != null) {
                	out.flush();
                	out.close();
                }
            } catch (IOException ex) {
            	//System.err.println("In saveFacilities, closing the BufferedWriter");
                ex.printStackTrace();
            }
        }		
	}
	
	public void saveMeasurementConstraints(HashMap<String, MeasurementConstraint> constraints, String fileName){
		BufferedWriter out=null;
		//String curKey = null;
		MeasurementConstraint curMC=null;
		String rdfMC = null;
		try {
			out = new BufferedWriter(new FileWriter(fileName));
			
			Iterator<Entry<String,MeasurementConstraint>> itrOut = constraints.entrySet().iterator();  
	        while (itrOut.hasNext()) {  
	            Entry<String, MeasurementConstraint> entryOut = itrOut.next();
	            //curKey = (String)entryOut.getKey(); ;  
	            curMC = (MeasurementConstraint)entryOut.getValue();
	            rdfMC = curMC.toString();
	            out.write(rdfMC);
	            //out.write("Key: "+curKey+"\n");
	            //curMC.printToFile(out);
	        }//end of while        
		} catch (IOException e) {
			//System.err.println("saveMeasurementConstraints, err");
			e.printStackTrace();
		} finally {
            //Close the BufferedWriter			
            try {            	
                if (out != null) {
                	out.flush();
                	out.close();
                }
            } catch (IOException ex) {
            	//System.err.println("In saveMeasurementConstraints, closing the BufferedWriter");
                ex.printStackTrace();
            }
        }	
	}

	
	
	public void printMeasurementConstraintMap(HashMap<String, MeasurementConstraint> constraints, String fileName){
		BufferedWriter out=null;
		String curKey = null;
		MeasurementConstraint curMC=null;
		try {
			out = new BufferedWriter(new FileWriter(fileName));
			//System.out.println("HashMap of Measurement Constraint");
			
			Iterator<Entry<String,MeasurementConstraint>> itrOut = constraints.entrySet().iterator();  
	        while (itrOut.hasNext()) {  
	            Entry<String,MeasurementConstraint> entryOut = itrOut.next();
	            curKey = (String)entryOut.getKey(); ;  
	            curMC = (MeasurementConstraint)entryOut.getValue();
	            out.write("Key: "+curKey+"\n");
	            curMC.printToFile(out);
	        }//end of while        
		} catch (IOException e) {
			//System.err.println("printFacilitiesToFile, err");
			e.printStackTrace();
		} finally {
            //Close the BufferedWriter			
            try {            	
                if (out != null) {
                	out.flush();
                	out.close();
                }
            } catch (IOException ex) {
            	//System.err.println("In printFacilitiesToFile, closing the BufferedWriter");
                ex.printStackTrace();
            }
        }	
	}
	
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
            	//System.err.println("In printMeasurementConstraintArrayList, closing the BufferedWriter");
                ex.printStackTrace();
            }
        }
	}

	
	public void printFacilityMeasurmentArrayList(ArrayList<FacilityMeasurement> facAL, String fileName){
		BufferedWriter out=null;
		FacilityMeasurement curFM=null;
		try {
			out = new BufferedWriter(new FileWriter(fileName));
			//System.out.println("Facility Measurement ArrayList");
			Iterator<FacilityMeasurement> itr = facAL.iterator();  
	        while (itr.hasNext()) {  
	        	curFM = (FacilityMeasurement)itr.next();
	        	curFM.printToFile(out);        	
	        }
	        
		} catch (IOException e) {
			//System.err.println("printFacilityMeasurmentArrayList, err");
			e.printStackTrace();
		} finally {
            //Close the BufferedWriter			
            try {            	
                if (out != null) {
                	out.flush();
                	out.close();
                }
            } catch (IOException ex) {
            	//System.err.println("In printFacilityMeasurmentArrayList, closing the BufferedWriter");
                ex.printStackTrace();
            }
        }
	}
	
	public void printFacilityArrayList(ArrayList<Facility> facAL, String fileName){
		BufferedWriter out=null;
		Facility curFacility=null;
		try {
			out = new BufferedWriter(new FileWriter(fileName));
			//System.out.println("Facility ArrayList");
			Iterator<Facility> itr = facAL.iterator();  
	        while (itr.hasNext()) {  
	        	curFacility = (Facility)itr.next();
	        	curFacility.printToFile(out);        	
	        }
	        
		} catch (IOException e) {
			//System.err.println("printFacilityArrayList, err");
			e.printStackTrace();
		} finally {
            //Close the BufferedWriter			
            try {            	
                if (out != null) {
                	out.flush();
                	out.close();
                }
            } catch (IOException ex) {
            	//System.err.println("In printFacilityArrayList, closing the BufferedWriter");
                ex.printStackTrace();
            }
        }		
	}
	
	/*
	 * Input strDate is in the format like 20100430
	 * */
	public Calendar str2Calendar(String strDate){
		Calendar cal = Calendar.getInstance();
		String year = strDate.substring(0, 4);
		String month = strDate.substring(4, 6);
		String day = strDate.substring(6, 8);
		cal.set(Integer.parseInt(year), Integer.parseInt(month)-1, Integer.parseInt(day),
				12, 0, 0);	
		//System.out.println("Year: "+year+", Month: "+month+", Day: "+day);
		return cal;		
	}
	
	public static void main(String[] args) {
		EpaUtil util1 = new EpaUtil();
		Calendar cal = util1.str2Calendar("20101228");
		System.out.println("Date is : " + cal.getTime()); 
		
	}
}
