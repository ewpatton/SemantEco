package edu.rpi.tw.escience.WaterQualityPortal.species;

@Deprecated
public class SpeciesHierarchy {
	private String spcClass=null;
	private String spcSubClass=null;
	
	SpeciesHierarchy(String curClass, String curSubClass){
		this.spcClass=curClass;
		this.spcSubClass=curSubClass;			
	}
	
	public String getSpcClass(){
		return this.spcClass;
	}
	
	public String getSpcSubClass(){
		return this.spcSubClass;
	}
}
