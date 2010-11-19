package edu.rpi.tw.eScience.WaterQualityPortal.epa;

import java.io.BufferedWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

import edu.rpi.tw.eScience.WaterQualityPortal.model.Ontology;

/*
 * Measurement Constraint is extracted from the CSV file from the EPA site
 */
public class MeasurementConstraint {
	int id;
	String elementName;
	int testNumber;
	//0: <, 1: <=, 2: ==, 3: >=, 4: >
	int cmpType;
	String cmpValue;
	String cmpUnit;
	String src;
	String postContent;
	int row;
	
	public MeasurementConstraint(int id, String curElementName, int curTestNumber, 
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
	
	
	public void setSourceDocument(String src, String postContent, int row) {
		this.src = src;
		this.postContent = postContent;
		this.row = row;
	}
	
	String asURI() {
		return Ontology.EPA.NS+elementName+"Constraint";
	}
	
	int cmpOpCol(int testNumber){
		int col=0;
		switch (testNumber){
		case 1: col = 22; break;
		case 2: col = 33; break;
		case 3: col = 44; break;
		case 4: col = 55; break;
		case 5: col = 66; break;
		default: System.err.println("valueCol, unkown testNumber"); break;			
		}
		
		return col;		
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
		result += "<xsd:"+strCmp+" rdf:datatype=\"http://www.w3.org/2001/XMLSchema#float\">"+
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
	
	public Individual asIndividual(OntModel owlModel, Model pmlModel) {
		int col = cmpOpCol(testNumber);
		Individual m = owlModel.createIndividual(Ontology.EPA.NS+"measure-"+id, Ontology.MeasurementConstraint(owlModel));
		OntProperty prop;
		
		Resource info;
		Property hasUsage = pmlModel.createProperty(Ontology.PMLP.hasReferenceSourceUsage);
		
		// Test Number
		prop = Ontology.hasTestNumber(owlModel);
		m.addLiteral(prop, testNumber);
		// PML
		info = pmlModel.createResource();
		info.addProperty(RDF.type, pmlModel.createResource(Ontology.PMLP.Information));
		info.addProperty(RDF.subject, m);
		info.addProperty(RDF.predicate, prop);
		info.addLiteral(RDF.object, testNumber);
		info.addProperty(hasUsage, rowColRef(col, pmlModel));
		
		// Unit
		prop = Ontology.hasUnit(owlModel);
		m.addLiteral(prop, cmpUnit);
		// PML
		info = pmlModel.createResource();
		info.addProperty(RDF.type, pmlModel.createResource(Ontology.PMLP.Information));
		info.addProperty(RDF.subject, m);
		info.addProperty(RDF.predicate, prop);
		info.addLiteral(RDF.object, cmpUnit);
		info.addProperty(hasUsage, rowColRef(col+2, pmlModel));
		
		// Value
		prop = Ontology.hasValue(owlModel);
		m.addLiteral(prop, cmpValue);
		// PML
		info = pmlModel.createResource();
		info.addProperty(RDF.type, pmlModel.createResource(Ontology.PMLP.Information));
		info.addProperty(RDF.subject, m);
		info.addProperty(RDF.predicate, prop);
		info.addLiteral(RDF.object, cmpValue);
		info.addProperty(hasUsage, rowColRef(col+1, pmlModel));

		// CMP Type???
		prop = Ontology.hasCmpType(owlModel);
		m.addLiteral(prop, cmpType);
		// PML
		info = pmlModel.createResource();
		info.addProperty(RDF.type, pmlModel.createResource(Ontology.PMLP.Information));
		info.addProperty(RDF.subject, m);
		info.addProperty(RDF.predicate, prop);
		info.addLiteral(RDF.object, cmpType);
		info.addProperty(hasUsage, rowColRef(col, pmlModel));
		
		// Element
		prop = Ontology.hasElement(owlModel);
		Individual elem = owlModel.createIndividual(asURI(), Ontology.Element(owlModel));
		m.addProperty(prop, elementName);
		// PML
		info = pmlModel.createResource();
		info.addProperty(RDF.type, pmlModel.createResource(Ontology.PMLP.Information));
		info.addProperty(RDF.subject, m);
		info.addProperty(RDF.predicate, prop);
		info.addLiteral(RDF.object, elem);
		info.addProperty(hasUsage, rowColRef(14, pmlModel));

		return m;
	}

}
