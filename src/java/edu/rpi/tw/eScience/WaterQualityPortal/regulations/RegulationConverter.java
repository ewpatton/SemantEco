package edu.rpi.tw.eScience.WaterQualityPortal.regulations;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import com.csvreader.CsvReader;

import edu.rpi.tw.eScience.WaterQualityPortal.util.ConfigReader;
import edu.rpi.tw.eScience.WaterQualityPortal.util.NameUtil;

public class RegulationConverter {
	List <String> provenances = null;
	List <String> regulations = null;
	StringBuilder regBuf = null;
	//StringBuilder regOfMeasurement = null;
	String contaminantCol;
	String threasholdCol;
	String unitCol;
	String defaultUnit;
	
	public RegulationConverter(){
		provenances = new ArrayList <String>();
		regulations = new ArrayList <String>();
		regBuf = new StringBuilder();
		//regOfMeasurement = new StringBuilder();
	}

	public void config(String confFile){
		ConfigReader reader = new ConfigReader(confFile);
		/*String ontoStr = reader.getProperty("OntologyFaimly");
		if(ontoStr.equals("OBOE"))
			ontoType = OntologyFamily.OBOE;
		else if(ontoStr.equals("TWC"))
			ontoType = OntologyFamily.TWC;
		else{
			System.err.println("Ontology Faimly "+ontoStr+" is not supported.");
			System.exit(-1);
		}*/
			
		contaminantCol = reader.getProperty("ContaminantCol");
		threasholdCol = reader.getProperty("ThreasholdCol");
		unitCol = reader.getProperty("UnitCol");
		defaultUnit = reader.getProperty("DefaultUnit");
		if(unitCol.equals("NA")&&defaultUnit.equals("NA")){
			System.err.println("Both unit column and default unit are NA." +
					"This is not valid case for the converter");
			System.exit(-1);
		}
	}

	private void convertRegulationInCSV(String confFile, String inputReg, 
			String outputOwl, OntologyFamily ontoType){		
		CsvReader reader = null;
		String element = null, value=null, unit=null;
		int recordNum = 0;

		config(confFile);
		
		try {			
			reader = new CsvReader(inputReg);		
			reader.readHeaders();
			recordNum++;

			while (reader.readRecord())
			{			
				recordNum++;
				System.out.println("Record " + recordNum);
				System.out.println(reader.getRawRecord());
				element = reader.get(contaminantCol).trim();
				value=reader.get(threasholdCol).trim();
				//value=value.replaceAll("ug/L", "");
				//unit="ug/l";
				if(unitCol.equals("NA"))
					unit=defaultUnit;
				else{
				unit = reader.get(unitCol).trim().toLowerCase();	
				if(unit==null || unit.isEmpty()){
					if(!defaultUnit.equals("NA"))
						unit = defaultUnit;
				}				
				}
				System.out.println(element+", "+value+", "+unit);
				insertRegulation(element, value,unit, ontoType);
			}//end of while

			outputRDFtoFile(outputOwl, ontoType);

		} catch (FileNotFoundException e) {
			System.err.println("In convertRegulationInCSV(), file name: " + inputReg);
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("In buildUnitLookupTablesFromCSV(), file name: " + inputReg);
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		}
		finally{
			reader.close();
		}
	}

	public void insertRegulation(String element,String value,String unit,
			OntologyFamily ontoType){
		/*element=element.replace("\"", "");
		element=element.replace(" ", "");
		element=element.replace("<","");
		element=element.replace(">","");
		element=element.replace("&gt;","");
		element=element.replace("&lt;","");
		element=element.replace("&","");*/
		
		element=NameUtil.processElementName(element);
		element=RegulationUtil.capitalizeString(element);
		value=value.replaceAll("\"", "");
		Double numValue = RegulationUtil.numStr2Double(value);
		if(numValue==null)
			return;

		switch (ontoType){
		case TWC: 
			//need to have both ug/l and mg/l elseif the portal identify less pollution occurences
			if(unit.compareTo("ug/l")==0){
				insertRegulationGivenUnit(element, numValue/1000, "mg/l");
				//insertRegulationGivenUnit(element, numValue, unit);
			}			
			else if(unit.compareTo("mg/l")==0){
				insertRegulationGivenUnit(element, numValue, unit);
				//insertRegulationGivenUnit(element, numValue*1000, "ug/l");			
			}
			break;
		case OBOE: 
			//only process mg/l for now
			if(unit.compareTo("ug/l")==0)
				insertRegulationGivenUnitForOBOE(element, numValue/1000, "mg/l"); 
			else if(unit.compareTo("mg/l")==0)
				insertRegulationGivenUnitForOBOE(element, numValue, unit); 
			break;
		default:
			System.err.println("In insertRegulation, the type of the ontoloy family "+
					ontoType+" is not supported");
			break;					
		}		

	}

