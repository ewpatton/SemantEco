package edu.rpi.tw.escience.WaterQualityPortal.species;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import com.csvreader.CsvReader;

/*
 * add scientific names 
 * use tables to get long status*/
@Deprecated
public class DistributionByCountyAgent {	
	static HashMap<String, String> stateStatusTable=new HashMap<String, String>();
	static HashMap<String, String> fedStatusTable=new HashMap<String, String>();
	FipsCodeAgent fipsAgent=null;

	static{
		buildStateStatusTable();
		buildfedStatusTable();
	}
	
	DistributionByCountyAgent(String countyCode){
		fipsAgent=new FipsCodeAgent(countyCode);
	}

	static private void buildStateStatusTable(){
		stateStatusTable.put("NA", "NA");
		stateStatusTable.put("None", "None");
		stateStatusTable.put("SE", "State Endangered");
		stateStatusTable.put("ST", "State Threatened");
		stateStatusTable.put("SC", "State Candidate");
		stateStatusTable.put("SS", "State Sensitive");
		stateStatusTable.put("SS", "State Monitored");		
	}

	static private void buildfedStatusTable(){
		fedStatusTable.put("NA", "NA");
		fedStatusTable.put("None", "None");
		fedStatusTable.put("FE", "Federal Endangered");
		fedStatusTable.put("FT", "Federal Threatened");
		fedStatusTable.put("FC", "Federal Candidate");
		fedStatusTable.put("Fco", "Federal Species of Concern");
		fedStatusTable.put("FCo", "Federal Species of Concern");
		fedStatusTable.put("FT/FE", "Federal Threatened/Endangered");
	}

	private String getStateStatus(String code){
		if(code.isEmpty())
			return "";
		String status=stateStatusTable.get(code);
		if(status==null){
			System.err.println("DistributionByCountyAgent.getStateStatus can Not " +
					"get the state status for code: "+ code);
			System.exit(-1);
		}
		return status;		
	}

	private String getFedStatus(String code){
		if(code.isEmpty())
			return "";
		String status=fedStatusTable.get(code);
		if(status==null){
			System.err.println("DistributionByCountyAgent.getFedStatus can Not " +
					"get the federal status for code: "+ code);
			System.exit(-1);
		}
		return status;		
	}

	@SuppressWarnings("unused")
	private void printArr(String[] strArr){
		for(String str:strArr){
			System.out.println(str);
		}		
	}

	private String[] procSpeciesName(String name){
		if(name.isEmpty())
			return null;
		//System.out.print("Name: "+name);
		String[] names = null;
		//for cases with :
		int pos=name.indexOf(":");
		if(pos!=-1){
			name=name.substring(pos+1);
			names=name.split(",");
			//for(String part:names) part=part.trim();
			return names;
		}
		//for cases with /
		pos = name.indexOf('/');
		if(pos!=-1){
			name=name.substring(0, pos);
		}
		names=new String[1];
		names[0]=name.trim();
		return names;
	}


	private List<String> procNames(String[] names){
		List<String> nameList = new ArrayList<String> ();
		for(String name:names){
			//System.out.println("Name: "+name);
			name=name.replaceAll("\\*", "").replaceAll("\\s+", " ");			
			nameList.add(name);
			//System.out.println(name);
		}			
		return nameList;		
	}

	private List<String> procSciNames(List<String> names){
		List<String> nameList = new ArrayList<String> ();
		for(String name:names){
			//			name=name.replace('\n', ',');			
			//			nameList.add(name);
			String[] parts=name.split("\n");
			StringBuilder sb=new StringBuilder();
			for(String part:parts){
				sb.append(part.replaceAll("\\s+", " ")).append(", ");
			}
			sb.setLength(sb.length()-2);
			nameList.add(sb.toString());			
		}			
		//System.out.println(nameList);
		return nameList;		
	}




