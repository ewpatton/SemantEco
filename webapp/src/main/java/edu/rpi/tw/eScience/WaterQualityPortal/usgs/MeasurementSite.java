package edu.rpi.tw.eScience.WaterQualityPortal.usgs;


import java.util.*;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

import edu.rpi.tw.eScience.WaterQualityPortal.model.Ontology;

public class MeasurementSite {
	double lat,longitude;
	String loc_id;
	String country_code;
	Integer state_code;
	Integer county_code;
	String label;
	String src;
	Integer row;
	ArrayList<Measurement> data = new ArrayList<Measurement>(); 
	
	public void setSourceDocument(String src, int row) {
		this.src = src;
		this.row = row;
	}
	
	public void setID(String ID) {
		loc_id = ID;
	}
	
	public void addData(Measurement x) {
		data.add(x);
	}
	
	public String getID() {
		return loc_id;
	}
	
	@Override
	public String toString() {
		String result = "";
		result += "<TestingSite rdf:ID=\""+loc_id+"\">\n";
		result += "<hasCountryCode>"+country_code+"</hasCountryCode>\n";
		result += "<hasStateCode>"+state_code+"</hasStateCode>\n";
		result += "<hasCountyCode>"+county_code+"</hasCountyCode>\n";
		result += "<hasLocation>\n";
		result += "<geo:Point>\n";
		result += "<geo:lat rdf:datatype=\"&xsd;float\">"+lat+"</geo:lat>\n";
		result += "<geo:long rdf:datatype=\"&xsd;float\">"+longitude+"</geo:long>\n";
		result += "</geo:Point>\n";
		result += "</hasLocation>\n";
	    for(Measurement m : data) {
			result += "<hasMeasurement>\n";
	    	result += m.toString();
	        result += "</hasMeasurement>\n";
	    }
		result += "</TestingSite>\n";
		return result;
	}
	
	public Resource agentRef(Model pmlModel) {
		Resource usgs = pmlModel.createResource(Ontology.EPA.NS+"USGS",pmlModel.createResource(Ontology.PMLP.Organization));
		Resource source = pmlModel.createResource(pmlModel.createResource(Ontology.PMLP.SourceUsage));
		
		Property prop;
		
		prop = pmlModel.createProperty(Ontology.PMLP.hasSource);
		source.addProperty(prop, usgs);
		
		return source;
	}
	
	public Resource rowColRef(int col, Model pmlModel) {
		Resource usgs = pmlModel.createResource(Ontology.EPA.NS+"USGS",pmlModel.createResource(Ontology.PMLP.Organization));
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
	
	@SuppressWarnings("unused")
	public Individual asIndividual(OntModel owlModel, Model pmlModel) {
		String uri = Ontology.EPA.NS+"site-"+loc_id;
		OntClass MeasurementSite = Ontology.MeasurementSite(owlModel);
		Individual site = owlModel.createIndividual(uri, MeasurementSite);
		Resource pmlsite = pmlModel.createResource(uri);
		site.addOntClass(Ontology.Point(owlModel));
		
		Resource info;
		Property hasUsage = pmlModel.createProperty(Ontology.PMLP.hasReferenceSourceUsage);

		// Country code
		OntProperty prop = Ontology.hasCountryCode(owlModel);
		site.addLiteral(prop, country_code);
		// PML
//		info = pmlModel.createResource(Ontology.Information(pmlModel));
//		info.addProperty(RDF.subject, pmlsite);
//		info.addProperty(RDF.predicate, prop);
//		info.addLiteral(RDF.object, country_code);
//		info.addProperty(hasUsage, rowColRef(24, pmlModel));
		
		// State code
		prop = Ontology.hasStateCode(owlModel);
		site.addLiteral(prop, state_code);
		// PML
//		info = pmlModel.createResource(Ontology.Information(pmlModel));
//		info.addProperty(RDF.subject, pmlsite);
//		info.addProperty(RDF.predicate, prop);
//		info.addLiteral(RDF.object, state_code);
//		info.addProperty(hasUsage, rowColRef(25, pmlModel));
		
		// County code
		prop = Ontology.hasCountyCode(owlModel);
		site.addLiteral(prop, county_code);
		// PML
//		info = pmlModel.createResource(Ontology.Information(pmlModel));
//		info.addProperty(RDF.subject, pmlsite);
//		info.addProperty(RDF.predicate, prop);
//		info.addLiteral(RDF.object, county_code);
//		info.addProperty(hasUsage, rowColRef(26, pmlModel));
		
		// Label
		prop = Ontology.label(owlModel);
		site.addLiteral(prop, label);
		// PML
//		info = pmlModel.createResource(Ontology.Information(pmlModel));
//		info.addProperty(RDF.subject, pmlsite);
//		info.addProperty(RDF.predicate, prop);
//		info.addLiteral(RDF.object, label);
//		info.addProperty(hasUsage, rowColRef(3, pmlModel));
		
		// Latitude
		prop = Ontology.lat(owlModel);
		site.addLiteral(prop, lat);
		// PML
//		info = pmlModel.createResource(Ontology.Information(pmlModel));
//		info.addProperty(RDF.subject, pmlsite);
//		info.addProperty(RDF.predicate, prop);
//		info.addLiteral(RDF.object, lat);
//		info.addProperty(hasUsage, rowColRef(11, pmlModel));
		
		// Longitude
		prop = Ontology.lng(owlModel);
		site.addLiteral(prop, longitude);
		// PML
//		info = pmlModel.createResource(Ontology.Information(pmlModel));
//		info.addProperty(RDF.subject, pmlsite);
//		info.addProperty(RDF.predicate, prop);
//		info.addLiteral(RDF.object, longitude);
//		info.addProperty(hasUsage, rowColRef(12, pmlModel));
		
		prop = Ontology.hasMeasurement(owlModel);
		for(Measurement m : data) {
			Individual item = m.asIndividual(owlModel, pmlModel);
			site.addProperty(prop, item);
			// PML
//			info = pmlModel.createResource(Ontology.Information(pmlModel));
//			info.addProperty(RDF.subject, pmlsite);
//			info.addProperty(RDF.predicate, prop);
//			info.addProperty(RDF.object, item);
//			info.addProperty(hasUsage, agentRef(pmlModel));
		}
		return site;
	}
	
	public MeasurementSite(String locationID, String label, double latitude, double longitude, String countrycode, Integer statecode, Integer countycode)  {
	    this.loc_id = locationID;
	    this.label = label;
	    this.lat = latitude;
	    this.longitude = longitude;
	    this.country_code = countrycode;
	    this.state_code = statecode;
	    this.county_code = countycode;
	}
}