package edu.rpi.tw.eScience.WaterQualityPortal.epa.foia;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class FoiaUtil {
	
	static public void printHashMap(HashMap<String, String> curMap){
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
	
	static public void printValueOfHashMap(HashMap<String, String> curMap){
		System.out.println("The Values of the HashMap:"); 
        Iterator<Entry<String, String>> iteratorValue = curMap.entrySet().iterator();
       
        int num=0;
        while(iteratorValue.hasNext()){        
            System.out.println(iteratorValue.next().getValue()+"|");
            num++;
        }		
        System.out.println("Num: "+num);
	}


}
