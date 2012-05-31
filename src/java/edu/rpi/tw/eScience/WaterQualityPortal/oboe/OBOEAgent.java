package edu.rpi.tw.eScience.WaterQualityPortal.oboe;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import org.mindswap.pellet.jena.PelletReasonerFactory;

import com.csvreader.CsvReader;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import edu.rpi.tw.eScience.WaterQualityPortal.epa.Facility;


public class OBOEAgent {
	static int MIdCount = 0;
	static int ObsIdCount = 0;
	static String pollutionOwl = "http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#";
	static String AmountOfSubstanceConcentration="http://ecoinformatics.org/oboe/oboe.1.0/oboe-characteristics.owl#AmountOfSubstanceConcentration";
	static String MilligramPerLiter = "http://ecoinformatics.org/oboe/oboe.1.0/oboe-standards.owl#MilligramPerLiter";
	//static String dcDate="http://purl.org/dc/terms/#date";
	static String oboeDate="http://ecoinformatics.org/oboe/oboe.1.0/oboe-pollution.owl#Date";
	//date format sample: 1963-08-26
	static String dateFormat="http://escience.rpi.edu/ontology/semanteco/2/0/oboe-pollution.owl#YYYY-MM-DD";
	//
	//static String timeInstant="http://ecoinformatics.org/oboe/oboe.1.0/oboe-temporal.owl#TimeInstant";
	static String temporalPointEntity="http://ecoinformatics.org/oboe/oboe.1.0/oboe-pollution.owl#TemporalPointEntity";

	
	private List<NumericMeasurement> measurements = null;
	private List<LiteralMeasurement> timeMeasurements = null;
	private List<Observation> observations = null;
	
	public void readCSV(String inputfileName){
		CsvReader reader = null;
		int recordNum = 0;
		measurements = new ArrayList<NumericMeasurement>();
		timeMeasurements = new ArrayList<LiteralMeasurement>();
		observations = new ArrayList<Observation>();
		
		try {			
			reader = new CsvReader(inputfileName);		

			reader.readHeaders();
			recordNum++;

			while (reader.readRecord())
			{			
				recordNum++;
				//start to process a date
				//eg. 1963-08-26
				String curDate = reader.get("ActivityStartDate");
				LiteralMeasurement curTime = new LiteralMeasurement(MIdCount++, oboeDate, 
						curDate, dateFormat);
				timeMeasurements.add(curTime);	
				//end of proecssing the date
				//start to process a measurement
				String unit = reader.get("ResultMeasure/MeasureUnitCode");
				//only proceed when unit is mg/l
				if(unit.compareToIgnoreCase("mg/l")!=0)
					continue;				
				String value = reader.get("ResultMeasureValue");				
				NumericMeasurement curM = new NumericMeasurement(MIdCount++, AmountOfSubstanceConcentration, 
						value, MilligramPerLiter);
				measurements.add(curM);
				//end of measurement

				//Create an Observation for the time measurement				
				Observation curTimeObs= new Observation(ObsIdCount++, temporalPointEntity, null, null);
				curTimeObs.addMeasurment(curTime);
				//Create an Observation for the measured value
				//CharacteristicName
				String charName = reader.get("CharacteristicName");
				Observation curObs= new Observation(ObsIdCount++, charName2Uri(charName), curTimeObs, null);
				curObs.addMeasurment(curM);
				//
				observations.add(curObs);
				
			}//end of while
		} catch (FileNotFoundException e) {
			System.err.println("In readCSV(), file name: " + inputfileName);
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("In readCSV(), file name: " + inputfileName);
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		}
		finally{
			reader.close();
		}			
	}
	
	private String charName2Uri(String charName){
		return pollutionOwl+charName;
	}
	
	private void loadMeasurements(OntModel owlModel, Model pmlModel){
		for(NumericMeasurement m : measurements) {
			m.asIndividual(owlModel, pmlModel);
		}
		for(LiteralMeasurement m : timeMeasurements) {
			m.asIndividual(owlModel, pmlModel);
		}
	}
	
	private void loadObservations(OntModel owlModel, Model pmlModel){
		for(Observation m : observations) {
			m.asIndividual(owlModel, pmlModel);
		}		
	}
	
	protected Model loadData(String inputfileName, String outputfileName) {
		long loadStart = System.currentTimeMillis();
		BufferedWriter bufferedWriter=null;
	
		// Load ontologies
		OntModel owlModel,pmlModel;
		owlModel = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
		pmlModel = ModelFactory.createOntologyModel();
		//rdfModel = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
		
/*		owlModel.read("http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl");
		owlModel.read("http://escience.rpi.edu/ontology/semanteco/2/0/water.owl");
		//owlModel.read("http://escience.rpi.edu/ontology/semanteco/2/0/health.owl");
		//owlModel.read("http://localhost/semantaqua/health/wildlife-healtheffect.owl");	
		owlModel.read("http://sparql.tw.rpi.edu/ontology/semanteco/2/0/wildlife-healtheffect.owl");
		//owlModel.read(regulation);
*/		
		// Load data
		readCSV(inputfileName);
		loadObservations(owlModel, pmlModel);

		//owlModel = null;
		//rdfModel = null;
		try{
			bufferedWriter = new BufferedWriter(new FileWriter(outputfileName));
			owlModel.write(bufferedWriter, "N-TRIPLE");
		}
		catch (Exception e) {
			System.err.println("In loadData, err");
			e.printStackTrace();
		}finally {
			//Close the BufferedReader and BufferedWriter			
			try {
				if (bufferedWriter != null) {
					bufferedWriter.flush();
					bufferedWriter.close();
				}
			} catch (IOException ex) {
				System.err.println("In loadData(), closing the BufferedWriter");
				ex.printStackTrace();
			}
		}
		return owlModel;
	}
	
	public static void main(String[] args) {
		OBOEAgent agent = new OBOEAgent();
		if (args.length <= 2) {
			System.out.println("Usage: ./FoiaDmrAgent inputFile outputFile");
			System.exit(0);
		}
		//String inputfileName = "data/oboe/US-44-003-result.sample.csv";
		//String outputfileName = "data/oboe/US-44-003-result.sample.rdf";
		String inputfileName = args[0];
		String outputfileName = args[1];
		
		agent.loadData(inputfileName, outputfileName);		
	}
	

}
