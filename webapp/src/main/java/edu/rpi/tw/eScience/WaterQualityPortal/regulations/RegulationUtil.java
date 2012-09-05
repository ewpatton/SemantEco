package edu.rpi.tw.eScience.WaterQualityPortal.regulations;

import java.text.DecimalFormat;

public class RegulationUtil {
	static DecimalFormat decFormat = new DecimalFormat("0.000000");
	
	public static String capitalizeString(String string) {
		char[] chars = string.toLowerCase().toCharArray();
		chars[0] = Character.toUpperCase(chars[0]);
		return String.valueOf(chars);
	}

	static public Double numStr2Double(String src){
		Double value=null;
		try{
			if(src==null || src.length()==0)
				return null;

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
			System.err.println("In numStr2Double, can't get a double for the value"+src);
			//System.exit(-1);
			value=null;
		}
		return value;		
	}
	

	
}
