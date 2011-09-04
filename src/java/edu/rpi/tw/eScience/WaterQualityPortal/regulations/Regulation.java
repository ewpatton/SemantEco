package edu.rpi.tw.eScience.WaterQualityPortal.regulations;

import java.io.*;
import java.util.Vector;

public class Regulation {

	private Vector <String> regulations;
	private Vector <String> provenances;
    static String inputFile="ri-regulation.csv";
    static String pmloutputFile="ri-regulation.pml";
    static String owloutputFile="ri-regulation.owl";
    static String pmlName="RI";
    static String original="http://www.dem.ri.gov/pubs/regs/regs/water/h20q09.pdf";
    static String unit="mg/l";
    
	public static void main(String [] args){
		
		Regulation reg=new Regulation();

	      try{
	    	    FileInputStream fstream = new FileInputStream(inputFile);
	    	    DataInputStream in = new DataInputStream(fstream);
	    	    BufferedReader br = new BufferedReader(new InputStreamReader(in));
	    	    
  	    	    //FileWriter tfstream = new FileWriter(outputFile);
  	    	   // BufferedWriter out = new BufferedWriter(tfstream);
	    	    
	    	    String strLine;
	    	    boolean isNum = true;
	    	    reg.insertPMLSource(original, inputFile, pmlName);
	    	    while ((strLine = br.readLine()) != null)   {
	    	      String [] thisRegulation = strLine.replace(" ","").split("\",\"");
	    	      if(thisRegulation.length>=3){
	    	    	  
	    	    	  try{
	    	    		
	    	    	  isNum=thisRegulation[1].replace("\"", "").matches("-?\\d+(.\\d+)?");
	    	    	  }catch (Exception e){isNum=false;}
	    	    	  
	    	    	  if(isNum==false){
	    	    		  //reg.insertProvenance(thisRegulation[0], owloutputFile,pmlName);
	    	    		  reg.insertRegulation(thisRegulation[0], "0","");
	    	    		  //out.write(""+thisRegulation[0].replaceAll("\\(.*\\)", "")+", \"0\", \"mg/l\"\n");
  		  
	    	    	  }else{
	    	    		  
	    	    		  thisRegulation[2]=thisRegulation[2].replace("\",","");
	    	    		  reg.insertRegulation(thisRegulation[0], thisRegulation[1].replace("",""),"mg/l");
	    	    		  //reg.insertProvenance(thisRegulation[0], owloutputFile,pmlName);
	    	    		  System.out.println("q"+thisRegulation[2]+"1");
	    	    	      if(thisRegulation[2].compareTo(unit)==0||unit.compareTo("")==0){
	    	    	    	  //reg.insertProvenance(thisRegulation[0], owloutputFile,pmlName);
	    	    	    	  reg.insertRegulation(thisRegulation[0], (Double.parseDouble(thisRegulation[1].replace("\"",""))*1000)+"","ug/l");
	    	    	      }
	    	    		  }
	    	    	  /*
	    	    	  thisRegulation[2]=thisRegulation[2].replace("\",","");

    	    	      if(thisRegulation[2].compareTo("mg/L")==0&&isNum==true){
    	    	    	  System.out.println(thisRegulation[2]);
    	    	    	  reg.insertProvenance(thisRegulation[0]+"ug", "EPA-regulation.owl","EPA");
    	    	      }
	    	    		  reg.insertProvenance(thisRegulation[0], "EPA-regulation.owl","EPA");
	    */	    		  
	    	      }
	    	      else if(thisRegulation.length ==2){
	    	    	  reg.insertProvenance(thisRegulation[0], owloutputFile,pmlName);
	    	      }
	    	      else
	    	      {
	    	         System.out.println(thisRegulation.length);
	    	      }
	    	      
	    	    }
	    	    in.close();//out.close();
	    	    }catch (Exception e){
	    	      System.err.println("Error: " + e.getMessage());
	    	    }
	    	   reg.outputPMLtoFile(pmloutputFile);
		       reg.outputRDFtoFile(owloutputFile);
	}

	public Regulation(){
		regulations = new Vector <String>();
		provenances = new Vector <String>();
	}
	
