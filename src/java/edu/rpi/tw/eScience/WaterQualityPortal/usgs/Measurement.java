package edu.rpi.tw.eScience.WaterQualityPortal.usgs;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

import edu.rpi.tw.eScience.WaterQualityPortal.model.Ontology;

public class Measurement {
	String ID;
	int id2;
	Date date;
	String time;
	String chemical;
	double value;
	String unit;
	
	@Override
	public String toString() {
		String result = "";
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		result += "<Measurement>\n";
		result += "<hasDate rdf:datatype=\"&xsd;dateTime\">"+df.format(date)+"</hasDate>\n";
        result += "<hasUnit>"+unit+"</hasUnit>\n";
        result += "<hasMeasurement>"+value+"</hasMeasurement>\n";
        result += "<hasElement>\n";
        result += "<Element rdf:about=\"#"+chemical+"\"/>\n";
        result += "</hasElement>\n";
		result += "</Measurement>\n";
		return result;
	}
	
	@SuppressWarnings("deprecation")
	public Measurement(String identification, int id2, Date Startdate, String StartTime, String chemicalname, double value_measurement, String unit_measurement) {
    	this.ID = identification; 
    	this.id2 = id2;
    	this.date = Startdate;
    	this.time = StartTime;
    	String[] parts = time.split(":");
    	this.date.setHours(Integer.parseInt(parts[0]));
    	this.date.setMinutes(Integer.parseInt(parts[1]));
    	this.date.setSeconds(Integer.parseInt(parts[2]));
        this.chemical = chemicalname;
    	this.value = value_measurement;
    	this.unit = unit_measurement;  
    }
	
	String asURI() {
		String temp = chemical.replace(' ', '_');
		return Ontology.EPA.NS+temp;
	}
	
	String src;
	int row;
	
	public void setSourceDocument(String src, int row) {
		this.src = src;
		this.row = row;
	}
	
	public Resource rowColRef(int col, Model pmlModel) {
		Resource usgs = pmlModel.createResource(Ontology.EPA.NS+"USGS");
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
		document.addProperty(prop, usgs);

		return source;
	}

	public Individual asIndividual(OntModel owlModel, Model pmlModel) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		Individual m = owlModel.createIndividual(Ontology.EPA.NS+"usgs-measure-"+id2, Ontology.WaterMeasurement(owlModel));
		Resource pmlm = pmlModel.createResource(m.getURI());
		OntProperty prop;
		
		Resource info;
		Property hasUsage = pmlModel.createProperty(Ontology.PMLP.hasReferenceSourceUsage);
		
		// Time
		prop = Ontology.inXSDDateTime(owlModel);
		m.addLiteral(prop, df.format(date));
		// PML
		info = pmlModel.createResource(Ontology.Information(pmlModel));
		info.addProperty(RDF.subject, pmlm);
		info.addProperty(RDF.predicate, prop);
		info.addLiteral(RDF.object, df.format(date));
		info.addProperty(hasUsage, rowColRef(6, pmlModel));
		
		// Unit
		prop = Ontology.hasUnit(owlModel);
		m.addLiteral(prop, unit);
		// PML
		info = pmlModel.createResource(Ontology.Information(pmlModel));
		info.addProperty(RDF.subject, pmlm);
		info.addProperty(RDF.predicate, prop);
		info.addLiteral(RDF.object, unit);
		info.addProperty(hasUsage, rowColRef(34, pmlModel));
		
		// Value
		prop = Ontology.hasValue(owlModel);
		m.addLiteral(prop, value);
		// PML
		info = pmlModel.createResource(Ontology.Information(pmlModel));
		info.addProperty(RDF.subject, pmlm);
		info.addProperty(RDF.predicate, prop);
		info.addLiteral(RDF.object, value);
		info.addProperty(hasUsage, rowColRef(33, pmlModel));

		// Element
		prop = Ontology.hasElement(owlModel);
		Individual elem = owlModel.createIndividual(asURI(), Ontology.Element(owlModel));
		m.addProperty(prop, elem);
		// PML
		info = pmlModel.createResource(Ontology.Information(pmlModel));
		info.addProperty(RDF.subject, pmlm);
		info.addProperty(RDF.predicate, prop);
		info.addLiteral(RDF.object, pmlModel.createResource(elem.getURI()));
		info.addProperty(hasUsage, rowColRef(31, pmlModel));

		return m;
	}
}