	public void insertRegulationGivenUnitForOBOE(String element,Double value,String unit){
		//
		String procUnit = unit.replaceAll("/", "-");
		insertObservationOfRegulationViolationForOBOE(element, value, procUnit, regBuf);
		insertMeasurementViolationForOBOE(value, procUnit, regBuf);
		insertDecimalValueViolationForOBOE(value, regBuf);
	}

	public void insertObservationOfRegulationViolationForOBOE(String element, Double value, String unit, StringBuilder curObs){
		curObs.append("<owl:Class rdf:about=\"#Excessive"+element+"Observation\">\n");
		curObs.append("<rdfs:subClassOf rdf:resource=\"http://escience.rpi.edu/ontology/semanteco/2/0/oboe-pollution.owl#ObservationOfRegulationViolation\"/>\n");
		curObs.append("<owl:intersectionOf rdf:parseType=\"Collection\">\n");
		//class: Observation
		curObs.append("<owl:Class rdf:about=\"http://ecoinformatics.org/oboe/oboe.1.0/oboe-core.owl#Observation\"/>\n");
		//restriction 1
		curObs.append("<owl:Restriction>\n");
		curObs.append("<owl:onProperty rdf:resource=\"http://ecoinformatics.org/oboe/oboe.1.0/oboe-core.owl#ofEntity\"/>\n");
		curObs.append("<owl:hasValue rdf:resource=\"http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#"+element+"\"/>\n");
		curObs.append("</owl:Restriction>\n");
		//restriction 2
		curObs.append("<owl:Restriction>\n");
		curObs.append("<owl:onProperty rdf:resource=\"http://ecoinformatics.org/oboe/oboe.1.0/oboe-core.owl#hasMeasurement\"/>\n");
		//curObs.append("<owl:someValuesFrom rdf:resource=\"http://escience.rpi.edu/ontology/semanteco/2/0/oboe-pollution.owl#RegulationViolation\"/>\n");
		curObs.append("<owl:someValuesFrom rdf:resource=\"#Excessive"+value+unit+"Measurement\"/>\n");
		curObs.append("</owl:Restriction>\n");
		curObs.append("</owl:intersectionOf>\n");
		curObs.append("</owl:Class>\n");		
	}

