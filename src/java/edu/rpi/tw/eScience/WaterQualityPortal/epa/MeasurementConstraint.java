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
	
	public String toRDFString() {
		String result = "";
		String strCmp = null;
		
		result += "<owl:Class rdf:about=\"#Excessive-"+elementName+"Measurement-"+testNumber+"\">\n";
		result += "<rdfs:subClassOf rdf:resource=\"http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#ExceededThreshold\"/>\n";
		result += "<owl:intersectionOf rdf:parseType=\"Collection\">\n";
		result += "<owl:Class rdf:about=\"http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#WaterMeasurement\"/>\n";		
		result += "<owl:Restriction>\n";
		result += "<owl:onProperty rdf:resource=\"http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#hasMeasuredValue\"/>\n";
		result += "<owl:someValuesFrom>\n";
		result += "<rdfs:Datatype>\n";
		result += "<owl:onDatatype rdf:resource=\"http://www.w3.org/2001/XMLSchema#float\"/>\n";
		result += "<owl:withRestrictions rdf:parseType=\"Collection\">\n";
		result += "<rdf:Description rdf:about=\"#Threshold-Drinking\">\n";
		//0: <, 1: <=, 2: ==, 3: >=, 4: >
		switch (cmpType) {
		case 0: strCmp = "minInclusive"; break;
		case 1: strCmp = "minExclusive"; break;
		case 2: System.err.println("toRDFString, unsupported cmp Type"); break;
		case 3: strCmp = "maxExclusive"; break;
		case 4: strCmp = "maxInclusive"; break;
		default: System.err.println("toRDFString, unknown cmp Type"); break;				
		}		
		result += "<xsd:minInclusive rdf:datatype=\"http://www.w3.org/2001/XMLSchema#float\">"+
					cmpValue+"</xsd:"+strCmp+">\n";
		result += "</rdf:Description>\n";
		result += "</owl:withRestrictions>\n";
		result += "</rdfs:Datatype>\n";
		result += "</owl:someValuesFrom>\n";
		result += "</owl:Restriction>\n";
		result += "<owl:Restriction>\n";
		result += "<owl:onProperty rdf:resource=\"http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#hasMeasuredElement\"/>\n";
		result += "<owl:hasValue rdf:resource=\"http://sweet.jpl.nasa.gov/2.1/matrElement.owl#"+elementName+"\"/>\n";
		result += "</owl:Restriction>\n";
		result += "<owl:Restriction>\n";
		result += "<owl:onProperty rdf:resource=\"http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#testNumber\"/>\n";
		result += "<owl:hasValue rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">"+testNumber+"</owl:hasValue>\n";
		result += "</owl:Restriction>\n";
		result += "</owl:intersectionOf>\n";
		result += "</owl:Class>\n";
		
		//result += "\n";
		return result;
	}
	
	
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
			default: out.write("cmpType: "+ "unknown" +"\n"); break;
				
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
		default: System.out.println("cmpType: "+ "unknown" +"\n"); break;
		}
		System.out.println("cmpValue: "+ cmpValue +"\n");
		System.out.println("cmpUnit: "+ cmpUnit +"\n");
	}

}
