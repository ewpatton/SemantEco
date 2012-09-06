package edu.rpi.tw.eScience.WaterQualityPortal.ny;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import com.csvreader.CsvReader;

public class DrinkingWaterDataAgent {

	/*public class Range {
		String lowerBound="", upperBound="", lowerComparator="", upperComparator="";

	}*/
	String [] rangeNames = {"AsMaxRange", "AsMeanRange", "HAA5MaxRange", "HAA5MeanRange",
			"NitrateMaxRange", "NitrateMeanRange", "TTHMMaxRange", "TTHMMeanRange"};
	String [] contamNames = {"Arsenic", "Arsenic", "Haloacetic acid 5", "Haloacetic acid 5",
			"Nitrate", "Nitrate", "TTHM", "TTHM"};
	String []concNames = {"AsMaxConc", "AsMeanConc", "HAA5MaxConc", "HAA5MeanConc",
			"NitrateMaxConc","NitrateMeanConc","TTHMMaxConc","TTHMMeanConc"};
	String []statTypes = {"Max", "Mean"};
//AsMaxRange AsMaxConc	AsMeanRange	AsMeanConc	
//HAA5MaxRange	HAA5MaxConc	HAA5MeanRange	HAA5MeanConc	
//NitrateMaxRange	NitrateMaxConc	NitrateMeanRange	NitrateMeanConc	
//TTHMMaxRange	TTHMMaxConc	TTHMMeanRange	TTHMMeanConc
	
	private void convertRangeName(String rangeName, StringBuilder sb){
		int index=-1;
		String pre="";
		if((index=rangeName.indexOf("Range"))!=-1){
			pre=rangeName.substring(0, index);
			sb.append(", "+pre+"LowerComparator");
			sb.append(", "+pre+"LowerBound");
			sb.append(", "+pre+"UpperComparator");
			sb.append(", "+pre+"UpperBound");			
		}
	}

	private void convertRange(String range, StringBuilder sb){
		String lowerBound="", upperBound="", lowerComparator="", upperComparator="";
		range=range.trim();
		int index=-1;
		if((index=range.indexOf("to"))!=-1){
			String[] parts=range.split("to");
			lowerBound = parts[0].trim();
			lowerComparator = ">=";
			upperBound =  parts[1].trim();
			upperComparator="<=";
			if(upperBound.startsWith("<")){
				upperBound=upperBound.substring(1);
				upperComparator="<";
			}		
		}
		else if (range.startsWith("<")){
			upperBound=range.substring(1);
			upperComparator="<";			
		}
		else if ((index=range.indexOf("+"))!=-1){
			lowerBound = range.substring(0,index);
			lowerComparator = ">=";			
		}
		else if(range.equals("ND")){
			upperBound="0";
			upperComparator="<=";
		}

		sb.append(", "+lowerComparator);
		sb.append(", "+lowerBound);
		sb.append(", "+upperComparator);
		sb.append(", "+upperBound);

		/*
		try {
			bufferedWriter.write(lowerComparator+", "+lowerBound+", "+
					upperComparator+", "+upperBound);
		} catch (IOException e) {
			System.err.println("In convertRange, IOException");
		}*/
	}

	public void process(String inputfileName, String outputfileName, int serviceType)
	{
		BufferedWriter bufferedWriter = null;
		try{
			bufferedWriter = new BufferedWriter(new FileWriter(outputfileName));
			if(serviceType==0)
				getPWS(inputfileName, bufferedWriter);
			else if (serviceType==1)
				getRegulations(inputfileName, bufferedWriter);
			else
				getConc(inputfileName, bufferedWriter);
		}

		catch (Exception e) {
			System.err.println("In process, err");
			e.printStackTrace();
		}finally {
			//Close the BufferedReader and BufferedWriter			
			try {
				if (bufferedWriter != null) {
					bufferedWriter.flush();
					bufferedWriter.close();
				}
			} catch (IOException ex) {
				System.err.println("In process, closing the reader and BufferedWriter");
				ex.printStackTrace();
			}
		}
	}

