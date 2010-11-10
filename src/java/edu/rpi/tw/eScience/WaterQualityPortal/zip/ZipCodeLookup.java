package edu.rpi.tw.eScience.WaterQualityPortal.zip;

public class ZipCodeLookup {
	final String queryBase="http://ws.geonames.org/postalCodeLookupJSON?postalcode=";
	final String queryEnd="&country=US";
	
	public static ZipCodeLookup execute(String zipCode) {
		ZipCodeLookup zcl = null;
		try {
			zcl = new ZipCodeLookup(zipCode);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return zcl;
	}
	
	String state="";
	String zip="";
	String county="";
	String city="";
	
	ZipCodeLookup(String zip) {
		this.zip = zip;
		String query = queryBase+zip+queryEnd;
		
	}
}
