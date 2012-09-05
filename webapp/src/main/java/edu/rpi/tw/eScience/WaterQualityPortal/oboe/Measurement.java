package edu.rpi.tw.eScience.WaterQualityPortal.oboe;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;

public class Measurement {
	int id;
	protected String characteristicUri;
	protected String value;
	protected String standardUri;
	
	public Measurement(int curId, String curCharUri, String curValue, 
			String curstandardUri){
		id = curId;
		characteristicUri = curCharUri;
		value = curValue;
		standardUri = curstandardUri;		
	}
	

	public Individual asIndividual(OntModel owlModel, Model pmlModel) {
		return null;
	}

	/*
	public String toString() {
		String result = "";
		result += "<Measurement>\n";
        result += "<ofCharacteristic>\n";
        result += "<Characteristic rdf:about="+characteristicUri+"\"/>\n";
        result += "</ofCharacteristic>\n";
        result += "<hasValue>"+value+"</hasValue>\n";
        result += "<usesStandard>"+standardUri+"</usesStandard>\n";
   		result += "</Measurement>\n";
		return result;
	}
	*/

}