	public void insertMeasurementViolationForOBOE(Double value,String unit, StringBuilder curBuf){
		curBuf.append("<owl:Class rdf:about=\"#Excessive"+value+unit+"Measurement\">\n");
		curBuf.append("<rdfs:subClassOf rdf:resource=\"http://escience.rpi.edu/ontology/semanteco/2/0/oboe-pollution.owl#RegulationViolation\"/>\n");
		curBuf.append("<owl:intersectionOf rdf:parseType=\"Collection\">\n");
		//class: Measurement
		curBuf.append("<owl:Class rdf:about=\"http://ecoinformatics.org/oboe/oboe.1.0/oboe-core.owl#Measurement\"/>\n");
		//Restriction 1
		curBuf.append("<owl:Restriction>\n");
		curBuf.append("<owl:onProperty rdf:resource=\"http://ecoinformatics.org/oboe/oboe.1.0/oboe-core.owl#ofCharacteristic\"/>\n");
		curBuf.append("<owl:hasValue rdf:resource=\"http://ecoinformatics.org/oboe/oboe.1.0/oboe-characteristics.owl#AmountOfSubstanceConcentration\"/>\n");
		curBuf.append("</owl:Restriction>\n");
		//Restriction 2
		if(unit.compareTo("")!=0){
			curBuf.append("<owl:Restriction>\n");
			curBuf.append("<owl:onProperty rdf:resource=\"http://ecoinformatics.org/oboe/oboe.1.0/oboe-core.owl#usesStandard\"/>\n");
			curBuf.append("<owl:hasValue rdf:resource=\"http://ecoinformatics.org/oboe/oboe.1.0/oboe-standards.owl#MilligramPerLiter\"/>\n");
			curBuf.append("</owl:Restriction>\n");
		}
		//Restriction 3
		curBuf.append("<owl:Restriction>\n");
		curBuf.append("<owl:onProperty rdf:resource=\"http://ecoinformatics.org/oboe/oboe.1.0/oboe-core.owl#hasValue\"/>\n");
		//curMeasurement.append("<owl:someValuesFrom rdf:resource=\"http://escience.rpi.edu/ontology/semanteco/2/0/oboe-pollution.owl#RegulationViolation\"/>\n");
		curBuf.append("<owl:someValuesFrom rdf:resource=\"#Excessive"+value+"DecimalValue\"/>\n");
		curBuf.append("</owl:Restriction>\n");
		/*
		curMeasurement.append("<owl:Restriction>\n");
		curMeasurement.append("<owl:onProperty rdf:resource=\"http://escience.rpi.edu/ontology/semanteco/2/0/oboe-pollution.owl#hasNumericValue\"/>\n");
		curMeasurement.append("<owl:someValuesFrom>\n");		
		curMeasurement.append("<rdfs:Datatype>\n");
		curMeasurement.append("<owl:onDatatype rdf:resource=\"http://www.w3.org/2001/XMLSchema#double\"/>\n");
		curMeasurement.append("<owl:withRestrictions rdf:parseType=\"Collection\">\n");
		curMeasurement.append("<rdf:Description rdf:about=\"#"+element+"Threshold\">\n");
		curMeasurement.append("<xsd:minInclusive rdf:datatype=\"http://www.w3.org/2001/XMLSchema#double\">");
		curMeasurement.append(RegulationUtil.decFormat.format(value));
		curMeasurement.append("</xsd:minInclusive>\n");
		curMeasurement.append("</rdf:Description>\n");
		curMeasurement.append("</owl:withRestrictions>\n");
		curMeasurement.append("</rdfs:Datatype>\n");
		
		curMeasurement.append("</owl:someValuesFrom>\n");
		curMeasurement.append("</owl:Restriction>\n");
		*/
		//end of restrictions
		curBuf.append("</owl:intersectionOf>\n");
		curBuf.append("</owl:Class>\n");
	}


	public void insertDecimalValueViolationForOBOE(Double value, StringBuilder curBuf){
		curBuf.append("<owl:Class rdf:about=\"#Excessive"+value+"DecimalValue\">\n");
		curBuf.append("<rdfs:subClassOf rdf:resource=\"http://ecoinformatics.org/oboe/oboe.1.0/oboe-core.owl#DecimalValueViolation\"/>\n");
		curBuf.append("<owl:intersectionOf rdf:parseType=\"Collection\">\n");
		//class: Decimal
		curBuf.append("<owl:Class rdf:about=\"http://ecoinformatics.org/oboe/oboe.1.0/oboe-core.owl#Decimal\"/>\n");
		//Restriction 1
				curBuf.append("<owl:Restriction>\n");
				curBuf.append("<owl:onProperty rdf:resource=\"http://ecoinformatics.org/oboe/oboe.1.0/oboe-core.owl#hasCode\"/>\n");
				curBuf.append("<owl:someValuesFrom>\n");
				curBuf.append("<rdfs:Datatype>\n");
				curBuf.append("<owl:onDatatype rdf:resource=\"http://www.w3.org/2001/XMLSchema#double\"/>\n");
				curBuf.append("<owl:withRestrictions rdf:parseType=\"Collection\">\n");
				curBuf.append("<rdf:Description rdf:about=\"#"+value+"Threshold\">\n");
				curBuf.append("<xsd:minInclusive rdf:datatype=\"http://www.w3.org/2001/XMLSchema#double\">");
				curBuf.append(RegulationUtil.decFormat.format(value));
				curBuf.append("</xsd:minInclusive>\n");
				curBuf.append("</rdf:Description>\n");
				curBuf.append("</owl:withRestrictions>\n");
				curBuf.append("</rdfs:Datatype>\n");
				curBuf.append("</owl:someValuesFrom>\n");
				curBuf.append("</owl:Restriction>\n");
				//end of restrictions
				curBuf.append("</owl:intersectionOf>\n");
				curBuf.append("</owl:Class>\n");
	}


