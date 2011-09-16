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
			else
				processCSV(inputfileName, bufferedWriter);
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
						bufferedWriter.write(principalcounty+","+PWSIDNumber+","+
							PWSName+","+systemPopulation+"\n");
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

	public void processCSV(String inputfileName, BufferedWriter bufferedWriter){		
		CsvReader reader = null;
		int recordNum = 0;
		String curRange;
		StringBuilder sb = new StringBuilder();

		try {			
			reader = new CsvReader(inputfileName);		

			reader.readHeaders();
			String[] headers=reader.getHeaders();
			int lenHeaders=headers.length;
			int i=0;
			for(i=0;i<lenHeaders-1;i++)
				bufferedWriter.write(headers[i]+",");
			bufferedWriter.write(headers[i]);
			//more column heads
			sb.setLength(0);
			for(int j=0;j<rangeNames.length;j++)			
				convertRangeName(rangeNames[j], sb);

			bufferedWriter.write(sb.toString());
			bufferedWriter.write("\n");
			recordNum++;

			while (reader.readRecord())
			{			
				recordNum++;

				//System.out.println("Record " + recordNum);
				//System.out.println(reader.getRawRecord());
				//bufferedWriter.write(reader.getRawRecord()+'\n');
				sb.setLength(0);
				for(int j=0;j<rangeNames.length;j++){
					curRange = reader.get(rangeNames[j]);					
					convertRange(curRange, sb);
				}
				bufferedWriter.write(reader.getRawRecord()+sb.toString()+"\n");

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
		String outputFile = dir+"/test/proc_water_system.csv";
		
		
		agent.process(inputFile, outputFile, 0);
	}


}
