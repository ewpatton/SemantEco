package edu.rpi.tw.eScience.WaterQualityPortal.regulations;

import java.io.*;
import java.util.Map;

@Deprecated
public class RegulationProvenance {
	static String PML_POSTFIX=".pml.ttl";
	
	private String genID() {
		//String command ="pwd";
		//logID=`java edu.rpi.tw.string.NameFactory`
		String idCommand = "java -cp /home/ping/software/csv2rdf4lod-automation/bin/dup/csv2rdf4lod.jar:/home/ping/software/csv2rdf4lod-automation/bin/dup/openrdf-sesame-2.3.1-onejar.jar:/home/ping/software/csv2rdf4lod-automation/bin/dup/slf4j-api-1.5.6.jar:/home/ping/software/csv2rdf4lod-automation/bin/dup/slf4j-nop-1.5.6.jar:/home/ping/software/csv2rdf4lod-automation/bin/lib/javacsv2.0/javacsv.jar edu.rpi.tw.string.NameFactory";
		return exeCommand(idCommand);		
	}
	
	//
	private String genAccountName(){
		String idCommand ="src/shell/user-account.sh --cite";
		return exeCommand(idCommand);		
	}
	
	@SuppressWarnings("unused")
	private String queryEnv(String varName){
		Map<String, String> env = System.getenv();
		return env.get(varName);		
	}
		
	private String exeCommand(String command){		
		String logId="";
		try {
			String line;
			Process p = Runtime.getRuntime().exec(command);

			BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedReader bre = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			while ((line = bri.readLine()) != null) {
				logId+=line;
				System.out.println(line);
			}
			bri.close();
			while ((line = bre.readLine()) != null) {
				System.err.println(line);
			}
			bre.close();
			p.waitFor();
			System.out.println("Done.");			
		} catch (IOException err) {
			System.err.println("In genName, IOException");
			err.printStackTrace();
		}
		catch (InterruptedException err){
			System.err.println("In genName, InterruptedException");
			err.printStackTrace();			
		}
		return logId;
		
	}

	public void provOfRawFile(String rawFileName, String rawFileURL){		
		
	}
	
	public void genProvenance(int stage, String rawFileName){
		
		
		
		
	}
	
	public void provOfRaw2CSV(String rawFileName){
		String requestID=genID();		
		String nodeSet="nodeSet"+requestID;
		String inferenceStep="inferenceSte"+requestID;
		String sourceUsage="sourceUsage"+requestID;
		String accountName=genAccountName();
		
		String destFileName=rawFileName.substring(0, rawFileName.lastIndexOf('.'))+".csv";
		System.out.println(destFileName);
		String pmlFileName=destFileName+PML_POSTFIX;		
		System.out.println(pmlFileName);
		//write to file
		BufferedWriter bufWriter=null;
		try {
			bufWriter = new BufferedWriter(new FileWriter(pmlFileName));
			
			bufWriter.write("<"+nodeSet+"_content>\n");
			bufWriter.write("   a pmlj:NodeSet;\n");
			bufWriter.write("   pmlj:hasConclusion <"+destFileName+">;\n");
			bufWriter.write("   pmlj:isConsequentOf <"+inferenceStep+"_content>;\n");
			bufWriter.write(".\n");
			
			bufWriter.write("<"+inferenceStep+"_content>\n");
			bufWriter.write("   a pmlj:InferenceStep;\n");
			bufWriter.write("   pmlj:hasIndex 0;\n");
			bufWriter.write("   pmlj:hasAntecedentList ();\n");
			bufWriter.write("   pmlj:hasSourceUsage     <"+sourceUsage+"_content>;\n");
			bufWriter.write("   pmlj:hasInferenceEngine conv:html2csv;\n");
			bufWriter.write("   pmlj:hasInferenceRule Extract_and_Reformat;\n");
			bufWriter.write("   oboro:has_agent  "+accountName+";\n");
			bufWriter.write("   hartigprov:involvedActor "+accountName+";\n");
			bufWriter.write(".\n");			
			
			bufWriter.write(".\n");
			
		} catch (IOException e) {
			System.err.println("In provOfRaw2CSV, err when writing to the output file");
			e.printStackTrace();
		}
		finally{
			//Close the BufferedReader and BufferedWriter			
			try {
				if (bufWriter != null) {
					bufWriter.flush();
					bufWriter.close();
				}
			} catch (IOException ex) {
				System.err.println("In provOfRaw2CSV, err when closing the BufferedWriter");
				ex.printStackTrace();
			}			
		}
		
	}
	
	public void provOfCSV2RDF(String rawFileName){
		
	}
	
	public static void main(String[] args){
		RegulationProvenance inst=new RegulationProvenance();
		//inst.genID();
		//System.out.println(inst.queryEnv("$CSV2RDF4LOD_HOME"));
		inst.genAccountName();
		//inst.provOfRaw2CSV("epa-reg.html");
	}

}