	public void printRDF(){		
		for(int i=0;i<regulations.size();i++){
			System.out.println(regulations.get(i));
		}
		
	}
	public void printPML(){
		for(int i=0; i<provenances.size();i++){
			System.out.println(provenances.get(i));
		}		
	}
	public void outputPMLtoFile(String file){
	      try{
	    	    FileWriter fstream = new FileWriter(file);
	    	    BufferedWriter out = new BufferedWriter(fstream);
	    	    out.write(getPrefix(pmloutputFile));
	    		for(int i=0;i<provenances.size();i++){
	    			out.write(provenances.get(i));
	    		}
	    		out.write("</rdf:RDF>");
	    	    out.close();
	    	    }catch (Exception e){//Catch exception if any
	    	      System.err.println("Error: " + e.getMessage());
	    	    }		
	}
	public void outputRDFtoFile(String file){
	      try{
	    	    FileWriter fstream = new FileWriter(file);
	    	    BufferedWriter out = new BufferedWriter(fstream);
	    	    out.write(getPrefix(file));
	    		for(int i=0;i<regulations.size();i++){
	    			out.write(regulations.get(i));
	    		}
	    		out.write("</rdf:RDF>");
	    	    out.close();
	    	    }catch (Exception e){//Catch exception if any
	    	      System.err.println("Error: " + e.getMessage());
	    	    }
		
	}
	public String getPrefix(String file){
		String output ="";
		output += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
		output += "<!DOCTYPE rdf:RDF [\n";
		output += "<!ENTITY rdf \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" >\n";
		output +=  "<!ENTITY geo \"http://www.w3.org/2003/01/geo/wgs84_pos#\" >\n";
		output +=  "<!ENTITY owl \"http://www.w3.org/2002/07/owl#\" >\n";
		output +=  "<!ENTITY xsd \"http://www.w3.org/2001/XMLSchema#\" >\n";
		output +=  "<!ENTITY rdfs \"http://www.w3.org/2000/01/rdf-schema#\" >\n";
		output +=  "<!ENTITY time \"http://www.w3.org/2006/time\" >\n";
		output +=  "<!ENTITY epa \"http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#\" >\n";
		output +=  "<!ENTITY elem \"http://sweet.jpl.nasa.gov/2.1/matrElement.owl#\" >\n";
		output +=  "<!ENTITY body \"http://sweet.jpl.nasa.gov/2.1/realmHydroBody.owl#\" >\n";

		output +=  "<!ENTITY pmlp \"http://inferenceweb.stanford.edu/2006/06/pml-provenance.owl#\">\n";
		output +=  "<!ENTITY comp \"http://sweet.jpl.nasa.gov/2.1/matrCompound.owl#\">";
		output +=  "<!ENTITY chem \"http://sweet.jpl.nasa.gov/2.1/matr.owl#\">";
		output +=  "<!ENTITY foaf \"http://xmlns.com/foaf/0.1/\">";
		output +=  "]>";
		output += "<rdf:RDF \n";
		output +=  "xml:base=\"http://tw2.tw.rpi.edu/zhengj3/owl/"+file+"\"\n";
		output +=  "xmlns=\"http://tw2.tw.rpi.edu/zhengj3/owl/"+file+"\"\n";
		output +=  "xmlns:epa=\"&epa;\"\n";
		output +=  "xmlns:owl=\"&owl;\"\n";
		output +=  "xmlns:rdfs=\"&rdfs;\"\n";
		output +=  "xmlns:time=\"&time;\"\n";
		output +=  "xmlns:rdf=\"&rdf;\"\n";
		output +=  "xmlns:geo=\"&geo;\"\n";
		output +=  "xmlns:pmlp=\"&pmlp;\"\n";
		output +=  "xmlns:elem=\"&elem;\"\n";
		output +=  "xmlns:body=\"&body;\"\n";
		output +=  "xmlns:comp=\"&comp;\"\n";
		output +=  "xmlns:foaf=\"&foaf;\"\n";
		output +=  "xmlns:chem=\"&chem;\"\n";
	    output +=  "xmlns:xsd=\"&xsd;\">\n";

		return output;
	}
	public void insertPMLSource(String Original, String Document, String name){
		  String sourceDoc = "";
		  sourceDoc += "<pmlp:SourceUsage rdf:ID=\""+name+"-WQR\">\n";
		  sourceDoc += "<pmlp:hasSource>\n";
		  sourceDoc += "<pmlp:Document rdf:about=\""+Document+"\">\n";
		  sourceDoc += "<pmlp:hasPublisher>\n";
		  sourceDoc += "<pmlp:Organization rdf:ID=\""+name+"-WQRD\">\n";
		  sourceDoc += "</pmlp:Organization>\n";
		  sourceDoc += "</pmlp:hasPublisher>\n";
		  sourceDoc += "</pmlp:Document>\n";
		  sourceDoc += "</pmlp:hasSource>\n";
		  sourceDoc += "</pmlp:SourceUsage>\n";

		  sourceDoc += "<pmlp:SourceUsage rdf:ID=\"ORIGINAL-"+name+"-WQR\">\n";
		  sourceDoc += "<pmlp:hasSource>\n";
		  sourceDoc += "<pmlp:Document rdf:about=\""+Original+"\">\n";
		  sourceDoc += "<pmlp:hasPublisher>\n";
		  sourceDoc += "<pmlp:Organization rdf:ID=\"Original-"+name+"-WQRD\">\n";
		  sourceDoc += "</pmlp:Organization>\n";
		  sourceDoc += "</pmlp:hasPublisher>\n";
		  sourceDoc += "</pmlp:Document>\n";
		  sourceDoc += "</pmlp:hasSource>\n";
		  sourceDoc += "</pmlp:SourceUsage>\n";
		  provenances.add(sourceDoc);
	
	}
	public void insertProvenance(String element, String filename, String name){
		element=element.replace("\"", "");
		element=element.replace(" ", "");
		element=element.replace("<","");
		element=element.replace(">","");
		element=element.replace("&gt;","");
		element=element.replace("&lt;","");
		element=element.replace("&","");
		String thisProvenance="";
		thisProvenance += "<pmlp:Information>\n";
		thisProvenance += "<pmlp:hasURL rdf:datatype=\"http://www.w3.org/2001/XMLSchema#anyURI\">";
		thisProvenance += "http://tw2.tw.rpi.edu/zhengj3/owl/"+filename+"#Excessive"+element+"Measurement";
		thisProvenance += "</pmlp:hasURL>\n";
		thisProvenance += "<pmlp:hasReferenceSourceUsage rdf:resource=\"#"+name+"-WQR\" />\n";
		thisProvenance += "</pmlp:Information>\n";
		provenances.add(thisProvenance);
	}
	public void insertRegulation(String element,String value,String unit){
		element=element.replace("\"", "");
		element=element.replace(" ", "");
		element=element.replace("<","");
		element=element.replace(">","");
		element=element.replace("&gt;","");
		element=element.replace("&lt;","");
		element=element.replace("&","");
		value=value.replace("\"", "");
		String name=element;
		if(unit.compareTo("ug/l")==0){
		name=element+"ug";
		}
		else if(unit.compareTo("mg/L")==0){
			unit="mg/l";
			
		}
		String thisRegulation="";
		thisRegulation += "<owl:Class rdf:about=\"#Excessive"+name+"Measurement\">\n";
		thisRegulation += "<rdfs:subClassOf rdf:resource=\"http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#ExceededThreshold\"/>\n";
		thisRegulation += "<owl:intersectionOf rdf:parseType=\"Collection\">\n";
		thisRegulation += "<owl:Class rdf:about=\"http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#WaterMeasurement\"/>\n";
		thisRegulation += "<owl:Restriction>\n";
		thisRegulation += "<owl:onProperty rdf:resource=\"http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#hasValue\"/>\n";
		thisRegulation += "<owl:someValuesFrom>\n";
		thisRegulation += "<rdfs:Datatype>\n";
		thisRegulation += "<owl:onDatatype rdf:resource=\"http://www.w3.org/2001/XMLSchema#decimal\"/>\n";
		thisRegulation += "<owl:withRestrictions rdf:parseType=\"Collection\">\n";
		thisRegulation += "<rdf:Description rdf:about=\"#"+name+"Threshold-Drinking\">\n";
		thisRegulation += "<xsd:minInclusive rdf:datatype=\"http://www.w3.org/2001/XMLSchema#decimal\">"+value+"</xsd:minInclusive>\n";
		thisRegulation += "</rdf:Description>\n";
		thisRegulation += "</owl:withRestrictions>\n";
		thisRegulation += "</rdfs:Datatype>\n";
		thisRegulation += "</owl:someValuesFrom>\n";
		thisRegulation += "</owl:Restriction>\n";	
		thisRegulation += "<owl:Restriction>\n";
		thisRegulation += "<owl:onProperty rdf:resource=\"http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#hasElement\"/>\n";
		thisRegulation += "<owl:hasValue rdf:resource=\"http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#"+element+"\"/>\n";
		thisRegulation += "</owl:Restriction>\n";
		if(unit.compareTo("")!=0){
		thisRegulation += "<owl:Restriction>\n";
		thisRegulation += "<owl:onProperty rdf:resource=\"http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#hasUnit\"/>\n";
		thisRegulation += "<owl:hasValue>"+unit+"</owl:hasValue>\n";
		thisRegulation += "</owl:Restriction>\n";
		}
		thisRegulation += "</owl:intersectionOf>\n";
		thisRegulation += "</owl:Class>\n";
		regulations.add(thisRegulation);
		insertProvenance(name, owloutputFile,pmlName);
		
	}
	
	
}
