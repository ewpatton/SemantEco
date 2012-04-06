package edu.rpi.tw.eScience.WaterQualityPortal.regulations;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import com.csvreader.CsvReader;

public class RegulationConverter {
	List <String> regulations = null;
	List <String> provenances = null;
	
	public RegulationConverter(){
		regulations = new ArrayList <String>();
		provenances = new ArrayList <String>();
	}

	
	private void convertRegulationInCSV(String inputReg, String outputOwl){		
		CsvReader reader = null;
		String element = null, value=null, unit=null;
		int recordNum = 0;
	
		try {			
			reader = new CsvReader(inputReg);		
			reader.readHeaders();
			recordNum++;

			while (reader.readRecord())
			{			
				recordNum++;
				System.out.println("Record " + recordNum);
				System.out.println(reader.getRawRecord());
				element = reader.get("Contaminant").trim();
				value=reader.get("Freshwater-CMC-chronic(ug/L)").trim();
				value=value.replaceAll("ug/L", "");
				unit="ug/l";
				System.out.println(element+", "+value+", "+unit);				
				insertRegulation(element, value,unit);
			}//end of while
			
			outputRDFtoFile(outputOwl);
	
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
	
	public void insertRegulation(String element,String value,String unit){
		element=element.replace("\"", "");
		element=element.replace(" ", "");
		element=element.replace("<","");
		element=element.replace(">","");
		element=element.replace("&gt;","");
		element=element.replace("&lt;","");
		element=element.replace("&","");
		element=RegulationUtil.capitalizeString(element);
		value=value.replace("\"", "");
		Double numValue = RegulationUtil.numStr2Double(value);
		if(numValue==null)
			return;

		if(unit.compareTo("ug/l")==0){
			insertRegulationGivenUnit(element, numValue/1000, "mg/l");
		}
		insertRegulationGivenUnit(element, numValue, unit);		
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
		thisRegulation += "<owl:onDatatype rdf:resource=\"http://www.w3.org/2001/XMLSchema#double\"/>\n";
		thisRegulation += "<owl:withRestrictions rdf:parseType=\"Collection\">\n";
		thisRegulation += "<rdf:Description rdf:about=\"#"+name+"Threshold-Drinking\">\n";
		thisRegulation += "<xsd:minInclusive rdf:datatype=\"http://www.w3.org/2001/XMLSchema#double\">"+RegulationUtil.decFormat.format(value)+"</xsd:minInclusive>\n";
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
			thisRegulation += "<owl:onProperty rdf:resource=\"http://sweet.jpl.nasa.gov/2.1/reprSciUnits.owl#hasUnit\"/>\n";
			thisRegulation += "<owl:hasValue>"+unit+"</owl:hasValue>\n";
			thisRegulation += "</owl:Restriction>\n";
		}
		thisRegulation += "</owl:intersectionOf>\n";
		thisRegulation += "</owl:Class>\n";
		regulations.add(thisRegulation);
		//insertProvenance(name, owloutputFile,pmlName);
	}
	
	public String getPrefix(String file){
		String output ="";
		output += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
		output += "<!DOCTYPE rdf:RDF [\n";
		output += "<!ENTITY rdf \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" >\n";
		output +=  "<!ENTITY geo \"http://www.w3.org/2003/01/geo/wgs84_pos#\" >\n";
		output +=  "<!ENTITY owl \"http://www.w3.org/2002/07/owl#\" >\n";
		output +=  "<!ENTITY xsd \"http://www.w3.org/2001/XMLSchema#\" >\n";
		output +=  "<!ENTITY rdfs \"http://www.w3.org/2000/01/rdf-schema#\" >\n";
		output +=  "<!ENTITY time \"http://www.w3.org/2006/time\" >\n";
		//output +=  "<!ENTITY epa \"http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#\" >\n";
		output += "<!ENTITY pol \"http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#\">\n";
		output += "<!ENTITY water \"http://escience.rpi.edu/ontology/semanteco/2/0/water.owl#\">\n";
		//
		output +=  "<!ENTITY elem \"http://sweet.jpl.nasa.gov/2.1/matrElement.owl#\" >\n";
		output +=  "<!ENTITY body \"http://sweet.jpl.nasa.gov/2.1/realmHydroBody.owl#\" >\n";

		output +=  "<!ENTITY pmlp \"http://inferenceweb.stanford.edu/2006/06/pml-provenance.owl#\">\n";
		output +=  "<!ENTITY comp \"http://sweet.jpl.nasa.gov/2.1/matrCompound.owl#\">";
		output +=  "<!ENTITY chem \"http://sweet.jpl.nasa.gov/2.1/matr.owl#\">";
		output +=  "<!ENTITY foaf \"http://xmlns.com/foaf/0.1/\">";
		output +=  "]>";
		output += "<rdf:RDF ";
		//output +=  "xml:base=\"http://tw2.tw.rpi.edu/zhengj3/owl/"+file+"\"\n";
		//output +=  "xmlns=\"http://tw2.tw.rpi.edu/zhengj3/owl/"+file+"\"\n";
		//output +=  "xmlns:epa=\"&epa;\"\n";
		output += "xml:base=\"http://escience.rpi.edu/ontology/semanteco/2/0/"+file+"\"\n";
		output += "xmlns=\"http://escience.rpi.edu/ontology/semanteco/2/0/"+file+"\"\n";
		output +=  "xmlns:pol=\"&pol;\"\n";
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
	
	public void outputRDFtoFile(String file){
		try{
			FileWriter fstream = new FileWriter(file);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(getPrefix(file));
			for(int i=0;i<regulations.size();i++){
				out.write(regulations.get(i));
			}
			out.write("</rdf:RDF>");
			out.close();
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	public static void main(String [] args){
		RegulationConverter reg=new RegulationConverter();
		String inputReg="aqua-epa-regulation.csv";
		String outputOwl="aqua-epa-regulation.owl";
		reg.convertRegulationInCSV(inputReg, outputOwl);
	}

}
