package edu.rpi.tw.eScience.WaterQualityPortal.epa;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/*read csv, if missing zip code
  1st, if the record has valid lat and long, use this to get zip code
  2nd if the record has street address and state, use this to lookup zip code
 */
@Deprecated
public class EpaFacilityAddressFixer {
	public static boolean DEBUG = false;
	//static String geoTargetPre = "http://maps.googleapis.com/maps/api/geocode/json?address=";
	static String geocodingUrl = "http://maps.googleapis.com/maps/api/geocode/json?address=";
	static String geoTargetPost ="&sensor=true";
	//static String getContent = "";
	//http://maps.googleapis.com/maps/api/geocode/json?latlng=40.714224,-73.961452&sensor=true_or_false
	static String reverseGeocodingUrl = "http://maps.googleapis.com/maps/api/geocode/json?latlng=";
	EpaCommAgent commAgent = null;
	CountyLookup ctyLookup = null;
	static String outputDir="/home/ping/Downloads/epa/fixer/";
	String outputFile = null;
	String stateAbbr = null;
	
	public EpaFacilityAddressFixer(String stateAbbr, String stateCode) {
		this.stateAbbr = stateAbbr;
		commAgent = new EpaCommAgent();
		ctyLookup = new CountyLookup(stateAbbr, stateCode);
		//System.out.print(ctyLookup);
		outputFile = outputDir+stateAbbr+"/fixed-"+stateAbbr+"-ICP01.TXT";
		init();
	}
	
	private void init(){
		File outFile = new File(outputFile);
		boolean exists = outFile.exists();
		if (exists) {
		    // File directory exists
			boolean success = outFile.delete();
		    if (!success){
		      System.err.println("File Deletion failed for " + outputFile);
		      System.exit(0);
		    }else{
		      System.out.println(outputFile + " deleted.");
		    }
		} 		
	}
	
	private boolean tryOneLocationResult(EpaFacilityAddress fclAdd, JSONObject result){
		double lat=0, lng=0;		
		String curCtyName="", curCtyCode="", curState="", curZip="";

		//get the attributes like city, ctyName, ctyCode, state, zip
		JSONArray components;
		try {
			components = result.getJSONArray("address_components");

			for(int i=0; i< components.length(); i++){					
				JSONObject curObj = components.getJSONObject(i);
				String longName = curObj.getString("long_name");
				String shortName = curObj.getString("short_name");
				JSONArray types = curObj.getJSONArray("types");
				String type = null;
				if(types.length()>0)
					type = types.getString(0);
				if(type.equals("administrative_area_level_2")){
					if(fclAdd.fclCtyName.length()==0){
						curCtyName = longName;
					}
				}
				else if(type.equals("administrative_area_level_1")){
					curState = shortName;
				}
				else if(type.equals("postal_code")){
					curZip = longName;
				}
			}//end of for
			//set the attributes like ctyName, ctyCode, state, zip
			//check 1: in the same state
			if(!curState.equals(this.stateAbbr))
				return false;
			//check 2: can find the county code
			if(curCtyName.length()!=0){
				curCtyCode = ctyLookup.name2Code(curCtyName);
				if(curCtyName.length()==0)
					return false;	
				if(fclAdd.fclCtyCode.length()==0)							
					fclAdd.setCtyCode(curCtyCode);
			}
			//
			if(curCtyName.length()!=0 && fclAdd.fclCtyName.length()==0)
				fclAdd.setCtyName(curCtyName);

			if(fclAdd.fclState.length()==0)
				fclAdd.setState(curState);
			if(curZip.length()!=0 && fclAdd.fclZip.length()==0)
				fclAdd.setZip(curZip);
			//set lat and lng if possible
			JSONObject loc = result.getJSONObject("geometry").getJSONObject("location");
			lat = loc.getDouble("lat");
			lng = loc.getDouble("lng");
			if(lat!=0 && fclAdd.fclLat == 0)
				fclAdd.setLat(lat);
			if(lng!=0 && fclAdd.fclLng == 0)
				fclAdd.setLng(lng);	
			return true;
		} catch (JSONException e) {
			System.err.println("In tryOneLocationResult, err");
			e.printStackTrace();
		}
		return false;
	}