	private void processCSV(String inputFile, String outputFile){		
		CsvReader reader = null;
		int recordNum = 0;
		BufferedWriter bufferedWriter = null;

		String subject = null;
		String[] spcNames=null;
		String[] sciNames=null;
		String[] stateStatus=null, fedStatus=null;
		List<String> spcNamesList = new ArrayList<String>();
		List<String> sciNamesList = new ArrayList<String>();

		try {			
			bufferedWriter = new BufferedWriter(new FileWriter(outputFile));
			bufferedWriter.write("\"Date\", \"CountryCode\", \"StateAbbr\", " +
					" \"CountyCode\", \"CountyName\", " +
					"\"SpeciesClass\", \"SpeciesSubClass\", "+
					"\"SpeciesName\", " + "\"Scientific Name or Family\", " +
					"\"StateStatusCode\", \"StateStatus\", " +
					"\"FedStatusCode\", \"FedStatus\""+
					"\n");


			reader = new CsvReader(inputFile);		
			reader.readHeaders();
			recordNum++;
			spcNames=reader.getHeaders();
			int numHeaders=spcNames.length;
			spcNamesList=procNames(spcNames);
			//printArr(headers);
			//System.out.println("Num of headers: "+numHeaders);


			reader.readRecord();
			recordNum++;
			subject = reader.get(0).trim();
			if(subject.compareTo("Scientific Name or Family")==0){
				sciNames=reader.getRawRecord().split(",");	
				//printArr(sciNames);
				sciNamesList=procSciNames(procNames(sciNames));				
			}

			while (reader.readRecord())
			{			
				recordNum++;
				subject = reader.get("Species/ Habitat").trim();				


				if(subject.compareTo("State Status")==0){
					stateStatus=reader.getRawRecord().split(",");					
					//printArr(stateStatus);
				}
				if(subject.compareTo("FedStatus")==0){
					fedStatus=reader.getRawRecord().split(",");					
					//printArr(fedStatus);
				}				
				//String classFlag=null;

				if(recordNum>=10){
					for(int i=21;i<numHeaders;i++){
						//System.out.println("Column " + i);
						String distributionFlag=reader.get(i).trim();
						//System.out.println("Distribution Flag "+distributionFlag);
						//no distribution for the county							
						if(distributionFlag.compareTo("x")!=0){
							//System.out.println("No distribution for county "+subject);
							continue;
						}
						String countyCode=fipsAgent.name2Code(subject);
						String spc = spcNamesList.get(i);
						@SuppressWarnings("unused")
						String spcClass=null;
						/*						if(recordNum==10)//findSpeciesClassBound(spc.trim(), i);							
							SpeciesNameAgent.findSpeciesSubClassBound(spc.trim(), i);*/
						String stCode=stateStatus[i].replaceAll("\"", "").trim();
						String fedCode=fedStatus[i].replaceAll("\"", "").trim();
						String sciName=sciNamesList.get(i).replaceAll("\"", "");
						//species hierarchy
						SpeciesHierarchy spcHrch=SpeciesNameAgent.getSpeciesHierarchy(i);
						//System.out.println(sciName);
						String[] curNames=procSpeciesName(spc);
						for(String spcName:curNames){
							bufferedWriter.write("\"2012-01-01\", \"US\", \"WA\", " +
									"\""+countyCode+"\", \""+subject+"\", \""+
									spcHrch.getSpcClass()+"\", \""+spcHrch.getSpcSubClass()+
									"\", \""+spcName.trim()+"\", \""+sciName+
									"\", \""+stCode+"\", \""+getStateStatus(stCode)+
									"\", \""+fedCode+"\", \""+getFedStatus(fedCode)+"\"\n");	
						}
					}
				}
				//System.out.println("Record " + recordNum);
				//System.out.println(reader.getRawRecord());
				//find the next element

			}//end of while

		} catch (FileNotFoundException e) {
			System.err.println("In processCSV(), file name: " + inputFile);
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("In processCSV(), file name: " + inputFile);
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		}
		finally{
			try{
				reader.close();
				if (bufferedWriter != null) {
					bufferedWriter.flush();
					bufferedWriter.close();
				}
			}catch (IOException ex) {
				System.err.println("In processCSV, closing the reader and BufferedWriter");
				ex.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		String input="./53-county-code.txt";
		DistributionByCountyAgent agent = new DistributionByCountyAgent(input);		
		String waFile = "./2012_distribution_by_county.csv";
		String out = "./wa_2012_distribution_by_county.csv";
		agent.processCSV(waFile, out);
	}
}
