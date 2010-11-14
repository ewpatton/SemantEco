package edu.rpi.tw.eScience.WaterQualityPortal.usgs;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;

import edu.rpi.tw.eScience.WaterQualityPortal.model.Ontology;

public class Measurement {
	String ID;
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
	public Measurement(String identification, Date Startdate, String StartTime, String chemicalname, double value_measurement, String unit_measurement) {
    	this.ID = identification; 
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

	public Individual asIndividual(OntModel owlModel, Model pmlModel) {
		Individual m = owlModel.createIndividual(Ontology.WaterMeasurement(owlModel));
		
		return m;
	}
}
