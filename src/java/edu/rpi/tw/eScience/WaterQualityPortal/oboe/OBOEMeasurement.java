package edu.rpi.tw.eScience.WaterQualityPortal.oboe;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.Model;

public class OBOEMeasurement {
	int id;
	protected String characteristicUri;
	protected String standardUri;
	protected String valueUri;
	protected String value;
	protected int valueType;

	
	public OBOEMeasurement(int curId, String curCharUri, String curstandardUri,
			String curValueUri, String curValue, int curValueType){
		id = curId;
		characteristicUri = curCharUri;
		standardUri = curstandardUri;
		valueUri=curValueUri;
		value = curValue;
		valueType = curValueType;
	}
	
	private Individual createEntity(OntModel owlModel){
		Individual entity = null;
		switch(valueType){
		case 0: 
			entity = owlModel.createIndividual(valueUri, OBOEOntology.Entity(owlModel));
			break;
		case 1:
			entity = owlModel.createIndividual(valueUri, OBOEOntology.Decimal(owlModel));
			break;
		case 2:
			entity = owlModel.createIndividual(valueUri, OBOEOntology.String(owlModel));
			break;
		default:
			System.err.println("Unkown valueType: "+valueType);				
		}		
		return entity;
	}
	
	public Individual asIndividual(OntModel owlModel, Model pmlModel) {
		
		Individual m = owlModel.createIndividual(OBOEOntology.OBOE.CORE.NS+"Measurement"+id, OBOEOntology.Measurement(owlModel));
		//for adding properties
		OntProperty prop;
		
		// hasValue and the value is an object		
		prop = OBOEOntology.hasValue(owlModel);
		Individual entity = createEntity(owlModel);
		m.addProperty(prop, entity);
		
		//the data value is pointed by the data type property hasCode
		prop = OBOEOntology.hasCode(owlModel);
		if(valueType==1)//for decimal
			entity.addLiteral(prop, Double.parseDouble(value));
		else
			entity.addLiteral(prop, value);
		
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
