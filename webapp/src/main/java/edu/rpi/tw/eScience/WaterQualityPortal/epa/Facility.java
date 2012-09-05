package edu.rpi.tw.eScience.WaterQualityPortal.epa;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

import edu.rpi.tw.eScience.WaterQualityPortal.model.Ontology;

public class Facility {
	String ID;
	String name;
	String addressLine1;
	String addressLine2;
	double lat;
	double lng;
	int numInspection;
	int numQtrNC;
	int numEE;
	ArrayList<String> qtrDurList=null;
	ArrayList<String> NCBoolList=null;//Non-compliance in Quarter	
	String OCVLink=null;
	ArrayList<FacilityMeasurement> coliformMeasurements = null;
	//ArrayList<MeasurementConstraint> coliformConstraints = null;
	String src=null;
	String postContent=null;
	Integer row;
	static String locationSrc =  "http://maps.googleapis.com/maps/api/geocode";

	
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
		coliformMeasurements = null;
		//coliformConstraints = null;
	}
	
	public void setQtrDurList(ArrayList<String> newQtrDurList){
		qtrDurList = newQtrDurList;		
	}
	
	public void setNCBoolList(ArrayList<String> newNCBoolList){
		NCBoolList = newNCBoolList;		
	}
	
	public void setOCVLink(String newOCVLink){
		OCVLink = newOCVLink;		
	}
	
	public void setcoliformMeasurements(ArrayList<FacilityMeasurement> newColiformMeasurements){
		coliformMeasurements = newColiformMeasurements;
	}
	
	/*
	public void setColiformConstraints(ArrayList<MeasurementConstraint> newColiformConstraints) {
		coliformConstraints = newColiformConstraints;		
	}
	*/
	
	
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
			out.write("OCVLink: "+OCVLink+"\n");			
			//printMeasurementConstraintArrayList(out, coliformConstraints);
			if(coliformMeasurements!=null)
				printFacilityMeasurmentArrayList(out, coliformMeasurements);
			
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
		System.out.println("OCVLink: "+OCVLink);
		
	}
	
	public void printMeasurementConstraintArrayList(BufferedWriter out, ArrayList<MeasurementConstraint> facCons){
		MeasurementConstraint curMC=null;
		System.out.println("Measurement Constraint ArrayList");
		Iterator<MeasurementConstraint> itr = facCons.iterator();  
		while (itr.hasNext()) {  
			curMC = (MeasurementConstraint)itr.next();
			curMC.printToFile(out);        	
		} 
	}
	
	public void printFacilityMeasurmentArrayList(BufferedWriter out, ArrayList<FacilityMeasurement> facAL){
		FacilityMeasurement curFM=null;
		//System.out.println("Facility Measurement ArrayList");
		Iterator<FacilityMeasurement> itr = facAL.iterator();  
		while (itr.hasNext()) {  
			curFM = (FacilityMeasurement)itr.next();
			curFM.printToFile(out);        	
		} 
	}
	
	public void setSourceDocument(String src, String postContent, int row) {
		this.src = src;
		this.postContent = postContent;
		this.row = row;
	}
	
	public Resource agentRef(Model pmlModel) {
		Resource epa = pmlModel.createResource(Ontology.EPA.NS+"EPA",pmlModel.createResource(Ontology.PMLP.Organization));
		Resource source = pmlModel.createResource(pmlModel.createResource(Ontology.PMLP.SourceUsage));
		
		Property prop;
		
		prop = pmlModel.createProperty(Ontology.PMLP.hasSource);
		source.addProperty(prop, epa);
		
		return source;
	}
	
	public Resource geoRef(Model pmlModel) {
		Resource google = pmlModel.createResource(Ontology.EPA.NS+"Google",pmlModel.createResource(Ontology.PMLP.Organization));
		Resource source = pmlModel.createResource(pmlModel.createResource(Ontology.PMLP.SourceUsage));
		Resource frag = pmlModel.createResource(pmlModel.createResource(Ontology.PMLP.DocumentFragmentByRowCol));
		Resource document = pmlModel.createResource(locationSrc, pmlModel.createResource(Ontology.PMLP.Dataset));

		Property prop;

		// Relate source to fragment
		prop = pmlModel.createProperty(Ontology.PMLP.hasSource);
		source.addProperty(prop, frag);
		
		// Relate fragment to document
		prop = pmlModel.createProperty(Ontology.PMLP.hasDocument);
		frag.addProperty(prop, document);
		
		// Relate document to publisher
		prop = pmlModel.createProperty(Ontology.PMLP.hasPublisher);
		document.addProperty(prop, google);

		return source;
	}
	
	public Resource rowColRef(int col, Model pmlModel) {
		Resource epa = pmlModel.createResource(Ontology.EPA.NS+"EPA",pmlModel.createResource(Ontology.PMLP.Organization));
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
		result += "<Facility rdf:ID=\""+ID+"\">\n";
		result += "<hasName>"+name+"</hasName>\n";
		result += "<hasAddress>"+addressLine1+" "+addressLine2+"</hasAddress>\n";		
		result += "<hasLocation>\n";
		result += "<geo:Point>\n";
		result += "<geo:lat rdf:datatype=\"&xsd;float\">"+lat+"</geo:lat>\n";
		result += "<geo:long rdf:datatype=\"&xsd;float\">"+lng+"</geo:long>\n";
		result += "</geo:Point>\n";
		result += "</hasLocation>\n";
		//result += "<hasNumInspection rdf:datatype=\"&xsd;float\">"+numInspection+"</hasNumInspection>\n";
		//result += "<hasNumQtrNC rdf:datatype=\"&xsd;float\">"+numQtrNC+"</hasNumQtrNC>\n";
		//result += "<hasNumEE rdf:datatype=\"&xsd;float\">"+numEE+"</hasNumEE>\n";
		if(coliformMeasurements!=null){
			for(FacilityMeasurement m : coliformMeasurements) {
				result += "<hasMeasurement>\n";
		    	result += m.toString();
		        result += "</hasMeasurement>\n";
		    }			
		}
		
	    
		result += "</Facility>\n";
		return result;
	}

	
	@SuppressWarnings("unused")
	public Individual asIndividual(OntModel owlModel, Model pmlModel) {
		String uri = Ontology.EPA.NS+"facility-"+ID;
		OntClass Facility = Ontology.Facility(owlModel);
		Individual facility = owlModel.createIndividual(uri, Facility);
		facility.addOntClass(Ontology.Point(owlModel));
		
		Resource info;
		Property hasUsage = pmlModel.createProperty(Ontology.PMLP.hasReferenceSourceUsage);
		OntProperty prop;

		// ID
		//prop = Ontology.hasID(owlModel);
		//facility.addLiteral(prop, ID);
		// PML
		//info = pmlModel.createResource(Ontology.Information(pmlModel));
		//info.addProperty(RDF.subject, facility);
		//info.addProperty(RDF.predicate, prop);
		//info.addLiteral(RDF.object, ID);
		//info.addProperty(hasUsage, rowColRef(2, pmlModel));
		
		// Name
		prop = Ontology.label(owlModel);
		facility.addLiteral(prop, name);
		// PML
//		info = pmlModel.createResource(Ontology.Information(pmlModel));
//		info.addProperty(RDF.subject, facility);
//		info.addProperty(RDF.predicate, prop);
//		info.addLiteral(RDF.object, name);
//		info.addProperty(hasUsage, rowColRef(1, pmlModel));
		
		// Address
		String address = addressLine1+" "+addressLine2;
		prop = Ontology.hasAddress(owlModel);
		facility.addLiteral(prop, address);
		// PML
//		info = pmlModel.createResource(Ontology.Information(pmlModel));
//		info.addProperty(RDF.subject, facility);
//		info.addProperty(RDF.predicate, prop);
//		info.addLiteral(RDF.object, address);
//		info.addProperty(hasUsage, rowColRef(1, pmlModel));
		
		// Latitude
		prop = Ontology.lat(owlModel);
		facility.addLiteral(prop, lat);
		// PML
//		info = pmlModel.createResource(Ontology.Information(pmlModel));
//		info.addProperty(RDF.subject, facility);
//		info.addProperty(RDF.predicate, prop);
//		info.addLiteral(RDF.object, lat);
//		info.addProperty(hasUsage, geoRef(pmlModel));
		
		// Longitude
		prop = Ontology.lng(owlModel);
		facility.addLiteral(prop, lng);
		// PML
//		info = pmlModel.createResource(Ontology.Information(pmlModel));
//		info.addProperty(RDF.subject, facility);
//		info.addProperty(RDF.predicate, prop);
//		info.addLiteral(RDF.object, lng);
//		info.addProperty(hasUsage, geoRef(pmlModel));
		
		prop = Ontology.hasMeasurement(owlModel);
		if(coliformMeasurements != null) {
		for(FacilityMeasurement m : coliformMeasurements) {
			Individual item = m.asIndividual(owlModel, pmlModel);
			facility.addProperty(prop, item);
			// PML
//			info = pmlModel.createResource(Ontology.Information(pmlModel));
//			info.addProperty(RDF.subject, facility);
//			info.addProperty(RDF.predicate, prop);
//			info.addProperty(RDF.object, item);
//			info.addProperty(hasUsage, agentRef(pmlModel));
		}
		}
		
		return facility;
	}



}
