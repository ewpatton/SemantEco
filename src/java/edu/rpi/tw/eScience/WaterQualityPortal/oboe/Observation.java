package edu.rpi.tw.eScience.WaterQualityPortal.oboe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.Model;

import edu.rpi.tw.eScience.WaterQualityPortal.epa.FacilityMeasurement;
import edu.rpi.tw.eScience.WaterQualityPortal.model.Ontology;

public class Observation {
	int id;
	private String entityUri;
	private ArrayList<Measurement> measurements;
	//only support "has one temporal context and one spatial context"
	private Observation temporalContext;
	private Observation spatialContext;	
	
	//curContext can be null, means the Observation has no Context 
	public Observation(int curId, String curEntity, Observation curTemporal,
			Observation curSpatial){
		id = curId;
		entityUri=curEntity;
		measurements = new ArrayList<Measurement>();
		temporalContext = curTemporal;	
		spatialContext = curSpatial;		
	}
	
	public void addMeasurment(Measurement curMeasurement){
		measurements.add(curMeasurement);
	}
	
	public Individual asIndividual(OntModel owlModel, Model pmlModel) {		
		Individual obs = owlModel.createIndividual(OBOEOntology.OBOE.CORE.NS+"Observation"+id, OBOEOntology.Observation(owlModel));
		//for adding properties
		OntProperty prop;
		
		// ofEntity
		prop = OBOEOntology.ofEntity(owlModel);
		Individual entity = owlModel.createIndividual(entityUri, OBOEOntology.Entity(owlModel));
		obs.addProperty(prop, entity);
		
		//hasMeasurement
		if(measurements != null) {
			prop = OBOEOntology.hasMeasurement(owlModel);
			for(Measurement curM : measurements) {
				Individual item = curM.asIndividual(owlModel, pmlModel);
				obs.addProperty(prop, item);
			}
		}
		
		//hasContext - temporalContext
		if(temporalContext != null) {
			prop = OBOEOntology.hasContext(owlModel);
			Individual curContext = temporalContext.asIndividual(owlModel, pmlModel);
			obs.addProperty(prop, curContext);
		}
		
		//hasContext - spatialContext
		if(spatialContext != null) {
			prop = OBOEOntology.hasContext(owlModel);
			Individual curContext = spatialContext.asIndividual(owlModel, pmlModel);
			obs.addProperty(prop, curContext);
		}				
		
		return obs;			
	}
	
	/*
	public String toString() {
		String result = "";
		result += "<Observation>\n";
        result += "<ofEntity>\n";
        result += "<Entity rdf:about="+entityUri+"\"/>\n";
        result += "</ofEntity>\n";
        result += "<hasMeasurement>"+measurement+"</hasMeasurement>\n";
        result += "<hasContext>"+context+"</hasContext>\n";
   		result += "</Observation>\n";
		return result;
	}*/

}
