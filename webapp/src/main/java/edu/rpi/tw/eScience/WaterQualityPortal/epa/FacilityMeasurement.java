package edu.rpi.tw.eScience.WaterQualityPortal.epa;

import java.io.BufferedWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

import edu.rpi.tw.eScience.WaterQualityPortal.model.Ontology;

public class FacilityMeasurement {
	int id;
	String elementName;
	int testNumber;
	String date;
	String value;
	String unit;	
	String src;
	String postContent;
	int row;
	
	public FacilityMeasurement(int curId, String curElementName, int curTestNumber, 
			String curDate, String curValue, String curUnit){
		id = curId;
		elementName = curElementName;
		testNumber = curTestNumber;
		date = curDate;
		value = curValue;
		unit = curUnit;		
	}
	
	/*
	 * 
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
	
	public void printToFile(BufferedWriter out){
		try{
			out.write("elementName: "+ elementName +"\n");
			out.write("testNumber: "+ testNumber +"\n");
			out.write("date: "+ date +"\n");
			out.write("value: "+ value +"\n");
			out.write("unit: "+ unit +"\n");
		} catch (IOException e) {
			System.out.println("In printToFile, err in writing to file");
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		}		
	}
	
	public void printFacility(){
		System.out.println("elementName: "+ elementName +"\n");
		System.out.println("testNumber: "+ testNumber +"\n");
		System.out.println("date: "+ date +"\n");
		System.out.println("value: "+ value +"\n");
		System.out.println("unit: "+ unit +"\n");
	}
	
	public void setSourceDocument(String src, String postContent, int row) {
		this.src = src;
		this.postContent = postContent;
		this.row = row;
	}
	
	String asURI() {
		return Ontology.EPA.NS+elementName;
	}
	
	public Resource rowColRef(int col, Model pmlModel) {
		Resource epa = pmlModel.createResource(Ontology.EPA.NS+"EPA");
		Resource source = pmlModel.createResource(pmlModel.createResource(Ontology.PMLP.SourceUsage));
		Resource frag = pmlModel.createResource(pmlModel.createResource(Ontology.PMLP.DocumentFragmentByRowCol));
		Resource document = pmlModel.createResource(src, pmlModel.createResource(Ontology.PMLP.Dataset));

		Property prop;

		// Relate source to fragment
		prop = pmlModel.createProperty(Ontology.PMLP.hasSource);
		source.addProperty(prop, frag);
		
		// Relate row/col information
		prop = pmlModel.createProperty(Ontology.PMLP.hasFromCol);
		frag.addLiteral(prop, col);
		prop = pmlModel.createProperty(Ontology.PMLP.hasToCol);
		frag.addLiteral(prop, col);
		prop = pmlModel.createProperty(Ontology.PMLP.hasFromRow);
		frag.addLiteral(prop, row);
		prop = pmlModel.createProperty(Ontology.PMLP.hasToRow);
		frag.addLiteral(prop, row);
		
		// Relate fragment to document
		prop = pmlModel.createProperty(Ontology.PMLP.hasDocument);
		frag.addProperty(prop, document);
		
		// Relate document to publisher
		prop = pmlModel.createProperty(Ontology.PMLP.hasPublisher);
		document.addProperty(prop, epa);

		return source;
	}

	
	@Override
	public String toString() {
		String result = "";
		Calendar cal = str2Calendar(date);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		result += "<FacilityMeasurement>\n";		
		result += "<hasDate rdf:datatype=\"&xsd;dateTime\">"+df.format(cal.getTime())+"</hasDate>\n";
        result += "<hasUnit>"+unit+"</hasUnit>\n";
        result += "<hasMeasurement>"+value+"</hasMeasurement>\n";
        result += "<hasElement>\n";
        result += "<Element rdf:about=\"#"+elementName+"\"/>\n";
        result += "</hasElement>\n";
        result += "<hasTestNumber rdf:datatype=\"&xsd;nonNegativeInteger\">"+testNumber+"</hasTestNumber>\n";
		result += "</FacilityMeasurement>\n";
		return result;
	}

	int unitCol(int testNumber){
		int col=0;
		switch (testNumber){
		case 1: col = 21; break;
		case 2: col = 32; break;
		case 3: col = 43; break;
		case 4: col = 54; break;
		case 5: col = 65; break;
		default: System.err.println("unitCol, unkown testNumber"); break;			
		}
		
		return col;		
	}
	
	int valueCol(int testNumber){
		int col=0;
		switch (testNumber){
		case 1: col = 20; break;
		case 2: col = 31; break;
		case 3: col = 42; break;
		case 4: col = 53; break;
		case 5: col = 64; break;
		default: System.err.println("valueCol, unkown testNumber"); break;			
		}
		
		return col;		
	}
	
	@SuppressWarnings("unused")
	public Individual asIndividual(OntModel owlModel, Model pmlModel) {
		int col = valueCol(testNumber);
		SimpleDateFormat srcFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		Individual m = owlModel.createIndividual(Ontology.EPA.NS+"epa-measure-"+id, Ontology.FacilityMeasurement(owlModel));
		//m.addProperty(RDF.type, owlModel.createClass(Ontology.EPA.NS+"EPA-"+elementName+"-test-"+testNumber));
		OntProperty prop;
		
		Resource info;
		Property hasUsage = pmlModel.createProperty(Ontology.PMLP.hasReferenceSourceUsage);
		
		// Time
		Date theDate=null;
		try {
			theDate = srcFormat.parse(date+"120000");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		prop = Ontology.inXSDDateTime(owlModel);
		m.addLiteral(prop, df.format(theDate));
		// PML
//		info = pmlModel.createResource(Ontology.Information(pmlModel));
//		info.addProperty(RDF.subject, m);
//		info.addProperty(RDF.predicate, prop);
//		info.addLiteral(RDF.object, df.format(theDate));
//		info.addProperty(hasUsage, rowColRef(18, pmlModel));
		
		// Test Number
		prop = Ontology.hasTestNumber(owlModel);
		m.addLiteral(prop, testNumber);
		// PML
//		info = pmlModel.createResource(Ontology.Information(pmlModel));
//		info.addProperty(RDF.subject, m);
//		info.addProperty(RDF.predicate, prop);
//		info.addLiteral(RDF.object, testNumber);
//		info.addProperty(hasUsage, rowColRef(col, pmlModel));
		
		// Unit
		prop = Ontology.hasUnit(owlModel);
		m.addLiteral(prop, unit);
		// PML
//		info = pmlModel.createResource(Ontology.Information(pmlModel));
//		info.addProperty(RDF.subject, m);
//		info.addProperty(RDF.predicate, prop);
//		info.addLiteral(RDF.object, unit);
//		info.addProperty(hasUsage, rowColRef(col+1, pmlModel));
		
		// Value
		prop = Ontology.hasValue(owlModel);
		m.addLiteral(prop, Double.parseDouble(value));
		// PML
//		info = pmlModel.createResource(Ontology.Information(pmlModel));
//		info.addProperty(RDF.subject, m);
//		info.addProperty(RDF.predicate, prop);
//		info.addLiteral(RDF.object, value);
//		info.addProperty(hasUsage, rowColRef(col, pmlModel));
		
		// Element
		prop = Ontology.hasElement(owlModel);
		Individual elem = owlModel.createIndividual(Ontology.EPA.NS+elementName, Ontology.Element(owlModel));
		m.addProperty(prop, elem);
		// PML
//		info = pmlModel.createResource(Ontology.Information(pmlModel));
//		info.addProperty(RDF.subject, m);
//		info.addProperty(RDF.predicate, prop);
//		info.addLiteral(RDF.object, elem);
//		info.addProperty(hasUsage, rowColRef(14, pmlModel));
		
		return m;
	}
	

}