	private void lookupLocation(EpaFacilityAddress fclAdd){
		String getTarget = null;
		//double lat=0, lng=0;		
		String fullAddress = fclAdd.fclName +", "+ fclAdd.fclSTAddress + ", " + fclAdd.fclCity+", "+fclAdd.fclCtyName+ ", "+fclAdd.fclState+", "+fclAdd.fclZip;
		//String curCtyName=null, curState=null, curZip=null;
		try {
			//http://maps.googleapis.com/maps/api/geocode/json?address=1600+Amphitheatre+Parkway,+Mountain+View,+CA&sensor=false
			getTarget = geocodingUrl+fullAddress+geoTargetPost;
			//if(DEBUG)
				System.out.println(getTarget);
			String targetUrl = getTarget.replace(' ', '+');
			//Pause for 2 seconds
			Thread.sleep(200);

			URL requestURL = new URL(targetUrl);
			URLConnection conn = requestURL.openConnection();
			conn.setReadTimeout(5000);
			InputStream o = (InputStream)conn.getContent();
			InputStreamReader isr = new InputStreamReader(o);
			BufferedReader br = new BufferedReader(isr);
			String response="";
			String line;
			while((line=br.readLine())!=null) response += line;
			JSONObject content = new JSONObject(response);
			JSONArray results = content.getJSONArray("results");
			for(int i=0; i< results.length(); i++){	
				JSONObject result = results.getJSONObject(i);
				if(DEBUG)
					System.out.println(result);
				if(tryOneLocationResult(fclAdd, result))
					break;
			}
			if(DEBUG)
				System.out.println(fclAdd);
		}
		catch(java.net.SocketTimeoutException e) {
			System.err.println("In lookupLocation, SocketTimeoutException");
			e.printStackTrace();
			//throw new ServerFailedToRespondException();
		}
		catch(Exception e) {
			System.err.println("In lookupLocation, err");
			e.printStackTrace();
		}
	}
	
	private boolean tryOneAddressResult(EpaFacilityAddress fclAdd, JSONObject result){
		//double lat=0, lng=0;		
		String curCtyName="", curCtyCode="", curState="", curZip="";

		//get the attributes like city, ctyName, ctyCode, state, zip
		JSONArray components;
		try {
			components = result.getJSONArray("address_components");

			for(int i=0; i< components.length(); i++){					
				JSONObject curObj = components.getJSONObject(i);
				String longName = curObj.getString("long_name");
				String shortName = curObj.getString("short_name");
				JSONArray types = curObj.getJSONArray("types");
				String type = null;
				if(types.length()>0)
					type = types.getString(0);
				if(type.equals("administrative_area_level_2")){
					if(fclAdd.fclCtyName.length()==0){
						curCtyName = longName;
					}
				}
				else if(type.equals("administrative_area_level_1")){
					curState = shortName;
				}
				else if(type.equals("postal_code")){
					curZip = longName;
				}
			}//end of for
			//set the attributes like ctyName, ctyCode, state, zip
			//check 1: in the same state
			if(!curState.equals(this.stateAbbr))
				return false;
			//check 2: can find the county code
			if(curCtyName.length()!=0){
				curCtyCode = ctyLookup.name2Code(curCtyName);
				if(curCtyName.length()==0)
					return false;	
				if(fclAdd.fclCtyCode.length()==0)							
					fclAdd.setCtyCode(curCtyCode);
			}
			//
			if(curCtyName.length()!=0 && fclAdd.fclCtyName.length()==0)
				fclAdd.setCtyName(curCtyName);

			if(fclAdd.fclState.length()==0)
				fclAdd.setState(curState);
			if(curZip.length()!=0 && fclAdd.fclZip.length()==0)
				fclAdd.setZip(curZip);
			return true;
		} catch (JSONException e) {
			System.err.println("In tryOneAddressResult, err");
			e.printStackTrace();
		}
		return false;
	}

	
	public void lookupAddress(EpaFacilityAddress fclAdd){		
		try {
			//http://maps.googleapis.com/maps/api/geocode/json?latlng=40.714224,-73.961452&sensor=true_or_false
			String getTarget = reverseGeocodingUrl+Double.toString(fclAdd.fclLat)+","+Double.toString(fclAdd.fclLng)+geoTargetPost;
			//if(DEBUG)
				System.out.println(getTarget);
			String targetUrl = getTarget.replace(' ', '+');
			//Pause for 2 seconds
			Thread.sleep(200);

			URL requestURL = new URL(targetUrl);
			URLConnection conn = requestURL.openConnection();
			conn.setReadTimeout(5000);
			InputStream o = (InputStream)conn.getContent();
			InputStreamReader isr = new InputStreamReader(o);
			BufferedReader br = new BufferedReader(isr);
			String response="";
			String line;
			while((line=br.readLine())!=null) response += line;
			JSONObject content = new JSONObject(response);
			JSONArray results = content.getJSONArray("results");
			for(int i=0; i< results.length(); i++){	
				JSONObject result = results.getJSONObject(i);
				if(DEBUG)
					System.out.println(result);
				if(tryOneAddressResult(fclAdd, result))
					break;
			}
			if(DEBUG)
				System.out.println(fclAdd);
		}
		catch(java.net.SocketTimeoutException e) {
			System.err.println("In lookupAddress, SocketTimeoutException");
			e.printStackTrace();
			//throw new ServerFailedToRespondException();
		}
		catch(Exception e) {
			System.err.println("In lookupAddress, err");
			e.printStackTrace();
		}
	}

