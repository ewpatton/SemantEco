package edu.rpi.tw.escience.WaterQualityPortal.epa.foia;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

@Deprecated
public class FoiaUnitConverter {
	static int BUFFER_SIZE = 4096;
	static String convTableFile = "./TABLE430.txt"; 
	HashMap<String, Double> convMap =null;
	static public Logger unitLogger = Logger.getLogger("UnitConvLogging");
	
	public FoiaUnitConverter(){
		convMap = new HashMap<String, Double>();
		buildConvTable();
	}

	private void buildConvTable(){
		FileInputStream fIn = null;
		BufferedReader reader = null;

		try{
			fIn =  new FileInputStream(convTableFile);
			reader = new BufferedReader(new InputStreamReader(fIn));		
			String strLine;
			String codePair=null, num=null;
			while ((strLine = reader.readLine()) != null)   {
				//System.out.println(strLine);
				if(strLine.startsWith("1Page")){
					for(int i=0;i<4&&strLine!=null;i++){
						strLine = reader.readLine();
						//System.out.println("line "+ i + strLine);
					}
				}				
				codePair=strLine.substring(1, 5);
				num=strLine.substring(5).trim();	
				//System.out.println(codePair+" "+num);
				if(convMap.get(codePair)==null)
					convMap.put(codePair, FoiaUtil.numStr2Double(num, strLine, false));
			}
		}
		catch (Exception e) {
			System.err.println("In FoiaUnitConverter.buildConvTable, err");
			e.printStackTrace();
		}finally {
			//Close the BufferedReader 			
			try {
				if (reader!=null)
					reader.close ();
			} catch (IOException ex) {
				System.err.println("In FoiaUnitConverter.buildConvTable, closing the reader");
				ex.printStackTrace();
			}
		}
	}

	public void printHashMap(HashMap<String, Double> curMap){
		System.out.println("The Keys of the HashMap:");        
		Iterator<String> iteratorKey = curMap.keySet().iterator();
		int num=0;
		while(iteratorKey. hasNext()){   
			String curKey=iteratorKey.next();
			System.out.println(curKey+"="+curMap.get(curKey));
			num++;
		}       
		System.out.println("Num: "+num);
	}
	
	public void printKeyOfHashMap(HashMap<String, Double> curMap){
		System.out.println("The Keys of the HashMap:");        
		Iterator<String> iteratorKey = curMap.keySet().iterator();
		int num=0;
		while(iteratorKey. hasNext()){        
			System.out.println(iteratorKey.next()+",");
			num++;
		}       
		System.out.println("Num: "+num);
	}
	
	 public void printValueOfHashMap(HashMap<String, Double> curMap){
		System.out.println("The Values of the HashMap:"); 
        Iterator<Entry<String, Double>> iteratorValue = curMap.entrySet().iterator();
       
        int num=0;
        while(iteratorValue.hasNext()){        
            System.out.println(iteratorValue.next().getValue()+"|");
            num++;
        }		
        System.out.println("Num: "+num);
	}
	 
	 public Double getConvRate(String from, String to){
		 Double rate = 1.0;
		 if(from.compareTo(to)==0)
			 rate=1.0;
		 else
			 rate=convMap.get(from+to);
		 //can't find the conversion rate, so give up the record
		 if(rate == null){
			 unitLogger.error("In getConvRate, can't get the name for unit code pair, from: "+from+", to: "+to);
			 System.err.println("\nIn getConvRate, can't get the name for unit code pair: "+from+" "+to);
			 //System.exit(-1);
			 rate=null;
		 }
		 return rate;	
	 }

	 public String convert(String from, String to, Double value){
		 //Double value = FoiaUtil.numStr2Double(src);		 
		 if(value!=null){
			 Double rate=getConvRate(from, to);
			 if(rate==null)
				 return "";
			 return FoiaUtil.decFormat.format(value*rate);
		 }
		 else
			 return "";
	 }

	public static void main(String[] args) {
		FoiaUnitConverter unitConv = new FoiaUnitConverter();	
		//unitConv.printKeyOfHashMap(unitConv.convMap);
		//unitConv.printValueOfHashMap(unitConv.convMap);
		unitConv.printHashMap(unitConv.convMap);
		System.out.println("0099: "+unitConv.getConvRate("00", "99"));
		System.out.println("1Q1M: "+unitConv.getConvRate("1Q", "1M"));
		System.out.println("1R02: "+unitConv.getConvRate("1R", "02"));
		System.out.println("2X3E: "+unitConv.getConvRate("2X", "3E"));
		System.out.println("9Y9Z: "+unitConv.getConvRate("9Y", "9Z"));
		System.out.println("9792: "+unitConv.getConvRate("97", "92"));
	}

}
