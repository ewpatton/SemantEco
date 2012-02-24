package edu.rpi.tw.eScience.WaterQualityPortal.epa.foia;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class FoiaUtil {
	static DecimalFormat decFormat = new DecimalFormat("0.00000000");
	
	static public String procNumStr(String src){		
		if(src==null || src.length()==0)
			return "";
		int pos=src.indexOf('E')+1;
		String befE=src.substring(0, pos);
		String afterE=src.substring(pos);
		String prcSrc=befE.trim()+afterE.trim();			
		double value=0;
		try{			
			value=Double.parseDouble(prcSrc);
			return decFormat.format(value);
		}
		catch(NumberFormatException e){
			return "";
		}		
	}
	
	static public Double numStr2Double(String src){
		try{
			if(src==null || src.length()==0)
				throw new NumberFormatException();
			int pos=src.indexOf('E')+1;
			String befE=src.substring(0, pos);
			String afterE=src.substring(pos);
			String prcSrc=befE.trim()+afterE.trim();			
			double value=Double.parseDouble(prcSrc);
			return value;
		}
		catch(NumberFormatException e){
			System.err.println("In FoiaUtil.numStr2Double, can't get a double value for "+src);
			System.exit(-1);
		}
		return null;		
	}
	
	static public void printHashMap(HashMap<String, Object> curMap){
        System.out.println("The Keys of the HashMap:");        
        Iterator<String> iteratorKey = curMap.keySet().iterator();
       
        while(iteratorKey. hasNext()){     
        	String key=iteratorKey.next();
            System.out.println(key+", "+curMap.get(key));
        }       
	}
	
	static public void printKeyOfHashMap(HashMap<String, String> curMap){
        System.out.println("The Keys of the HashMap:");        
        Iterator<String> iteratorKey = curMap.keySet().iterator();
        int num=0;
        while(iteratorKey. hasNext()){        
            System.out.println(iteratorKey.next()+",");
            num++;
        }       
        System.out.println("Num: "+num);
	}
	

	static public void printValueOfHashMap(HashMap<String, Object> curMap){
		System.out.println("The Values of the HashMap:"); 
        Iterator<Entry<String, Object>> iteratorValue = curMap.entrySet().iterator();
       
        int num=0;
        while(iteratorValue.hasNext()){        
            System.out.println(iteratorValue.next().getValue()+"|");
            num++;
        }		
        System.out.println("Num: "+num);
	}

//from http://stackoverflow.com/questions/4871051/getting-the-current-working-directory-in-java
	public void findCurDir(){
		String current;
		try {
			current = new java.io.File( "." ).getCanonicalPath();
	        System.out.println("Current dir :"+current);
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	public static void main(String[] args) {
		//System.out.println(translator.procSciNum("DELMON"));
//		System.out.println(translator.procSciNum("9.00000012"));
//		System.out.println(translator.procSciNum("+.180000000000001E-01"));
//		System.out.println(translator.procSciNum("+.100000000000001E 01"));
//		System.out.println(translator.procSciNum("+0.150000000000001E 02"));	
//		System.out.println(translator.procSciNum("+.100000000000001E -03"));	
		 
//		System.out.println(translator.procSciNum("+.136990000000001E-02"));
//		System.out.println(translator.procSciNum("+.220500000000002E+01"));
//		System.out.println(translator.procSciNum("+.136990000000001E-02"));
//		//
//		System.out.println(translator.procSciNum("+.228268800000002E+08"));		
//		System.out.println(translator.procSciNum("+.999999999999999E-06"));			
//		System.out.println(translator.procSciNum("+.166666660000000E+00"));		
//		//
//		System.out.println(translator.procSciNum("ADDMON"));	
	}
	
}
