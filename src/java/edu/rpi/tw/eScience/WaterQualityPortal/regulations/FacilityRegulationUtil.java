package edu.rpi.tw.eScience.WaterQualityPortal.regulations;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class FacilityRegulationUtil {
	public static String processElementName(String elementName){
		System.out.println(elementName);
		String prd = elementName.replaceAll("\\.", "_");		
		//System.out.println(prd);
		prd = prd.replaceAll(",", "_");
		//System.out.println(prd);
		prd = prd.replaceAll("'", "_");
		//System.out.println(prd);
		prd = prd.replaceAll("&", "_"); 
		//System.out.println(prd);
		prd = prd.replaceAll("\\(", "_");
		//System.out.println(prd);
		prd = prd.replaceAll("\\)", "_");
		//System.out.println(prd);
		prd = prd.replaceAll("\\/", "_");
		//System.out.println(prd)
		prd = prd.replaceAll("\\+", "_");
		//System.out.println(prd)
		prd = prd.replaceAll("\\%", "_");
		//System.out.println(prd)		
		prd = prd.replaceAll("\\s", "_");
		prd = prd.replaceAll("_+", "_");
		if(prd.startsWith("_"))
			prd=prd.substring(1, prd.length());		
		if(prd.endsWith("_"))
			prd=prd.substring(0, prd.length()-1);
		System.out.println(prd);
		return prd;
	}
	
	
	static public void saveMap(String outFile, HashMap<String, EPAFacilityRegulationRule> curMap){
		System.out.println("The Values of the Map:"); 
		BufferedWriter bufferedWriter = null;
		
		try{
			bufferedWriter = new BufferedWriter(new FileWriter(outFile));
			bufferedWriter.write("\"ElementName\", \"TestType\", \"CmpOperator\", " +
					"\"CmpValue\", \"CmpUnit\"\n");
			Iterator<Entry<String, EPAFacilityRegulationRule>> itr = curMap.entrySet().iterator();
			int num=0;
			while(itr.hasNext()){  
				EPAFacilityRegulationRule curRule = itr.next().getValue();
				bufferedWriter.write(curRule.toString());				
				num++;
			}		
			System.out.println("Num: "+num);

		}
		catch (Exception e) {
			System.err.println("In saveMap, err");
			e.printStackTrace();
		}finally {
			//Close the BufferedWriter			
			try {
				if (bufferedWriter != null) {
					bufferedWriter.flush();
					bufferedWriter.close();
				}
			} catch (IOException ex) {
				System.err.println("In saveMap, closing the reader and BufferedWriter");
				ex.printStackTrace();
			}
		}
		

	}

}
