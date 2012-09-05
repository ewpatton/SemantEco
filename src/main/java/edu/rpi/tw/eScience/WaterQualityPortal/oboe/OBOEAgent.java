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

import edu.rpi.tw.eScience.WaterQualityPortal.util.NameUtil;

public class OBOEAgent {
	static int MIdCount = 0;
	static int ObsIdCount = 0;
	static String pollutionOwl = "http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#";
	static String AmountOfSubstanceConcentration="http://ecoinformatics.org/oboe/oboe.1.0/oboe-characteristics.owl#AmountOfSubstanceConcentration";
	static String MilligramPerLiter = "http://ecoinformatics.org/oboe/oboe.1.0/oboe-standards.owl#MilligramPerLiter";
	//static String dcDate="http://purl.org/dc/terms/#date";
	static String OBOE_POL="http://escience.rpi.edu/ontology/semanteco/2/0/oboe-pollution.owl#";
	static String oboeDate=OBOE_POL+"Date";
	//date format sample: 1963-08-26
	static String dateFormat=OBOE_POL+"YYYY-MM-DD";
	//
	//static String timeInstant="http://ecoinformatics.org/oboe/oboe.1.0/oboe-temporal.owl#TimeInstant";
	static String temporalPointEntity=OBOE_POL+"TemporalPointEntity";
	//spatial
	static String spatialLocationEntity=OBOE_POL+"SpatialLocationEntity";
	static String strongEngityName=OBOE_POL+"StrongEntityName";
	static String USGSSiteIdentifyStandard=OBOE_POL+"USGSSiteIdentifyStandard";
	//water:WaterSite-USGS-413756071363901
	static String USGSSitePrefix="http://escience.rpi.edu/ontology/semanteco/2/0/water.owl#WaterSite-";
	
	private List<OBOEMeasurement> measurements = null;
	private List<OBOEMeasurement> timeMeasurements = null;
	private List<OBOEMeasurement> locationMeasurements = null;
	private List<Observation> observations = null;
	
	public void readCSV(String inputfileName){
		CsvReader reader = null;
		int recordNum = 0;
		measurements = new ArrayList<OBOEMeasurement>();
		timeMeasurements = new ArrayList<OBOEMeasurement>();
		locationMeasurements = new ArrayList<OBOEMeasurement>();
		
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
				OBOEMeasurement curTime = new OBOEMeasurement(MIdCount++, oboeDate, 
						dateFormat, OBOE_POL+curDate, curDate, 2);
				timeMeasurements.add(curTime);	
				//end of processing the date
				String siteId= reader.get("MonitoringLocationIdentifier");
				OBOEMeasurement curSite = new OBOEMeasurement(MIdCount++, strongEngityName, 
						USGSSiteIdentifyStandard, USGSSitePrefix+siteId, siteId, 0);
				locationMeasurements.add(curSite);				
				//start to process a measurement
				String unit = reader.get("ResultMeasure/MeasureUnitCode");			
				String value = reader.get("ResultMeasureValue");			
				//only proceed when unit are mg/l or ug/l
				if(unit.compareToIgnoreCase("mg/l")!=0 && unit.compareToIgnoreCase("ug/l")!=0)
					continue;	
				if(unit.compareToIgnoreCase("ug/l")==0){
					unit="mg/l";
					Double conValue = Double.parseDouble(value)/1000;
					value=conValue.toString();
				}
				OBOEMeasurement curM = new OBOEMeasurement(MIdCount++, AmountOfSubstanceConcentration, 
						MilligramPerLiter, OBOE_POL+value, value, 1);
				measurements.add(curM);
				//end of measurement

				//Create an Observation for the time measurement				
				Observation curTimeObs= new Observation(ObsIdCount++, temporalPointEntity, null, null);
				curTimeObs.addMeasurment(curTime);
				//Create an Observation for the location measurement				
				Observation curLocationObs= new Observation(ObsIdCount++, spatialLocationEntity, null, null);
				curLocationObs.addMeasurment(curSite);				
				//Create an Observation for the measured value
				//CharacteristicName
				String charName = NameUtil.processElementName(reader.get("CharacteristicName"));
				Observation curObs= new Observation(ObsIdCount++, charName2Uri(charName), curTimeObs, curLocationObs);
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
		for(OBOEMeasurement m : measurements) {
			m.asIndividual(owlModel, pmlModel);
		}
		for(OBOEMeasurement m : timeMeasurements) {
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
	
	protected void genProv(String inputfileName, String outputfileName) {
		System.out.println("In genProv");
		BufferedWriter bufferedWriter=null;

		try{
			bufferedWriter = new BufferedWriter(new FileWriter(outputfileName));
			bufferedWriter.write("Hello!");

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
	}
	
	public static void main(String[] args) {
		OBOEAgent agent = new OBOEAgent();
		if (args.length <= 2) {
			System.out.println("Usage: ./OBOEConverter inputFile -w outputFile");
			System.exit(0);
		}
		
		//String inputfileName = "data/oboe/US-44-003-result.sample.csv";
		//String outputfileName = "data/oboe/US-44-003-result.sample.ttl";
		
		boolean provCall = false;
		String inputfileName = args[0];
		String outputfileName = null;
		String provfileName = null;
		
		for(int i=0;i<args.length-1;i++){
			System.out.println("arg "+i+": "+args[i]);
			if(args[i].compareTo("-w")==0){				
				outputfileName = args[i+1];
			}
			if(args[i].compareTo("-prov")==0){	
				provCall=true;
				provfileName = args[i+1];
			}			
		}
		
		if(!provCall){
			if(outputfileName==null){
				System.out.println("Usage: inputFile -w outputFile");
				System.exit(0);
			}	
			agent.loadData(inputfileName, outputfileName);	
		}
		else{
			if(provfileName==null){
				System.out.println("Usage: inputFile -prov provfileName");
				System.exit(0);
			}
			agent.genProv(inputfileName, provfileName);			
		}

		
	
	}
	

}