	public void insertRegulationGivenUnit(String element,Double value,String unit){
		String name=element;
		if(unit.compareTo("ug/l")==0){
			name=element+"ug";
		}

		String thisRegulation="";
		thisRegulation += "<owl:Class rdf:about=\"#Excessive"+name+"Measurement\">\n";
		//thisRegulation += "<rdfs:subClassOf rdf:resource=\"http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#ExceededThreshold\"/>\n";
		thisRegulation += "<rdfs:subClassOf rdf:resource=\"http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#RegulationViolation\"/>\n";
		thisRegulation += "<owl:intersectionOf rdf:parseType=\"Collection\">\n";
		//thisRegulation += "<owl:Class rdf:about=\"http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#WaterMeasurement\"/>\n";
		thisRegulation += "<owl:Class rdf:about=\"http://escience.rpi.edu/ontology/semanteco/2/0/water.owl#WaterMeasurement\"/>\n";
		thisRegulation += "<owl:Restriction>\n";
		thisRegulation += "<owl:onProperty rdf:resource=\"http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#hasValue\"/>\n";
		thisRegulation += "<owl:someValuesFrom>\n";
		thisRegulation += "<rdfs:Datatype>\n";
		thisRegulation += "<owl:onDatatype rdf:resource=\"http://www.w3.org/2001/XMLSchema#decimal\"/>\n";
		thisRegulation += "<owl:withRestrictions rdf:parseType=\"Collection\">\n";
		thisRegulation += "<rdf:Description rdf:about=\"#"+name+"Threshold-Drinking\">\n";
		thisRegulation += "<xsd:minInclusive rdf:datatype=\"http://www.w3.org/2001/XMLSchema#decimal\">"+RegulationUtil.decFormat.format(value)+"</xsd:minInclusive>\n";
		thisRegulation += "</rdf:Description>\n";
		thisRegulation += "</owl:withRestrictions>\n";
		thisRegulation += "</rdfs:Datatype>\n";
		thisRegulation += "</owl:someValuesFrom>\n";
		thisRegulation += "</owl:Restriction>\n";	
		thisRegulation += "<owl:Restriction>\n";
		//		thisRegulation += "<owl:onProperty rdf:resource=\"http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#hasElement\"/>\n";
		thisRegulation += "<owl:onProperty rdf:resource=\"http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#hasCharacteristic\"/>\n";
		///		thisRegulation += "<owl:hasValue rdf:resource=\"http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#"+element+"\"/>\n";
		thisRegulation += "<owl:hasValue rdf:resource=\"http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#"+element+"\"/>\n";
		thisRegulation += "</owl:Restriction>\n";
		if(unit.compareTo("")!=0){
			thisRegulation += "<owl:Restriction>\n";
			//		thisRegulation += "<owl:onProperty rdf:resource=\"http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#hasUnit\"/>\n";
			//thisRegulation += "<owl:onProperty rdf:resource=\"http://sweet.jpl.nasa.gov/2.1/reprSciUnits.owl#hasUnit\"/>\n";
			thisRegulation += "<owl:onProperty rdf:resource=\"http://sweet.jpl.nasa.gov/2.1/repr.owl#hasUnit\"/>\n";
			thisRegulation += "<owl:hasValue>"+unit+"</owl:hasValue>\n";
			thisRegulation += "</owl:Restriction>\n";
		}
		thisRegulation += "</owl:intersectionOf>\n";
		thisRegulation += "</owl:Class>\n";
		regulations.add(thisRegulation);
		//insertProvenance(name, owloutputFile,pmlName);
	}

