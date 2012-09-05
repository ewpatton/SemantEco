package edu.rpi.tw.eScience.WaterQualityPortal.epa.foia;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

@Deprecated
public class FoiaUtil {
	static DecimalFormat decFormat = new DecimalFormat("0.00000000");
	static public Logger mValueLogger = Logger.getLogger("MeasuredValueLogging");
	static public Logger lValueLogger = Logger.getLogger("LimitValueLogging");
	/* Get actual class name to be printed on */
	//static public Logger dmrLogger = Logger.getLogger("FoiaDmrAgent");

	/*convert the number string in scientific format, e.g +.180000000000001E-01
	 * to float point format, e.g. 0.01800000*/
	static public String procNumStr(String src, String rcd, boolean isLimit){	
		Double value=numStr2Double(src, rcd, isLimit);
		if(value==null){			
			//System.err.println("In FoiaUtil.procNumStr, can't get a double value for "+src);
			return "";
		}
		else
			return decFormat.format(value);
	}

	static public String double2Str(Double value){		
		if(value==null){			
			//System.err.println("In FoiaUtil.procNumStr, can't get a double value for "+src);
			return "";
		}
		else
			return decFormat.format(value);
	}

	static public Double numStr2Double(String src, String rcd, boolean isLimit){
		Double value=null;
		try{
			if(src==null || src.length()==0)
				return null;
			/*
Delmon - No Monitoring required
Optmon - Optional monitoring
addmon - value requared, but not limited*/
			if(src.compareToIgnoreCase("DELMON")==0 //
					|| src.compareToIgnoreCase("OPTMON")==0 
					|| src.compareToIgnoreCase("ADDMON")==0 )
				return null;
			//start with < or >
			if(src.charAt(0)=='<'||src.charAt(0)=='>'){
				if(isLimit){
					lValueLogger.info("record has a limit value with > or <");
					lValueLogger.info("limit value string: "+src);
					lValueLogger.info("record: "+rcd);
					src=src.substring(1);
				}
				else{
					mValueLogger.info("record has a measured value with > or <");
					mValueLogger.info("measured value string: "+src);
					mValueLogger.info("record: "+rcd);
					return null;
				}
			}
			//-1 if the character does not occur, then pos is 0
			int pos=src.indexOf('E')+1;
			String prcSrc=null;
			if(pos==0)
				prcSrc=src.trim();
			else{
				String befE=src.substring(0, pos);
				String afterE=src.substring(pos);
				prcSrc=befE.trim()+afterE.trim();	
			}
			//
			value=Double.parseDouble(prcSrc);
			return value;
		}
		catch(NumberFormatException e){
			if(isLimit){
				lValueLogger.error("In FoiaUtil.numStr2Double, can't get a double for the limit value"+src);
				System.err.println("In FoiaUtil.numStr2Double, can't get a double for the limit value"+src);
			}else{
				mValueLogger.error("In FoiaUtil.numStr2Double, can't get a double for the non limit value"+src);
				System.err.println("In FoiaUtil.numStr2Double, can't get a double for the non limit value"+src);
			}
			//System.exit(-1);
			value=null;
		}
		return value;		
	}

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
