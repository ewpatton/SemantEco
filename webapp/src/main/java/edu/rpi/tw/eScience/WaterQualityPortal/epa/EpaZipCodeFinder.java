package edu.rpi.tw.eScience.WaterQualityPortal.epa;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import com.csvreader.CsvReader;

/*read csv, if missing zip code
  1st, if the record has valid lat and long, use this to get zip code
  2nd if the record has street address and state, use this to lookup zip code
*/
@Deprecated
public class EpaZipCodeFinder {
	
	public void processCSVFile(String inputFileName, String ouputFileName){
		CsvReader reader = null;
		BufferedWriter out = null;
		String stateAbbr;
		String zipCode;
		@SuppressWarnings("unused")
		String latitude, longitude;
		
		try {			
			reader = new CsvReader(inputFileName, '|');	
			reader.setSafetySwitch(false);
			reader.readHeaders();		
			// Create the output file 
		     out = new BufferedWriter(new FileWriter(ouputFileName));

			while (reader.readRecord())
			{		
				stateAbbr = reader.get("PEREXNO").substring(0, 2);
				if(stateAbbr.equals("NY")){
					zipCode = reader.get("FCLZIPC");
					if(zipCode.compareTo("")==0){
						latitude = reader.get("FCLGLAT");
						longitude = reader.get("FCLGLON");
						
						
					}
					else 
						out.write(zipCode+"\n");
						//System.out.println(zipCode);
				}
				
				
			}
		}catch (FileNotFoundException e) {
			System.err.println("In CSVRead(), file name: " + inputFileName);
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("In CSVRead(), file name: " + inputFileName);
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		}
		finally{
			try {
				out.close();
			} catch (IOException e) {
				System.err.println("Error: " + e.getMessage());
			}
			reader.close();
			
		}
		
	}
	
	@SuppressWarnings("unused")
	private boolean hasLatLong(String latitude, String longitude){
		if(latitude.compareTo("")!=0 && longitude.compareTo("")!=0){
			return (Double.parseDouble(latitude)!=0 || Double.parseDouble(longitude)!=0);
		}
		else
			return false;
	}
	
/*	private String lookupByLatLong(String latitude, String longitude){
		String query = ByLatLongQueryBase+zip+ByLatLongQueryEnd;
		try {
			URL requestURL = new URL(query);
			URLConnection conn = requestURL.openConnection();
			conn.setReadTimeout(5000);
			InputStream o = (InputStream)conn.getContent();
			InputStreamReader isr = new InputStreamReader(o);
			BufferedReader br = new BufferedReader(isr);
			String result="";
			String line;
			while((line=br.readLine())!=null) result += line;
			JSONObject content = new JSONObject(result);
			JSONArray codes = content.getJSONArray("postalcodes");
			if(codes.length()>0) {
				content = codes.getJSONObject(0);
				state = content.getString("adminName1");
				stateAbbr = content.getString("adminCode1");
				stateNum = stateLookup.get(state.toLowerCase());
				county = content.getString("adminName2");
				countyNum = content.getString("adminCode2");
				city = content.getString("placeName");
				lat = content.getDouble("lat");
				lng = content.getDouble("lng");
				loaded = true;
			}
		}
		catch(java.net.SocketTimeoutException e) {
			throw new ServerFailedToRespondException();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}*/
	
	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		EpaZipCodeFinder zipCodeFinder = new EpaZipCodeFinder();
		zipCodeFinder.processCSVFile("/media/DATA/source/epa-gov/enforcement-and-compliance-history-online-echo/version/2011-Mar-19/source/ICP01.TXT",
				"/home/ping/Downloads/epa/zipcode/ny.txt");
	
	}

}
