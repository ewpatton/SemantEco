package edu.rpi.tw.eScience.WaterQualityPortal.epa;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.csvreader.*;

public class EpaCSVAgent {
	
	public void CSVRead(){
		CsvReader reader = null;
		int recordNum = 0;
		try {
			reader = new CsvReader("/home/ping/research/python/water/csv/128967058817850.csv");		

		reader.readHeaders();
		recordNum++;

		while (reader.readRecord())
		{
			
			String productID = reader.get(13);
			recordNum++;
			System.out.println("Record " + recordNum + ": "+productID);
			// perform program logic here

		}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			reader.close();
		}
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		EpaCSVAgent csvAgent = new EpaCSVAgent();
		csvAgent.CSVRead();
	}

}
