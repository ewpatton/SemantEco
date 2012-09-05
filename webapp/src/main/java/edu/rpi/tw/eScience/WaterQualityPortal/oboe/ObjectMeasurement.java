package edu.rpi.tw.eScience.WaterQualityPortal.oboe;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.Model;

@Deprecated
public class ObjectMeasurement extends Measurement {
	
	public ObjectMeasurement(int curId, String curCharUri, String curValue,
			String curstandardUri) {
		super(curId, curCharUri, curValue, curstandardUri);
	}

	public Individual asIndividual(OntModel owlModel, Model pmlModel) {
		
		Individual m = owlModel.createIndividual(OBOEOntology.OBOE.CORE.NS+"Measurement"+id, OBOEOntology.Measurement(owlModel));
		//for adding properties
		OntProperty prop;
		
		// hasValue and the value is an object		
		prop = OBOEOntology.hasValue(owlModel);
		Individual entity = owlModel.createIndividual(value, OBOEOntology.Entity(owlModel));
		m.addProperty(prop, entity);
		
		// ofCharacteristic
		prop = OBOEOntology.ofCharacteristic(owlModel);
		Individual charac = owlModel.createIndividual(characteristicUri, OBOEOntology.Characteristic(owlModel));
		m.addProperty(prop, charac);
		
		// usesStandard
		prop = OBOEOntology.usesStandard(owlModel);
		Individual standard = owlModel.createIndividual(standardUri, OBOEOntology.Standard(owlModel));
		m.addProperty(prop, standard);
		
		return m;
	}
}