	public void getPWS(String inputfileName, BufferedWriter bufferedWriter){
			CsvReader reader = null;
			int recordNum = 0;
			String principalcounty, PWSIDNumber, PWSName, systemPopulation;
			HashMap<String, String> pwsIDMap = new HashMap<String, String> ();

			try {			
				reader = new CsvReader(inputfileName);		

				reader.readHeaders();
				String[] headers=reader.getHeaders();
				int i=0;
				for(i=0;i<3;i++)
					bufferedWriter.write(headers[i]+",");
				bufferedWriter.write(headers[i]);
				bufferedWriter.write("\n");
				recordNum++;

				while (reader.readRecord())
				{			
					recordNum++;
					//System.out.println("Record " + recordNum);

					principalcounty=reader.get("principalcounty");	
					PWSIDNumber=reader.get("PWSIDNumber");	
					PWSName=reader.get("PWS_name");
					systemPopulation=reader.get("System_population");
					if(pwsIDMap.get(PWSIDNumber)==null){
						pwsIDMap.put(PWSIDNumber, PWSIDNumber);			
						//only write the 1st time seen
						bufferedWriter.write("\""+principalcounty+"\",\""+PWSIDNumber+"\",\""+
							PWSName+"\",\""+systemPopulation+"\"\n");
					}

				}//end of while

			} catch (FileNotFoundException e) {
				System.err.println("In CSVRead(), file name: " + inputfileName);
				System.err.println("Error: " + e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				System.err.println("In CSVRead(), file name: " + inputfileName);
				System.err.println("Error: " + e.getMessage());
				e.printStackTrace();
			}
			finally{
				reader.close();
			}
		}
	
	public void getRegulations(String inputfileName, BufferedWriter bufferedWriter){
		CsvReader reader = null;
		int recordNum = 0;
		HashMap<String, String> regulationsMap = new HashMap<String, String> ();
		String curRange;
		//StringBuilder sb = new StringBuilder();
		String year, county;

		try {			
			reader = new CsvReader(inputfileName);		

			reader.readHeaders();
			//String[] headers=reader.getHeaders();
			bufferedWriter.write("Contaminant, County, Year, LowerComparator, LowerBound, UpperComparator, UpperBound");
			bufferedWriter.write("\n");
			recordNum++;

			while (reader.readRecord())
			{			
				recordNum++;
				//System.out.println("Record " + recordNum);
				county=reader.get("principalcounty");
				year=reader.get("Year");
				for(int j=0;j<rangeNames.length;j++){
					curRange = reader.get(rangeNames[j]);
					StringBuilder sb = new StringBuilder();
					//sb.setLength(0);
					convertRange(curRange, sb);
					String hashKey=contamNames[j]+year+county;
					if(regulationsMap.get(hashKey)==null){
						regulationsMap.put(hashKey, contamNames[j]);			
						//only write the 1st time seen
						bufferedWriter.write(contamNames[j]+","+county+","+year+sb.toString()+"\n");
					}
				}
			}//end of while

		} catch (FileNotFoundException e) {
			System.err.println("In CSVRead(), file name: " + inputfileName);
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("In CSVRead(), file name: " + inputfileName);
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		}
		finally{
			reader.close();
		}

	}

	public void getConc(String inputfileName, BufferedWriter bufferedWriter){		
		CsvReader reader = null;
		int recordNum = 0;
		String pwsId, year, curConc;
		StringBuilder sb = new StringBuilder();

		try {			
			reader = new CsvReader(inputfileName);		

			reader.readHeaders();
			//String[] headers=reader.getHeaders();
			bufferedWriter.write("PWSIDNumber, Contaminant, Concentration, StatType, Year");
			bufferedWriter.write("\n");
			recordNum++;

			while (reader.readRecord())
			{			
				recordNum++;
				//System.out.println("Record " + recordNum);
				//System.out.println(reader.getRawRecord());
				//bufferedWriter.write(reader.getRawRecord()+'\n');
				pwsId=reader.get("PWSIDNumber");
				year=reader.get("Year");

				sb.setLength(0);
				for(int j=0;j<concNames.length;j++){
					curConc = reader.get(concNames[j]);		
					if(curConc.equals("ND"))
						curConc="0";
					bufferedWriter.write(pwsId+","+
							contamNames[j]+","+
							curConc+","+
							statTypes[j%2]+","+
							year+"\n");	
				}
			}//end of while

		} catch (FileNotFoundException e) {
			System.err.println("In CSVRead(), file name: " + inputfileName);
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("In CSVRead(), file name: " + inputfileName);
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		}
		finally{
			reader.close();
		}

	}

	public static void main(String[] args) {
		DrinkingWaterDataAgent agent = new DrinkingWaterDataAgent();

		/*
		if (args.length <= 0) {
			System.out.println("Usage: ./FoiaDataAgent inputFileName");
			System.exit(0);
		}*/
		//String inputFile="/media/DATA/epaMetaData/head3000FOIA_DMRs_NH.csv";
		String dir="/media/DATA/source/ny-gov/drinking-water-contaminants/version/2011-Sep-15";
		String inputFile = dir+"/test/drinking_water.csv";
		
		String wsFile = dir+"/test/proc_water_system.csv";		
		agent.process(inputFile, wsFile, 0);
		
		String regFile = dir+"/test/proc_ny_health_reg.csv";		
		//agent.process(inputFile, regFile, 1);
		
		String concFile = dir+"/test/proc_drinking_water.csv";		
		agent.process(inputFile, concFile, 2);
	}


}
