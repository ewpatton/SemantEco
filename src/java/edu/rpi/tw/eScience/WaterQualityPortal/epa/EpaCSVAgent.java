package edu.rpi.tw.eScience.WaterQualityPortal.epa;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import com.csvreader.*;

public class EpaCSVAgent {

	private int convertCmpType(String strCmpType){
		if(strCmpType.compareTo("<")==0)
			return 0;
		else if(strCmpType.compareTo("<=")==0)
			return 1;
		else if(strCmpType.compareTo("==")==0)
			return 2;		
		else if(strCmpType.compareTo(">=")==0)
			return 3;		
		else if(strCmpType.compareTo(">")==0)
			return 4;	
		else{
			System.err.println("convertCmpType, unknown strCmpType");
			return -1;
		}
	}
	
	public void CSVRead(String inputfileName, Facility curFac, HashMap<String, MeasurementConstraint> constraintsMap){		
		CsvReader reader = null;
		int recordNum = 0;
		String focusedName = null;
		boolean coliformTag = false;
		String elementName= null;
		//String testNumber= null;
		String date= null;
		String value= null;
		String unit= null;
		//for constraint
		String strCmpType;
		int cmpType;
		String cmpValue;
		String cmpUnit;
		//
		String constraintName=null;
		ArrayList<FacilityMeasurement> coliformMeasurements = new ArrayList<FacilityMeasurement> ();
		//ArrayList<MeasurementConstraint> coliformConstraints = new ArrayList<MeasurementConstraint> ();
		//HashMap<String, MeasurementConstraint> coliformConstraints = null;
		
		try {			
			reader = new CsvReader(inputfileName);		

			reader.readHeaders();
			recordNum++;

			while (reader.readRecord())
			{			
				elementName = reader.get(13);
				recordNum++;
				//System.out.println("Record " + recordNum + ": "+elementName);
				//find the next element
				if(coliformTag == true && elementName.compareTo("")!=0){
					coliformTag = false;
					focusedName = null;
					break;//if we want to process other elements, use continue;
				}
				//process the line with Name = "Coliform, fecal general"
				if(coliformTag == false && elementName.compareTo("Coliform, fecal general")==0)
				{
					coliformTag = true;
					focusedName = elementName.replace(' ', '_');
					//
					cmpValue = reader.get("C1_LVAL");	
					if(cmpValue.compareTo("")!=0)
					{
						strCmpType = reader.get("C1_LSENSE");
						cmpType = convertCmpType(strCmpType);
						if(cmpType == -1){
							System.err.println("For record "+ recordNum + ", unknown strCmpType");						
						}
						
						cmpUnit = reader.get("C1_LUNIT");
						MeasurementConstraint cons1 = new MeasurementConstraint(focusedName, 1, cmpType, cmpValue, cmpUnit);
						constraintName = focusedName+1;
						if(constraintsMap.get(constraintName)==null)
							constraintsMap.put(constraintName, cons1);
						//coliformConstraints.add(cons1);
					}
					//
					cmpValue = reader.get("C2_LVAL");	
					if(cmpValue.compareTo("")!=0)
					{
						strCmpType = reader.get("C2_LSENSE");
						cmpType = convertCmpType(strCmpType);
						cmpUnit = reader.get("C2_LUNIT");					
						MeasurementConstraint cons2 = new MeasurementConstraint(focusedName, 2, cmpType, cmpValue, cmpUnit);
						constraintName = focusedName+2;
						if(constraintsMap.get(constraintName)==null)
							constraintsMap.put(constraintName, cons2);
						//coliformConstraints.add(cons2);
					}
					//
					cmpValue = reader.get("C3_LVAL");	
					if(cmpValue.compareTo("")!=0)
					{
						strCmpType = reader.get("C3_LSENSE");
						cmpType = convertCmpType(strCmpType);
						cmpUnit = reader.get("C3_LUNIT");						
						MeasurementConstraint cons3 = new MeasurementConstraint(focusedName, 3, cmpType, cmpValue, cmpUnit);
						constraintName = focusedName+3;
						if(constraintsMap.get(constraintName)==null)
							constraintsMap.put(constraintName, cons3);
						//coliformConstraints.add(cons3);
					}
					//
					cmpValue = reader.get("Q1_LVAL");	
					if(cmpValue.compareTo("")!=0)
					{
						strCmpType = reader.get("Q1_LSENSE");
						cmpType = convertCmpType(strCmpType);
						cmpUnit = reader.get("Q1_LUNIT");
						MeasurementConstraint cons4 = new MeasurementConstraint(focusedName, 4, cmpType, cmpValue, cmpUnit);
						constraintName = focusedName+4;
						if(constraintsMap.get(constraintName)==null)
							constraintsMap.put(constraintName, cons4);
						//coliformConstraints.add(cons4);
					}
					//
					cmpValue = reader.get("Q2_LVAL");
					if(cmpValue.compareTo("")!=0)
					{
						strCmpType = reader.get("Q2_LSENSE");
						cmpType = convertCmpType(strCmpType);
						cmpUnit = reader.get("Q2_LUNIT");
						MeasurementConstraint cons5 = new MeasurementConstraint(focusedName, 5, cmpType, cmpValue, cmpUnit);
						constraintName = focusedName+5;
						if(constraintsMap.get(constraintName)==null)
							constraintsMap.put(constraintName, cons5);
						//coliformConstraints.add(cons5);
					}
					//
				}
				if(coliformTag == true){
					date = reader.get("DATE");
					//C1 == 1
					value = reader.get("C1_VALUE");
					unit = reader.get("C1_UNIT"); 
					if(value.compareTo("")!=0)
					{
						FacilityMeasurement fm1 = new FacilityMeasurement(focusedName, 1, date, value, unit);
						coliformMeasurements.add(fm1);
					}
					//C2 == 2
					value = reader.get("C2_VALUE");
					unit = reader.get("C2_UNIT"); 
					if(value.compareTo("")!=0)
					{
						FacilityMeasurement fm2 = new FacilityMeasurement(focusedName, 2, date, value, unit);
						coliformMeasurements.add(fm2);
					}
					//C3 == 3
					value = reader.get("C3_VALUE");
					unit = reader.get("C3_UNIT"); 
					if(value.compareTo("")!=0)
					{
						FacilityMeasurement fm3 = new FacilityMeasurement(focusedName, 3, date, value, unit);
						coliformMeasurements.add(fm3);
					}
					//Q1 == 4
					value = reader.get("Q1_VALUE");
					unit = reader.get("Q1_UNIT"); 
					if(value.compareTo("")!=0)
					{
						FacilityMeasurement fm4 = new FacilityMeasurement(focusedName, 4, date, value, unit);
						coliformMeasurements.add(fm4);
					}
					//Q2 == 5
					value = reader.get("Q2_VALUE");
					unit = reader.get("Q2_UNIT"); 
					if(value.compareTo("")!=0)
					{
						FacilityMeasurement fm5 = new FacilityMeasurement(focusedName, 5, date, value, unit);
						coliformMeasurements.add(fm5);
					}

				}//end of if(coliformTag == true)
			}//end of while
			//curFac.setColiformConstraints(coliformConstraints);
			curFac.setcoliformMeasurements(coliformMeasurements);		
		} catch (FileNotFoundException e) {
			System.err.println("In CSVRead(), file name: " + inputfileName);
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("In CSVRead(), file name: " + inputfileName);
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		}
		finally{
			reader.close();
		}

	}

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		EpaCSVAgent csvAgent = new EpaCSVAgent();
		EpaUtil epaUtil = new EpaUtil();
		HashMap<String, MeasurementConstraint> testConstraints = new HashMap<String, MeasurementConstraint>();
		
		Facility fac1 = new Facility("100000000001");
		//"home/ping/research/python/water/csv/128967058817850.csv
		String fileName = "/home/ping/research/python/water/CgiSoupOutput/02809/CSV/110009444869.csv";
		csvAgent.CSVRead(fileName, fac1, testConstraints);
		
		//epaUtil.printMeasurementConstraintArrayList(fac1.coliformConstraints, "/home/ping/research/python/water/csv/coliformConstraints");
		//epaUtil.printFacilityMeasurmentArrayList(fac1.coliformMeasurements, "/home/ping/research/python/water/csv/coliformMeasurements");
		
	}

}
