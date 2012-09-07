package edu.rpi.tw.escience.WaterQualityPortal.util;

import java.util.*;
import java.io.*;

@Deprecated
public class ConfigReader {
 
  private String fileName;
  private Properties properties;
 
  public String getProperty(String propName) {
    return properties.getProperty(propName);
  }
 
  public void printPropertyList() {
    properties.list(System.out);
  }
 
  public ConfigReader(String fileName) {
    this.fileName = fileName;
    readProperties();
  }
 
  
  private void readProperties() {
    try {
      properties = new Properties();
      properties.load(new FileInputStream(fileName));
      System.out.println("Configuration file is read successfully.");
    }
    catch (Exception e) {
      System.out.println("Configuration file reading failed.");
      System.out.println(e);
    }
  }
  
	public static void main(String[] args) {
		ConfigReader reader = new ConfigReader("data/reason.config");
		//reader.printPropertyList();
		System.out.println(reader.getProperty("SiteData"));
	}
}