	public void fixFile(String inputFileName){
		FileInputStream fIn = null;
		BufferedReader reader = null;
		BufferedWriter bufferedWriter = null;

		try{
			fIn =  new FileInputStream(inputFileName);
			reader = new BufferedReader(new InputStreamReader(fIn));
			bufferedWriter = new BufferedWriter(new FileWriter(outputFile));
			String strLine;
			//skip the 1st line of column names
			strLine = reader.readLine();
			while ((strLine = reader.readLine()) != null)   {
				//System.out.println (strLine);
				fixOneFacility(strLine, bufferedWriter);
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
	

	public void fixOneFacility(String fclData, BufferedWriter bufWriter) {
		//String fclNAME=null, fclSTAddress=null, fclCity=null, fclCtyCode=null;
		//String fclStateCode=null, fclZipCode=null;
		//String falLatitude, falLongitude;
		String curS303D="";
		String delimiter = "\\|";

		if(DEBUG)
			System.out.println(fclData);
		String[] parts = fclData.split(delimiter);
		if(DEBUG){
			for(int i =0; i < parts.length ; i++)
				System.out.println(parts[i]);
		}
		if(parts.length==11)
			curS303D=parts[10];
			
		EpaFacilityAddress curFclAdd = new EpaFacilityAddress(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], 
				parts[6], parts[7], Double.parseDouble(parts[8]), Double.parseDouble(parts[9]), curS303D);
		//System.out.println(curFclAdd);
		
		 if(!curFclAdd.missingLatLng()){
				//from lat and lng to look up address
				lookupAddress(curFclAdd);
		}
		 else if(!curFclAdd.hasNoAddressInfo()){
			//from address to look up lat, lng, cty, state, zip
			if(curFclAdd.missingLatLng() || curFclAdd.missingAddress())
				lookupLocation(curFclAdd);
		}
		
		//write to file
		try {
			bufWriter.write(curFclAdd.toString());
		} catch (IOException e) {
			System.err.println("In fixOneFacility, err when writing to the output file");
			e.printStackTrace();
		}
	}

	public class EpaFacilityAddress{
		String fclPermitNo;
		String fclUIN;
		String fclName;
		String fclSTAddress;
		String fclCity;
		String fclCtyCode;
		String fclCtyName;
		String fclState;
		String fclZip;
		double fclLat;
		double fclLng;
		String fclS303D;
		//
		EpaFacilityAddress(String permitNo, String UIN, String name, 
				String address, String city, String ctyCode, 
				String state, String zip, double lat, double lng, String S303D){
			fclPermitNo = permitNo;
			fclUIN = UIN;
			fclName = name;
			fclSTAddress = address;
			fclCity = city;
			fclCtyCode = ctyCode;
			if(ctyCode.length()!=0)
				fclCtyName = ctyLookup.code2Name(ctyCode);		
			else
				fclCtyName="";
			fclState = state;
			fclZip = zip;
			fclLat = lat;
			fclLng = lng;		
			fclS303D = S303D;
		}
		
		boolean hasNoAddressInfo(){
			return (fclName.length()==0 && fclSTAddress.length()==0);
		}
		
		boolean missingLatLng(){
			return (fclLat==0 && fclLng == 0);
		}
		
		boolean missingAddress(){
			//return (fclCity.length()==0 || fclCtyCode.length()==0 || fclState.length()==0 || fclZip.length()==0);
			return (fclCtyCode.length()==0 || fclState.length()==0 || fclZip.length()==0);
		}
		
		public String toString(){
			StringBuffer sbuf = new StringBuffer();
			String delimiter = "|";
			sbuf.append(fclPermitNo); sbuf.append(delimiter);
			sbuf.append(fclUIN); sbuf.append(delimiter);
			sbuf.append(fclName); sbuf.append(delimiter);
			sbuf.append(fclSTAddress); sbuf.append(delimiter);
			sbuf.append(fclCity); sbuf.append(delimiter);
			sbuf.append(fclCtyCode); sbuf.append(delimiter);
			sbuf.append(fclState); sbuf.append(delimiter);
			sbuf.append(fclZip); sbuf.append(delimiter);
			sbuf.append(Double.toString(fclLat)); sbuf.append(delimiter);
			sbuf.append(Double.toString(fclLng)); sbuf.append(delimiter);
			sbuf.append(fclS303D); sbuf.append(delimiter); sbuf.append("\n");
			return sbuf.toString();
		}
		
		public void setAddress(String stAddress){
			fclSTAddress = stAddress;
		}
		
		public void setCity(String city){
			fclCity = city;
		}
		
		public void setCtyCode(String ctyCode){
			fclCtyCode = ctyCode;
		}
		public void setCtyName(String ctyName){
			fclCtyName = ctyName;
		}
		public void setState(String state){
			fclState= state;
		}
		public void setZip(String zip){
			fclZip = zip;			
		}
		public void setLat(double lat){
			fclLat = lat;
		}
		public void setLng(double lng){
			fclLng=lng;
		}
	}

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		String state="RI", stateCode="44";
		EpaFacilityAddressFixer fixer = new EpaFacilityAddressFixer(state, stateCode);
		//missing lat, lng
		String facData1="RIG250001|110004908529|INTERNATIONAL MFG. SVCS., INC.|50 SCHOOLHOUSE LANE|PORTSMOUTH|RI005|RI|02871|0.000000|0.000000||";
		//missing lat, lng and zip
		String facData2="RIR100252|110009700751|SEVENTEEN FARMS|MAJOR POTTER ROAD|WARWICK|RI009|RI||0.000000|0.000000||";
		//missing st address, lat and lng
		String facData3="RIR100340|110010955029|WARWICK MALL|UNKNOWN|WARWICK|RI003|RI|02886|0.000000|0.000000||";
		//missing everything
		String facData4="RIG250000||||||||0.000000|0.000000||";
		//------------------------Reverse Geocoding
		//missing ctyCode
		String facData5="RIR5AA004|110032604901|BARKER STEEL COMPANY, INC.|30 LOCKBRIDGE STREET|PAWTUCKET||RI|02860|41.879166|-71.408325||";
	
		String inputFile = "/home/ping/Downloads/epa/fixer/RI/RI-ICP01.TXT";//RI-ICP01.TXT//ri-test.txt
		fixer.fixFile(inputFile);
		//fixer.fixOneFacility(facData1, fixer.outputFile);
		//zipCodeFinder.processCSVFile("/home/ping/Downloads/epa/fixer/CA-ICP01.TXT", "/home/ping/Downloads/epa/fixer/fixed-CA-ICP01.TXT");

	}

}
