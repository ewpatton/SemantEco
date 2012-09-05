package edu.rpi.tw.eScience.WaterQualityPortal.epa.foia;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.rpi.tw.eScience.WaterQualityPortal.epa.industry.NAICSAgent;

@Deprecated
public class FoiaFacAgent {
	FRSAgent frsagent=null;
	NAICSAgent naicsAgent=null;
	
	public class FoiaFacility {
		public String regionCode;
		public String state;
		
	}
	
	FoiaFacAgent(String stateUINFile, String naicsCodeFile){
		frsagent = new FRSAgent(stateUINFile);
		naicsAgent = new NAICSAgent(naicsCodeFile);
		
	}
	/**
	 * @param fclData
	 * @param bufWriter
	 */
	public void processOneFacility1(String fclData, BufferedWriter bufWriter){
		StringBuilder strBuilder= new StringBuilder();
		//NPID     009        1-   9   Facility ID Number  
		String facID=fclData.substring(0, 9).trim();
		//REGN     002       10-  11   Region Code   
		String regionCode=fclData.substring(9, 11).trim();
		//STTE     002       12-  13   State     
		String state=fclData.substring(11, 13).trim();
		//FNMS     030       14-  43   Facility Name 
		String facName=fclData.substring(13, 43).trim();	
		//MADI     001       44-  44   Major Discharge Indicator    
		String majorDischargeIndicator=fclData.substring(43, 44).trim();	
		//FTYP     001       45-  45   Facility Type     
		String facType =fclData.substring(44, 45).trim();
		//FTYP     015       46-  60   FTYP Description 
		String ftypDes=fclData.substring(45, 60).trim();	
		//IACC     001       61-  61   Inactive Code    
		String IACC=fclData.substring(60, 61).trim();
		//    ORID     006       62-  67   Original Issue Date 
		String ORID=fclData.substring(61, 67).trim();	
//	    MST1     030       68-  97   Primary Mailing Street
		String MST1=fclData.substring(67, 97).trim();	
//	    MST2     030       98- 127   Primary Mailing Street   
		String MST2=fclData.substring(97, 127).trim();
//	    MCTY     023      128- 150   Primary Mailing City    
		String MCTY=fclData.substring(127, 150).trim();
//	    MSTT     002      151- 152   Primary Mailing State   
		String MSTT=fclData.substring(150, 152).trim();
//	    MZIP     009      153- 161   Primary Mailing Zip    
		String MZIP=fclData.substring(152, 161).trim();
//		RNAM     030      162- 191   Facility Location Name  
		String RNAM=fclData.substring(161, 191).trim();
//	    RST1     030      192- 221   Facility Location Street       
		String RST1=fclData.substring(191, 221).trim();
//	    RST2     030      222- 251   Facility Location Street 
		String RST2=fclData.substring(221, 251).trim();
//	    RCTY     023      252- 274   Facility Location City      
		String RCTY=fclData.substring(251, 274).trim();
//	    RSTT     002      275- 276   Facility Location State 
		String RSTT=fclData.substring(274, 276).trim();
//	    RTEL     010      277- 286   Facility Location Telephone  
		String RTEL=fclData.substring(276, 286).trim();
//	    RZIP     009      287- 295   Facility Location Zip   
		String RZIP=fclData.substring(286, 295).trim();
		
//		OFFL     030      296- 325   Cognizant Official    
		String OFFL=fclData.substring(295, 325).trim();
//	    TELE     010      326- 335   Telephone    
		String TELE=fclData.substring(325, 335).trim();
//	    CITY     005      336- 340   City Code    
		String CITY=fclData.substring(335, 340).trim();
//	    CYNM     020      341- 360   City Name     
		String CYNM=fclData.substring(340, 360).trim();
//	    CNTY     003      361- 363   County Code  
		String CNTY=fclData.substring(360, 363).trim();
//	    CNTN     020      364- 383   County Name  
		String CNTN=fclData.substring(363, 383).trim();
//	    TYPO     003      384- 386   Type of Ownership 
		String TYPO=fclData.substring(383, 386).trim();
//	    TYPO     007      387- 393   TYPO Description   
		String TYPODes=fclData.substring(386, 393).trim();
//	    SIC2     004      394- 397   Standard Industrial Code 
		String SIC2=fclData.substring(393, 397).trim();
//	    SIC2     030      398- 427   SIC2 Description   
		String SIC2Desc=fclData.substring(397, 427).trim();
//	    FLOW     010      428- 437   Avg. Design Flow (MGD)  
		String FLOW=fclData.substring(427, 437).trim();
//	    RWAT     035      438- 472   Receiving Water       
		String RWAT=fclData.substring(437, 472).trim();
//	    BAS6     006      473- 478   River Basin Code 
		String BAS6=fclData.substring(472, 478).trim();
//	    BAS2     002      479- 480   River Basin Major 
		String BAS2=fclData.substring(478, 480).trim();
//	    BAS2     020      481- 500   BAS2 Description  
		String BAS2Desc=fclData.substring(480, 500).trim();
//	    BAS4     004      501- 504   River Basin Code (Major/Minor)   
		String BAS4=fclData.substring(500, 504).trim();
//	    BAS4     020      505- 524   BAS4 Description  
		String BAS4Desc=fclData.substring(504, 524).trim();
//	    PTYP     001      525- 525   Permit Type Code      
		String PTYP=fclData.substring(524, 525).trim();
//	    PTYP     011      526- 536   PTYP Description     
		String PTYPDesc=fclData.substring(525, 536).trim();
//	    PERD     006      537- 542   Permit Date Issued  
		String PERD=fclData.substring(536, 542).trim();
//	    PEFD     006      543- 548   Permit Date Effective  
		String PEFD=fclData.substring(542, 548).trim();
//	    PERE     006      549- 554   Permit Date Expired   
		String PERE=fclData.substring(548, 554).trim();
//	    FLON     009      555- 563     Longitude         
		String FLON=fclData.substring(554, 563).trim();
//	    FLAT     008      564- 571     Latitude        
		String FLAT=fclData.substring(563, 571).trim();
//	    FLLC     001      572- 572     Lat/Long Code of Accuracy  
		String FLLC=fclData.substring(571, 572).trim();
	    
		strBuilder.append("\""); 		strBuilder.append(facID);
		strBuilder.append("\",");
		strBuilder.append("\"");		strBuilder.append(regionCode);
		strBuilder.append("\",");
		strBuilder.append("\"");		strBuilder.append(state);
		strBuilder.append("\",");
		strBuilder.append("\"");		strBuilder.append(facName);
		strBuilder.append("\",");
		strBuilder.append("\"");		strBuilder.append(majorDischargeIndicator);
		strBuilder.append("\",");
		strBuilder.append("\"");		strBuilder.append(facType);
		strBuilder.append("\",");
		strBuilder.append("\"");		strBuilder.append(ftypDes);
		strBuilder.append("\",");
		strBuilder.append("\"");		strBuilder.append(IACC);
		strBuilder.append("\",");
		strBuilder.append("\"");		strBuilder.append(ORID);		
		strBuilder.append("\",");
		strBuilder.append("\"");		strBuilder.append(MST1);	strBuilder.append(", ");	
 										strBuilder.append(MST2);	
		strBuilder.append("\",");
		strBuilder.append("\"");		strBuilder.append(MCTY);		
		strBuilder.append("\",");
		strBuilder.append("\"");		strBuilder.append(MSTT);		
		strBuilder.append("\",");
		strBuilder.append("\"");		strBuilder.append(MZIP);		
		strBuilder.append("\",");
		strBuilder.append("\"");		strBuilder.append(RNAM);		
		strBuilder.append("\",");
		strBuilder.append("\"");		strBuilder.append(RST1);	strBuilder.append(", ");	
										strBuilder.append(RST2);		
		strBuilder.append("\",");
		strBuilder.append("\"");		strBuilder.append(RCTY);		
		strBuilder.append("\",");
		strBuilder.append("\"");		strBuilder.append(RSTT);		
		strBuilder.append("\",");
		strBuilder.append("\"");		strBuilder.append(RTEL);		
		strBuilder.append("\",");
		strBuilder.append("\"");		strBuilder.append(RZIP);		
		strBuilder.append("\",");
		strBuilder.append("\"");		strBuilder.append(OFFL);		
		strBuilder.append("\",");
		strBuilder.append("\"");		strBuilder.append(TELE);		
		strBuilder.append("\",");
		strBuilder.append("\"");		strBuilder.append(CITY);		
		strBuilder.append("\",");
		strBuilder.append("\"");		strBuilder.append(CYNM);		
		strBuilder.append("\",");
		strBuilder.append("\"");		strBuilder.append(CNTY);		
		strBuilder.append("\",");
		strBuilder.append("\"");		strBuilder.append(CNTN);		
		strBuilder.append("\",");
		strBuilder.append("\"");		strBuilder.append(TYPO);		
		strBuilder.append("\",");
		strBuilder.append("\"");		strBuilder.append(TYPODes);		
		strBuilder.append("\",");
		strBuilder.append("\"");		strBuilder.append(SIC2);		
		strBuilder.append("\",");
		strBuilder.append("\"");		strBuilder.append(SIC2Desc);		
		strBuilder.append("\",");		
		strBuilder.append("\"");		strBuilder.append(FLOW);		
		strBuilder.append("\",");
		strBuilder.append("\"");		strBuilder.append(RWAT);		
		strBuilder.append("\",");
		strBuilder.append("\"");		strBuilder.append(BAS6);		
		strBuilder.append("\",");
		strBuilder.append("\"");		strBuilder.append(BAS2);		
		strBuilder.append("\",");
		strBuilder.append("\"");		strBuilder.append(BAS2Desc);		
		strBuilder.append("\",");
		
		strBuilder.append("\"");		strBuilder.append(BAS4);		
		strBuilder.append("\",");
		strBuilder.append("\"");		strBuilder.append(BAS4Desc);		
		strBuilder.append("\",");
		strBuilder.append("\"");		strBuilder.append(PTYP);		
		strBuilder.append("\",");
		strBuilder.append("\"");		strBuilder.append(PTYPDesc);		
		strBuilder.append("\",");
		strBuilder.append("\"");		strBuilder.append(PERD);		
		strBuilder.append("\",");
		strBuilder.append("\"");		strBuilder.append(PEFD);		
		strBuilder.append("\",");
		strBuilder.append("\"");		strBuilder.append(PERE);		
		strBuilder.append("\",");
		strBuilder.append("\"");		strBuilder.append(FLON);		
		strBuilder.append("\",");
		strBuilder.append("\"");		strBuilder.append(FLAT);		
		strBuilder.append("\",");
		strBuilder.append("\"");		strBuilder.append(FLLC);		
		strBuilder.append("\",");
		
		
		strBuilder.append("\n");
		try {
			bufWriter.write(strBuilder.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void processOneFacility(String fclData, BufferedWriter bufWriter){
		DecimalFormat decFormat = new DecimalFormat("0.0000000");
		StringBuilder strBuilder= new StringBuilder();	
//		NPID     009        1-   9   Facility ID Number   
		strBuilder.append("\""); 		
		String perm=fclData.substring(0, 9).trim();
		strBuilder.append(perm);
		strBuilder.append("\",");
		//UIN
		strBuilder.append("\""); 		
		String uin=frsagent.perm2UIN(perm);
		strBuilder.append(uin);
		strBuilder.append("\",");
//	    REGN     002       10-  11   Region Code    
		strBuilder.append("\""); 		strBuilder.append(fclData.substring(9, 11).trim());
		strBuilder.append("\",");
//	    STTE     002       12-  13   State    
		String stateCode=fclData.substring(11, 13).trim();
		strBuilder.append("\""); 		strBuilder.append(stateCode);
		strBuilder.append("\",");
//	    FNMS     030       14-  43   Facility Name     
		strBuilder.append("\""); 		strBuilder.append(fclData.substring(13, 43).trim());
		strBuilder.append("\",");
//	    MADI     001       44-  44   Major Discharge Indicator   
		strBuilder.append("\""); 		strBuilder.append(fclData.substring(43, 44).trim());
		strBuilder.append("\",");
//	    FTYP     001       45-  45   Facility Type       
		strBuilder.append("\""); 		strBuilder.append(fclData.substring(44, 45).trim());
		strBuilder.append("\",");
//	    FTYP     015       46-  60   FTYP Description  
		strBuilder.append("\""); 		strBuilder.append(fclData.substring(45, 60).trim());
		strBuilder.append("\",");
//	    IACC     001       61-  61   Inactive Code      
		strBuilder.append("\""); 		strBuilder.append(fclData.substring(60, 61).trim());
		strBuilder.append("\",");
//	    ORID     006       62-  67   Original Issue Date     
		strBuilder.append("\""); 		strBuilder.append(fclData.substring(61, 67).trim());
		strBuilder.append("\",");
//	    MST1     030       68-  97   Primary Mailing Street             
//	    MST2     030       98- 127   Primary Mailing Street   
		strBuilder.append("\""); 		strBuilder.append(fclData.substring(67, 97).trim());
		strBuilder.append(", "); 		strBuilder.append(fclData.substring(97, 127).trim());
		strBuilder.append("\",");
//	    MCTY     023      128- 150   Primary Mailing City    
		strBuilder.append("\""); 		strBuilder.append(fclData.substring(127, 150).trim());
		strBuilder.append("\",");
//	    MSTT     002      151- 152   Primary Mailing State    
		strBuilder.append("\""); 		strBuilder.append(fclData.substring(150, 152).trim());
		strBuilder.append("\",");
//	    MZIP     009      153- 161   Primary Mailing Zip     
		strBuilder.append("\""); 		strBuilder.append(fclData.substring(152, 161).trim());
		strBuilder.append("\",");
//	    RNAM     030      162- 191   Facility Location Name      
		strBuilder.append("\""); 		strBuilder.append(fclData.substring(161, 191).trim());
		strBuilder.append("\",");
//	    RST1     030      192- 221   Facility Location Street          
//	    RST2     030      222- 251   Facility Location Street 
		strBuilder.append("\""); 		strBuilder.append(fclData.substring(191, 221).trim());
		strBuilder.append(", "); 		strBuilder.append(fclData.substring(221, 251).trim());
		strBuilder.append("\",");
//	    RCTY     023      252- 274   Facility Location City      
		strBuilder.append("\""); 		strBuilder.append(fclData.substring(251, 274).trim());
		strBuilder.append("\",");
//	    RSTT     002      275- 276   Facility Location State   
		strBuilder.append("\""); 		strBuilder.append(fclData.substring(274, 276).trim());
		strBuilder.append("\",");
//	    RTEL     010      277- 286   Facility Location Telephone  
		strBuilder.append("\""); 		strBuilder.append(fclData.substring(276, 286).trim());
		strBuilder.append("\",");
//	    RZIP     009      287- 295   Facility Location Zip    
		strBuilder.append("\""); 		strBuilder.append(fclData.substring(286, 295).trim());
		strBuilder.append("\",");
//	    OFFL     030      296- 325   Cognizant Official   
		strBuilder.append("\""); 		strBuilder.append(fclData.substring(295, 325).trim());
		strBuilder.append("\",");
//	    TELE     010      326- 335   Telephone     
		strBuilder.append("\""); 		strBuilder.append(fclData.substring(325, 335).trim());
		strBuilder.append("\",");
//	    CITY     005      336- 340   City Code     
		strBuilder.append("\""); 		strBuilder.append(fclData.substring(335, 340).trim());
		strBuilder.append("\",");
//	    CYNM     020      341- 360   City Name     
		strBuilder.append("\""); 		strBuilder.append(fclData.substring(340, 360).trim());
		strBuilder.append("\",");
//	    CNTY     003      361- 363   County Code  
		String countyCode=fclData.substring(360, 363).trim();
		strBuilder.append("\""); 		
		if(countyCode.length()==0)
			strBuilder.append("");
		else
			strBuilder.append(stateCode+countyCode);
		strBuilder.append("\",");
//	    CNTN     020      364- 383   County Name     
		strBuilder.append("\""); 		strBuilder.append(fclData.substring(363, 383).trim());
		strBuilder.append("\",");
//	    TYPO     003      384- 386   Type of Ownership  
		strBuilder.append("\""); 		strBuilder.append(fclData.substring(383, 386).trim());
		strBuilder.append("\",");
//	    TYPO     007      387- 393   TYPO Description   
		strBuilder.append("\""); 		strBuilder.append(fclData.substring(386, 393).trim());
		strBuilder.append("\",");
//	    SIC2     004      394- 397   Standard Industrial Code  
		strBuilder.append("\""); 		strBuilder.append(fclData.substring(393, 397).trim());
		strBuilder.append("\",");
//	    SIC2     030      398- 427   SIC2 Description       
		strBuilder.append("\""); 		strBuilder.append(fclData.substring(397, 427).trim());
		strBuilder.append("\",");
//	    FLOW     010      428- 437   Avg. Design Flow (MGD)  
		strBuilder.append("\""); 		strBuilder.append(fclData.substring(427, 437).trim());
		strBuilder.append("\",");
//	    RWAT     035      438- 472   Receiving Water   
		strBuilder.append("\""); 		strBuilder.append(fclData.substring(437, 472).trim());
		strBuilder.append("\",");
//	    BAS6     006      473- 478   River Basin Code 
		strBuilder.append("\""); 		strBuilder.append(fclData.substring(472, 478).trim());
		strBuilder.append("\",");
//	    BAS2     002      479- 480   River Basin Major  
		strBuilder.append("\""); 		strBuilder.append(fclData.substring(478, 480).trim());
		strBuilder.append("\",");
//	    BAS2     020      481- 500   BAS2 Description    
		strBuilder.append("\""); 		strBuilder.append(fclData.substring(480, 500).trim());
		strBuilder.append("\",");
//	    BAS4     004      501- 504   River Basin Code (Major/Minor) 
		strBuilder.append("\""); 		strBuilder.append(fclData.substring(500, 504).trim());
		strBuilder.append("\",");
//	    BAS4     020      505- 524   BAS4 Description  
		strBuilder.append("\""); 		strBuilder.append(fclData.substring(504, 524).trim());
		strBuilder.append("\",");
//	    PTYP     001      525- 525   Permit Type Code      
		strBuilder.append("\""); 		strBuilder.append(fclData.substring(524, 525).trim());
		strBuilder.append("\",");
//	    PTYP     011      526- 536   PTYP Description   
		strBuilder.append("\""); 		strBuilder.append(fclData.substring(525, 536).trim());
		strBuilder.append("\",");
//	    PERD     006      537- 542   Permit Date Issued  
		strBuilder.append("\""); 		strBuilder.append(fclData.substring(536, 542).trim());
		strBuilder.append("\",");
//	    PEFD     006      543- 548   Permit Date Effective  
		strBuilder.append("\""); 		strBuilder.append(fclData.substring(542, 548).trim());
		strBuilder.append("\",");
//	    PERE     006      549- 554   Permit Date Expired 
		strBuilder.append("\""); 		strBuilder.append(fclData.substring(548, 554).trim());
		strBuilder.append("\",");
//	    FLON     009      555- 563     Longitude  
		strBuilder.append("\"");
		String longStr=decFormat.format(LongDegree2decimal(fclData.substring(554, 563).trim()));
		strBuilder.append(longStr);
		strBuilder.append("\",");
//	    FLAT     008      564- 571     Latitude  
		strBuilder.append("\""); 		
		String latStr=decFormat.format(LatDegree2decimal(fclData.substring(563, 571).trim()));
		strBuilder.append(latStr);
		strBuilder.append("\",");
//	    FLLC     001      572- 572     Lat/Long Code of Accuracy  
		strBuilder.append("\""); 		strBuilder.append(fclData.substring(571, 572).trim());
		strBuilder.append("\",");		
		//NAICS Code
		strBuilder.append("\""); 		strBuilder.append(naicsAgent.uin2NaicsCode(uin));
		strBuilder.append("\"");	
		
		strBuilder.append("\n");		
		try {
			bufWriter.write(strBuilder.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	
	}
	
	
	public double LongDegree2decimal(String srcValue){
		//System.out.println(srcValue);
		if(srcValue.length()<8)
			return 0;
		int sign=1;
		if(srcValue.charAt(0)=='-')
			sign=-1;
		double deg = Double.parseDouble(srcValue.substring(1, 4));
		double min =Double.parseDouble(srcValue.substring(4, 6));
		double sec =Double.parseDouble(srcValue.substring(6, 8));
		double tenthsec =0;
		if(srcValue.length()>=8)
			tenthsec =Double.parseDouble(srcValue.substring(8));
		sec+=tenthsec*0.1;
		double dec = deg+min/60+sec/3600;
		dec=sign*dec;
		//System.out.println(dec);
		return dec;
		
		
	}

	public double LatDegree2decimal(String srcValue){
		//System.out.println(srcValue);
		if(srcValue.length()<7)
			return 0;
		int sign=1;
		if(srcValue.charAt(0)=='-')
			sign=-1;		
		double deg = Double.parseDouble(srcValue.substring(1, 3));
		double min =Double.parseDouble(srcValue.substring(3, 5));
		double sec =Double.parseDouble(srcValue.substring(5, 7));
		double tenthsec =0;
		if(srcValue.length()>=7)
			tenthsec =Double.parseDouble(srcValue.substring(7));
		sec+=tenthsec*0.1;
		double dec = deg+min/60+sec/3600;
		dec=sign*dec;
		//System.out.println(dec);
		return dec;
	}
	
	public void extractOneState(String inputFileName, String outputFile, String fips){
		FileInputStream fIn = null;
		BufferedReader reader = null;
		BufferedWriter bufferedWriter = null;

		try{
			fIn =  new FileInputStream(inputFileName);
			reader = new BufferedReader(new InputStreamReader(fIn));
			bufferedWriter = new BufferedWriter(new FileWriter(outputFile));			
			String strLine;

			while ((strLine = reader.readLine()) != null)   {
//			    RSTT     002      275- 276   Facility Location State   
				if(fips.equals(strLine.substring(274, 276).trim())){
					bufferedWriter.write(strLine+"\n");
				}
			}
		}
		catch (Exception e) {
			System.err.println("In fixFile, err");
			e.printStackTrace();
		}finally {
			//Close the BufferedReader and BufferedWriter			
			try {
				if (reader!=null)
					reader.close ();
				if (bufferedWriter != null) {
					bufferedWriter.flush();
					bufferedWriter.close();
				}
			} catch (IOException ex) {
				System.err.println("In fixFile, closing the reader and BufferedWriter");
				ex.printStackTrace();
			}
		}
	}
	
	//not used
	public void preprocessFile(String inputFileName, String outputFile){
		FileInputStream fIn = null;
		BufferedReader reader = null;
		BufferedWriter bufferedWriter = null;

		try{
			fIn =  new FileInputStream(inputFileName);
			reader = new BufferedReader(new InputStreamReader(fIn));
			bufferedWriter = new BufferedWriter(new FileWriter(outputFile));			
			String strLine, curState;

			while ((strLine = reader.readLine()) != null)   {
				System.out.println (strLine);
				curState=strLine.substring(0, 2);
				String reg = curState+"[0-9]";
				Pattern p = Pattern.compile(reg);
				Matcher m = p.matcher(strLine); // get a matcher object
				int last = 0, current = 0;
				while(m.find()) {					
				   current=m.start();
				   if(current==0 || current==150 || current ==274)
					   continue;
		           System.out.println("last: "+last+", current: "+current);		           
		           bufferedWriter.write(strLine.substring(last, current)+"\n");
		           last=current;		           
		       }
				bufferedWriter.write(strLine.substring(last)+"\n");
			}
		}
		catch (Exception e) {
			System.err.println("In fixFile, err");
			e.printStackTrace();
		}finally {
			//Close the BufferedReader and BufferedWriter			
			try {
				if (reader!=null)
					reader.close ();
				if (bufferedWriter != null) {
					bufferedWriter.flush();
					bufferedWriter.close();
				}
			} catch (IOException ex) {
				System.err.println("In fixFile, closing the reader and BufferedWriter");
				ex.printStackTrace();
			}
		}
	}
	
	public void processFile(String inputFileName, String outputFile){
		FileInputStream fIn = null;
		BufferedReader reader = null;
		BufferedWriter bufferedWriter = null;

		try{
			fIn =  new FileInputStream(inputFileName);
			reader = new BufferedReader(new InputStreamReader(fIn));
			bufferedWriter = new BufferedWriter(new FileWriter(outputFile));			
			String strLine;

			bufferedWriter.write("\"PEREXNO\", \"FCLTUIN\", \"Region Code\", \"State\", " +
			"\"Facility Name\", \"Major Discharge Indicator\", \"Facility Type\", " +
			"\"FTYP Description\", \"Inactive Code\", \"Original Issue Date\", " +
			"\"Primary Mailing Street\", \"Primary Mailing City\", \"Primary Mailing State\", " +
			"\"Primary Mailing Zip\", \"Facility Location Name\", \"Facility Location Street\", " +
			"\"Facility Location City\", \"Facility Location State\", \"Facility Location Telephone\", " +
			"\"Facility Location Zip\", \"Cognizant Official\", \"Telephone\", \"City Code\", \"City Name\", " +
			"\"County Code\", \"County Name\", \"Type of Ownership\", \"TYPO Description\", \"Standard Industrial Code\", " +
			"\"SIC2 Description\", \"Avg. Design Flow (MGD)\", \"Receiving Water\", \"River Basin Code\", " +
			"\"River Basin Major\", \"BAS2 Description\", \"River Basin Code (Major/Minor)\", \"BAS4 Description\", " +
			"\"Permit Type Code\", \"PTYP Description\", \"Permit Date Issued\", \"Permit Date Effective\", " +
			"\"Permit Date Expired\", \"Longitude\", \"Latitude\", \"Lat/Long Code of Accuracy\", " +
			"\"NAICS\"\n");
			
			while ((strLine = reader.readLine()) != null)   {
				//System.out.println(strLine);
				processOneFacility(strLine, bufferedWriter);
			}

		}
		catch (Exception e) {
			System.err.println("In fixFile, err");
			e.printStackTrace();
		}finally {
			//Close the BufferedReader and BufferedWriter			
			try {
				if (reader!=null)
					reader.close ();
				if (bufferedWriter != null) {
					bufferedWriter.flush();
					bufferedWriter.close();
				}
			} catch (IOException ex) {
				System.err.println("In fixFile, closing the reader and BufferedWriter");
				ex.printStackTrace();
			}
		}
	}
	
	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) {

		String curState="WY";
		//"WV" "VT"
		//"VA" "SC" "ND" "OR"//"NJ"//"NC"//"NC"
		//"MO"//"ME"//"KS"//"IA"//"FL"//"DE"//"AZ";//WA//IA
		String dir="/media/DATA/epaMetaData/fac/";
		String wholeFile="F#01613F.txt";
		

		String curFile=curState+"-"+wholeFile;
		String UINFile =curState+ "UIN.CSV";//"./WAUIN.CSV"
		String naicsFile=curState+"_NAICS.CSV";
		FoiaFacAgent facAgent = new FoiaFacAgent(dir+UINFile, dir+naicsFile);
		facAgent.extractOneState(dir+wholeFile, dir+curFile, curState);
		String outputFile=curFile.replace('#', '-').replaceAll(".txt", ".csv");
		facAgent.processFile(dir+curFile, dir+outputFile);		
	}
	
	//facAgent.LongDegree2decimal("-11046516");	
	//String testFile="head500-F#01613F.txt";				
	//String procFile="proc"+inputFile;
	//facAgent.preprocessFile(dir+inputFile, dir+procFile);
	//facAgent.processFile(dir+inputFile, dir+outputFile);

}