	public String getPrefix(String file){
		String filePrefix=file.substring(file.lastIndexOf('/')+1, file.indexOf('.'));
		String output ="";
		output += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
		output += "<!DOCTYPE rdf:RDF [\n";
		output += "<!ENTITY rdf \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" >\n";
		output +=  "<!ENTITY geo \"http://www.w3.org/2003/01/geo/wgs84_pos#\" >\n";
		output +=  "<!ENTITY owl \"http://www.w3.org/2002/07/owl#\" >\n";
		output +=  "<!ENTITY xsd \"http://www.w3.org/2001/XMLSchema#\" >\n";
		output +=  "<!ENTITY rdfs \"http://www.w3.org/2000/01/rdf-schema#\" >\n";
		output +=  "<!ENTITY time \"http://www.w3.org/2006/time\" >\n";
		output += "<!ENTITY pol \"http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#\">\n";
		output += "<!ENTITY oboe-pol \"http://escience.rpi.edu/ontology/semanteco/2/0/oboe-pollution.owl#\" >\n";
		output += "<!ENTITY "+filePrefix+" \"http://escience.rpi.edu/ontology/semanteco/2/0/"+filePrefix+"#\" >\n";
		output += "<!ENTITY water \"http://escience.rpi.edu/ontology/semanteco/2/0/water.owl#\">\n";
		//
		output +=  "<!ENTITY elem \"http://sweet.jpl.nasa.gov/2.1/matrElement.owl#\" >\n";
		output +=  "<!ENTITY body \"http://sweet.jpl.nasa.gov/2.1/realmHydroBody.owl#\" >\n";

		output +=  "<!ENTITY pmlp \"http://inferenceweb.stanford.edu/2006/06/pml-provenance.owl#\">\n";
		output +=  "<!ENTITY comp \"http://sweet.jpl.nasa.gov/2.1/matrCompound.owl#\">\n";
		output +=  "<!ENTITY chem \"http://sweet.jpl.nasa.gov/2.1/matr.owl#\">\n";
		output +=  "<!ENTITY foaf \"http://xmlns.com/foaf/0.1/\">\n";
		output +=  "]>\n";
		output += "<rdf:RDF ";
		output += "xml:base=\"&"+filePrefix+";\"\n";
		output += "xmlns=\"&"+filePrefix+";\"\n";
		//output += "xml:base=\"http://escience.rpi.edu/ontology/semanteco/2/0/"+file+"\"\n";
		//output += "xmlns=\"http://escience.rpi.edu/ontology/semanteco/2/0/"+file+"\"\n";
		output +=  "xmlns:pol=\"&pol;\"\n";
		output +=  "xmlns:oboe-pol=\"&oboe-pol;\"\n";
		output +=  "xmlns:water=\"&water;\"\n";	
		//
		output +=  "xmlns:owl=\"&owl;\"\n";
		output +=  "xmlns:rdfs=\"&rdfs;\"\n";
		output +=  "xmlns:time=\"&time;\"\n";
		output +=  "xmlns:rdf=\"&rdf;\"\n";
		output +=  "xmlns:geo=\"&geo;\"\n";
		output +=  "xmlns:pmlp=\"&pmlp;\"\n";
		output +=  "xmlns:elem=\"&elem;\"\n";
		output +=  "xmlns:body=\"&body;\"\n";
		output +=  "xmlns:comp=\"&comp;\"\n";
		output +=  "xmlns:foaf=\"&foaf;\"\n";
		output +=  "xmlns:chem=\"&chem;\"\n";
		output +=  "xmlns:xsd=\"&xsd;\">\n";

		return output;
	}

	public void outputRDFtoFile(String file, OntologyFamily ontoType){
		try{
			FileWriter fstream = new FileWriter(file);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(getPrefix(file));
			outputRDFBody(out, ontoType);
			out.write("</rdf:RDF>");
			out.close();
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}


	public void outputRDFBody(BufferedWriter out, OntologyFamily ontoType){
		try {
			switch(ontoType){
			case TWC:
				for(int i=0;i<regulations.size();i++)
					out.write(regulations.get(i));
				break;
			case OBOE:
				out.write(regBuf.toString());
				break;
			default:
				System.err.println("In outputRDFBody, the type of the ontoloy family "+
						ontoType+" is not supported");
				break;	
			}
		} catch (IOException e) {
			System.err.println("outputRDFBody");
			e.printStackTrace();
		}
	}

	public static void main(String [] args){
		RegulationConverter reg = new RegulationConverter();
		//String inputReg="epa-aqua-regulation-step2.csv";
		//String outputOwl="epa-aqua-acute-regulation.owl";	
		//"Freshwater-CMC-acute(ug/L)"; "Freshwater-CMC-chronic(ug/L)";
		List<String> states = new ArrayList<String>();
		states.add("ri");
		states.add("mass");
		states.add("ca");
		states.add("ny");
		states.add("EPA");

		for(String state:states){
		String conf = "data/oboe/reg/"+state+".config";
		String inputReg="data/oboe/reg/"+state+"-regulation.csv";
		String outputOwl="data/oboe/reg/"+state+"-regulation-TWC.owl";
		reg.convertRegulationInCSV(conf, inputReg, outputOwl, OntologyFamily.TWC);
		}
	}

}
